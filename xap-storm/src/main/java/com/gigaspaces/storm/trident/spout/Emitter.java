package com.gigaspaces.storm.trident.spout;

import com.gigaspaces.storm.spout.TupleConverter;
import com.gigaspaces.streaming.offset.service.PartitionedStreamService;
import com.gigaspaces.streaming.registry.ConsumerRegistryService;
import org.openspaces.core.GigaSpace;
import org.openspaces.remoting.ExecutorRemotingProxyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout;
import storm.trident.topology.TransactionAttempt;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Oleksiy_Dyagilev
 */
public class Emitter<T> implements ITridentSpout.Emitter<String> {

    private static Logger log = LoggerFactory.getLogger(Emitter.class);
    private GigaSpace gigaSpace;
    private String streamId;
    private int batchSize;
    private TupleConverter<T> tupleConverter;
    private int routingKey;
    private PartitionedStreamService<T> streamService;

    public Emitter(String streamId, GigaSpace gigaSpace, int batchSize, TupleConverter<T> tupleConverter) {
        log.trace("Creating Emitter");
        this.streamId = streamId;
        this.batchSize = batchSize;
        this.tupleConverter = tupleConverter;
        this.gigaSpace = gigaSpace;

        registerStreamConsumer();
        initStreamService();

    }

    @Override
    public void emitBatch(TransactionAttempt tx, String coordinatorMeta, TridentCollector collector) {
        log.trace(String.format("emitBatch txId %s, attempt id %s", tx.getTransactionId(),tx.getAttemptId()));

        List<T> spaceObjects = streamService.readBatch(routingKey, streamId, tx.getTransactionId(), batchSize);
        for (T object : spaceObjects) {
            List<Object> tuple = tupleConverter.spaceObjectToTuple(object);
            collector.emit(tuple);
        }
    }

    @Override
    public void success(TransactionAttempt tx) {
        log.trace("Emitter success txid" + tx.getTransactionId());
        streamService.ack(routingKey, streamId, tx.getTransactionId());
    }

    @Override
    public void close() {
        log.debug("close");
    }

    private void registerStreamConsumer() {
        ConsumerRegistryService registryService = new ExecutorRemotingProxyConfigurer<>(gigaSpace, ConsumerRegistryService.class).proxy();
        this.routingKey = registryService.registerConsumer(streamId);
        log.info(String.format("Registered emitter for stream %s, routingKey %d", streamId, routingKey));
    }

    @SuppressWarnings("unchecked")
    private void initStreamService(){
        streamService = new ExecutorRemotingProxyConfigurer<>(gigaSpace, PartitionedStreamService.class).proxy();
    }
}
