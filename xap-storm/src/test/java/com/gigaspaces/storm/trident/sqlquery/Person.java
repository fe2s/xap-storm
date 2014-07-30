package com.gigaspaces.storm.trident.sqlquery;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

/**
 * @author Oleksiy_Dyagilev
 */
@SpaceClass
public class Person {
    private String name;
    private Integer age;
    private byte[] photo;

    public Person() {
    }

    public Person(String name, Integer age, byte[] photo) {
        this.name = name;
        this.age = age;
        this.photo = photo;
    }

    @SpaceId(autoGenerate = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
