package com.jaribha.models;

import java.io.Serializable;

public class SectionItem implements Item, Serializable {

    public String title;

    @Override
    public boolean isSection() {
        return true;
    }
}
