package com.gigaspaces.storm.trident.spout;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import com.gigaspaces.storm.config.ConfigConstants;
import com.gigaspaces.storm.spout.TupleConverter;
import com.gigaspaces.storm.tools.GigaSpaceFactory;
import org.openspaces.core.GigaSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.spout.ITridentSpout;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Oleksiy_Dyagilev
 */
public class XAPTransactionalTridentSpout<T> implements ITridentSpout<String> {

    private static Logger log = LoggerFactory.getLogger(XAPTransactionalTridentSpout.class);

    private String streamId;
    private int batchSize;
    private TupleConverter<T> tupleConverter;

    public XAPTransactionalTridentSpout(TupleConverter<T> tupleConverter, String streamId, int batchSize) {
        log.trace("Creating XAPTransactionalTridentSpout");
        this.tupleConverter = tupleConverter;
        this.batchSize = batchSize;
        this.streamId = streamId;
    }

    @Override
    public BatchCoordinator<String> getCoordinator(String txStateId, Map conf, TopologyContext context) {
        log.trace("getCoordinator");
        return new Coordinator();
    }

    @Override
    public ITridentSpout.Emitter<String> getEmitter(String txStateId, Map conf, TopologyContext context) {
        log.trace("getEmitter txStateId " + txStateId);
        GigaSpace gigaSpace = getGigaSpace(conf);

        return new com.gigaspaces.storm.trident.spout.Emitter<>(streamId, gigaSpace, batchSize, tupleConverter);
    }

    private GigaSpace getGigaSpace(Map conf) {
        String spaceUrl = (String) conf.get(ConfigConstants.XAP_SPACE_URL_KEY);
        if (spaceUrl == null) {
            throw new RuntimeException("XAP space url is not set");
        }
        return GigaSpaceFactory.getInstance(spaceUrl);
    }

    @Override
    public Map getComponentConfiguration() {
        return null;
    }

    @Override
    public Fields getOutputFields() {
        return tupleConverter.tupleFields();
    }
}
