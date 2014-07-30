package com.gigaspaces.storm.trident.wordcount;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.gigaspaces.storm.config.ConfigConstants;
import com.gigaspaces.storm.trident.state.XAPStateFactory;
import com.gigaspaces.storm.trident.state.XAPStateItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openspaces.core.GigaSpace;
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

import static junit.framework.Assert.assertEquals;

/**
 * @author Mykola_Zalyayev
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TridentWordCountTest {

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
    public void testTridentTopologyWithXapOpaqueState() throws InterruptedException {
        StateFactory stateFactory = XAPStateFactory.opaque();
        createTrident(stateFactory, "opaqueWordCounter");
    }

    @Test
    public void testTridentTopologyWithXapNonTransactionalOpaqueState() throws InterruptedException {
        StateFactory stateFactory = XAPStateFactory.nonTransactional();
        createTrident(stateFactory, "nonTransactionalWordCounter");
    }

    @Test
    public void testTridentTopologyWithXapTransactionalState() throws InterruptedException {
        StateFactory stateFactory = XAPStateFactory.transactional();
        createTrident(stateFactory, "transactionalWordCounter");
    }

    private void createTrident(StateFactory stateFactory, String topologyName) throws InterruptedException {
        Config conf = new Config();
        conf.setMaxSpoutPending(20);
        conf.put(ConfigConstants.XAP_SPACE_URL_KEY, "jini://*/*/space?groups=trident-test");

        cluster.submitTopology(topologyName, conf, buildTopology(stateFactory));

        Thread.sleep(5000);
        String result = drpc.execute("words", "cat the dog jumped");

        assertEquals("[[6]]", result);
    }

    private StormTopology buildTopology(StateFactory stateFactory) {
        FixedBatchSpout spout = new FixedBatchSpout(new Fields("sentence"), 3, new Values("the cow jumped over the moon"),
                new Values("the man went to the store and bought some candy"), new Values("four score and seven years ago"),
                new Values("how many apples can you eat"), new Values("to be or not to be the person"));
        TridentTopology topology = new TridentTopology();

        TridentState wordCounts = topology
                .newStream("spout1", spout).parallelismHint(16)
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
