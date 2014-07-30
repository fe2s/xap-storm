package com.gigaspaces.streaming.pollingcontainer.service;

import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.streaming.pollingcontainer.Batch;
import com.j_spaces.core.client.SQLQuery;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mykola_Zalyayev
 */
public interface PollingContainerStreamService {

    void write(Serializable o);

    void writeBatch(List<Serializable> batch);

    Batch read(Long txId, int partitionNumber);

    void acknowledge(Long txId, int partitionNumber);
}
