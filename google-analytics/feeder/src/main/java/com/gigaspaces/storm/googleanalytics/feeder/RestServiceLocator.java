package com.gigaspaces.storm.googleanalytics.feeder;

import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;

/**
 * @author Oleksiy_Dyagilev
 */
public class RestServiceLocator {
    private static final String WEB_PU_NAME = "web";

    public static String findRestServiceAddress(String xapLookupLocator) {
        Admin admin = new AdminFactory().addLocators(xapLookupLocator).createAdmin();
        ProcessingUnit webPu = admin.getProcessingUnits().waitFor(WEB_PU_NAME);
        ProcessingUnitInstance putInstance = webPu.getInstances()[0];
        String hostName = putInstance.getJeeDetails().getHost();
        Integer port = putInstance.getJeeDetails().getPort();
        return "http://" + hostName + ":" + port;
    }
}
