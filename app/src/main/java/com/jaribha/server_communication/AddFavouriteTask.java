package com.jaribha.server_communication;

import android.os.AsyncTask;

import com.djhs16.net.JSONParser;
import com.jaribha.interfaces.AddFavourite;
import com.jaribha.utility.Urls;

import org.json.JSONObject;

public class AddFavouriteTask extends AsyncTask<Void, Void, JSONObject> {

    AddFavourite addFavourite;
    JSONObject jsonObject;

    public AddFavouriteTask(JSONObject jsonObject, AddFavourite addFavourite1) {
        this.addFavourite = addFavourite1;
        this.jsonObject = jsonObject;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        return new JSONParser().getJsonObjectFromUrl1(Urls.ADD_FAVOURITE, jsonObject);
    }

    @Override
    protected void onPostExecute(final JSONObject response) {
        addFavourite.OnSuccess(response);
    }

    @Override
    protected void onCancelled() {
        addFavourite.OnFail();
    }
}