package com.jaribha.interfaces;

import org.json.JSONObject;


public interface GetCity {

    void OnSuccess(JSONObject response);

    void OnFail();
}
