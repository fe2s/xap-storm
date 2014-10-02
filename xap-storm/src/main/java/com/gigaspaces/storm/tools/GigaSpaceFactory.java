package com.gigaspaces.storm.tools;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ensures that one gigaspace proxy exists per JVM
 *
 * @author Oleksiy_Dyagilev
 */
public class GigaSpaceFactory {

    private static final int LOOKUP_TIMEOUT = 60000;

    private static Map<String, GigaSpace> gigaSpaces = new ConcurrentHashMap<String, GigaSpace>();

    public static GigaSpace getInstance(String spaceUrl){
        if (gigaSpaces.containsKey(spaceUrl)){
            return gigaSpaces.get(spaceUrl);
        } else {
            return getOrCreateInstance(spaceUrl);
        }
    }

    private static synchronized GigaSpace getOrCreateInstance(String spaceUrl){
        if (gigaSpaces.containsKey(spaceUrl)) {
            return gigaSpaces.get(spaceUrl);
        }

        UrlSpaceConfigurer urlSpaceConfigurer = new UrlSpaceConfigurer(spaceUrl);
        urlSpaceConfigurer.lookupTimeout(LOOKUP_TIMEOUT);
        GigaSpace gigaSpace = new GigaSpaceConfigurer(urlSpaceConfigurer.space()).gigaSpace();
        gigaSpaces.put(spaceUrl, gigaSpace);
        return gigaSpace;
    }
}
