package com.gigaspaces.storm.trident.reach;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.gigaspaces.storm.config.ConfigConstants;
import com.gigaspaces.storm.reach.Followers;
import com.gigaspaces.storm.reach.ReachData;
import com.gigaspaces.storm.reach.TweetedUrl;
import com.gigaspaces.storm.trident.state.readonly.XAPReadOnlyStateFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.CombinerAggregator;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.builtin.MapGet;
import storm.trident.operation.builtin.Sum;
import storm.trident.tuple.TridentTuple;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

/**
 * @author Oleksiy_Dyagilev
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TridentReachTest {

    @Autowired
    private GigaSpace space;

    @Before
    public void setUp() throws Exception {
        ReachData.writeToSpace(space);
    }

    private StormTopology buildTopology(LocalDRPC drpc) {
        TridentTopology topology = new TridentTopology();

        TridentState tweetedUrls = topology.newStaticState(XAPReadOnlyStateFactory.byIds(TweetedUrl.class));
        TridentState followers = topology.newStaticState(XAPReadOnlyStateFactory.byIds(Followers.class));

        topology.newDRPCStream("reach", drpc)
                .stateQuery(tweetedUrls, new Fields("args"), new MapGet(), new Fields("tweetedUrls"))
                .each(new Fields("tweetedUrls"), new ExpandTweetersList(), new Fields("tweeter"))
                .shuffle()
                .stateQuery(followers, new Fields("tweeter"), new MapGet(), new Fields("followers"))
                .each(new Fields("followers"), new ExpandFollowersList(), new Fields("follower"))
                .groupBy(new Fields("follower"))
                .aggregate(new One(), new Fields("one"))
                .aggregate(new Fields("one"), new Sum(), new Fields("reach"));

        return topology.build();
    }

    @Test
    public void testTopology() throws Exception {
        LocalDRPC drpc = new LocalDRPC();

        Config conf = new Config();
        conf.put(ConfigConstants.XAP_SPACE_URL_KEY, "jini://*/*/space?groups=trident-reach-test");

        LocalCluster cluster = new LocalCluster();

        cluster.submitTopology("reach", conf, buildTopology(drpc));

        Thread.sleep(2000);

        assertEquals("[[0]]", drpc.execute("reach", "aaa"));
        assertEquals("[[16]]", drpc.execute("reach", "foo.com/blog/1"));
        assertEquals("[[14]]", drpc.execute("reach", "engineering.twitter.com/blog/5"));

        // change reference state
        space.write(new TweetedUrl("foo.com/blog/1", Arrays.asList("sally")));
        assertEquals("[[7]]", drpc.execute("reach", "foo.com/blog/1"));

        cluster.shutdown();
        drpc.shutdown();
    }


    public static class ExpandTweetersList extends BaseFunction {
        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            TweetedUrl tweetedUrl = (TweetedUrl) tuple.getValue(0);
            if (tweetedUrl != null) {
                for (String tweeter : tweetedUrl.getTweeters()) {
                    collector.emit(new Values(tweeter));
                }
            }
        }
    }

    public static class ExpandFollowersList extends BaseFunction {
        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            Followers followers = (Followers) tuple.getValue(0);
            if (followers != null) {
                for (String follower : followers.getFollowers()) {
                    collector.emit(new Values(follower));
                }
            }
        }
    }

    public static class One implements CombinerAggregator<Integer> {
        @Override
        public Integer init(TridentTuple tuple) {
            return 1;
        }

        @Override
        public Integer combine(Integer val1, Integer val2) {
            return 1;
        }

        @Override
        public Integer zero() {
            return 1;
        }
    }
}
