package com.jaribha.interfaces;

import org.json.JSONObject;


public interface AddFavourite {

    void OnSuccess(JSONObject response);

    void OnFail();
}
