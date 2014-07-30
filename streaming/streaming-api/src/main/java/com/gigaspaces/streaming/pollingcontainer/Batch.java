package com.gigaspaces.streaming.pollingcontainer;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceIndex;
import com.gigaspaces.annotation.pojo.SpaceRouting;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mykola_Zalyayev
 */
@SpaceClass
public class Batch implements Serializable{

    private String id;
    private Long txId;
    private Integer partitionNumber;
    private List<BatchItem> items;

    @SpaceId(autoGenerate = true)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SpaceIndex
    public Long getTxId() {
        return txId;
    }

    public void setTxId(Long txId) {
        this.txId = txId;
    }

    @SpaceRouting
    public Integer getPartitionNumber() {
        return partitionNumber;
    }

    public void setPartitionNumber(Integer partitionNumber) {
        this.partitionNumber = partitionNumber;
    }

    public List<BatchItem> getItems() {
        return items;
    }

    public void setItems(List<BatchItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Batch{" +
                "id='" + id + '\'' +
                ", txId=" + txId +
                ", partitionNumber=" + partitionNumber +
                ", items=" + items +
                '}';
    }
}
