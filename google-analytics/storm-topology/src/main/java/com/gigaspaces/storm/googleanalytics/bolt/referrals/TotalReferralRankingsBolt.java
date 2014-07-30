package com.gigaspaces.storm.googleanalytics.bolt.referrals;

import backtype.storm.topology.BasicOutputCollector;
import com.gigaspaces.storm.googleanalytics.bolt.generic.TotalRankingsBolt;
import com.gigaspaces.storm.googleanalytics.model.reports.TopReferralsReport;
import com.gigaspaces.storm.googleanalytics.tools.Rankable;

import java.util.LinkedHashMap;

/**
 * @author Oleksiy_Dyagilev
 */
public class TotalReferralRankingsBolt extends TotalRankingsBolt {

    public TotalReferralRankingsBolt(int topN, int emitFrequencyInSeconds) {
        super(topN, emitFrequencyInSeconds);
    }

    @Override
    protected void emitRankings(BasicOutputCollector collector) {
        super.emitRankings(collector);

        LinkedHashMap<String, Long> topReferrals = new LinkedHashMap<>();
        for (Rankable rankable : getRankings().getRankings()) {
            topReferrals.put((String)rankable.getObject(), rankable.getCount());
        }

        TopReferralsReport topReferralsReport = new TopReferralsReport(topReferrals);
        space.write(topReferralsReport);
    }
}
