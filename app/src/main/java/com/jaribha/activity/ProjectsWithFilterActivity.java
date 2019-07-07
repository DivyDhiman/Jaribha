package com.jaribha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.models.GetProjects;
import com.jaribha.utility.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProjectsWithFilterActivity extends BaseAppCompatActivity implements View.OnClickListener {

    BezelImageView img_user_image, profileImage;

    ImageView iv_close, searchBtn, filterBtn;

    TextView tv_title, projectCount;

    ListView filteredProjectList;

    ArrayList<GetProjects> projectList = new ArrayList<>();

    private FilteredProjectListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects_with_filter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setOnClickListener(this);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.VISIBLE);
        displayUserImage(img_user_image);
        img_user_image.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        projectCount = (TextView) findViewById(R.id.projectCount);
        tv_title.setVisibility(View.GONE);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Lato-Bold_0.ttf");
        projectCount.setTypeface(tf);

        searchBtn = (ImageView) findViewById(R.id.iv_share);
        searchBtn.setVisibility(View.VISIBLE);
        searchBtn.setImageResource(R.drawable.search_icon);
        searchBtn.setOnClickListener(this);

        filterBtn = (ImageView) findViewById(R.id.iv_favourite);
        filterBtn.setVisibility(View.VISIBLE);
        filterBtn.setImageResource(R.drawable.filter_icon);
        filterBtn.setOnClickListener(this);

        adapter = new FilteredProjectListAdapter(getActivity(), projectList);
        filteredProjectList = (ListView) findViewById(R.id.filteredProjectList);
        filteredProjectList.setAdapter(adapter);

        profileImage = (BezelImageView) findViewById(R.id.profileImage);
        profileImage.setColorFilter(Color.argb(255, 255, 255, 255));
        if (isInternetConnected())
            loadProjects("", "", "");
        else {
            showNetworkDialog();
        }
    }

    public void loadProjects(String cat, String country, String feature) {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);
            projectJson.put("user_id", getUser().id);
            projectJson.put("user_token", getUser().user_token);
            projectJson.put("category", cat);
            projectJson.put("country", country);
            projectJson.put("feature", feature);
            projectJson.put("offset", "");

            GetProjectsAPI mAuthTask = new GetProjectsAPI(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.iv_favourite://Filter button
                finish();
                break;
            case R.id.img_user_image:
                startActivity(new Intent(getActivity(), MyPortfolioActivity.class));
                break;
            default:
                break;

        }
    }

    public class FilteredProjectListAdapter extends BaseAdapter {

        Context context;
        List<GetProjects> rowItems;

        public FilteredProjectListAdapter(Context context, List<GetProjects> items) {
            this.context = context;
            this.rowItems = items;
        }

        private class ViewHolder {
            ImageView projectImage;
            TextView projectHeader;
            TextView projectType;
            TextView projectCategory;
            TextView projectLocation;
            TextView projectPeriod;
            TextView goalAmount;
            TextView supportPercent;
            TextView status;
            ProgressBar progressBar;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.project_list_item_card_view, null);
                holder = new ViewHolder();
                holder.projectHeader = (TextView) convertView.findViewById(R.id.projectHeader);
                holder.projectType = (TextView) convertView.findViewById(R.id.projectType);
                holder.projectCategory = (TextView) convertView.findViewById(R.id.projectCategory);
                holder.projectLocation = (TextView) convertView.findViewById(R.id.projectLocation);
                holder.projectImage = (ImageView) convertView.findViewById(R.id.projectImage);
                holder.projectPeriod = (TextView) convertView.findViewById(R.id.projectPeriod);
                holder.goalAmount = (TextView) convertView.findViewById(R.id.goalAmount);
                holder.supportPercent = (TextView) convertView.findViewById(R.id.supportPercent);
                holder.status = (TextView) convertView.findViewById(R.id.projectStatus);
                holder.progressBar = (ProgressBar) convertView.findViewById(R.id.projectProgress);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            GetProjects rowItem = (GetProjects) getItem(position);

            String proHeader = rowItem.title;
            String proType = rowItem.project_by;
            String proCategory = rowItem.category_name;
            String proLocation = rowItem.city_name;
            String proImage = rowItem.image_url;

            holder.projectHeader.setText(proHeader);
            holder.projectType.setText(proType);
            holder.projectCategory.setText(proCategory);
            holder.projectLocation.setText(proLocation);
            holder.projectPeriod.setText(rowItem.period);
            holder.goalAmount.setText("&" + rowItem.goal);
            switch (rowItem.status) {
                case "publish":
                    holder.status.setText(getString(R.string.successfully_published));
                    holder.status.setBackgroundResource(R.drawable.green_strip_transparent);
                    break;
                case "complete":
                    holder.status.setBackgroundResource(R.drawable.green_strip_transparent);
                    holder.status.setText(getString(R.string.campaign_ended));
                    break;
                default:
                    holder.status.setText(getString(R.string.failed));
                    holder.status.setBackgroundResource(R.drawable.red_strip);
                    break;
            }
            holder.progressBar.setProgress(getProgressPercent(Double.parseDouble(rowItem.total_support_amount), Double.parseDouble(rowItem.goal)));
            holder.supportPercent.setText(getSupportPercentage(Double.parseDouble(rowItem.total_support_amount), Double.parseDouble(rowItem.goal)) + "%");
            displayImage(context, holder.projectImage, proImage, ContextCompat.getDrawable(ProjectsWithFilterActivity.this, R.drawable.server_error_placeholder));
            return convertView;
        }

        @Override
        public int getCount() {
            return rowItems.size();
        }

        @Override
        public Object getItem(int position) {
            return rowItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return rowItems.indexOf(getItem(position));
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
            showLoading();
            projectList.clear();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_PROJECTS, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {

            hideLoading();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            GetProjects data = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), GetProjects.class);
                            projectList.add(data);
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        switch (msg) {
                            case "Data Not Found":
                                showDataNotFoundDialog();
                                break;
                            case "User Not Found":
                                showSessionDialog();
                                break;
                            default:
                                showServerDialogDialog();
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showServerDialogDialog();
                }
            } else {
                showServerDialogDialog();
            }
        }

        @Override
        protected void onCancelled() {
            hideLoading();
        }
    }
}
