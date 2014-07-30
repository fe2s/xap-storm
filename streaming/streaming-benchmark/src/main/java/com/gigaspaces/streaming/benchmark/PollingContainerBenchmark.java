package com.gigaspaces.streaming.benchmark;

import com.gigaspaces.streaming.pollingcontainer.Batch;
import com.gigaspaces.streaming.pollingcontainer.service.PollingContainerStreamService;
import com.gigaspaces.streaming.pollingcontainer.service.PollingContainerStreamServiceImpl;
import com.gigaspaces.streaming.pollingcontainer.service.StreamRemoteService;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.openspaces.core.GigaSpace;
import org.openspaces.remoting.ExecutorRemotingProxyConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Mykola_Zalyayev
 */
@Component
public class PollingContainerBenchmark {

    private static Logger log = Logger.getLogger(Benchmark.class);

    @Autowired
    private GigaSpace remoteSpace;

    private final int itemsNumber = 200000;
    private final int writerThreadsN = 10;
    private final int partitionNumber = 4;
    private final int batchSize = 200;

    private static Long txID = 0L;

    private PollingContainerStreamService streamService;


    @PostConstruct
    public void start() throws Exception {
        log.info("Starting benchmark");

        ExecutorService readWriteExecutor = Executors.newFixedThreadPool(2);

        StreamRemoteService remoteService = new ExecutorRemotingProxyConfigurer<>(remoteSpace, StreamRemoteService.class)
                .proxy();

        streamService = new PollingContainerStreamServiceImpl(remoteSpace, remoteService);

        StopWatch totalStopWatch = new StopWatch();
        totalStopWatch.start();

        readWriteExecutor.execute(new ReadExecutor());

//        readWriteExecutor.execute(new WriteExecutor());

        readWriteExecutor.shutdown();
        readWriteExecutor.awaitTermination(1, TimeUnit.MINUTES);

        float duration = totalStopWatch.getTime();

        log.info(String.format("Time taken to write and read %d items is %s ms. Throughput per sec %s", itemsNumber, duration, itemsNumber / (duration / 1000)));

    }

    class ReadExecutor implements Runnable {

        @Override
        public void run() {
            ExecutorService readExecutorService = Executors.newFixedThreadPool(partitionNumber);

            for (int i = 0; i < partitionNumber; i++) {
                final int routing = i;
                readExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        int i = 0;
                        while (i < (itemsNumber / partitionNumber)) {

                            Batch batch = streamService.read((long) txID, routing);
                            if (batch == null) {
                                continue;
                            }

                            txID++;

                            int readCount = batch.getItems().size();
                            i += readCount;

                            streamService.acknowledge(batch.getTxId(), batch.getPartitionNumber());
                        }
                    }
                });
            }

            StopWatch readStopWatch = new StopWatch();
            readStopWatch.start();

            readExecutorService.shutdown();
            try {
                readExecutorService.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException ignored) {

            }
            readStopWatch.stop();
            float readDuration = readStopWatch.getTime();
            log.info(String.format("Time taken to read %d items is %s ms. Read throughput per sec %s", itemsNumber, readDuration, itemsNumber / (readDuration / 1000)));
        }
    }

    class WriteExecutor implements Runnable {

        @Override
        public void run() {
            ExecutorService writeExecutorService = Executors.newFixedThreadPool(writerThreadsN);

            StopWatch writeStopWatch = new StopWatch();
            writeStopWatch.start();

            for (int t = 0; t < writerThreadsN; t++) {
                writeExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        StopWatch stopWatch = new StopWatch();
                        stopWatch.start();
                        Person person = new Person(UUID.randomUUID().toString());
                        int i = 0;
                        List<Serializable> batchList = new ArrayList<>();
                        for (; i < itemsNumber / writerThreadsN; i++) {
                            batchList.add(person);
                            if (i == batchSize) {
                                streamService.writeBatch(batchList);
                                batchList.clear();
                            }
                        }

                        if (!batchList.isEmpty()) {
                            streamService.writeBatch(batchList);
                            batchList.clear();
                        }
                        stopWatch.stop();
                        float duration = stopWatch.getTime();
                        log.info(String.format("Time taken to write %d items is %s ms. Write throughput per sec %s", i, duration, i / (duration / 1000)));
                    }
                });
            }
            writeExecutorService.shutdown();
            try {
                writeExecutorService.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException ignored) {

            }

            writeStopWatch.stop();
            float duration = writeStopWatch.getTime();
            log.info(String.format("Time taken to write %d items is %s ms. Write throughput per sec %s", itemsNumber, duration, itemsNumber / (duration / 1000)));


        }
    }
}
