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
import android.widget.ListView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.ChattingActivity;
import com.jaribha.adapters.GetMessagesAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.EndlessScrollListener;
import com.jaribha.models.MessageData;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyMessagesFragment extends BaseFragment {

    private ListView messageList;

    Intent intent;

    GetMessagesAdapter getMessagesAdapter;

    GetMessageTask mMessageTask = null;

    ArrayList<MessageData> messageDataArrayList = new ArrayList<>();

    int pos;

    TextView noItems;

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
        View view = inflater.inflate(R.layout.fragment_my_messages, container, false);
        messageList = (ListView) view.findViewById(R.id.messageList);

        noItems = (TextView) view.findViewById(R.id.noMessages);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        getMessagesAdapter = new GetMessagesAdapter(activity, messageDataArrayList);
        messageList.setAdapter(getMessagesAdapter);

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                String otherUserId = "";

                messageList.setEnabled(false);

                MessageData messageData = messageDataArrayList.get(position);
                if (!(getUser().id).equals(messageData.from_user_id)) {
                    otherUserId = messageData.from_user_id;
                } else if (!(getUser().id).equals(messageData.to_user_id)) {
                    otherUserId = messageData.to_user_id;
                }
                if (progressDialog != null && !progressDialog.isShowing()) {
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                }

                if (isInternetConnected())
                    resetCount(otherUserId);
                else
                    showNetworkDialog();
            }
        });

        messageList.setOnScrollListener(new EndlessScrollListener() {

            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (nextOffset != 0 && nextOffset != -1 && messageDataArrayList.size() >= nextOffset) {
                    attemptGetMessages(nextOffset);
                }
                return true;
            }
        });

        if (isInternetConnected()) {
            attemptGetMessages(0);
        } else {
            showNetworkDialog();
        }

        return view;
    }

    int nextOffset = 0;

    public class GetMessageTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetMessageTask(JSONObject params) {
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
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_MESSAGES, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mMessageTask = null;
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    nextOffset = Integer.parseInt(jsonObject.getString("nextOffset"));
                    if (status) {
                        messageDataArrayList.clear();
                        JSONArray messagejsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < messagejsonArray.length(); i++) {
                            MessageData messageData = new Gson().fromJson(messagejsonArray.getJSONObject(i).getJSONObject("Message").toString(), MessageData.class);
                            messageDataArrayList.add(messageData);
                        }
                        if (messageDataArrayList != null) {

                            getMessagesAdapter.notifyDataSetChanged();
                        }
                    } else {
                        switch (msg) {
                            case "Data Not Found":
                                noItems.setVisibility(View.VISIBLE);
                                if (isAdded())
                                    noItems.setText(getString(R.string.no_records));

                                messageList.setVisibility(View.GONE);
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
            mMessageTask = null;
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void attemptGetMessages(int offset) {
        try {
            JSONObject GetMessagesJsonObject = new JSONObject();
            GetMessagesJsonObject.put("apikey", Urls.API_KEY);
            GetMessagesJsonObject.put("user_id", getUser().id);
            GetMessagesJsonObject.put("user_token", getUser().user_token);
            GetMessagesJsonObject.put("offset", offset);

            mMessageTask = new GetMessageTask(GetMessagesJsonObject);
            mMessageTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ResetReadCount extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        ResetReadCount(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.UPDATE_MESSAGE_STATUS, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            resetReadCount = null;
            messageList.setEnabled(true);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");

                    if (status) {
                        intent = new Intent(activity, ChattingActivity.class);
                        intent.putExtra(Constants.DATA, messageDataArrayList.get(pos));
                        startActivity(intent);
                    } else {
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
                showServerErrorDialog();
            }
        }

        @Override
        protected void onCancelled() {
            resetReadCount = null;
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void resetCount(String senderId) {
        try {
            JSONObject GetMessagesJsonObject = new JSONObject();
            GetMessagesJsonObject.put("apikey", Urls.API_KEY);
            GetMessagesJsonObject.put("user_id", getUser().id);
            GetMessagesJsonObject.put("user_token", getUser().user_token);
            GetMessagesJsonObject.put("sender_id", senderId);

            resetReadCount = new ResetReadCount(GetMessagesJsonObject);
            resetReadCount.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ResetReadCount resetReadCount;

    @Override
    public void onResume() {
        super.onResume();
        if (isInternetConnected()) {
            attemptGetMessages(0);
        } else {
            showNetworkDialog();
        }
    }
}
