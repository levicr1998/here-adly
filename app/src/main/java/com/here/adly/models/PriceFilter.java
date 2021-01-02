package com.here.adly.models;

import java.util.List;


public class PriceFilter extends Filter {

    private List<Float> selected;

    public PriceFilter(String name, List<Float> selected) {
        super(name);
        this.selected = selected;
    }

    public List<Float> getSelected() {
        return selected;
    }

    public void setSelected(List<Float> selected) {
        this.selected = selected;
    }
}
