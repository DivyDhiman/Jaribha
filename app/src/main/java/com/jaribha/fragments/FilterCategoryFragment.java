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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.base.BaseFragment;
import com.jaribha.models.Category;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FilterCategoryFragment extends BaseFragment implements View.OnClickListener {

    GridView grid_filter_category;

    private ArrayList<Category> list = new ArrayList<>();

    private FilterCategoryAdapter adapter;

    TextView tv_all_category, tv_reset;

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
            if (isInternetConnected())
                getCategory();
            else {
                showNetworkDialog();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_filter, container, false);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        tv_all_category = (TextView) view.findViewById(R.id.tv_all_category);
        tv_all_category.setOnClickListener(this);
        tv_all_category.setText(activity.getResources().getString(R.string.all_categories));

        tv_reset = (TextView) view.findViewById(R.id.tv_reset);
        tv_reset.setOnClickListener(this);
        tv_reset.setText(activity.getResources().getString(R.string.reset));

        grid_filter_category = (GridView) view.findViewById(R.id.grid_filter_category);
        adapter = new FilterCategoryAdapter(activity, list);

        grid_filter_category.setAdapter(adapter);
        grid_filter_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category bean = list.get(position);
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).selected = false;
                }
                if (bean.selected) {
                    bean.selected = false;
                } else {
                    if (isArabic())
                        JaribhaPrefrence.setPref(activity, Constants.CATEGORY_NAME, bean.name_ara);
                    else
                        JaribhaPrefrence.setPref(activity, Constants.CATEGORY_NAME, bean.name_eng);

                    JaribhaPrefrence.setPref(activity, Constants.CATEGORY_ID, bean.id);
                    bean.selected = true;

                }
                adapter.notifyDataSetChanged();
            }
        });
        for (int i = 0; i < list.size(); i++) {
            Category bean = list.get(i);
            bean.selected = true;
        }
        adapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_reset:
                for (int i = 0; i < list.size(); i++) {
                    Category bean = list.get(i);
                    bean.selected = false;
                }
                JaribhaPrefrence.setPref(activity, Constants.CATEGORY_NAME, getString(R.string.all_cat));
                JaribhaPrefrence.setPref(activity, Constants.CATEGORY_ID, "");
                adapter.notifyDataSetChanged();
                break;

            case R.id.tv_all_category:
                for (int i = 0; i < list.size(); i++) {
                    Category bean = list.get(i);
                    bean.selected = true;
                }
                JaribhaPrefrence.setPref(activity, Constants.CATEGORY_NAME, getString(R.string.all_cat));
                JaribhaPrefrence.setPref(activity, Constants.CATEGORY_ID, "");
                adapter.notifyDataSetChanged();
                break;

            default:
                break;
        }
    }

    public class FilterCategoryAdapter extends ArrayAdapter<Category> {

        public FilterCategoryAdapter(Context context, ArrayList<Category> beans) {
            super(context, R.layout.item_category_filter, beans);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_category_filter, parent, false);

                holder.img_filter = (ImageView) convertView.findViewById(R.id.img_filter);

                holder.img_checked = (ImageView) convertView.findViewById(R.id.img_checked);
                holder.img_checked.setVisibility(View.VISIBLE);

                holder.tv_filter = (TextView) convertView.findViewById(R.id.tv_filter);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Category mBean = getItem(position);

            if (isArabic())
                holder.tv_filter.setText(mBean.name_ara);
            else
                holder.tv_filter.setText(mBean.name_eng);

            holder.img_filter.setImageResource(Utils.getCategoryImage(mBean.id, mBean.selected));

            if (mBean.selected) {
//                cat_id = mBean.id;
                holder.img_checked.setVisibility(View.VISIBLE);
            } else {
//                holder.img_filter.setImageResource(Utils.getCategoryImage(mBean.getId(), false));
                holder.img_checked.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

        class ViewHolder {
            ImageView img_filter, img_checked;
            TextView tv_filter;
        }
    }

    public void getCategory() {
        //showLoading();
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                list.clear();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", Urls.API_KEY);

                    String uid = getUser() == null ? "" : getUser().id;
                    String utoken = getUser() == null ? "" : getUser().user_token;

                    jsonObject.put("user_id", uid);
                    jsonObject.put("user_token", utoken);
                    jsonObject.put("category_id", "");
                    jsonObject.put("offset", "");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new JSONParser().getJsonObjectFromUrl1(Urls.GET_CATEGORY, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject responce) {
                //hideLoading();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (responce != null) {
                    try {
                        JSONObject jsonObject = responce.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        String s = jsonObject.optString("msg");
                        if (status) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject countryJson = jsonArray.getJSONObject(i).getJSONObject("Category");
                                Category category = new Gson().fromJson(countryJson.toString(), Category.class);

                                if (!JaribhaPrefrence.getPref(activity, Constants.CATEGORY_NAME, "").equals(activity.getResources().getString(R.string.all_cat))) {
                                    if (countryJson.optString("name_eng").equals(JaribhaPrefrence.getPref(activity, Constants.CATEGORY_NAME, ""))) {
                                        category.selected = true;
                                    }
                                } else {
                                    category.selected = true;
                                }
                                //countryFilterBean.setSelected(false);
                                list.add(category);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            if (s.equalsIgnoreCase("User Not Found")) {
                                showSessionDialog();
                            } else if (s.equalsIgnoreCase("Data Not Found")) {
                                showDataNotFoundDialog();
                            } else {
                                showServerErrorDialog();
                            }
                        }
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
                //hideLoading();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }.execute();
    }
}
