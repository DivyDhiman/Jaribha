package com.jaribha.server_communication;

import android.os.AsyncTask;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.interfaces.GetCountry;
import com.jaribha.models.CountryBean;
import com.jaribha.utility.Urls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetCountryTask extends AsyncTask<Void, Void, JSONObject> {
    GetCountry country;
    JSONObject jsonObject;
    ArrayList<CountryBean> countryList = new ArrayList<>();

    public GetCountryTask(JSONObject jsonObject, GetCountry getCountry) {
        this.country = getCountry;
        this.jsonObject = jsonObject;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        return new JSONParser().getJsonObjectFromUrl1(Urls.GET_COUNTRIES, jsonObject);
    }

    @Override
    protected void onPostExecute(final JSONObject response) {
        try {
            JSONObject jsonObject = response.getJSONObject("result");
            Boolean status = jsonObject.getBoolean("status");
            String message = jsonObject.getString("msg");
            if (status) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject countryJson = jsonArray.getJSONObject(i).getJSONObject("Country");
                    CountryBean countryBean = new Gson().fromJson(countryJson.toString(), CountryBean.class);

                    countryList.add(countryBean);
                }
                country.OnSuccess(countryList);
            } else {
                country.OnFail(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            country.OnFail("Exception");
        }
    }

    @Override
    protected void onCancelled() {
        country.OnFail("Cancelled");
    }
}