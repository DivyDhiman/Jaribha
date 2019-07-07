package com.jaribha.models;

import java.io.Serializable;


public class CountryFilterBean implements Serializable {

    int countryImageId;
    String id;
    String countryName;
    boolean selected;
    String countryCode;
    String countryCharCode;

    public CountryFilterBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryCharCode() {
        return countryCharCode;
    }

    public void setCountryCharCode(String countryCharCode) {
        this.countryCharCode = countryCharCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getCountryImageId() {
        return countryImageId;
    }

    public void setCountryImageId(int countryImageId) {
        this.countryImageId = countryImageId;
    }

    public CountryFilterBean(int imageId, String filter_name) {
        this.countryImageId = imageId;
        this.countryName = filter_name;
    }

    public CountryFilterBean(String code, String codeChar, String countryName) {
        this.countryCode = code;
        this.countryCharCode = codeChar;
        this.countryName = countryName;
    }

    public int getImageId() {
        return countryImageId;
    }

    public void setImageId(int imageId) {
        this.countryImageId = imageId;
    }

    public String getFilter_name() {
        return countryName;
    }

    public void setFilter_name(String filter_name) {
        this.countryName = filter_name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
