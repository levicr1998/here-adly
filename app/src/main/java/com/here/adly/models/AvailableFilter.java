package com.here.adly.models;

import java.util.List;

public class AvailableFilter extends Filter {

    private boolean selected;

    public AvailableFilter(String name, boolean selected) {
        super(name);
        this.selected = selected;

    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
