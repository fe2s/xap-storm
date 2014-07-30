package com.gigaspaces.storm.spout;

import backtype.storm.tuple.Fields;

import java.io.Serializable;
import java.util.List;

/**
 * @author Oleksiy_Dyagilev
 */
public interface TupleConverter<T> extends Serializable {

    Fields tupleFields();

    List<Object> spaceObjectToTuple(T spaceObject);

}
