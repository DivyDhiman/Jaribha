package com.jaribha.models;

import java.io.Serializable;


public class CountryBean implements Serializable {
    public String id;
    public String name_eng;
    public String name_ara;
    public String status;
    public String code;
    public String postal;
    public boolean selected;


    public CountryBean(String code) {
        this.code = code;
    }
}
