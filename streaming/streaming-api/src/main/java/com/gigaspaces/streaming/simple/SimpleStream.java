package com.gigaspaces.streaming.simple;

import com.gigaspaces.client.ReadModifiers;
import com.gigaspaces.client.TakeModifiers;
import com.gigaspaces.client.WriteModifiers;
import org.openspaces.core.GigaSpace;

import java.util.List;

/**
 * Simple stream with ability to write and read data.
 *
 * @author Oleksiy_Dyagilev
 */
public class SimpleStream<T> {

    private GigaSpace space;
    private T template;

    public SimpleStream(GigaSpace space, T template) {
        this.space = space;
        this.template = template;
    }

    public void write(T value){
        space.write(value);
    }

    public void writeBatch(List<T> values){
        space.writeMultiple(values.toArray());
    }

    public T read() {
        return space.take(template, 0L, TakeModifiers.FIFO);
    }

    public T[] readBatch(int maxNumber){
        return space.takeMultiple(template, maxNumber, TakeModifiers.FIFO);
    }

}
