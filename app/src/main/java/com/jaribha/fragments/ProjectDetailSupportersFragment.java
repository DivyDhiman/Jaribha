package com.jaribha.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.adapters.SupporterListAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.EndlessScrollListener;
import com.jaribha.models.Supporters;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProjectDetailSupportersFragment extends BaseFragment {

    private ArrayList<Supporters> arrayList = new ArrayList<>();

    private ListView listView;

    private SupporterListAdapter adapter;

    private TextView noItems;

    private int nextOffset = 0;

    private ProgressDialog progressDialog;

    private Activity activity;

    private boolean isLoadMore = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            arrayList.clear();
            if (isInternetConnected()) {
                loadUpdates(0);
            } else {
                showNetworkDialog();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_detail_supporters, container, false);
        listView = (ListView) view.findViewById(R.id.supporterListView);
        noItems = (TextView) view.findViewById(R.id.noSupporters);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        adapter = new SupporterListAdapter(activity, arrayList);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (nextOffset != 0 && nextOffset != -1 && arrayList.size() >= nextOffset) {
                    isLoadMore = true;
                    loadUpdates(nextOffset);
                } else {
                    isLoadMore = false;
                }
                return true;
            }
        });
        return view;
    }


    public void loadUpdates(int offset) {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);
            projectJson.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));
            projectJson.put("offset", offset);
            GetProjectInfo mAuthTask = new GetProjectInfo(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class GetProjectInfo extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetProjectInfo(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // showLoading();
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
//            arrayList.clear();
    }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_PROJECT_SUPPORTERS, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            //hideLoading();
            //if (!isLoadMore) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            //}

            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    nextOffset = Integer.parseInt(jsonObject.getString("nextOffset"));
                    if (status) {
                        JSONArray data = jsonObject.optJSONObject("data").optJSONArray("Supporter");
                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                Supporters supporters = new Gson().fromJson(data.optJSONObject(i).toString(), Supporters.class);
                                arrayList.add(supporters);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        //hideLoading();
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        switch (msg) {
                            case "Supporter Data not Found":
                                if (isAdded())
                                    noItems.setText(getString(R.string.no_records));
                                noItems.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
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
                    showServerErrorDialog();
                }
            } else {

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                showServerErrorDialog();
            }
        }

        @Override
        protected void onCancelled() {
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }
}
