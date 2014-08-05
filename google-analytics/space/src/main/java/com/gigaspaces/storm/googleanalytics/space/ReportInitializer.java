package com.gigaspaces.storm.googleanalytics.space;

import com.gigaspaces.storm.googleanalytics.model.reports.OverallReport;
import org.apache.log4j.Logger;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.cluster.ClusterInfo;
import org.openspaces.core.cluster.ClusterInfoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
* @author Mykola_Zalyayev
*/
@Component
public class ReportInitializer {

    private static Logger log = Logger.getLogger(ReportInitializer.class);

    @Autowired
    private GigaSpace space;

    @ClusterInfoContext
    private ClusterInfo clusterInfo;

    @PostConstruct
    public void initReport() {
        // clusterInfo is null when running Integrated PU
        int instanceId = clusterInfo == null ? 1 : clusterInfo.getInstanceId();

        if (instanceId == 1) {
            log.info("Initializing report object");
            space.getClustered().write(new OverallReport());
        }
    }
}
