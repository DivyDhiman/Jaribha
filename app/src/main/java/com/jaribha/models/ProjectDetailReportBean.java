package com.jaribha.models;

import java.io.Serializable;


public class ProjectDetailReportBean implements Serializable {

    String filterTextHeading;
    String filterTextDesc;
    boolean selected;

    public ProjectDetailReportBean(String heading, String desc) {
        this.filterTextHeading = heading;
        this.filterTextDesc = desc;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getFilterTextDesc() {
        return filterTextDesc;
    }

    public void setFilterTextDesc(String filterTextDesc) {
        this.filterTextDesc = filterTextDesc;
    }

    public String getFilterTextHeading() {
        return filterTextHeading;
    }

    public void setFilterTextHeading(String filterTextHeading) {
        this.filterTextHeading = filterTextHeading;
    }
}
