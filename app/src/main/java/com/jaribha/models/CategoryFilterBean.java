package com.jaribha.models;

import java.io.Serializable;


public class CategoryFilterBean implements Serializable {
    //
//    int imageId;
//    int imageSelId;
    String id;
    String filter_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    boolean selected;

    public CategoryFilterBean() {
    }

//    public int getImageSelId() {
//        return imageSelId;
//    }
//
//    public void setImageSelId(int imageSelId) {
//        this.imageSelId = imageSelId;
//    }

    public CategoryFilterBean(String filter_name) {
//        this.imageId = imageId;
        this.filter_name = filter_name;
//        this.imageSelId = imgSelId;
    }

//    public int getImageId() {
//        return imageId;
//    }
//
//    public void setImageId(int imageId) {
//        this.imageId = imageId;
//    }

    public String getFilter_name() {
        return filter_name;
    }

    public void setFilter_name(String filter_name) {
        this.filter_name = filter_name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
