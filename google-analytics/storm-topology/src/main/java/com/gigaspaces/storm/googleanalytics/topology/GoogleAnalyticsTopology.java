package com.gigaspaces.storm.googleanalytics.topology;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import com.gigaspaces.storm.config.ConfigConstants;
import com.gigaspaces.storm.googleanalytics.bolt.activeusers.PartitionedActiveUsersBolt;
import com.gigaspaces.storm.googleanalytics.bolt.activeusers.TotalActiveUsersBolt;
import com.gigaspaces.storm.googleanalytics.bolt.generic.IntermediateRankingsBolt;
import com.gigaspaces.storm.googleanalytics.bolt.geoip.CountryRollingCountBolt;
import com.gigaspaces.storm.googleanalytics.bolt.geoip.GeoIpBolt;
import com.gigaspaces.storm.googleanalytics.bolt.pageviews.PageViewCountBolt;
import com.gigaspaces.storm.googleanalytics.bolt.pageviews.PageViewTimeSeriesBolt;
import com.gigaspaces.storm.googleanalytics.bolt.referrals.ReferralRollingCountBolt;
import com.gigaspaces.storm.googleanalytics.bolt.referrals.TotalReferralRankingsBolt;
import com.gigaspaces.storm.googleanalytics.bolt.urls.TotalUrlRankingsBolt;
import com.gigaspaces.storm.googleanalytics.bolt.urls.UrlRollingCountBolt;
import com.gigaspaces.storm.googleanalytics.spout.PageViewSpout;
import com.gigaspaces.storm.googleanalytics.util.StormRunner;

/**
 * Storm topology.
 * <p/>
 * You can run this topology in two modes:
 * <ul>
 *     <li> locally with embedded Storm. No arguments required</li>
 *     <li> deploy topology to cluster. Two arguments: topologyName gsmLocator</li>
 * </ul>
 *
 * @author Oleksiy_Dyagilev
 */
public class GoogleAnalyticsTopology {

    public static void main(String[] args) throws Exception {
        StormTopology stormTopology = buildTopology();
        Config conf = createTopologyConfiguration();

        if (args.length == 2) {
            String topologyName = args[0];
            String gsmLocator = args[1];

            conf.put(ConfigConstants.XAP_SPACE_URL_KEY, "jini://" + gsmLocator + "/*/space?groups=storm");
            StormSubmitter.submitTopologyWithProgressBar(topologyName, conf, stormTopology);
        } else if (args.length == 0) {
            conf.put(ConfigConstants.XAP_SPACE_URL_KEY, "jini://*/*/space");
            StormRunner.runTopologyLocally(stormTopology, "topology", conf, 100000);
        } else {
            System.err.println("Unexpected number of parameters. You can run this topology in two modes: \n" +
                    "   - locally with embedded Storm. No arguments required \n" +
                    "   - deploy topology to cluster. Two arguments: topologyName gsmLocator \n");
        }
    }

    private static Config createTopologyConfiguration() {
        Config conf = new Config();
        conf.setNumWorkers(2);
//        conf.setDebug(true);
        return conf;
    }

    /**
     * stream forked to several branches, each branch computes its report
     */
    private static StormTopology buildTopology() {
        String spoutId = "pageViewsSpout";
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(spoutId, new PageViewSpout());
//        builder.setSpout(spoutId, new TestPageViewSpout());
        topUrlsTopologyBranch(spoutId, builder);
        topReferralsTopologyBranch(spoutId, builder);
        activeUsersTopologyBranch(spoutId, builder);
        pageViewTimeSeriesBranch(spoutId, builder);
        geoBranch(spoutId, builder);

        return builder.createTopology();
    }

    /**
     * Top urls
     */
    private static void topUrlsTopologyBranch(String spoutId, TopologyBuilder builder) {
        String urlCounterId = "urlCounter";
        String urlIntermediateRankerId = "intermediateUrlRanker";
        String totalUrlRankerId = "totalUrlRanker";
        builder.setBolt(urlCounterId, new UrlRollingCountBolt(10, 2), 3).fieldsGrouping(spoutId, new Fields("url"));
        builder.setBolt(urlIntermediateRankerId, new IntermediateRankingsBolt(10, 1), 4).fieldsGrouping(urlCounterId, new Fields("obj"));
        builder.setBolt(totalUrlRankerId, new TotalUrlRankingsBolt(10, 1)).globalGrouping(urlIntermediateRankerId);
    }

    /**
     * Top referrals
     */
    private static void topReferralsTopologyBranch(String spoutId, TopologyBuilder builder) {
        String referralCounterId = "referralCounter";
        String referralIntermediateRankerId = "intermediateReferralRanker";
        String totalReferralRankerId = "totalReferralRanker";
        builder.setBolt(referralCounterId, new ReferralRollingCountBolt(10, 2), 3).fieldsGrouping(spoutId, new Fields("referral"));
        builder.setBolt(referralIntermediateRankerId, new IntermediateRankingsBolt(10, 1), 4).fieldsGrouping(referralCounterId, new Fields("obj"));
        builder.setBolt(totalReferralRankerId, new TotalReferralRankingsBolt(10, 1)).globalGrouping(referralIntermediateRankerId);
    }

    /**
     * Active users
     */
    private static void activeUsersTopologyBranch(String spoutId, TopologyBuilder builder) {
        String partitionedActiveUsers = "partitionedActiveUsers";
        String totalActiveUsers = "totalActiveUsers";
        builder.setBolt(partitionedActiveUsers, new PartitionedActiveUsersBolt(1, 5), 3).fieldsGrouping(spoutId, new Fields("session"));
        builder.setBolt(totalActiveUsers, new TotalActiveUsersBolt(1), 1).globalGrouping(partitionedActiveUsers);
    }

    /**
     * Pave views time series
     */
    private static void pageViewTimeSeriesBranch(String spoutId, TopologyBuilder builder) {
        String pageViewCounter = "pageViewCounter";
        String pageViewTimeSeries = "pageViewTimeSeries";
        builder.setBolt(pageViewCounter, new PageViewCountBolt(1), 3).shuffleGrouping(spoutId);
        builder.setBolt(pageViewTimeSeries, new PageViewTimeSeriesBolt(60, 1), 1).globalGrouping(pageViewCounter);
    }

    /**
     * Geo map
     */
    private static void geoBranch(String spoutId, TopologyBuilder builder) {
        String geoIp = "geoIp";
        String countryAgg = "countryAgg";
        builder.setBolt(geoIp, new GeoIpBolt(), 3).shuffleGrouping(spoutId);
        builder.setBolt(countryAgg, new CountryRollingCountBolt(10, 1), 1).globalGrouping(geoIp);
    }


}
