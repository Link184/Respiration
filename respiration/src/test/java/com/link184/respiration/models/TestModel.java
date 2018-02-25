package com.link184.respiration.models;

/**
 * Created by Ryzen on 2/25/2018.
 */

public class TestModel {
    String name;
    int age;
    String alias;

    public TestModel() {
    }

    public TestModel(String name, int age, String alias) {
        this.name = name;
        this.age = age;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
