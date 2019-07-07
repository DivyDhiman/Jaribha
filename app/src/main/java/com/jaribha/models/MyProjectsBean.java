package com.jaribha.models;

import java.io.Serializable;


public class MyProjectsBean implements Serializable {

    String projectHeader;
    String projectType;
    String projectCategory;
    String projectLocation;
    int porojectImage;

    public MyProjectsBean(String projectType, String projectCategory, String projectHeader, String projectLocation, int porojectImage) {
        this.projectType = projectType;
        this.projectCategory = projectCategory;
        this.projectHeader = projectHeader;
        this.projectLocation = projectLocation;
        this.porojectImage = porojectImage;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getProjectHeader() {
        return projectHeader;
    }

    public void setProjectHeader(String projectHeader) {
        this.projectHeader = projectHeader;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    public String getProjectCategory() {
        return projectCategory;
    }

    public void setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
    }

    public int getPorojectImage() {
        return porojectImage;
    }

    public void setPorojectImage(int porojectImage) {
        this.porojectImage = porojectImage;
    }
}
