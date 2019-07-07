package com.jaribha.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.LoginScreenActivity;
import com.jaribha.activity.WebViewActivity;
import com.jaribha.adapters.UpdateAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.EndlessScrollListener;
import com.jaribha.interfaces.DetailTabUpdateListener;
import com.jaribha.models.ProjectUpdates;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProjectDetailUpdateFragment extends BaseFragment implements View.OnClickListener {

    TextView goWeb, tv_add_comment;

    Button submit;

    View footerView;

    Button leaveBtnFooter;

    private EditText commentBox;

    private ArrayList<ProjectUpdates> dataList = new ArrayList<>();

    ListView listView;

    private UpdateAdapter adapter;

    //LinearLayout linearLayout;

    private ProgressDialog progressDialog;

    Activity activity;

    boolean addUpdate = false, isLoadMore = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            addUpdate = false;
//            dataList.clear();
            if (isInternetConnected()) {
                loadUpdates(0);
            } else {
                showNetworkDialog();
            }
        }
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_detail_update, container, false);
        dataList.clear();
        progressDialog = new ProgressDialog(activity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        adapter = new UpdateAdapter(activity, dataList);

        listView = (ListView) view.findViewById(R.id.updateList);
        footerView = activity.getLayoutInflater().inflate(R.layout.comments_footer, null);

        tv_add_comment = (TextView) footerView.findViewById(R.id.tv_add_comment);
        tv_add_comment.setText(getString(R.string.add_update));

        leaveBtnFooter = (Button) footerView.findViewById(R.id.leave_comment_btn);
        leaveBtnFooter.setOnClickListener(this);

        commentBox = (EditText) footerView.findViewById(R.id.commentEdit);

        goWeb = (TextView) footerView.findViewById(R.id.goWebTv);
        goWeb.setVisibility(View.VISIBLE);
        goWeb.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        goWeb.setOnClickListener(this);

        if (isAdded()) {
            goWeb.setText(activity.getResources().getString(R.string.if_you_want_to_access_all_features_go_on_the_website));
            commentBox.setHint(getString(R.string.description_here));
            leaveBtnFooter.setText(activity.getResources().getString(R.string.submit));
            tv_add_comment.setText(getString(R.string.update_error));
        }

        if (isTabletDevice()) {
            if (getUser() != null) {
                if (!JaribhaPrefrence.getPref(activity, Constants.CREATOR_ID, "").equals(getUser().id)) {
                    leaveBtnFooter.setVisibility(View.GONE);
                    goWeb.setVisibility(View.GONE);
                    commentBox.setVisibility(View.GONE);
                } else {
                    leaveBtnFooter.setVisibility(View.VISIBLE);
                    goWeb.setVisibility(View.VISIBLE);
                    commentBox.setVisibility(View.VISIBLE);
                }
            } else {
                leaveBtnFooter.setVisibility(View.GONE);
                goWeb.setVisibility(View.GONE);
                commentBox.setVisibility(View.GONE);
            }
        } else {
            submit = (Button) view.findViewById(R.id.btn_update);
            submit.setOnClickListener(this);
            leaveBtnFooter.setVisibility(View.GONE);
            if (isAdded())
                submit.setText(activity.getResources().getString(R.string.submit));

            if (getUser() != null) {
                if (!JaribhaPrefrence.getPref(activity, Constants.CREATOR_ID, "").equals(getUser().id)) {
                    commentBox.setVisibility(View.GONE);
                    submit.setVisibility(View.GONE);
                    goWeb.setVisibility(View.GONE);
                } else {
                    submit.setVisibility(View.VISIBLE);
                    goWeb.setVisibility(View.VISIBLE);
                    commentBox.setVisibility(View.VISIBLE);
                }
            } else {
                goWeb.setVisibility(View.GONE);
                commentBox.setVisibility(View.GONE);
                submit.setVisibility(View.GONE);
            }
        }

        listView.addFooterView(footerView);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (nextOffset != 0 && nextOffset != -1 && dataList.size() >= nextOffset) {
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

    int nextOffset = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                attempToUpdate();
                break;

            case R.id.leave_comment_btn:
                attempToUpdate();
                break;

            case R.id.goWebTv:
                Intent intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra("web_url", "https://jaribha.com/");
                intent.putExtra("title", "Jaribha");
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void attempToUpdate() {
        if (getUser() != null) {
            addUpdate = true;
            String msg = commentBox.getText().toString().trim();
            boolean cancel = false;
            View focusView = null;

            if (TextUtils.isNullOrEmpty(msg)) {
                commentBox.setError(getString(R.string.error_field_required));
                focusView = commentBox;
                cancel = true;
            }

            if (cancel) {
                focusView.requestFocus();
            } else {
                if (isInternetConnected()) {
                    addUpdate(msg);
                } else {
                    showNetworkDialog();
                }
            }
        } else {
            Intent loginIntent = new Intent(activity, LoginScreenActivity.class);
            activity.startActivity(loginIntent);
        }
    }

    public void addUpdate(String msg) {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            String uid = getUser() == null ? "" : getUser().id;
            String utoken = getUser() == null ? "" : getUser().user_token;

            projectJson.put("user_id", uid);
            projectJson.put("user_token", utoken);
            projectJson.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));
            projectJson.put("description", msg);
            projectJson.put("project_title", JaribhaPrefrence.getPref(activity, Constants.PROJECT_TITLE, ""));

            AddUpdateAPI mAuthTask = new AddUpdateAPI(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class AddUpdateAPI extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        AddUpdateAPI(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dataList.clear();
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.UPDATE_PROJECT, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        commentBox.setText("");
                        if (jsonObject.optJSONObject("data") != null) {
                            ProjectUpdates projectUpdates = new Gson().fromJson(jsonObject.optJSONObject("data").optJSONObject("Projectupdates").toString(), ProjectUpdates.class);
                            dataList.add(projectUpdates);
                            adapter.notifyDataSetChanged();
                            //  if (addUpdate) {
                            if (isAdded())
                                showCustomeDialog(R.drawable.right_icon, getString(R.string.success), getString(R.string.update_added), getString(R.string.dgts__okay), R.drawable.btn_bg_green);

                            DetailTabUpdateListener callBack = (DetailTabUpdateListener) activity;
                            callBack.setTabCount(dataList.size(), -1);

                            // }
                        }
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                    } else {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        switch (msg) {
                            case "Data Not Found":
                                showDataNotFoundDialog();
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

    public void loadUpdates(int offset) {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            if (getUser() != null) {
                projectJson.put("user_id", getUser().id);
                projectJson.put("user_token", getUser().user_token);
            } else {
                projectJson.put("user_id", "");
                projectJson.put("user_token", "");
            }
            projectJson.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));
            projectJson.put("offset", offset);
            GetUpdatesAPI mAuthTask = new GetUpdatesAPI(projectJson);
            mAuthTask.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GetUpdatesAPI extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetUpdatesAPI(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showLoading();
//            dataList.clear();
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_PROJECT_UPDATES, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            //hideLoading();
            // if (!isLoadMore) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            // }
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    nextOffset = Integer.parseInt(jsonObject.getString("nextOffset"));
                    if (status) {
                        JSONArray data = jsonObject.optJSONArray("data");
                        if (data != null && data.length() != 0) {
                            for (int i = 0; i < data.length(); i++) {
                                ProjectUpdates projectUpdates = new Gson().fromJson(data.getJSONObject(i).optJSONObject("Projectupdates").toString(), ProjectUpdates.class);
                                dataList.add(projectUpdates);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            showDataNotFoundDialog();
                        }

                    } else {
                        switch (msg) {
                            case "Update Details not Found":
                                showDataNotFoundDialog();
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
                //hideLoading();
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
