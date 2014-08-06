package com.gigaspaces.storm.googleanalytics.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Tracks last seen time for users.
 *
 * @author Oleksiy_Dyagilev
 */
public class LastSeenTracker<T> {

    private static Logger log = LoggerFactory.getLogger(LastSeenTracker.class);

    private Map<T, Long> lastSeenMap = new HashMap<>();

    public void track(T obj) {
        lastSeenMap.put(obj, currentTime());
    }

    public int numberOfSeenInLastNSeconds(long nSeconds) {
        truncateNotSeenLongerThanN(nSeconds);
        return lastSeenMap.size();
    }

    private void truncateNotSeenLongerThanN(long nSeconds) {
        long nMillis = nSeconds * 1000;
        Long currentTime = currentTime();
        Iterator<Map.Entry<T, Long>> iterator = lastSeenMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<T, Long> entry = iterator.next();
            Long lastSeenTime = entry.getValue();
            if (currentTime - lastSeenTime > nMillis) {
                iterator.remove();
            }
        }
    }

    // extracted to method for testing purpose
    public Long currentTime() {
        return System.currentTimeMillis();
    }

}
