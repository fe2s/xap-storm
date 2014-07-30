package com.gigaspaces.storm.googleanalytics.feeder;

import org.springframework.stereotype.Component;

/**
 * @author Mykola_Zalyayev
 */
@Component
public class RequestCountHelper {

    private static final int NORMAL_COUNT = 100;
    private static final int MIN_COUNT = 20;
    private static final int MAX_COUNT = 500;

    private int batchSize;
    private long loadIncreaseInterval;
    private long lastLoadIncreaseTime;
    private State currentState;

    public RequestCountHelper() {
        batchSize = 100;
        loadIncreaseInterval = System.currentTimeMillis();
        lastLoadIncreaseTime = nextInterval();
        currentState = State.NORMAL;
    }

    public int nextCount() {
        if (lastLoadIncreaseTime + loadIncreaseInterval < System.currentTimeMillis()) {
            lastLoadIncreaseTime = System.currentTimeMillis();
            loadIncreaseInterval = nextInterval();

            currentState = nextState();
        }

        switch (currentState) {
            case NORMAL:
                decreaseBatchSize(NORMAL_COUNT);
                increaseBatchSize(NORMAL_COUNT);
                break;
            case INCREASE:
                increaseBatchSize(MAX_COUNT);
                break;
            case DECREASE:
                decreaseBatchSize(MIN_COUNT);
                break;
        }

        int maxSendCount = batchSize / 10;
        int minSendCount = (int) (maxSendCount * 0.8);

        return (int) (Math.random() * (maxSendCount - minSendCount) + maxSendCount);
    }

    private void increaseBatchSize(int increaseTo){
        if(batchSize<increaseTo){
            batchSize++;
        }
    }

    private void decreaseBatchSize(int decreaseTo){
        if(batchSize> decreaseTo){
            batchSize--;
        }
    }

    private State nextState() {
        int code = (int) (Math.random() * 100);
        return State.stateByCode(code % 3);
    }

    private long nextInterval() {
        return (long) ((Math.random() * (50000 - 10000)) + 10000);
    }

    enum State {
        NORMAL(0), INCREASE(1), DECREASE(2);

        private int code;

        State(int code) {
            this.code = code;
        }

        public static State stateByCode(int code) {
            for (State state : values()) {
                if (state.code == code) {
                    return state;
                }
            }
            return null;
        }
    }
}
