package com.here.adly.models;


public class User {

    public String id;
    public String fullName;
    public String email;

    public User(){

    }

    public User(String id,String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }
}
