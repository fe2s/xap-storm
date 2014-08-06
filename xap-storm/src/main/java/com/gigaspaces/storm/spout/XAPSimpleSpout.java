package com.gigaspaces.storm.spout;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import com.gigaspaces.storm.config.ConfigConstants;
import com.gigaspaces.storm.tools.GigaSpaceFactory;
import com.gigaspaces.streaming.simple.SimpleStream;
import org.openspaces.core.GigaSpace;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Oleksiy_Dyagilev
 */
public class XAPSimpleSpout<T extends Serializable> extends BaseRichSpout {

    protected int defaultXapStreamBatchSize = 300;

    protected SpoutOutputCollector collector;

    protected String xapSpaceUrl;
    protected TupleConverter<T> tupleConverter;
    protected SimpleStream<T> xapStream;
    protected T xapStreamTemplateItem;
    protected int xapStreamBatchSize;

    public XAPSimpleSpout(TupleConverter<T> tupleConverter, T xapStreamTemplateItem) {
        this.tupleConverter = tupleConverter;
        this.xapStreamTemplateItem = xapStreamTemplateItem;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        this.setupXapSpaceUrl(conf);
        this.setupXapStreamBatchSize(conf);
        this.setupXapStream();
    }

    @Override
    public void nextTuple() {
        T[] spaceObjects = xapStream.readBatch(xapStreamBatchSize);
        for (T spaceObject : spaceObjects) {
            List<Object> tuple = tupleConverter.spaceObjectToTuple(spaceObject);
            collector.emit(tuple);
        }
    }

    protected void setupXapStreamBatchSize(Map conf) {
        Integer xapStreamBatchSize = (Integer) conf.get(ConfigConstants.XAP_STREAM_BATCH_SIZE);
        if (xapStreamBatchSize == null) {
            xapStreamBatchSize = defaultXapStreamBatchSize;
        }
        this.xapStreamBatchSize = xapStreamBatchSize;
    }

    protected void setupXapSpaceUrl(Map conf) {
        // if not set explicitly, lookup in config
        if (xapSpaceUrl == null) {
            xapSpaceUrl = (String) conf.get(ConfigConstants.XAP_SPACE_URL_KEY);
        }
        if (xapSpaceUrl == null) {
            throw new RuntimeException("XAP space url is not set");
        }
    }

    protected void setupXapStream() {
        GigaSpace space = GigaSpaceFactory.getInstance(xapSpaceUrl);
        this.xapStream = new SimpleStream<>(space, xapStreamTemplateItem);
    }

    public void setXapSpaceUrl(String xapSpaceUrl) {
        this.xapSpaceUrl = xapSpaceUrl;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(tupleConverter.tupleFields());
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

}
