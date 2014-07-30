package com.gigaspaces.storm.googleanalytics;

import com.gigaspaces.storm.googleanalytics.model.feeder.PageView;
import com.gigaspaces.storm.tools.GigaSpaceFactory;
import org.openspaces.core.GigaSpace;

/**
 * @author Oleksiy_Dyagilev
 */
public class TestFeeder {
    public static void main(String[] args) {
        GigaSpace space = GigaSpaceFactory.getInstance("jini://*/*/space");

        PageView pageView = new PageView();
        pageView.setPage("page");
        pageView.setReferral("refferal");
        pageView.setSessionId("session");
        pageView.setIp("151.38.39.114");

        space.write(pageView);
    }
}
