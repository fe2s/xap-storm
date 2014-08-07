package com.gigaspaces.storm.googleanalytics.feeder;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main class for google analytics feeder.
 *
 * Parameters:
 *  LOOKUPLOCATOR - XAP lookup locator
 *
 *
 * Lookup locator used to find out ip address of Web PU using Admin API.
 * We could pass that address directly here, but it makes deployment more complex on cloud.
 *
 * @author Mykola_Zalyayev
 */
public class Main {

    public static void main(String[] args) {
        if(args.length<1){
            throw new IllegalArgumentException("Unable to find <LOOKUPLOCATOR> parameter");
        }
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        FeederStarter starter = context.getBean(FeederStarter.class);

        String xapLookupLocator = args[0];
        starter.start(xapLookupLocator, "gigaspaces.com");

    }
}
