package com.jaribha.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.PagerSlidingTabStrip;
import com.jaribha.models.ProjectHistoryBean;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyHistoryFragment extends BaseFragment {

    ArrayList<String> titles = new ArrayList<>();

    HistoryPagerAdapter adapter;

    PagerSlidingTabStrip tabStrip;

    ViewPager viewPager;

    private ProgressDialog progressDialog;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_history, container, false);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        adapter = new HistoryPagerAdapter(getChildFragmentManager(), titles);

        viewPager = (ViewPager) view.findViewById(R.id.history_pager);
        viewPager.setAdapter(adapter);

        tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.history_sliding_tabs);
        tabStrip.setTypeface(Utils.getBoldFont(getActivity()), Typeface.BOLD);


        if (!isTabletDevice())
            tabStrip.setTextSize(14);
        else {
            tabStrip.setTextSize(16);
        }

        if (isInternetConnected()) {
            getProjectHistory();
        } else {
            showNetworkDialog();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public class HistoryPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> TITLES;

        public HistoryPagerAdapter(FragmentManager fm, ArrayList<String> TITLE) {
            super(fm);
            this.TITLES = TITLE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES.get(position);
        }

        @Override
        public int getCount() {
            return TITLES.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = HistorySponsorsFragment.newInstance(sponsorsList);
            }
            if (position == 1) {
                fragment = HistorySupportersFragment.newInstance(supportersList);
            }
            return fragment;

        }

    }

    private void getProjectHistory() {
        //showLoading();
        titles.clear();

        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("user_id", getUser().id);
            jsonObject.put("user_token", getUser().user_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isInternetConnected()) {
            new GetProjectHistoryTask(jsonObject).execute();
        } else {
            showNetworkDialog();
        }

    }

    ArrayList<ProjectHistoryBean> supportersList = new ArrayList<>();
    ArrayList<ProjectHistoryBean> sponsorsList = new ArrayList<>();

    public class GetProjectHistoryTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject jsonObject;

        GetProjectHistoryTask(JSONObject params) {
            this.jsonObject = params;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_MY_HISTORY, jsonObject);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            //mAuthTask = null;
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String message = jsonObject.getString("msg");
                    if (status) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject projectHistoryJson = jsonArray.getJSONObject(i);
                            ProjectHistoryBean projectHistoryBean = new Gson().fromJson(projectHistoryJson.toString(), ProjectHistoryBean.class);
                            if ((projectHistoryBean.supporter_type).equalsIgnoreCase("supporter")) {
                                supportersList.add(projectHistoryBean);
                            } else {
                                sponsorsList.add(projectHistoryBean);
                            }
                        }
                    } else {
                        if (message.equalsIgnoreCase("User Not Found")) {
                            showSessionDialog();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                showServerErrorDialog();
            }
            if (TextUtils.isNullOrEmpty(sponsorsList)) {
                titles.add(activity.getResources().getString(R.string.sponsors));
            } else {
                titles.add(activity.getResources().getString(R.string.sponsors) + "(" + sponsorsList.size() + ")");
            }

            if (TextUtils.isNullOrEmpty(supportersList)) {
                titles.add(activity.getResources().getString(R.string.supporters));
            } else {
                titles.add(activity.getResources().getString(R.string.supporters) + "(" + supportersList.size() + ")");
            }

            tabStrip.setViewPager(viewPager);
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }
}
