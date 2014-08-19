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

    private static Map<String, GigaSpace> gigaSpaces = new ConcurrentHashMap<String, GigaSpace>();

    public static GigaSpace getInstance(String spaceUrl){
        if (gigaSpaces.containsKey(spaceUrl)){
            return gigaSpaces.get(spaceUrl);
        } else {
            return createInstance(spaceUrl);
        }
    }

    private static synchronized GigaSpace createInstance(String spaceUrl){
        UrlSpaceConfigurer urlSpaceConfigurer = new UrlSpaceConfigurer(spaceUrl);
        GigaSpace gigaSpace = new GigaSpaceConfigurer(urlSpaceConfigurer.space()).gigaSpace();
        gigaSpaces.put(spaceUrl, gigaSpace);
        return gigaSpace;
    }
}
