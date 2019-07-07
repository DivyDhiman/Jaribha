package com.jaribha.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.jaribha.R;
import com.jaribha.activity.EditProjectActivity;
import com.jaribha.adapters.AddFAQAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.ExpandableHeightListView;
import com.jaribha.models.AddFAQ;
import com.jaribha.models.GetProjects;
import com.jaribha.models.ProjectStory;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditProjectStoryFragment extends BaseFragment implements View.OnClickListener {

    Button btn_story_next, btn_save_que;

    TextView impDesc, impRisk;

    EditText edt_video_url, edt_proj_description, edt_risk_chang, edt_faq_que, edt_faq_ans;

    ExpandableHeightListView faq_list;

    AddFAQAdapter addFAQAdapter;

    boolean isEdit = false;

    int editPosition;

    ArrayList<AddFAQ> arrayList = new ArrayList<>();

    JSONObject object;

    String next, desc, risk;

    private ProgressDialog progressDialog;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    public static EditProjectStoryFragment newInstance(JSONObject jsonObject) {
        Bundle args = new Bundle();
        args.putString(Constants.DATA, jsonObject.toString());
        EditProjectStoryFragment fragment = new EditProjectStoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mString = getArguments().getString(Constants.DATA);
        try {
            object = new JSONObject(mString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project_story, null);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        if (isAdded()) {
            next = "<font color='#000000'><b>" + getString(R.string.important) + "</b></font>";
            desc = "<font color='#C3C3Ce'>" + getString(R.string.please_remember_to_include) + "</font>";
            risk = "<font color='#C3C3Ce'>" + getString(R.string.There_are_always_risks_and_challenges_when_completing) + "</font>";
        }
        impDesc = (TextView) view.findViewById(R.id.descImp);
        impDesc.setText(Html.fromHtml(next + desc));

        impRisk = (TextView) view.findViewById(R.id.riskImp);
        impRisk.setText(Html.fromHtml(next + risk));

        btn_story_next = (Button) view.findViewById(R.id.btn_story_next);
        btn_story_next.setOnClickListener(this);

        btn_save_que = (Button) view.findViewById(R.id.btn_save_que);
        btn_save_que.setOnClickListener(this);

        edt_video_url = (EditText) view.findViewById(R.id.edt_video_url);

        edt_proj_description = (EditText) view.findViewById(R.id.edt_proj_description);

        edt_risk_chang = (EditText) view.findViewById(R.id.edt_risk_chang);

        edt_faq_que = (EditText) view.findViewById(R.id.edt_faq_que);

        edt_faq_ans = (EditText) view.findViewById(R.id.edt_faq_ans);

        addFAQAdapter = new AddFAQAdapter(activity, arrayList);
        addFAQAdapter.setOnEditListener(new AddFAQAdapter.OnEditListener() {
            @Override
            public void onEditClick(AddFAQ addFAQ, int position, boolean edit) {
                isEdit = edit;
                editPosition = position;
                edt_faq_que.setText(addFAQ.question);
                edt_faq_ans.setText(addFAQ.answer);
            }
        });

        faq_list = (ExpandableHeightListView) view.findViewById(R.id.faq_list);
        faq_list.setAdapter(addFAQAdapter);
        faq_list.setExpanded(true);

        SetData();

        return view;
    }

    GetProjects projects;

    ProjectStory story;

    private void SetData() {
        try {
            story = new Gson().fromJson(object.getJSONObject("Story").toString(), ProjectStory.class);

            projects = new Gson().fromJson(object.optJSONObject("Project").toString(), GetProjects.class);

            if (!TextUtils.isNullOrEmpty(story.video))
                edt_video_url.setText(story.video);

            if (!TextUtils.isNullOrEmpty(story.prjdesc))
                edt_proj_description.setText(story.prjdesc);

            if (!TextUtils.isNullOrEmpty(story.riskchallenges))
                edt_risk_chang.setText(story.riskchallenges);

            if (!TextUtils.isNullOrEmpty(story.question_answers)) {
                ArrayList<AddFAQ> list = new Gson().fromJson(story.question_answers,
                        new TypeToken<List<AddFAQ>>() {
                        }.getType());
                arrayList.addAll(list);
                addFAQAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {
            case R.id.btn_story_next:
                if (isInternetConnected()) {
                    addStory();
                } else {
                    showNetworkDialog();
                }
                break;

            case R.id.btn_save_que:
                if (isInternetConnected()) {
                    addQuestions();
                } else {
                    showNetworkDialog();
                }
                break;

            default:
                break;
        }
    }

    private void addStory() {
        edt_video_url.setError(null);
        edt_risk_chang.setError(null);
        edt_proj_description.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isNullOrEmpty(edt_video_url.getText().toString())) {
            edt_video_url.setError(getString(R.string.error_field_required));
            focusView = edt_video_url;
            cancel = true;

        } else if (!Utils.checkURL(edt_video_url.getText().toString().trim())) {
            edt_video_url.setError(getString(R.string.please_enter_valid_url));
            focusView = edt_video_url;
            cancel = true;
        }/*else if (edt_video_url.getText().toString().contains("vimeo") && !Utils.isValidURL(Constants.VIMEO_REG,edt_video_url.getText().toString())) {

            edt_video_url.setError("Please enter a valid URL.");
            focusView = edt_video_url;
            cancel = true;
        }*/ else if (TextUtils.isNullOrEmpty(edt_proj_description.getText().toString())) {
            edt_proj_description.setError(getString(R.string.error_field_required));
            focusView = edt_proj_description;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_risk_chang.getText().toString())) {
            edt_risk_chang.setError(getString(R.string.error_field_required));
            focusView = edt_risk_chang;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(arrayList)) {
            edt_faq_que.setError(getString(R.string.error_field_required));
            //edt_faq_ans.setError(getString(R.string.error_field_required));
            focusView = edt_faq_que;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (isInternetConnected()) {
                try {
                    Gson gson = new GsonBuilder().create();
                    JsonArray myCustomArray = gson.toJsonTree(arrayList).getAsJsonArray();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("apikey", Urls.API_KEY);
                    jsonObject.put("user_id", getUser().id);
                    jsonObject.put("user_token", getUser().user_token);
                    jsonObject.put("project_id", projects.id);
                    jsonObject.put("video", edt_video_url.getText().toString());
                    jsonObject.put("project_desc", edt_proj_description.getText().toString());
                    jsonObject.put("riskchallenges", edt_risk_chang.getText().toString());
                    jsonObject.put("question_answers", myCustomArray.toString());

                    if (TextUtils.isNullOrEmpty(story.id)) {
                        jsonObject.put("storyID", "");
                    } else {
                        jsonObject.put("storyID", story.id);
                    }

                    addProjectStory(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showNetworkDialog();
            }
        }
    }

    private void addQuestions() {
        edt_faq_que.setError(null);
        edt_faq_ans.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isNullOrEmpty(edt_faq_que.getText().toString().trim())) {
            edt_faq_que.setError(getString(R.string.error_field_required));
            focusView = edt_faq_que;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_faq_ans.getText().toString().trim())) {
            edt_faq_ans.setError(getString(R.string.error_field_required));
            focusView = edt_faq_ans;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            AddFAQ addFAQ = new AddFAQ(edt_faq_que.getText().toString(), edt_faq_ans.getText().toString());
            edt_faq_que.setText("");
            edt_faq_ans.setText("");
            if (isEdit) {
                arrayList.set(editPosition, addFAQ);
                addFAQAdapter.notifyDataSetChanged();
            } else {
                arrayList.add(addFAQ);
                addFAQAdapter.notifyDataSetChanged();
            }
        }
    }

    public void addProjectStory(final JSONObject jsonObject) {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //showLoading();
                if (progressDialog != null && !progressDialog.isShowing())
                    progressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                return new JSONParser().getJsonObjectFromUrl1(Urls.ADD_STORIES, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject response) {
                //hideLoading();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject jsonObject = response.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        String message = jsonObject.getString("msg");
                        if (status) {
                            ((EditProjectActivity) activity).firstLoad = false;
                            if (!isTabletDevice())
                                ((EditProjectActivity) activity).displayTab(3, true);
                            else {
                                ((EditProjectActivity) activity).displayFragment(3, true);
                            }
                        } else {
                            if (message.equalsIgnoreCase("User Not Found")) {
                                showSessionDialog();
                            } else if (message.equalsIgnoreCase("Data Not Found")) {
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
