package com.gigaspaces.storm.trident.spout;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.storm.config.ConfigConstants;
import com.gigaspaces.storm.spout.TupleConverter;
import com.gigaspaces.storm.trident.state.XAPStateFactory;
import com.gigaspaces.storm.trident.state.XAPStateItem;
import com.gigaspaces.storm.trident.wordcount.Split;
import com.gigaspaces.streaming.offset.service.PartitionedStreamService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openspaces.core.GigaSpace;
import org.openspaces.remoting.ExecutorRemotingProxyConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;
import storm.trident.operation.builtin.FilterNull;
import storm.trident.operation.builtin.MapGet;
import storm.trident.operation.builtin.Sum;
import storm.trident.state.StateFactory;
import storm.trident.testing.FixedBatchSpout;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author Oleksiy_Dyagilev
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class XAPTransactionalTridentSpoutTest {

    @Autowired
    private GigaSpace space;
    private LocalCluster cluster;
    private LocalDRPC drpc;

    @Before
    public void init(){
        cluster = new LocalCluster();
        drpc = new LocalDRPC();
    }

    @After
    public void shutdown() throws InterruptedException {
        space.clear(new XAPStateItem());
        cluster.shutdown();
        drpc.shutdown();

        Thread.sleep(5000);
    }

    @Test
    public void testSpout() throws Exception {
        Config conf = new Config();
        conf.setMaxSpoutPending(20);
        conf.put(ConfigConstants.XAP_SPACE_URL_KEY, "jini://*/*/space?groups=XAPTransactionalTridentSpoutTest-test");


        cluster.submitTopology("word-counter", conf, buildTopology());

        Thread.sleep(5000);
        String result = drpc.execute("words", "cat the dog jumped");

        assertEquals("[[6]]", result);
    }

    private StormTopology buildTopology() throws Exception {
        StateFactory stateFactory = XAPStateFactory.transactional();

        Thread streamWriterThread = new Thread(new StreamWriter(space));
        streamWriterThread.start();
        streamWriterThread.join();

        XAPTransactionalTridentSpout<Sentence> spout = new XAPTransactionalTridentSpout<>(new SentenceConverter(), "sentenceStream", 10);


        TridentTopology topology = new TridentTopology();

        TridentState wordCounts = topology
                .newStream("spout1", spout)
                .each(new Fields("sentence"), new Split(), new Fields("word"))
                .groupBy(new Fields("word"))
                .persistentAggregate(stateFactory, new Count(), new Fields("count")).parallelismHint(16);

        topology.newDRPCStream("words", drpc)
                .each(new Fields("args"), new Split(), new Fields("word"))
                .groupBy(new Fields("word"))
                .stateQuery(wordCounts, new Fields("word"), new MapGet(), new Fields("count"))
                .each(new Fields("count"), new FilterNull())
                .aggregate(new Fields("count"), new Sum(), new Fields("sum"));

        return topology.build();
    }

}

class StreamWriter implements Runnable {

    GigaSpace space;

    StreamWriter(GigaSpace space) {
        this.space = space;
    }

    @Override
    public void run() {
        Sentence[] sentences = new Sentence[]{
                new Sentence("the cow jumped over the moon"),
                new Sentence("the man went to the store and bought some candy"),
                new Sentence("four score and seven years ago"),
                new Sentence("how many apples can you eat"),
                new Sentence("to be or not to be the person")
        };

        PartitionedStreamService<Sentence> streamService = new ExecutorRemotingProxyConfigurer<>(space, PartitionedStreamService.class).proxy();
        for (Sentence sentence : sentences) {
            streamService.write(1, "sentenceStream", sentence);
        }

    }
}


@SpaceClass
class Sentence implements Serializable {
    private String id;
    private String val;

    Sentence() {
    }

    Sentence(String val) {
        this.val = val;
    }

    @SpaceId(autoGenerate = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}

class SentenceConverter implements TupleConverter<Sentence>{

    @Override
    public Fields tupleFields() {
        return new Fields("sentence");
    }

    @Override
    public List<Object> spaceObjectToTuple(Sentence sentence) {
        return Arrays.<Object>asList(sentence.getVal());
    }
}