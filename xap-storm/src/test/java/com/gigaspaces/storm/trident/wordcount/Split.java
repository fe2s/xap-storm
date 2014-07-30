package com.gigaspaces.storm.trident.wordcount;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * @author Mykola_Zalyayev
 */
public class Split extends BaseFunction {
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        String sentence = tuple.getString(0);
        for (String word : sentence.split(" ")) {
            collector.emit(new Values(word));
        }
    }
}
