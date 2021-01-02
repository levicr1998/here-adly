package com.here.adly.models;

import java.util.List;

public abstract class Filter {
    public static Integer INDEX_PRICE = 0;
    public static Integer INDEX_TYPE = 1;

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
