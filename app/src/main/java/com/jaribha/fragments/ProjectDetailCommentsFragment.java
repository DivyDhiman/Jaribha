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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.LoginScreenActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.EndlessScrollListener;
import com.jaribha.interfaces.DetailTabUpdateListener;
import com.jaribha.models.Comments;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.TimeConstants;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ProjectDetailCommentsFragment extends BaseFragment implements View.OnClickListener {

    ListView list_comments;

    View footerView;

    Button leaveBtnFooter, leaveBtn;

    private ArrayList<Comments> commentsList = new ArrayList<>();

    private CommentsAdapter adapter;

    private TextView noComments;

    private EditText commentBox;

    TextView tv_add_comment;

    private ProgressDialog progressDialog;

    private Activity activity;

    boolean addComment = false, isLoadMore = false;

    private int nextOffset = 0;

    ArrayList<String> getsupporterslist = new ArrayList<>();

    ArrayList<String> getsponserslist = new ArrayList<>();

    public static ProjectDetailCommentsFragment newInstance(ArrayList<String> arrayList, ArrayList<String> sponserList) {
        Bundle args = new Bundle();
        args.putSerializable("data", arrayList);
        args.putSerializable("sponser", sponserList);
        ProjectDetailCommentsFragment fragment = new ProjectDetailCommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getsupporterslist.clear();
            getsponserslist.clear();
            getsupporterslist = (ArrayList<String>) getArguments().getSerializable("data");
            getsponserslist = (ArrayList<String>) getArguments().getSerializable("sponser");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            addComment = false;
            //commentsList.clear();
            nextOffset = 0;
            if (isInternetConnected()) {
                loadComments(false, 0);
            } else {
                showNetworkDialog();
            }
        }
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_detail_comments, container, false);
//        getsupporterslist.clear();
//        getsponserslist.clear();
        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        list_comments = (ListView) view.findViewById(R.id.list_comments);

        footerView = activity.getLayoutInflater().inflate(R.layout.comments_footer, null);

        leaveBtnFooter = (Button) footerView.findViewById(R.id.leave_comment_btn);

        commentBox = (EditText) footerView.findViewById(R.id.commentEdit);

        noComments = (TextView) view.findViewById(R.id.noComments);
        if (isAdded()) {
            commentBox.setHint(getString(R.string.description_here));
            leaveBtnFooter.setText(activity.getResources().getString(R.string.leave_a_comment));
        }

        tv_add_comment = (TextView) footerView.findViewById(R.id.tv_add_comment);

        if (isTabletDevice()) {
            if (getUser() != null) {
                if (getsupporterslist.contains(getUser().id) || getsponserslist.contains(getUser().id)) {
                    leaveBtnFooter.setVisibility(View.VISIBLE);
                    leaveBtnFooter.setOnClickListener(this);
                } else {
                    if (isAdded())
                        tv_add_comment.setText(getString(R.string.leave_comment));
                    leaveBtnFooter.setVisibility(View.GONE);
                    commentBox.setVisibility(View.GONE);
                }
            } else {
                tv_add_comment.setVisibility(View.GONE);
                leaveBtnFooter.setVisibility(View.GONE);
                commentBox.setVisibility(View.GONE);
            }
        } else {

            leaveBtn = (Button) view.findViewById(R.id.leaveCommentBtn);

            if (getUser() != null) {
                if (getsupporterslist.contains(getUser().id) || getsponserslist.contains(getUser().id)) {
                    leaveBtn = (Button) view.findViewById(R.id.leaveCommentBtn);
                    leaveBtn.setVisibility(View.VISIBLE);
                    leaveBtn.setOnClickListener(this);

                    leaveBtnFooter.setVisibility(View.VISIBLE);
                    leaveBtnFooter.setOnClickListener(this);
                    leaveBtnFooter.setVisibility(View.GONE);
                } else {
                    if (isAdded())
                        tv_add_comment.setText(getString(R.string.leave_comment));
                    leaveBtnFooter.setVisibility(View.GONE);
                    commentBox.setVisibility(View.GONE);
                    leaveBtn.setVisibility(View.GONE);
                }
            } else {
                tv_add_comment.setVisibility(View.GONE);
                leaveBtn.setVisibility(View.GONE);
                leaveBtnFooter.setVisibility(View.GONE);
                commentBox.setVisibility(View.GONE);
            }
        }

        adapter = new CommentsAdapter(activity, commentsList);

        list_comments.addFooterView(footerView);
        list_comments.setAdapter(adapter);

        list_comments.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (nextOffset != 0 && nextOffset != -1 && commentsList.size() >= nextOffset) {
                    isLoadMore = true;
                    loadComments(false, nextOffset);
                } else {
                    isLoadMore = false;
                }
                return true;
            }
        });
        return view;
    }

    public void loadComments(boolean isAddComment, int off) {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            String uid = getUser() == null ? "" : getUser().id;
            String utoken = getUser() == null ? "" : getUser().user_token;

            projectJson.put("user_id", uid);
            projectJson.put("user_token", utoken);
            projectJson.put("offset", off);
            projectJson.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));

            if (isAddComment) {
                projectJson.put("description", msg);
                projectJson.put("username", getUser().name);
                AddCommentsAPI mAuthTask = new AddCommentsAPI(projectJson);
                mAuthTask.execute();
            } else {
                GetCommentsAPI mAuthTask = new GetCommentsAPI(projectJson);
                mAuthTask.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String msg;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.leave_comment_btn:

                if (getUser() != null) {
                    addComment = true;
                    boolean cancel = false;
                    View focusView = null;

                    msg = commentBox.getText().toString().trim();

                    if (TextUtils.isNullOrEmpty(msg)) {
                        if (isAdded())
                            commentBox.setError(getString(R.string.error_field_required));
                        focusView = commentBox;
                        cancel = true;
                    }

                    if (cancel) {
                        focusView.requestFocus();
                    } else {

                        if (isInternetConnected()) {
                            loadComments(true, 0);
                        } else {
                            showNetworkDialog();
                        }
                    }
                } else {
                    Intent loginIntent = new Intent(activity, LoginScreenActivity.class);
                    //loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(loginIntent);
//                    activity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                    activity.finish();
                }
            case R.id.leaveCommentBtn:
                if (getUser() != null) {
                    boolean cancel1 = false;
                    View focusView1 = null;

                    msg = commentBox.getText().toString().trim();

                    if (TextUtils.isNullOrEmpty(msg)) {
                        if (isAdded())
                            commentBox.setError(getString(R.string.error_field_required));
                        focusView1 = commentBox;
                        cancel1 = true;
                    }

                    if (cancel1) {
                        focusView1.requestFocus();
                    } else {

                        if (isInternetConnected()) {
                            loadComments(true, 0);
                        } else {
                            showNetworkDialog();
                        }
                    }
                } else {
                    Intent loginIntent = new Intent(activity, LoginScreenActivity.class);
                    activity.startActivity(loginIntent);
                    //activity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }

                break;
        }
    }


    public class CommentsAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private ArrayList<Comments> dataList;

        public CommentsAdapter(Context context, ArrayList<Comments> data) {
            this.dataList = data;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null)
                view = layoutInflater.inflate(R.layout.item_project_detail_comments, null);

            Comments comments = getItem(position);

            TextView commentDesc = (TextView) view.findViewById(R.id.commentDesc);
            commentDesc.setText(comments.description);

            TextView commentDate = (TextView) view.findViewById(R.id.commentDate);
            String time = Utils.formateDateFromstring(activity, "yyyy-MM-dd HH:mm:ss", "d MMM, yyyy", comments.created);
            commentDate.setText(time);

            TextView commentName = (TextView) view.findViewById(R.id.commentName);
            commentName.setText(comments.name);

            TextView commentTime = (TextView) view.findViewById(R.id.commentTime);
            Date date = Utils.getStringToDate(activity, comments.created);

            commentTime.setText(TimeConstants.getRelativeTime(date));

            return view;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Comments getItem(int position) {
            return dataList.get(position);
        }

    }

    public class GetCommentsAPI extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetCommentsAPI(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();

        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_PROJECT_COMMENTS, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    nextOffset = Integer.parseInt(jsonObject.getString("nextOffset"));

                    if (status) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Comments data = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), Comments.class);
                            commentsList.add(data);
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        //hideLoading();
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        switch (msg) {
                            case "Data Not Found":
                                //showDataNotFoundDialog();
                                noComments.setVisibility(View.VISIBLE);
                                if (isAdded())
                                    noComments.setText(getString(R.string.no_records));
                                //list_comments.setVisibility(View.INVISIBLE);
                                //list_comments.addFooterView(null);

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

    public class AddCommentsAPI extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        AddCommentsAPI(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            commentsList.clear();
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.ADD_COMMENTS, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            // hideLoading();
            nextOffset = 0;
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        Comments projectUpdates = new Gson().fromJson(jsonObject.optJSONObject("data").optJSONObject("Projectcomment").toString(), Comments.class);
                        commentsList.add(projectUpdates);
                        adapter.notifyDataSetChanged();
                        commentBox.setText("");

                        //loadComments(false, 0);
                        //if (addComment) {
                        if (isAdded())
                            showCustomeDialog(R.drawable.right_icon, getString(R.string.success), getString(R.string.comment_added), getString(R.string.ok), R.drawable.btn_bg_green);
                        DetailTabUpdateListener callBack = (DetailTabUpdateListener) activity;
                        callBack.setTabCount(-1, commentsList.size());
                        // }
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
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
