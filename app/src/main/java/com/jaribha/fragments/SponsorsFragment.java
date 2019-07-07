package com.jaribha.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.SponserDetailActivity;
import com.jaribha.adapters.SponsorsFragmentAdapter;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.EndlessScrollListener;
import com.jaribha.models.Sponsors;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SponsorsFragment extends BaseFragment {

    private ListView sponsorListView;

    private SponsorsFragmentAdapter adapter;

    private ArrayList<Sponsors> sponsorsList = new ArrayList<>();

    TextView noRecord;

    private ProgressDialog progressDialog;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            sponsorsList.clear();
            //Call Sponsor API for get sponsor Data.
            attemptSponsorsData(0);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sponsers_new, null);

        sponsorListView = (ListView) view.findViewById(R.id.sponsorListView);
        noRecord = (TextView) view.findViewById(R.id.noSponsor);
        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        adapter = new SponsorsFragmentAdapter(activity, sponsorsList);
        sponsorListView.setAdapter(adapter);

        sponsorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent sponsorsDetails = new Intent(activity, SponserDetailActivity.class);
                sponsorsDetails.putExtra("sponsors_object", sponsorsList.get(position));
                JaribhaPrefrence.setPref(activity, Constants.SPONSOR_ID, sponsorsList.get(position).sponsor_id);
                startActivity(sponsorsDetails);
            }
        });
        sponsorListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (nextOffset != 0 && nextOffset != -1 && sponsorsList.size() >= nextOffset) {
                    attemptSponsorsData(nextOffset);
                }
                return true;
            }
        });
        return view;
    }

    int nextOffset = 0;

    private SponsorsTask mSponsorsTask = null;

    /**
     * Represents an asynchronous SponsorsTask used to show Sponsor Delail like picture, name and bio
     */

    public class SponsorsTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        SponsorsTask(JSONObject params) {
            this.nameValuePairs = params;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  showLoading();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_SPONSORS, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mSponsorsTask = null;
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    nextOffset = Integer.parseInt(jsonObject.getString("nextOffset"));
                    if (status) {
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("Sponsor");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (!jsonArray.getJSONObject(i).optString("sponsor_id").equals("0")) {
                                Sponsors sponsors = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), Sponsors.class);
                                sponsorsList.add(sponsors);
                            }
                        }

                        adapter.notifyDataSetChanged();

                    } else {
                        switch (msg) {
                            case "Data Not Found":
                                //  showDataNotFoundDialog();
                                noRecord.setVisibility(View.VISIBLE);
                                if(isAdded())
                                noRecord.setText(getString(R.string.no_records));
                                sponsorListView.setVisibility(View.GONE);
                                break;
                            case "User Not Found":
                                showSessionDialog();
                                break;
                            default:
                                showServerErrorDialog();
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                }
            } else {
                ((BaseAppCompatActivity) getContext()).showServerDialogDialog();
            }
        }

        @Override
        protected void onCancelled() {
            mSponsorsTask = null;
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

        }
    }

    private void attemptSponsorsData(int offset) {
        if (isInternetConnected()) {
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();

            try {
                JSONObject SponsorsJsonObject = new JSONObject();
                SponsorsJsonObject.put("apikey", Urls.API_KEY);
                SponsorsJsonObject.put("offset", offset);

                mSponsorsTask = new SponsorsTask(SponsorsJsonObject);
                mSponsorsTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showNetworkDialog();
        }
    }
}
