package com.gigaspaces.streaming.pollingcontainer;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;

import java.io.Serializable;

/**
 * @author Mykola_Zalyayev
 */
@SpaceClass
public class BatchItem implements Serializable {

    private String id;
    Serializable value;

    @SpaceId(autoGenerate = true)
    @SpaceRouting
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Serializable value) {
        this.value = value;
    }
}
