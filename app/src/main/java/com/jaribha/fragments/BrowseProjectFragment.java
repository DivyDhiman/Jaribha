package com.jaribha.fragments;

import android.annotation.SuppressLint;
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
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.BrowseProjectHomeFilter;
import com.jaribha.activity.ProjectDetailsTabs;
import com.jaribha.adapters.ProjectsAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.EndlessScrollListener;
import com.jaribha.models.GetProjects;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BrowseProjectFragment extends BaseFragment implements View.OnClickListener {

    private ListView projectListView;

    private ProjectsAdapter adapter;

    LinearLayout recentlyLaunched;

    private GridView projectGridView;

    private ArrayList<GetProjects> projectList = new ArrayList<>();

    boolean tabletSize;

    private TextView titleFilter;

    private ProgressDialog progressDialog;

    private TextView noItem;

    private Activity mActivity;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (Utils.isInternetConnected(mActivity)) {
                projectList.clear();
                loadProjects(JaribhaPrefrence.getPref(mActivity, Constants.FEATURE_ID, ""), 0);
            } else {
                showNetworkDialog();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_project, null);

        recentlyLaunched = (LinearLayout) view.findViewById(R.id.recentlyLaunched);
        recentlyLaunched.setOnClickListener(this);

        projectList.clear();

        //LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("com.load_projects"));

        adapter = new ProjectsAdapter(mActivity, projectList);

        tabletSize = mActivity.getResources().getBoolean(R.bool.isTablet);

        titleFilter = (TextView) view.findViewById(R.id.titleFilter);

        noItem = (TextView) view.findViewById(R.id.noRecents);

        progressDialog = new ProgressDialog(mActivity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        if (tabletSize) {
            projectGridView = (GridView) view.findViewById(R.id.projectsGrid);
            projectGridView.setAdapter(adapter);
            projectGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mActivity, ProjectDetailsTabs.class);
                    intent.putExtra("detail_object", projectList.get(position));
                    hideKeyBoard(view);
                    JaribhaPrefrence.setPref(mActivity, Constants.isBrowseProject, true);
                    JaribhaPrefrence.setPref(mActivity, Constants.PROJECT_ID, projectList.get(position).id);
                    JaribhaPrefrence.setPref(mActivity, Constants.CREATOR_ID, projectList.get(position).user_id);
                    JaribhaPrefrence.setPref(mActivity, Constants.END_DATE, projectList.get(position).enddate);
                    JaribhaPrefrence.setPref(mActivity, Constants.PROJECT_TITLE, projectList.get(position).title);
                    startActivity(intent);
                }
            });

            projectGridView.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    if (nextOffset != 0 && nextOffset != -1 && projectList.size() >= nextOffset) {
                        loadProjects(JaribhaPrefrence.getPref(mActivity, Constants.FEATURE_ID, ""), nextOffset);
                    }
                    return true;
                }
            });

        } else {
            projectListView = (ListView) view.findViewById(R.id.projectsList);
            projectListView.setAdapter(adapter);

            projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hideKeyBoard(view);
                    Intent intent = new Intent(mActivity, ProjectDetailsTabs.class);
                    intent.putExtra("detail_object", projectList.get(position));
                    JaribhaPrefrence.setPref(mActivity, Constants.PROJECT_ID, projectList.get(position).id);
                    JaribhaPrefrence.setPref(mActivity, Constants.isBrowseProject, true);
                    JaribhaPrefrence.setPref(mActivity, Constants.CREATOR_ID, projectList.get(position).user_id);
                    JaribhaPrefrence.setPref(mActivity, Constants.END_DATE, projectList.get(position).enddate);
                    JaribhaPrefrence.setPref(mActivity, Constants.PROJECT_TITLE, projectList.get(position).title);
                    startActivity(intent);
                }
            });

            projectListView.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    if (nextOffset != 0 && nextOffset != -1 && projectList.size() >= nextOffset) {
                        loadProjects(JaribhaPrefrence.getPref(mActivity, Constants.FEATURE_ID, ""), nextOffset);
                    }
                    return true;
                }
            });
        }

        return view;
    }

    int nextOffset = 0;

    public void loadProjects(String feature, int offset) {
        try {
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();

            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            String uid = getUser() == null ? "" : getUser().id;
            String utoken = getUser() == null ? "" : getUser().user_token;

            projectJson.put("user_id", uid);
            projectJson.put("user_token", utoken);
            projectJson.put("category", "");
            projectJson.put("country", "");
            projectJson.put("feature", feature);
            projectJson.put("offset", offset);
            projectJson.put("search", "");

            GetProjectsAPI mAuthTask = new GetProjectsAPI(projectJson);
            mAuthTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // projectList.clear();
        //titleFilter.setText(JaribhaPrefrence.getPref(mActivity, Constants.FEATURE, getString(R.string.recently_launched)));
        if (JaribhaPrefrence.getPref(mActivity, Constants.isBrowseProject, false)) {
            projectList.clear();
            JaribhaPrefrence.setPref(mActivity, Constants.isBrowseProject, false);
            if (Utils.isInternetConnected(mActivity))
                loadProjects(JaribhaPrefrence.getPref(mActivity, Constants.FEATURE_ID, ""), 0);
            else {
                showNetworkDialog();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recentlyLaunched:
                JaribhaPrefrence.setPref(mActivity, Constants.isBrowseProject, true);
                Intent intent = new Intent(mActivity, BrowseProjectHomeFilter.class);
                startActivityForResult(intent, 2001);
                break;
            default:
                break;

        }
    }

    public class GetProjectsAPI extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        public GetProjectsAPI(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_PROJECTS, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            titleFilter.setText(JaribhaPrefrence.getPref(mActivity, Constants.FEATURE, ""));
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    nextOffset = jsonObject.optInt("nextOffset");
                    if (status) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            GetProjects data = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), GetProjects.class);
                            data.status = Utils.setProjectStatus(data);
                            data.days_left = Utils.getDaysLeft(data);
                            projectList.add(data);
                        }
                        if (projectList.size() == 0) {
                            noItem.setVisibility(View.VISIBLE);
                            if (!tabletSize) {
                                projectListView.setVisibility(View.GONE);
                            } else {
                                projectGridView.setVisibility(View.GONE);

                            }
                            //  titleFilter.setVisibility(View.GONE);
                        } else {
                            noItem.setVisibility(View.GONE);
                            if (!tabletSize) {
                                projectListView.setVisibility(View.VISIBLE);
                            } else {
                                projectGridView.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {

                        if (msg.equals("Data Not Found")) {
                            noItem.setVisibility(View.VISIBLE);
                            if (isAdded())
                                noItem.setText(getString(R.string.no_projects));
                            //noItems.setVisibility(View.VISIBLE);
                            if (!tabletSize) {
                                projectListView.setVisibility(View.GONE);
                            } else {
                                projectGridView.setVisibility(View.GONE);

                            }
                            // titleFilter.setVisibility(View.GONE);
                        } else if (msg.equals("User Not Found")) {
                            showSessionDialog();
                        } else if (!msg.equals("Data Not Found")) {
                            showServerErrorDialog();
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
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

//    // Our handler for received Intents. This will be called whenever an Intent
//// with an action named "custom-event-name" is broadcasted.
//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get extra data included in the Intent
////            if (intent.getAction().equals("com.load_projects")) {
////
////                if (Utils.isInternetConnected(mActivity)()) {
////                    // projectList.clear();
////
////                } else {
////                    showNetworkDialog();
////                }
////            }
//        }
//    };
//
//    @Override
//    public void onDestroy() {
//        // Unregister since the activity is about to be closed.
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
//        super.onDestroy();
//    }
}
