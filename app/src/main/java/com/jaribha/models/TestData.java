package com.jaribha.models;


public class TestData {

    String title;
    int state;

    public TestData(String title, int state) {
        this.state = state;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
