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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.ProjectDetailsTabs;
import com.jaribha.adapters.MyFavoritesAdapter;
import com.jaribha.base.BaseAppCompatActivity;
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

public class MyFavoritesFragment extends BaseFragment {

    private ListView projectListView;

    private GridView projectGrid;

    private ArrayList<GetProjects> favouritesList = new ArrayList<>();

    private MyFavoritesAdapter adapter;

    TextView noItems;

    boolean loadMore = false;

    private ProgressDialog progressDialog;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        favouritesList.clear();
//        attemptGetFavourites(0);
        activity = (Activity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
//        favouritesList.clear();
        attemptGetFavourites(0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_favorites, container, false);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);


        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        boolean tabletSize = activity.getResources().getBoolean(R.bool.isTablet);

        noItems = (TextView) view.findViewById(R.id.noFavs);

        adapter = new MyFavoritesAdapter(activity, favouritesList);

        if (tabletSize) {

            projectGrid = (GridView) view.findViewById(R.id.projectsGrid);
            projectGrid.setAdapter(adapter);

            adapter.notifyDataSetChanged();

            projectGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(activity, ProjectDetailsTabs.class);
                    intent.putExtra("detail_object", favouritesList.get(position));
                    JaribhaPrefrence.setPref(activity, Constants.PROJECT_TITLE, favouritesList.get(position).title);
                    JaribhaPrefrence.setPref(activity, Constants.PROJECT_ID, favouritesList.get(position).id);
                    startActivity(intent);
                }
            });

            projectGrid.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    if (nextOffset != 0 && nextOffset != -1 && favouritesList.size() >= nextOffset) {
                        loadMore = true;
                        attemptGetFavourites(nextOffset);
                    }
                    return true;
                }
            });

        } else {

            projectListView = (ListView) view.findViewById(R.id.projectsList);
            projectListView.setAdapter(adapter);

            adapter.notifyDataSetChanged();

            projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(activity, ProjectDetailsTabs.class);
                    intent.putExtra("detail_object", favouritesList.get(position));
                    JaribhaPrefrence.setPref(activity, Constants.PROJECT_TITLE, favouritesList.get(position).title);
                    JaribhaPrefrence.setPref(activity, Constants.PROJECT_ID, favouritesList.get(position).id);
                    startActivity(intent);
                }
            });
            projectListView.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    if (nextOffset != 0 && nextOffset != -1 && favouritesList.size() >= nextOffset) {
                        loadMore = true;
                        attemptGetFavourites(nextOffset);
                    }
                    return true;
                }
            });

        }

        //Call GetFavourite API
//        attemptGetFavourites(0);

        return view;
    }

//    public void setNoRecords(int size) {
//        if (size == 0) {
//            noItems.setVisibility(View.VISIBLE);
//            if (isAdded())
//                noItems.setText(getString(R.string.no_records));
//            if (!isTabletDevice()) {
//                projectListView.setVisibility(View.GONE);
//            } else {
//                projectGrid.setVisibility(View.GONE);
//
//            }
//        }
//    }

    int nextOffset = 0;

    private GetFavouritesTask mGetFavouritesTask = null;

    public class GetFavouritesTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetFavouritesTask(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showLoading();
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
            if (!loadMore)
                favouritesList.clear();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_FAVOURITES, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mGetFavouritesTask = null;
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    nextOffset = Integer.parseInt(jsonObject.getString("nextOffset"));
                    if (status) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            GetProjects data = new Gson().fromJson(jsonArray.getJSONObject(i).getJSONObject("Project").toString(), GetProjects.class);
                            data.status = Utils.setProjectStatus(data);
                            data.days_left = Utils.getDaysLeft(data);
                            data.is_favourite = true;
                            favouritesList.add(data);
                        }

                        adapter.notifyDataSetChanged();

                    } else {
                        switch (msg) {
                            case "Data Not Found":
                                noItems.setVisibility(View.VISIBLE);
                                if (isAdded())
                                    noItems.setText(getString(R.string.no_records));

                                if (!isTabletDevice()) {
                                    projectListView.setVisibility(View.GONE);
                                } else {
                                    projectGrid.setVisibility(View.GONE);
                                }
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
                    Crashlytics.logException(e);
                    e.printStackTrace();
                    ((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                }
            } else {
                ((BaseAppCompatActivity) getContext()).showServerDialogDialog();
            }
        }

        @Override
        protected void onCancelled() {
            mGetFavouritesTask = null;
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void attemptGetFavourites(int offset) {
        if (isInternetConnected()) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("apikey", Urls.API_KEY);
                jsonObject.put("user_id", getUser().id);
                jsonObject.put("user_token", getUser().user_token);
                jsonObject.put("offset", offset);
                mGetFavouritesTask = new GetFavouritesTask(jsonObject);
                mGetFavouritesTask.execute();
            } catch (Exception e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        } else {
            showNetworkDialog();
        }
    }
}
