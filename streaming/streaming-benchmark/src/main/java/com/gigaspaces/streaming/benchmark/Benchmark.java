package com.gigaspaces.streaming.benchmark;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.streaming.offset.service.PartitionedStreamService;
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
 * @author Oleksiy_Dyagilev
 */
//@Component
public class Benchmark {

    private static Logger log = Logger.getLogger(Benchmark.class);

    @Autowired
    private GigaSpace remoteSpace;

    private final String streamId = "myStream";
    private final int itemsNumber = 1000000;
    private final int readBatchSize = 1000;
    private final int writeBatchSize = 1000;
    private final int partitionsN = 4;
    private final int writerThreadsN = 10;

    private PartitionedStreamService<Person> streamService;

    @PostConstruct
    public void start() throws Exception {
        log.info("Starting benchmark");
        streamService = new ExecutorRemotingProxyConfigurer<>(remoteSpace, PartitionedStreamService.class).proxy();

        ExecutorService readWriteExecutor = Executors.newFixedThreadPool(2);

        StopWatch totalStopWatch = new StopWatch();
        totalStopWatch.start();

//        readWriteExecutor.execute(new Writer());
        readWriteExecutor.execute(new BatchWriter());
        readWriteExecutor.execute(new Reader());

        readWriteExecutor.shutdown();
        readWriteExecutor.awaitTermination(5, TimeUnit.MINUTES);
        float duration = totalStopWatch.getTime();

        log.info(String.format("Time taken to write and read %d items is %s ms. Throughput per sec %s", itemsNumber, duration, itemsNumber / (duration / 1000)));
    }

    class Writer implements Runnable {
        public void run() {
            final Person person = new Person(UUID.randomUUID().toString());

            ExecutorService executor = Executors.newFixedThreadPool(writerThreadsN);

            StopWatch totalStopWatch = new StopWatch();
            totalStopWatch.start();

            for (int t = 0; t < writerThreadsN; t++) {
                executor.execute(new Runnable() {
                    public void run() {
                        StopWatch stopWatch = new StopWatch();
                        stopWatch.start();
                        int i = 0;
                        for (; i < itemsNumber / writerThreadsN; i++) {
                            streamService.write(i, streamId, person);
                        }
                        stopWatch.stop();
                        float duration = stopWatch.getTime();
                        log.info(String.format("Time taken to write %d items is %s ms. Thread Write throughput per sec %s", i, duration, i / (duration / 1000)));
                    }
                });
            }
            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            totalStopWatch.stop();
            float duration = totalStopWatch.getTime();
            log.info(String.format("Time taken to write %d items is %s ms. Total Write throughput per sec %s", itemsNumber, duration, itemsNumber / (duration / 1000)));
        }
    }

    class BatchWriter implements Runnable {
        public void run() {
            final List<Person> batch = new ArrayList<>();
            for (int i = 0; i < writeBatchSize; i++){
                Person person = new Person(UUID.randomUUID().toString());
                batch.add(person);
            }

            ExecutorService executor = Executors.newFixedThreadPool(writerThreadsN);

            StopWatch totalStopWatch = new StopWatch();
            totalStopWatch.start();

            for (int t = 0; t < writerThreadsN; t++) {
                executor.execute(new Runnable() {
                    public void run() {
                        StopWatch stopWatch = new StopWatch();
                        stopWatch.start();
                        int i = 0;
                        for (; i < itemsNumber / writerThreadsN / writeBatchSize; i++) {
                            streamService.writeBatch(i, streamId, batch);
                        }
                        stopWatch.stop();
                        float duration = stopWatch.getTime();
                        int itemsWritten = itemsNumber / writerThreadsN;
                        log.info(String.format("Time taken to write %d items is %s ms. Thread Write throughput per sec %s", itemsWritten, duration, itemsWritten / (duration / 1000)));
                    }
                });
            }
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            totalStopWatch.stop();
            float duration = totalStopWatch.getTime();
            log.info(String.format("Time taken to write %d items is %s ms. Total Write throughput per sec %s", itemsNumber, duration, itemsNumber / (duration / 1000)));
        }
    }


    class Reader implements Runnable {
        public void run() {
            ExecutorService executor = Executors.newFixedThreadPool(partitionsN);

            StopWatch totalStopWatch = new StopWatch();
            totalStopWatch.start();

            class PartitionReader implements Runnable {
                int partition;
                PartitionReader(int partition) {
                    this.partition = partition;
                }

                public void run() {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    int batchId = 1;
                    int readN = 0;
                    while (readN != itemsNumber/partitionsN) {
                        List<Person> people = streamService.readBatch(partition, streamId, batchId, readBatchSize);
                        streamService.ack(partition, streamId, batchId);
                        log.info(String.format("Read %d items in a batch", people.size()));
                        if (people.size() != readBatchSize){
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ignored) {
                            }
                        }
                        batchId++;
                        readN += people.size();
                    }
                    stopWatch.stop();
                    float duration = stopWatch.getTime();
                    log.info(String.format("Time taken to read %d items is %s ms. Thread Read throughput per sec %s", readN, duration, readN / (duration / 1000)));
                }
            }

            for (int p = 0; p < partitionsN; p++) {
                executor.execute(new PartitionReader(p));
            }

            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException ignored) {
            }

            totalStopWatch.stop();
            float duration = totalStopWatch.getTime();
            log.info(String.format("Time taken to read %d items is %s ms. Total Read throughput per sec %s", itemsNumber, duration, itemsNumber / (duration / 1000)));
        }
    }

}

class Person implements Serializable {
    String name;
    Person(String name) {
        this.name = name;
    }
}

