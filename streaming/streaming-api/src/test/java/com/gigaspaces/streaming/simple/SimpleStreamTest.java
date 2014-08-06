package com.gigaspaces.streaming.simple;

import com.gigaspaces.annotation.pojo.FifoSupport;
import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SimpleStreamTest {

    @Autowired
    private GigaSpace space;

    @Test
    public void testReadWrite() {
        SimpleStream<Person> stream = new SimpleStream<>(space, new Person());
        stream.write(new Person("1"));
        stream.write(new Person("2"));
        stream.write(new Person("3"));
        Person p1 = stream.read();
        Person p2 = stream.read();
        Person p3 = stream.read();
        assertEquals("1", p1.getName());
        assertEquals("2", p2.getName());
        assertEquals("3", p3.getName());

        assertNull(stream.read());
    }

    @Test
    public void testReadWriteBatch() {
        SimpleStream<Person> stream = new SimpleStream<>(space, new Person());
        List<Person> people = Arrays.asList(new Person("1"), new Person("2"), new Person("3"));
        stream.writeBatch(people);
        Person[] persons = stream.readBatch(2);
        assertEquals(2, persons.length);
        assertEquals("1", persons[0].getName());
        assertEquals("2", persons[1].getName());

        persons = stream.readBatch(1);
        assertEquals(1, persons.length);
        assertEquals("3", persons[0].getName());
    }


}


@SpaceClass(fifoSupport = FifoSupport.OPERATION)
class Person implements Serializable {
    private String id;
    private String name;

    Person() {
    }

    Person(String name) {
        this.name = name;
    }

    @SpaceId(autoGenerate = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}