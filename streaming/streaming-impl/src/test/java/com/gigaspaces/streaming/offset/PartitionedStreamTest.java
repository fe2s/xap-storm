package com.gigaspaces.streaming.offset;

import com.gigaspaces.annotation.pojo.FifoSupport;
import com.gigaspaces.annotation.pojo.SpaceClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.cluster.ClusterInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PartitionedStreamTest {

    @Autowired
    private GigaSpace space;
    @Autowired
    private StreamManager streamManager;


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testReadWriteSizeAck() {
        PartitionedStream<Person> stream = streamManager.getStream("myStream");

        stream.write(new Person("1"));
        stream.write(new Person("2"));
        stream.write(new Person("3"));
        stream.write(new Person("4"));

        assertEquals(4, stream.size());

        List<Person> batch1 = stream.readBatch(1, 2);
        assertEquals(2, batch1.size());
        assertEquals("1", batch1.get(0).name);
        assertEquals("2", batch1.get(1).name);

        List<Person> batch2 = stream.readBatch(2, 2);
        assertEquals(2, batch2.size());
        assertEquals("3", batch2.get(0).name);
        assertEquals("4", batch2.get(1).name);

        // reread batch 1
        List<Person> batch1Reread = stream.readBatch(1, 2);
        assertEquals(2, batch1Reread.size());
        assertEquals("1", batch1Reread.get(0).name);
        assertEquals("2", batch1Reread.get(1).name);

        stream.write(new Person("5"));
        stream.write(new Person("6"));

        List<Person> batch3 = stream.readBatch(3, 2);
        assertEquals(2, batch3.size());
        assertEquals("5", batch3.get(0).name);
        assertEquals("6", batch3.get(1).name);

        assertEquals(6, stream.size());

        assertEquals(3, stream.ackPendingBatchCount());
        stream.ack(1L);
        assertEquals(4, stream.size());
        assertEquals(2, stream.ackPendingBatchCount());
        stream.ack(2L);
        stream.ack(3L);
        assertEquals(0, stream.size());
        assertEquals(0, stream.ackPendingBatchCount());

        stream.write(new Person("7"));
        stream.write(new Person("8"));

        // reload stream
        streamManager.reloadStreams();
        stream = streamManager.getStream("myStream");
        List<Person> batch4 = stream.readBatch(4, 4);
        assertEquals(2, batch4.size());
        assertEquals("7", batch4.get(0).name);
        assertEquals("8", batch4.get(1).name);

        List<Person> rereadBatch4 = stream.readBatch(4, 4);
        assertEquals(2, rereadBatch4.size());

    }

    @Test
    public void testWriteBatch() {
        PartitionedStream<Person> stream = streamManager.getStream("writeBatchTest");
        stream.writeBatch(Arrays.asList(new Person("1"), new Person("2"), new Person("3")));
        stream.writeBatch(Arrays.asList(new Person("4"), new Person("5"), new Person("6")));
        List<Person> batch = stream.readBatch(1, 6);
        assertEquals(6, batch.size());
    }


}

class Person {
    public String name;

    Person(String name) {
        this.name = name;
    }
}