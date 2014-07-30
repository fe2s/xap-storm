package com.gigaspaces.storm.googleanalytics.topology;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.FailedException;
import backtype.storm.tuple.Fields;
import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.storm.config.ConfigConstants;
import com.gigaspaces.storm.googleanalytics.util.StormRunner;
import com.gigaspaces.storm.spout.TupleConverter;
import com.gigaspaces.storm.tools.GigaSpaceFactory;
import com.gigaspaces.storm.trident.spout.XAPTransactionalTridentSpout;
import com.gigaspaces.streaming.offset.service.PartitionedStreamService;
import org.openspaces.core.GigaSpace;
import org.openspaces.remoting.ExecutorRemotingProxyConfigurer;
import storm.trident.TridentTopology;
import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Oleksiy_Dyagilev
 */
public class TestTopology {

    public static void main(String[] args) throws Exception {
        GigaSpace space = GigaSpaceFactory.getInstance("jini://*/*/space?locators=127.0.0.1");
        Sentence[] sentences = new Sentence[]{
                new Sentence("the cow jumped over the moon"),
                new Sentence("the man went to the store and bought some candy"),
                new Sentence("four score and seven years ago"),
                new Sentence("how many apples can you eat"),
                new Sentence("to be or not to be the person")
        };

        PartitionedStreamService<Sentence> streamService = new ExecutorRemotingProxyConfigurer<>(space, PartitionedStreamService.class).proxy();
        for (int i = 0; i < 1000; i++) {
            int sentenceIndex = i % sentences.length;
            streamService.write(i, "sentenceStream", sentences[sentenceIndex]);
        }

        StormTopology stormTopology = buildTopology();

        Config conf = createTopologyConfiguration();
        conf.setNumWorkers(2);

        if (args != null && args.length > 0) {
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, stormTopology);
        } else {
            StormRunner.runTopologyLocally(stormTopology, "test", conf, 100000);
        }
    }

    private static Config createTopologyConfiguration() {
        Config conf = new Config();
        conf.setMaxSpoutPending(20);
        conf.setDebug(false);
        conf.put(ConfigConstants.XAP_SPACE_URL_KEY, "jini://*/*/space?locators=127.0.0.1");
        return conf;
    }

    private static StormTopology buildTopology() {
        XAPTransactionalTridentSpout spout = new XAPTransactionalTridentSpout(new SentenceConverter(), "sentenceStream", 10);
//        PartitionedSpout spout = new PartitionedSpout();
        TridentTopology topology = new TridentTopology();

        topology.newStream("spout1", spout).parallelismHint(2).shuffle().each(new Fields("sentence"), new Function() {
            @Override
            public void execute(TridentTuple tuple, TridentCollector collector) {
                String val = tuple.getString(0);
                System.out.println(val);
//                try {
//                    Thread.sleep(100L);
//                    throw new FailedException("failed to process");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                collector.emit(Arrays.<Object>asList(val));
            }

            @Override
            public void prepare(Map conf, TridentOperationContext context) {

            }

            @Override
            public void cleanup() {

            }
        }, new Fields("output_field2"));

        return topology.build();
//    }

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

class SentenceConverter implements TupleConverter<Sentence> {

    @Override
    public Fields tupleFields() {
        return new Fields("sentence");
    }

    @Override
    public List<Object> spaceObjectToTuple(Sentence sentence) {
        return Arrays.<Object>asList(sentence.getVal());
    }
}
