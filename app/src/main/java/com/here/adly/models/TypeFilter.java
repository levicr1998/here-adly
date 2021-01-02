package com.here.adly.models;

import java.util.List;

public class TypeFilter extends Filter {

    private List<String> selected;

    public TypeFilter(String name, List<String> selected) {
        super(name);
        this.selected = selected;

    }

    public List<String> getSelected() {
        return selected;
    }

    public void setSelected(List<String> selected) {
        this.selected = selected;
    }
}
