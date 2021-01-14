package com.here.adly.models;


public abstract class Filter {
    public static Integer INDEX_PRICE = 0;
    public static Integer INDEX_TYPE = 1;
    public static Integer INDEX_LOCATION = 2;
    public static Integer INDEX_AVAILABLE = 3;

    private String name;

    public Filter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
