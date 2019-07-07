package com.jaribha.models;

import java.io.Serializable;


public class FeaturedFilterBean implements Serializable {

    String filterText;
    boolean selected;

    public FeaturedFilterBean(String filterText) {
        this.filterText = filterText;
    }

    public String getFilterText() {
        return filterText;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
