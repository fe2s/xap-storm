package com.gigaspaces.storm.reach;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.drpc.LinearDRPCTopologyBuilder;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBatchBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.gigaspaces.storm.bolt.XAPAwareBasicBolt;
import com.gigaspaces.storm.config.ConfigConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

/**
 * @author Oleksiy_Dyagilev
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ReachTest {

    @Autowired
    private GigaSpace space;

    @Before
    public void setUp() throws Exception {
        ReachData.writeToSpace(space);
    }

    private LinearDRPCTopologyBuilder construct() {
        LinearDRPCTopologyBuilder builder = new LinearDRPCTopologyBuilder("reach");
        builder.addBolt(new GetTweeters(), 4);
        builder.addBolt(new GetFollowers(), 12).shuffleGrouping();
        builder.addBolt(new PartialUniquer(), 6).fieldsGrouping(new Fields("id", "follower"));
        builder.addBolt(new CountAggregator(), 3).fieldsGrouping(new Fields("id"));

        return builder;
    }

    @Test
    public void testTopology() throws Exception {
        LinearDRPCTopologyBuilder builder = construct();

        LocalDRPC drpc = new LocalDRPC();

        Config conf = new Config();
        conf.put(ConfigConstants.XAP_SPACE_URL_KEY, "jini://*/*/space?groups=storm-reach-test");

        LocalCluster cluster = new LocalCluster();

        cluster.submitTopology("reach", conf, builder.createLocalTopology(drpc));

        Thread.sleep(2000);

        assertEquals("0", drpc.execute("reach", "aaa"));
        assertEquals("16", drpc.execute("reach", "foo.com/blog/1"));
        assertEquals("14", drpc.execute("reach", "engineering.twitter.com/blog/5"));

        cluster.shutdown();
        drpc.shutdown();
    }

    public static class GetTweeters extends XAPAwareBasicBolt {
        @Override
        public void execute(Tuple tuple, BasicOutputCollector collector) {
            Object id = tuple.getValue(0);
            String url = tuple.getString(1);

            TweetedUrl tweetedUrl = space.readById(TweetedUrl.class, url);
            if (tweetedUrl != null) {
                for (String tweeter : tweetedUrl.getTweeters()) {
                    collector.emit(new Values(id, tweeter));
                }
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("id", "follower"));
        }
    }

    public static class GetFollowers extends XAPAwareBasicBolt {
        @Override
        public void execute(Tuple tuple, BasicOutputCollector collector) {
            Object id = tuple.getValue(0);
            String tweeter = tuple.getString(1);
            Followers followers = space.readById(Followers.class, tweeter);
            if (followers != null) {
                for (String follower : followers.getFollowers()) {
                    collector.emit(new Values(id, follower));
                }
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("id", "follower"));
        }
    }

    public static class PartialUniquer extends BaseBatchBolt {
        BatchOutputCollector collector;
        Object id;
        Set<String> followers = new HashSet<String>();

        @Override
        public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, Object id) {
            this.collector = collector;
            this.id = id;
        }

        @Override
        public void execute(Tuple tuple) {
            followers.add(tuple.getString(1));
        }

        @Override
        public void finishBatch() {
            collector.emit(new Values(id, followers.size()));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("id", "partial-count"));
        }
    }

    public static class CountAggregator extends BaseBatchBolt {
        BatchOutputCollector collector;
        Object id;
        int count = 0;

        @Override
        public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, Object id) {
            this.collector = collector;
            this.id = id;
        }

        @Override
        public void execute(Tuple tuple) {
            count += tuple.getInteger(1);
        }

        @Override
        public void finishBatch() {
            collector.emit(new Values(id, count));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("id", "reach"));
        }
    }
}
