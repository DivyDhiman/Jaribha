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
import android.widget.ListView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.ProjectDetailsTabs;
import com.jaribha.adapters.ProjectsAdapter;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.EndlessScrollListener;
import com.jaribha.interfaces.HomeBadgeCount;
import com.jaribha.models.GetProjects;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StaffPicksFragment extends BaseFragment implements View.OnClickListener {

    private ListView projectListView;

    private GridView projectGridView;

    private ProjectsAdapter adapter;

    private ArrayList<GetProjects> projectList = new ArrayList<>();

    boolean tabletSize;

    private String favCount = "0", unreadCount = "0";

    private ProgressDialog progressDialog;
    //    int offset;
    private boolean loadMore = false;

    private TextView noItem;

    private int nextOffset = 0;

    private Activity mActivity;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            projectList.clear();

            if (isInternetConnected())
                loadProjects("", "", "staffpicks", 0);
            else {
                showNetworkDialog();
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_picks, container, false);
        projectList.clear();
        tabletSize = getActivity().getResources().getBoolean(R.bool.isTablet);
        progressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        noItem = (TextView) view.findViewById(R.id.noRecents);

        adapter = new ProjectsAdapter(getActivity(), projectList);

        if (tabletSize) {
            projectGridView = (GridView) view.findViewById(R.id.projectsGrid);
            projectGridView.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
            projectGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    hideKeyBoard(view);
                    Intent intent = new Intent(getActivity(), ProjectDetailsTabs.class);
                    intent.putExtra("detail_object", projectList.get(position));
                    JaribhaPrefrence.setPref(getActivity(), Constants.CREATOR_ID, projectList.get(position).user_id);
                    JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_ID, projectList.get(position).id);
                    JaribhaPrefrence.setPref(getActivity(), Constants.END_DATE, projectList.get(position).enddate);
                    JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_TITLE, projectList.get(position).title);
                    JaribhaPrefrence.setPref(getActivity(), Constants.isStaffPicks, true);
                    startActivity(intent);
                }
            });

            projectGridView.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    if (nextOffset != 0 && nextOffset != -1 && projectList.size() >= nextOffset) {
                        loadProjects("", "", "staffpicks", nextOffset);
                    }
                    return true;
                }
            });
        } else {
            projectListView = (ListView) view.findViewById(R.id.projectsList);
            projectListView.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
            projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ProjectDetailsTabs.class);
                    hideKeyBoard(view);
                    intent.putExtra("detail_object", projectList.get(position));
                    JaribhaPrefrence.setPref(getActivity(), Constants.CREATOR_ID, projectList.get(position).user_id);
                    JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_ID, projectList.get(position).id);
                    JaribhaPrefrence.setPref(getActivity(), Constants.END_DATE, projectList.get(position).enddate);
                    JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_TITLE, projectList.get(position).title);
                    JaribhaPrefrence.setPref(getActivity(), Constants.isStaffPicks, true);
                    startActivity(intent);
                }
            });

            projectListView.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    if (nextOffset != 0 && nextOffset != -1 && projectList.size() >= nextOffset) {
                        loadProjects("", "", "staffpicks", nextOffset);
                    }
                    return true;
                }
            });
        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (JaribhaPrefrence.getPref(getActivity(), Constants.isStaffPicks, false)) {
            projectList.clear();

            JaribhaPrefrence.setPref(getActivity(), Constants.isStaffPicks, false);
            if (isInternetConnected())
                loadProjects("", "", "staffpicks", 0);
            else {
                showNetworkDialog();
            }
        }
    }

    public void loadProjects(String cat, String country, String feature, int offset) {
        try {

            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();

            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            String uid = getUser() == null ? "" : getUser().id;
            String utoken = getUser() == null ? "" : getUser().user_token;

            projectJson.put("user_id", uid);
            projectJson.put("user_token", utoken);
            projectJson.put("category", cat);
            projectJson.put("country", country);
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
    public void onClick(View v) {
        switch (v.getId()) {

            case 0:

                break;

            default:
                break;

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

        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_PROJECTS, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    nextOffset = jsonObject.optInt("nextOffset");
                    // loadMore = offset != 0;
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
                            projectListView.setVisibility(View.GONE);
                        }

                        if (projectList.size() == nextOffset) {
                            loadMore = true;
                        }

                        ///if (Utils.isInternetConnected(mActivity)) {
                        if (((BaseAppCompatActivity) mActivity).getUser() != null && !countLoaded) {
                            attemptGetCounts();
                        } else {
                            if (progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                       /* } else {
                            showNetworkDialog();
                        }*/
                    } else {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();

                        if (msg.equals("User Not Found")) {
                            showSessionDialog();
                        } else if (!msg.equals("Data Not Found")) {
                            showServerErrorDialog();
                        } else if (msg.equals("Data Not Found")) {
                            noItem.setVisibility(View.VISIBLE);
                            if (isAdded())
                                noItem.setText(getString(R.string.no_projects));
                            if (!isTabletDevice()) {
                                projectListView.setVisibility(View.GONE);
                            } else {
                                projectGridView.setVisibility(View.GONE);

                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
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
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        countLoaded = false;
        loadMore = false;

    }

    private GetCounts mGetFavouritesTask = null;

    public class GetCounts extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetCounts(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_MSG_FAV_COUNT, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mGetFavouritesTask = null;
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        JSONObject data = jsonObject.optJSONObject("data");
                        HomeBadgeCount badgeCount = (HomeBadgeCount) mActivity;
                        if (data != null) {
                            favCount = data.optString("favouriteCount");
                            unreadCount = data.optString("messageCount");
                        }
                        countLoaded = true;
                        if (!TextUtils.isNullOrEmpty(favCount))
                            JaribhaPrefrence.setPref(mActivity, Constants.FAV_COUNT, Integer.parseInt(favCount));

                        badgeCount.setTabCount(favCount, unreadCount);

                    } else {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            } else {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            mGetFavouritesTask = null;
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private boolean countLoaded = false;

    private void attemptGetCounts() {
        if (Utils.isInternetConnected(mActivity)) {
            try {
                JSONObject GetFavouritesJsonObject = new JSONObject();
                GetFavouritesJsonObject.put("apikey", Urls.API_KEY);
                GetFavouritesJsonObject.put("user_id", getUser().id);
                GetFavouritesJsonObject.put("user_token", getUser().user_token);

                mGetFavouritesTask = new GetCounts(GetFavouritesJsonObject);
                mGetFavouritesTask.execute();

            } catch (Exception e) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                e.printStackTrace();
            }
        } else {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            showNetworkDialog();
        }
    }
}
