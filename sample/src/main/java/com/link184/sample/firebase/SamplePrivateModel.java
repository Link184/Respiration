package com.link184.sample.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

@IgnoreExtraProperties
public class SamplePrivateModel {
    public int age;
    public String name;
    public String surname;
    public Map<String, String> friends;

    public SamplePrivateModel() {
    }

    public SamplePrivateModel(String name, String surname, int age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Map<String, String> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, String> friends) {
        this.friends = friends;
    }
}
