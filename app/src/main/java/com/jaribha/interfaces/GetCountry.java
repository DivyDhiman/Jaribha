package com.jaribha.interfaces;

import com.jaribha.models.CountryBean;

import java.util.ArrayList;

public interface GetCountry {

    void OnSuccess(ArrayList<CountryBean> list);

    void OnFail(String s);
}
