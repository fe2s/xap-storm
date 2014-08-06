package com.gigaspaces.storm.googleanalytics.spout;

import backtype.storm.tuple.Fields;
import com.gigaspaces.storm.googleanalytics.model.feeder.PageView;
import com.gigaspaces.storm.spout.TupleConverter;
import com.gigaspaces.storm.spout.XAPSimpleSpout;

import java.util.Arrays;
import java.util.List;

/**
 * Spout of page views.
 * Streams page views from XAP.
 *
 * @author Oleksiy_Dyagilev
 */
public class PageViewSpout extends XAPSimpleSpout<PageView> {
    public PageViewSpout() {
        super(new PageViewTupleConverter(), new PageView());
    }
}

class PageViewTupleConverter implements TupleConverter<PageView> {
    @Override
    public Fields tupleFields() {
        return new Fields("url", "referral", "session", "ip");
    }

    @Override
    public List<Object> spaceObjectToTuple(PageView pageView) {
        return Arrays.<Object>asList(pageView.getPage(), pageView.getReferral(), pageView.getSessionId(), pageView.getIp());
    }
}