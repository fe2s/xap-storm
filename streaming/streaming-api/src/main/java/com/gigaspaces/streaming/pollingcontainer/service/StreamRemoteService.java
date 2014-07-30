package com.gigaspaces.streaming.pollingcontainer.service;

import com.gigaspaces.streaming.pollingcontainer.Batch;
import org.openspaces.remoting.Routing;

/**
 * @author Mykola_Zalyayev
 */
public interface StreamRemoteService {

    Batch read(@Routing int partitionId, Long txId);
}
