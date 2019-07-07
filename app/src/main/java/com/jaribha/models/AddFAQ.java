package com.jaribha.models;

import java.io.Serializable;


public class AddFAQ implements Serializable {
    public String question, answer;

    public AddFAQ(String que, String ans) {
        this.question = que;
        this.answer = ans;
    }
}
