package com.jaribha.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.adapters.ChatMessageAdapter;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.models.Item;
import com.jaribha.models.MessageData;
import com.jaribha.models.SectionItem;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class ChattingActivity extends BaseAppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ImageView iv_close;

    TextView tv_title;

    BezelImageView img_user_image, chatImg;

    private ListView lvChat;

    private EditText edt_message;

    LinearLayout buttonSend;

    private ArrayList<MessageData> messageHistoryArrayList = new ArrayList<>();

    private MessageData messageData;

    private String otherUserId = "";

    private ChatMessageAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int pageOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        messageData = (MessageData) getIntent().getSerializableExtra(Constants.DATA);
        if (!(getUser().id).equals(messageData.from_user_id)) {
            otherUserId = messageData.from_user_id;
        } else if (!(getUser().id).equals(messageData.to_user_id)) {
            otherUserId = messageData.to_user_id;
        }

        /*
        NSString *strSenderId = @"";
        if(![[[[NSUserDefaults standardUserDefaults] valueForKey:@"userSignupRecord"] valueForKey:@"id"] isEqualToString:[self.dicConversation valueForKey:@"from_user_id"]]){
            strSenderId = [self.dicConversation valueForKey:@"from_user_id"];
        }else if(![[[[NSUserDefaults standardUserDefaults] valueForKey:@"userSignupRecord"] valueForKey:@"id"] isEqualToString:[self.dicConversation valueForKey:@"to_user_id"]]){
            strSenderId = [self.dicConversation valueForKey:@"to_user_id"];
        }
         */

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        chatImg = (BezelImageView) findViewById(R.id.chat_img);
        buttonSend = (LinearLayout) findViewById(R.id.buttonSend);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setRefreshing(false);

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(messageData.name);

        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setOnClickListener(this);

        img_user_image.setVisibility(View.GONE);
        img_user_image.setOnClickListener(this);
        displayUserImage(img_user_image);

        chatImg.setVisibility(View.VISIBLE);

        if (messageData.imgurl.isEmpty()) {
            chatImg.setImageResource(R.drawable.user_icon);
        } else {
            displayImage(ChattingActivity.this, chatImg, messageData.imgurl, ContextCompat.getDrawable(this, R.drawable.user_icon));
        }

        buttonSend.setOnClickListener(this);

        lvChat = (ListView) findViewById(R.id.chatListView);

        mAdapter = new ChatMessageAdapter(ChattingActivity.this, mainMessageList);
        lvChat.setAdapter(mAdapter);

        edt_message = (EditText) findViewById(R.id.chatText);
        edt_message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lvChat.setStackFromBottom(true);
            }
        });

        if (isInternetConnected())
            getConversationHistory(pageOffset);
        else
            showNetworkDialog();

    }

    boolean loadMore;

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        if (pageOffset != -1 && storeList.size() >= pageOffset) {
            loadMore = true;
            mSwipeRefreshLayout.setRefreshing(true);
            getConversationHistory(pageOffset);
        }
    }

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {

            case R.id.iv_close:
                finish();
                break;

            case R.id.buttonSend:
                if (isInternetConnected()) {
                    String message = edt_message.getText().toString();
                    if (TextUtils.isNullOrEmpty(message)) {
                        showToast(getString(R.string.please_enter_message));
                    } else {
                        sendMessage(message);
                    }
                } else {
                    showNetworkDialog();
                }

                break;

            case R.id.img_user_image:
                startActivity(new Intent(getActivity(), MyPortfolioActivity.class));
                break;

            default:
                break;
        }
    }

    private void getConversationHistory(final int offset) {

        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!mSwipeRefreshLayout.isRefreshing())
                    showLoading();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", Urls.API_KEY);
                    jsonObject.put("user_id", getUser().id);
                    jsonObject.put("user_token", getUser().user_token);
                    jsonObject.put("other_user_id", otherUserId);
                    jsonObject.put("offset", offset);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new JSONParser().getJsonObjectFromUrl1(Urls.GET_MESSAGES_HISTORY, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject responce) {
                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);

                if (responce != null) {
                    try {
                        JSONObject jsonObject = responce.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        String msg = jsonObject.optString("msg");
                        pageOffset = Integer.parseInt(jsonObject.getString("nextOffset"));
                        if (status) {
                            messageSend = false;
                            JSONArray messagejsonArray = jsonObject.getJSONArray("data");
                            messageHistoryArrayList.clear();
                            for (int i = 0; i < messagejsonArray.length(); i++) {
                                MessageData messageData = new Gson().fromJson(messagejsonArray.getJSONObject(i).getJSONObject("Message").toString(), MessageData.class);
                                messageHistoryArrayList.add(messageData);
                            }

                            displayConversationData(messageHistoryArrayList);

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
                        hideLoading();
                        showServerDialogDialog();
                    }
                } else {
                    hideLoading();
                    showServerDialogDialog();
                }
            }

            @Override
            protected void onCancelled() {
                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);

                hideLoading();
            }
        }.execute();
    }

    HashSet<String> hashSet = new HashSet<>();
    ArrayList<Item> mainMessageList = new ArrayList<>();
    ArrayList<SectionItem> sectionDates = new ArrayList<>();
    ArrayList<MessageData> storeList = new ArrayList<>();
    ArrayList<MessageData> demoList = new ArrayList<>();

    private void displayConversationData(ArrayList<MessageData> messageHistoryList) {

        demoList.addAll(messageHistoryList);

        storeList.addAll(messageHistoryList);

        for (MessageData data : storeList) {
            hashSet.add(Utils.formateDateFromstring(getActivity(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", data.created));
        }

        // check values
        sectionDates.clear();
        for (Object aHashSet : hashSet) {
            SectionItem sectionItem = new SectionItem();
            sectionItem.title = aHashSet.toString();
            sectionDates.add(sectionItem);
        }

        if (!messageSend)
            Collections.reverse(storeList);

        if (loadMore) {
            loadMore = false;
            Collections.reverse(storeList);
        }

        Collections.reverse(sectionDates);
        Collections.sort(sectionDates, new Comparator<SectionItem>() {
            public int compare(SectionItem m1, SectionItem m2) {
                return m1.title.compareTo(m2.title);
            }
        });

        mainMessageList.clear();
        for (SectionItem aHashSet : sectionDates) {
            mainMessageList.add(aHashSet);
            for (int j = 0; j < storeList.size(); j++) {
                if (storeList.get(j).getCreated().equals(aHashSet.title))
                    mainMessageList.add(storeList.get(j));
            }
        }


        mAdapter.notifyDataSetChanged();

        if (messageSend) {
            lvChat.smoothScrollToPosition(mAdapter.getCount() - 1);
        }
        hideLoading();
    }

    boolean messageSend = false;

    private void sendMessage(final String message) {
        edt_message.setText("");
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", Urls.API_KEY);
                    jsonObject.put("user_id", getUser().id);
                    jsonObject.put("user_token", getUser().user_token);
                    jsonObject.put("receiver_id", otherUserId);
                    jsonObject.put("chat_id", messageData.parent_id);
                    jsonObject.put("message", message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new JSONParser().getJsonObjectFromUrl1(Urls.SEND_MESSAGE, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject responce) {
                if (responce != null) {
                    try {
                        JSONObject jsonObject = responce.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        if (status) {
                            messageSend = true;
                            messageHistoryArrayList.clear();
                            MessageData messageData = new Gson().fromJson(jsonObject.getJSONObject("data").getJSONObject("Message").toString(), MessageData.class);
                            messageHistoryArrayList.add(messageData);
                            lvChat.smoothScrollToPosition(mAdapter.getCount() - 1);
                            displayConversationData(messageHistoryArrayList);

                        } else {
                            if (jsonObject.getString("msg").equals("Data Not Found")) {
                                if (!ChattingActivity.this.isFinishing()) {
                                    showServerDialogDialog();
                                    //show dialog
                                }
                            } else if (jsonObject.getString("msg").equals("User Not Found")) {
                                if (!ChattingActivity.this.isFinishing()) {
                                    showServerDialogDialog();
                                    //show dialog
                                }
                            } else {
                                if (!ChattingActivity.this.isFinishing()) {
                                    showServerDialogDialog();
                                    //show dialog
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (!ChattingActivity.this.isFinishing()) {
                            showServerDialogDialog();
                            //show dialog
                        }
                    }
                } else {
                    if (!ChattingActivity.this.isFinishing()) {
                        showServerDialogDialog();
                        //show dialog
                    }
                }
            }

            @Override
            protected void onCancelled() {
            }
        }.execute();
    }
}
