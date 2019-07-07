package com.jaribha.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class AskQuestionActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private ImageView iv_close;

    private TextView tv_title, name, title;

    private BezelImageView img_user_image;

    private EditText edt_msg;

    private Button btn_send;

    private String receiverId, receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.ask_a_question));

        name = (TextView) findViewById(R.id.toAskTv);
        title = (TextView) findViewById(R.id.askTitle);

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.close_icon);
        iv_close.setOnClickListener(this);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.GONE);

        edt_msg = (EditText) findViewById(R.id.edt_msg);

        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);

        if (getIntent().getExtras() != null) {
            receiverId = getIntent().getStringExtra("receiver_id");
            receiverName = getIntent().getStringExtra("receiver_name");
            name.setText(getString(R.string.to) + " " + receiverName);
            title.setText(getString(R.string.ask_a_question_about) + " " + getIntent().getStringExtra("project_name"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_close:
                finish();
                break;

            case R.id.btn_send:
                if (isInternetConnected()) {
                    sendAskQuestion();
                } else
                    showNetworkDialog();
                break;

            default:
                break;
        }
    }

    private AskQuestionTask mAskQuestionTask = null;

    /**
     * Represents an asynchronous AskQuestionTask used to ask question to project creator.
     */

    public class AskQuestionTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        AskQuestionTask(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.ASK_QUESTION, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mAskQuestionTask = null;
            hideLoading();
            if (responce != null) {
                try {

                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        edt_msg.setText("");
                        String message = jsonObject.getString("msg");
                        showCustomeDialog(R.drawable.right_icon, getString(R.string.success), message, getString(R.string.msg_add), R.drawable.btn_bg_green);
                    } else {
                        hideLoading();
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
            mAskQuestionTask = null;
            hideLoading();
        }
    }

    public void sendAskQuestion() {
        edt_msg.setError(null);

        // Store values at the time of sent E-mail.
        String msg = edt_msg.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for message not be null.
        if (TextUtils.isNullOrEmpty(msg)) {
            edt_msg.setError(getString(R.string.error_field_required));
            focusView = edt_msg;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            try {
                JSONObject AskQuestionJsObj = new JSONObject();
                AskQuestionJsObj.put("apikey", Urls.API_KEY);
                AskQuestionJsObj.put("user_id", getUser().id);
                AskQuestionJsObj.put("user_token", getUser().user_token);
                AskQuestionJsObj.put("receiver_id", receiverId);
                AskQuestionJsObj.put("message", msg);

                mAskQuestionTask = new AskQuestionTask(AskQuestionJsObj);
                mAskQuestionTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
