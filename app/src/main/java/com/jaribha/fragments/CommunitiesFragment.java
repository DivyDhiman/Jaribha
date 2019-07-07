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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.AddNewCommunityActivity;
import com.jaribha.activity.CommunityDetailActivity;
import com.jaribha.activity.LoginScreenActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.EndlessScrollListener;
import com.jaribha.customviews.ExpandableHeightListView;
import com.jaribha.models.GetCommunities;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommunitiesFragment extends BaseFragment implements View.OnClickListener {

    private ExpandableHeightListView communityListView;

    private ListView listView;

    private Button addNewCommunity;

    private ArrayList<GetCommunities> communitiesList = new ArrayList<>();

    private Intent intent;

    private CommunityAdapter adapter;

    private ProgressDialog progressDialog;

    private TextView noItem;

    private int nextOffset = 0;

    private Activity activity;

    boolean loadMore;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            communitiesList.clear();
            if (Utils.isInternetConnected(activity))
                loadCommunities(0);
            else
                showNetworkDialog();

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.community_fragment, null);

        adapter = new CommunityAdapter(activity, communitiesList);

        noItem = (TextView) view.findViewById(R.id.noCommunity);

        if (isTabletDevice()) {
            communityListView = (ExpandableHeightListView) view.findViewById(R.id.communityListView);
            communityListView.setExpanded(true);
            communityListView.setAdapter(adapter);
            communityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent communityDetails = new Intent(activity, CommunityDetailActivity.class);
                    communityDetails.putExtra("community_object", communitiesList.get(position));
                    startActivity(communityDetails);
                }
            });
            communityListView.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    if (nextOffset != 0 && nextOffset != -1 && communitiesList.size() >= nextOffset) {
                        loadCommunities(nextOffset);
                    }
                    return true;
                }
            });
        } else {
            listView = (ListView) view.findViewById(R.id.communityListView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent communityDetails = new Intent(activity, CommunityDetailActivity.class);
                    communityDetails.putExtra("community_object", communitiesList.get(position));
                    startActivity(communityDetails);
                }
            });
            listView.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    if (nextOffset != 0 && nextOffset != -1 && communitiesList.size() >= nextOffset) {
                        loadCommunities(nextOffset);
                    }
                    return true;
                }
            });
        }

        addNewCommunity = (Button) view.findViewById(R.id.addNewCommunity);
        addNewCommunity.setOnClickListener(this);
        addNewCommunity.setVisibility(View.GONE);

        if (isAdded())
            addNewCommunity.setText(activity.getResources().getString(R.string.add_new_community));

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);


        return view;
    }

    public void loadCommunities(int offset) {
        try {
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();

            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            String uid = getUser() == null ? "" : getUser().id;
            String utoken = getUser() == null ? "" : getUser().user_token;

            projectJson.put("user_id", uid);
            projectJson.put("user_token", utoken);
            projectJson.put("community_id", "");
            projectJson.put("offset", offset);

            GetCommunitiesAPI mAuthTask = new GetCommunitiesAPI(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewCommunity:
                if (getUser() != null) {
                    startActivity(new Intent(activity, AddNewCommunityActivity.class));
                } else {
                    startActivity(new Intent(activity, LoginScreenActivity.class));
                }
                break;

            case R.id.communityPic:
                intent = new Intent(activity, CommunityDetailActivity.class);
                startActivity(intent);
                break;

            default:
                break;

        }
    }

    public class CommunityAdapter extends BaseAdapter {

        Context context;
        List<GetCommunities> rowItems;

        public CommunityAdapter(Context context, List<GetCommunities> items) {
            this.context = context;
            this.rowItems = items;
        }

        private class ViewHolder {
            ImageView image;
            TextView name;
            TextView description;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.new_community_fragment, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.communityName);
                holder.description = (TextView) convertView.findViewById(R.id.communityDesc);
                holder.image = (ImageView) convertView.findViewById(R.id.communityPic);

                holder.image.setScaleType(ImageView.ScaleType.FIT_XY);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            GetCommunities rowItem = (GetCommunities) getItem(position);
            //holder.name.setText(rowItem.name_eng);
            if (isArabic())
                if (TextUtils.isNullOrEmpty(rowItem.name_ara))
                    holder.name.setText(rowItem.name_eng);
                else
                    holder.name.setText(rowItem.name_ara);
            else
                holder.name.setText(rowItem.name_eng);

            holder.description.setText(rowItem.description);

            holder.image.setTag(position);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent communityDetails = new Intent(activity, CommunityDetailActivity.class);
                    communityDetails.putExtra("community_object", communitiesList.get(position));
                    startActivity(communityDetails);
                }
            });

            displayImage(context, holder.image, rowItem.image_url, ContextCompat.getDrawable(activity, R.drawable.server_error_placeholder));

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


    public class GetCommunitiesAPI extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetCommunitiesAPI(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // showLoading();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_COMMUNITIES, nameValuePairs);
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
                            if (!jsonArray.optJSONObject(i).has("nextOffset")) {
                                GetCommunities data = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), GetCommunities.class);
                                communitiesList.add(data);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        addNewCommunity.setVisibility(View.VISIBLE);
                    } else {
                        switch (msg) {
                            case "Data Not Found":
                                noItem.setVisibility(View.VISIBLE);
                                if (isAdded())
                                    noItem.setText(getString(R.string.no_records));
                                addNewCommunity.setVisibility(View.GONE);

                                if (!isTabletDevice()) {
                                    listView.setVisibility(View.GONE);
                                } else {
                                    communityListView.setVisibility(View.GONE);

                                }
                                // showDataNotFoundDialog();
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
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }
}
