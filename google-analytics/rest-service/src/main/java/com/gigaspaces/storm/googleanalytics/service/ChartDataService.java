package com.gigaspaces.storm.googleanalytics.service;

import com.gigaspaces.storm.googleanalytics.model.reports.*;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mykola_Zalyayev
 */
@Service
public class ChartDataService {

    @Autowired
    private GigaSpace space;

    public OverallReport getOverallChartDataReport() {
        return space.readById(OverallReport.class, "gigaspaces.com");
    }
}
