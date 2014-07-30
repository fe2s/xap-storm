package com.gigaspaces.streaming.pollingcontainer;

import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.cluster.ClusterInfo;
import org.openspaces.core.cluster.ClusterInfoAware;
import org.openspaces.core.cluster.ClusterInfoContext;
import org.openspaces.core.context.GigaSpaceContext;
import org.openspaces.events.EventDriven;
import org.openspaces.events.EventTemplate;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.polling.Polling;
import org.openspaces.events.polling.ReceiveHandler;
import org.openspaces.events.polling.receive.MultiTakeReceiveOperationHandler;
import org.openspaces.events.polling.receive.ReceiveOperationHandler;

import java.util.Arrays;

/**
 * @author Mykola_Zalyayev
 */
@EventDriven
@Polling(receiveTimeout = 60000L, passArrayAsIs = true, concurrentConsumers = 1)
@TransactionalEvent
public class StreamPollingContainer {

    @GigaSpaceContext
    private GigaSpace gigaSpace;

    @ClusterInfoContext
    private ClusterInfo clusterInfo;

    @ReceiveHandler
    ReceiveOperationHandler receiveHandler() {
        MultiTakeReceiveOperationHandler receiveHandler = new MultiTakeReceiveOperationHandler();
        receiveHandler.setMaxEntries(100);
        return receiveHandler;
    }

    @EventTemplate
    BatchItem unprocessedData() {
        return new BatchItem();
    }

    @SpaceDataEvent
    public void eventListener(BatchItem[] events) {

        Batch batch = new Batch();
        batch.setItems(Arrays.asList(events));
        batch.setPartitionNumber(clusterInfo.getInstanceId()-1);

        gigaSpace.write(batch);

    }
}
