package com.gigaspaces.storm.googleanalytics.feeder;

import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.pu.ProcessingUnit;

/**
 * @author Oleksiy_Dyagilev
 */
public class RestServiceLocator {
    private static final String WEB_PU_NAME = "web";
    private static final String WEB_PU_PORT = "8090";

    public static String findRestServiceAddress(String xapLookupLocator) {
        Admin admin = new AdminFactory().addLocators(xapLookupLocator).createAdmin();
        ProcessingUnit webPu = admin.getProcessingUnits().waitFor(WEB_PU_NAME);
        String hostName = webPu.getInstances()[0].getMachine().getHostName();
        return "http://" + hostName + ":" + WEB_PU_PORT;
    }
}
