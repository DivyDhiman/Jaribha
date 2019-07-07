package com.jaribha.base;

import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    protected AppCompatActivity getActivity() {
        return this;
    }
}
