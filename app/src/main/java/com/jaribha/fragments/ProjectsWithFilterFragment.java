package com.jaribha.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.ProjectDetailsTabs;
import com.jaribha.adapters.ProjectsAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.customviews.EndlessScrollListener;
import com.jaribha.models.GetProjects;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.ColorTransformation;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectsWithFilterFragment extends BaseFragment {

    private ListView projectListView;

    private ProjectsAdapter adapter;

    private GridView projectGridView;

    private ArrayList<GetProjects> projectList = new ArrayList<>();

    private TextView filterTv, projectCount, noItems;

    private int nextOffset = 0;

    private ProgressDialog progressDialog;

    private Activity mActivity;

    private String uid, utoken;

    boolean isBroadCast = false;

    boolean isTablet;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        projectList.clear();
        nextOffset = 0;
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.project_with_filter_fragment, null);
        progressDialog = new ProgressDialog(mActivity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        isTablet = getResources().getBoolean(R.bool.isTablet);

        ColorFilter filter = new LightingColorFilter(Color.WHITE, Color.WHITE);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("com.load_projects"));

        adapter = new ProjectsAdapter(mActivity, projectList);

        projectCount = (TextView) view.findViewById(R.id.projectCount);

        filterTv = (TextView) view.findViewById(R.id.filterTv);

        noItems = (TextView) view.findViewById(R.id.noProjects);

        final boolean tabletSize = mActivity.getResources().getBoolean(R.bool.isTablet);

        uid = getUser() == null ? "" : getUser().id;
        utoken = getUser() == null ? "" : getUser().user_token;

        if (tabletSize) {
            projectGridView = (GridView) view.findViewById(R.id.filteredProjectListGrid);
            projectGridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            projectGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mActivity, ProjectDetailsTabs.class);
                    intent.putExtra("detail_object", projectList.get(position));
                    JaribhaPrefrence.setPref(mActivity, Constants.isFilterProject, true);
                    JaribhaPrefrence.setPref(mActivity, Constants.CREATOR_ID, projectList.get(position).user_id);
                    JaribhaPrefrence.setPref(mActivity, Constants.PROJECT_TITLE, projectList.get(position).title);
                    JaribhaPrefrence.setPref(mActivity, Constants.END_DATE, projectList.get(position).enddate);
                    JaribhaPrefrence.setPref(mActivity, Constants.PROJECT_ID, projectList.get(position).id);
                    startActivity(intent);
                }
            });
            projectGridView.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    if (nextOffset != 0 && nextOffset != -1 && projectList.size() >= nextOffset) {
                        loadProjects(JaribhaPrefrence.getPref(getActivity(), Constants.CATEGORY_ID, ""),
                                JaribhaPrefrence.getPref(getActivity(), Constants.COUNTRY_ID, ""),
                                JaribhaPrefrence.getPref(getActivity(), Constants.FEATURE_ID_SEARCH, ""), nextOffset, uid, utoken,
                                JaribhaPrefrence.getPref(getActivity(), Constants.SEARCH, ""));

                    }
                    return true;
                }
            });
        } else {
            projectListView = (ListView) view.findViewById(R.id.filteredProjectList);

            projectListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mActivity, ProjectDetailsTabs.class);
                    intent.putExtra("detail_object", projectList.get(position));
                    JaribhaPrefrence.setPref(mActivity, Constants.isFilterProject, true);
                    JaribhaPrefrence.setPref(mActivity, Constants.CREATOR_ID, projectList.get(position).user_id);
                    JaribhaPrefrence.setPref(mActivity, Constants.PROJECT_TITLE, projectList.get(position).title);
                    JaribhaPrefrence.setPref(mActivity, Constants.PROJECT_ID, projectList.get(position).id);
                    JaribhaPrefrence.setPref(mActivity, Constants.END_DATE, projectList.get(position).enddate);
                    startActivity(intent);
                }
            });

            projectListView.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {

                    if (nextOffset != 0 && nextOffset != -1 && projectList.size() >= nextOffset) {

                        loadProjects(JaribhaPrefrence.getPref(getActivity(), Constants.CATEGORY_ID, ""),
                                JaribhaPrefrence.getPref(getActivity(), Constants.COUNTRY_ID, ""),
                                JaribhaPrefrence.getPref(getActivity(), Constants.FEATURE_ID_SEARCH, ""), nextOffset, uid, utoken,
                                JaribhaPrefrence.getPref(getActivity(), Constants.SEARCH, ""));

                    }

                    return true;
                }
            });
        }

        return view;
    }

    public void loadProjects(String cat, String country, String feature, int offset, String userId, String userToken, String searchStr) {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            projectJson.put("user_id", userId);
            projectJson.put("user_token", userToken);
            projectJson.put("category", cat);
            projectJson.put("country", country);
            projectJson.put("feature", feature);
            projectJson.put("offset", offset);
            projectJson.put("search", searchStr);

            GetProjectsAPI mAuthTask = new GetProjectsAPI(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInternetConnected()) {
            if (!isBroadCast && !JaribhaPrefrence.getPref(mActivity, Constants.FILTER_SEARCH, false)) {
                nextOffset = 0;
                projectList.clear();

                if (adapter != null)
                    adapter.notifyDataSetChanged();

                loadProjects(JaribhaPrefrence.getPref(mActivity, Constants.CATEGORY_ID, ""),
                        JaribhaPrefrence.getPref(mActivity, Constants.COUNTRY_ID, ""),
                        JaribhaPrefrence.getPref(mActivity, Constants.FEATURE_ID_SEARCH, ""), 0, uid, utoken,
                        JaribhaPrefrence.getPref(mActivity, Constants.SEARCH, ""));
            }
        } else {
            showNetworkDialog();
        }

    }

    public class GetProjectsAPI extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetProjectsAPI(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
            }
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_PROJECTS, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            projectCount.setVisibility(View.VISIBLE);
            noItems.setVisibility(View.GONE);

            if (!isTablet)
                projectListView.setVisibility(View.VISIBLE);
            else
                projectGridView.setVisibility(View.VISIBLE);

            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    String total_count = jsonObject.optString("total_no_of_projects");
                    JaribhaPrefrence.setPref(mActivity, Constants.FILTER_COUNT, total_count);
                    if (isAdded())
                        projectCount.setText(total_count + " " + getString(R.string.projects));

                    nextOffset = jsonObject.optInt("nextOffset");
                    if (status) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            GetProjects data = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), GetProjects.class);
                            data.status = Utils.setProjectStatus(data);
                            data.days_left = Utils.getDaysLeft(data);
                            projectList.add(data);
                        }

                    } else {
                        switch (msg) {
                            case "Data Not Found":
                                //JaribhaPrefrence.getPref(mActivity, Constants.FILTER_COUNT, projectList.size() + "");
                                projectCount.setVisibility(View.GONE);
                                //showDataNotFoundDialog();
                                noItems.setVisibility(View.VISIBLE);
                                if (isAdded())
                                    noItems.setText(getString(R.string.no_projects));
                                if (!isTabletDevice())
                                    projectListView.setVisibility(View.GONE);
                                else
                                    projectGridView.setVisibility(View.GONE);

                                break;
                            case "User Not Found":
                                showSessionDialog();
                                break;
                            default:
                                showServerErrorDialog();
                                break;
                        }
                    }
                    if (isAdded()) {
                        if (JaribhaPrefrence.getPref(mActivity, Constants.FEATURE_NAME, getString(R.string.all_features)).equals(getString(R.string.all_features)) && JaribhaPrefrence.getPref(mActivity, Constants.CATEGORY_NAME, getString(R.string.all_cat)).equals(getString(R.string.all_cat)) && JaribhaPrefrence.getPref(mActivity, Constants.COUNTRY_NAME, getString(R.string.all_country)).equals(getString(R.string.all_country))) {
                            filterTv.setText(getString(R.string.all_projects));
                        } else {
                            filterTv.setText(JaribhaPrefrence.getPref(mActivity, Constants.FEATURE_NAME, "") + " " + getString(R.string.for_str) + " " + JaribhaPrefrence.getPref(mActivity, Constants.CATEGORY_NAME, "")
                                    + getString(R.string.in) + JaribhaPrefrence.getPref(mActivity, Constants.COUNTRY_NAME, ""));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    showServerErrorDialog();
                }
            } else {
                showServerErrorDialog();
            }
        }

        @Override
        protected void onCancelled() {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.getAction().equals("com.load_projects")) {
                isBroadCast = true;
                if (isInternetConnected()) {

                    projectList.clear();

                    if (adapter != null)
                        adapter.notifyDataSetChanged();

                    String uid = getUser() == null ? "" : getUser().id;
                    String utoken = getUser() == null ? "" : getUser().user_token;

                    nextOffset = 0;

                    loadProjects(JaribhaPrefrence.getPref(context, Constants.CATEGORY_ID, ""),
                            JaribhaPrefrence.getPref(context, Constants.COUNTRY_ID, ""),
                            JaribhaPrefrence.getPref(context, Constants.FEATURE_ID_SEARCH, ""), 0, uid, utoken,
                            JaribhaPrefrence.getPref(context, Constants.SEARCH, ""));
                } else {
                    showNetworkDialog();
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
