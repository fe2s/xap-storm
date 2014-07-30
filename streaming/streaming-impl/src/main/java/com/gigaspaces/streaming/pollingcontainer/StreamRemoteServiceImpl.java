package com.gigaspaces.streaming.pollingcontainer;

import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.streaming.pollingcontainer.service.StreamRemoteService;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.context.GigaSpaceContext;
import org.openspaces.remoting.RemotingService;
import org.openspaces.remoting.Routing;

/**
 * @author Mykola_Zalyayev
 */
@RemotingService
public class StreamRemoteServiceImpl implements StreamRemoteService {

    private Long lastTxId;

    @GigaSpaceContext
    private GigaSpace space;

    @Override
    public Batch read(@Routing int partitionId, Long txId) {
        if (lastTxId == null) {
            Batch batch = replayBatch(txId, partitionId);
            if (batch != null) {
                return batch;
            }
            return readNewBatch(txId, partitionId);
        }
        if (lastTxId < txId) {
            return readNewBatch(txId, partitionId);
        } else {
            return replayBatch(txId, partitionId);
        }
    }

    private Batch readNewBatch(Long txId, int partitionNumber) {
        SQLQuery<Batch> readQuery = new SQLQuery<>(Batch.class, "txId is null AND partitionNumber = ?");
        readQuery.setParameters(partitionNumber);
        Batch batch = space.read(readQuery);
        if (batch == null) {
            return null;
        }
        SQLQuery<Batch> query = new SQLQuery<>(Batch.class, "id=?");
        query.setParameters(batch.getId());

        space.change(query, new ChangeSet().set("txId", txId));

        lastTxId = txId;
        batch.setTxId(txId);
        return batch;
    }

    private Batch replayBatch(Long txId, int partitionNumber){
        SQLQuery<Batch> readQuery = new SQLQuery<>(Batch.class, "txId =? AND partitionNumber = ?");
        readQuery.setParameters(txId,partitionNumber);
        Batch batch = space.read(readQuery);
        return batch;
    }
}
