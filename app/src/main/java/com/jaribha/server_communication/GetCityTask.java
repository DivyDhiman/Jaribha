package com.jaribha.server_communication;

import android.os.AsyncTask;

import com.djhs16.net.JSONParser;
import com.jaribha.interfaces.GetCity;
import com.jaribha.utility.Urls;

import org.json.JSONObject;

public class GetCityTask extends AsyncTask<Void, Void, JSONObject> {
    GetCity city;
    JSONObject jsonObject;

    public GetCityTask(JSONObject jsonObject, GetCity getCity) {
        this.city = getCity;
        this.jsonObject = jsonObject;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        return new JSONParser().getJsonObjectFromUrl1(Urls.GET_CITIES_BY_COUNTRIES, jsonObject);
    }

    @Override
    protected void onPostExecute(final JSONObject response) {
        city.OnSuccess(response);
    }

    @Override
    protected void onCancelled() {
        city.OnFail();
    }
}