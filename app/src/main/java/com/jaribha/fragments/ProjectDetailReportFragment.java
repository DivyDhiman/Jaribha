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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.jaribha.R;
import com.jaribha.activity.LoginScreenActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.ExpandableHeightListView;
import com.jaribha.models.ProjectDetailReportBean;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectDetailReportFragment extends BaseFragment implements View.OnClickListener {

    ListView list_filter_featured, listView;

    //ExpandableHeightListView listView;

    Button btn_send;

    private ArrayList<ProjectDetailReportBean> list = new ArrayList<>();

    private EditText reportBox;

    private FeaturedCategoryAdapter adapter;

    private ProgressDialog progressDialog;

    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_detail_report, container, false);

        reportBox = (EditText) view.findViewById(R.id.edtTxtReport);
//        reportBox.setVisibility(View.GONE);
        reportBox.setHint(getString(R.string.add_comment));

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        String[] filterHeading = new String[]{
                activity.getResources().getString(R.string.this_project_creator_is_spamming_me),
                activity.getResources().getString(R.string.this_is_a_project),
                activity.getResources().getString(R.string.prohibited_rewards),
                activity.getResources().getString(R.string.bad_language),
                activity.getResources().getString(R.string.bad_images),
                activity.getResources().getString(R.string.bad_video),
                activity.getResources().getString(R.string.other)

        };
        String[] filterDesc = new String[]{
                activity.getResources().getString(R.string.spamming_me_desc),
                activity.getResources().getString(R.string.this_is_desc),
                activity.getResources().getString(R.string.prohibited_rewards_desc),
                "",
                "",
                "",
                ""
        };

        for (int i = 0; i < filterHeading.length; i++) {
            list.add(new ProjectDetailReportBean(filterHeading[i], filterDesc[i]));
        }
        list.get(0).setSelected(true);

        adapter = new FeaturedCategoryAdapter(activity, list);

        // View view1 = activity.getLayoutInflater().inflate(R.layout.report_footer,null);
        //reportBox = (EditText) view1.findViewById(R.id.edtTxtReport);
        //reportBox.setHint(getString(R.string.add_comment));


        if (!isTabletDevice()) {
            list_filter_featured = (ListView) view.findViewById(R.id.list_filter_featured);
            // list_filter_featured.addFooterView(view1);
            list_filter_featured.setAdapter(adapter);
            list_filter_featured.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ProjectDetailReportBean bean = list.get(position);

                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setSelected(false);
                    }
                    if (bean.isSelected()) {
                        bean.setSelected(false);
                    } else {
                        bean.setSelected(true);
                    }

                    adapter.notifyDataSetChanged();
                }
            });

        } else {
            listView = (ListView) view.findViewById(R.id.list_filter_featured);
            //listView.addFooterView(view1);
            //listView.setExpanded(true);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ProjectDetailReportBean bean = list.get(position);

                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setSelected(false);
                    }
                    if (bean.isSelected()) {
                        bean.setSelected(false);
                    } else {
                        bean.setSelected(true);
                    }

                    adapter.notifyDataSetChanged();
                }
            });
        }

        btn_send = (Button) view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        if (isAdded())
            btn_send.setText(activity.getResources().getString(R.string.send));
        return view;
    }

    public void addReport(String reason, String msg) {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            String uid = getUser() == null ? "" : getUser().id;
            String utoken = getUser() == null ? "" : getUser().user_token;

            projectJson.put("user_id", uid);
            projectJson.put("user_token", utoken);
            projectJson.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));
            projectJson.put("reason", reason);
            projectJson.put("message", msg);

            AddReportAPI mAuthTask = new AddReportAPI(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int reasonId;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (getUser() != null) {
                    String msg = reportBox.getText().toString().trim();
//                    boolean cancel = false;
//                    View focusView = null;
//
//                    if (TextUtils.isNullOrEmpty(msg)) {
//                        if (isAdded())
//                            reportBox.setError(getString(R.string.error_field_required));
//                        focusView = reportBox;
//                        cancel = true;
//                    }
//
//                    if (cancel) {
//                        focusView.requestFocus();
//                    } else {

                    if (TextUtils.isNullOrEmpty(msg)) {
                        msg = "";
                    }

                    if (isInternetConnected()) {
                        addReport(String.valueOf(reasonId), msg);
                    } else {
                        if (isAdded())
                            showAlertDialog(getResources().getString(R.string.internet));
                    }
//                    }
                } else {
                    Intent loginIntent = new Intent(activity, LoginScreenActivity.class);
                    //loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(loginIntent);
//                    activity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                    activity.finish();
                }

                break;
            default:
                break;
        }
    }

    public class FeaturedCategoryAdapter extends ArrayAdapter<ProjectDetailReportBean> {

        public FeaturedCategoryAdapter(Context context, ArrayList<ProjectDetailReportBean> beans) {
            super(context, R.layout.item_featured_filter, beans);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.project_detail_report_item, parent, false);
                holder.img_checked = (ImageView) convertView.findViewById(R.id.img_checked);
                // holder.img_checked.setVisibility(View.INVISIBLE);
                holder.tv_filter = (TextView) convertView.findViewById(R.id.tv_featured_filter);
                holder.tv_filter2 = (TextView) convertView.findViewById(R.id.tv_featured_filter2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ProjectDetailReportBean mBean = getItem(position);
            holder.tv_filter.setText(mBean.getFilterTextHeading());
            holder.tv_filter2.setText(mBean.getFilterTextDesc());

            if (mBean.isSelected()) {
                reasonId = position;
                holder.img_checked.setImageResource(R.drawable.radio_deff);
            } else {
                holder.img_checked.setImageResource(R.drawable.radio_red_unselected);
            }

            return convertView;
        }

        class ViewHolder {
            ImageView img_checked;
            TextView tv_filter;
            TextView tv_filter2;
        }
    }

    public class AddReportAPI extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        AddReportAPI(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showLoading();
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            // TODO: register the new account here.
            return new JSONParser().getJsonObjectFromUrl1(Urls.REPORT_PROJECT, nameValuePairs);
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
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        reportBox.setText("");
                        if (isAdded())
                            showCustomeDialog(R.drawable.right_icon, getString(R.string.success), getString(R.string.reported), getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                    } else {
                        //hideLoading();
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
                                if (msg.equals("Already marked as spam")) {
                                    showCustomeDialog(R.drawable.icon_error, getString(R.string.error), msg, getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                                } else {
                                    showServerErrorDialog();
                                }
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
