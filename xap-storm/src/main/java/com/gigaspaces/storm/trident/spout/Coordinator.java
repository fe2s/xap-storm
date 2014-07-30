package com.gigaspaces.storm.trident.spout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.spout.ITridentSpout;

import java.util.UUID;

/**
 * @author Oleksiy_Dyagilev
 */
public class Coordinator implements ITridentSpout.BatchCoordinator<String> {

    private static Logger log = LoggerFactory.getLogger(Emitter.class);

    public Coordinator() {
        log.trace("Creating coordinator");
    }

    @Override
    public String initializeTransaction(long txid, String prevMetadata, String currMetadata) {
        log.trace("initializeTransaction txid " + txid);
        return null;
    }

    @Override
    public void success(long txid) {
        log.trace("success txid" + txid);
    }

    @Override
    public boolean isReady(long txid) {
        log.trace("isReady txId" + txid);
        return true;
    }

    @Override
    public void close() {
        log.trace("close");
    }
}
