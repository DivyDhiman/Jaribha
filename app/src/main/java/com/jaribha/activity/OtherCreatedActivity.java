package com.jaribha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.adapters.OtherCreatedProjectsAdapter;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.models.GetProjects;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;

import java.util.ArrayList;

public class OtherCreatedActivity extends BaseAppCompatActivity implements View.OnClickListener {

    ImageView iv_close, iv_share, searchImg;

    TextView tv_title;

    BezelImageView img_user_image;

    ListView projectListView;

    GridView projectGrid;

    private OtherCreatedProjectsAdapter adapter;

    private ArrayList<GetProjects> creatorsDataArrayList = new ArrayList<>();

    ArrayList<GetProjects> tempList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_projectby_created);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        searchImg = (ImageView) findViewById(R.id.iv_search);
        searchImg.setVisibility(View.VISIBLE);
        searchImg.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.created));

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setOnClickListener(this);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.GONE);

        iv_share = (ImageView) findViewById(R.id.iv_share);
        iv_share.setVisibility(View.GONE);

        Intent intent = getIntent();
        creatorsDataArrayList = (ArrayList<GetProjects>) intent.getSerializableExtra(Constants.DATA);
//        tempList = (ArrayList<GetProjects>) intent.getSerializableExtra(Constants.DATA);
//
//        for (int i = 0; i < tempList.size(); i++) {
//
//            GetProjects data = tempList.get(i);
//
//            if (!TextUtils.isNullOrEmpty(data.total_support_amount) && !TextUtils.isNullOrEmpty(data.goal)) {
//
//                double supportAmt = Double.parseDouble(data.total_support_amount);
//                double goalAmt = Double.parseDouble(data.goal);
//
//                if (!TextUtils.isNullOrEmpty(data.enddate)) {
//                    try {
//                        int dayLeft = Utils.getDateDifference(data.enddate);
//                        if (dayLeft < 0) {
//                            data.period = "0";
//                        } else
//                            data.period = String.valueOf(dayLeft);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    if (!data.enddate.equals("0000-00-00") && Utils.isProjectExpired(data.enddate)) {
//                        if ((supportAmt < goalAmt)) {
//                            data.status = "complete";
//                        } else if (supportAmt >= goalAmt) {
//                            data.status = "supported";
//                        }
//                    }
//                }
//            }
//            creatorsDataArrayList.add(data);
//
//        }

        adapter = new OtherCreatedProjectsAdapter(getActivity(), creatorsDataArrayList);

        if (isTablet()) {
            projectGrid = (GridView) findViewById(R.id.projectsGrid);

            projectGrid.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            projectGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //if (position == 0) {
                    Intent intent = new Intent(getActivity(), ProjectDetailsTabs.class);
                    intent.putExtra("detail_object", creatorsDataArrayList.get(position));
                    JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_TITLE, creatorsDataArrayList.get(position).title);
                    JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_ID, creatorsDataArrayList.get(position).id);
                    startActivity(intent);
                    //}
                }
            });
        } else {
            projectListView = (ListView) findViewById(R.id.projectsList);
            //adapter = new PopularProjectsAdapter(getActivity(), creatorsDataArrayList);
            projectListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //if (position == 0) {
                    Intent intent = new Intent(getActivity(), ProjectDetailsTabs.class);
                    intent.putExtra("detail_object", creatorsDataArrayList.get(position));
                    JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_TITLE, creatorsDataArrayList.get(position).title);
                    JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_ID, creatorsDataArrayList.get(position).id);
                    startActivity(intent);
                    //}
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_close:
                onBackPressed();
                break;

            case R.id.iv_search:
                initiatePopupWindow();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        Log.d("intentPos", "" + adapter.listPosition);
        if (adapter.listPosition.size() != 0) {
            returnIntent.putExtra("result", adapter.listPosition);
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        finish();
    }

    private PopupWindow pwindo;

    private void initiatePopupWindow() {
        try {
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.search_popup,
                    (ViewGroup) findViewById(R.id.popupElement));
            pwindo = new PopupWindow(this);
            pwindo.setContentView(layout);
            pwindo.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            pwindo.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            pwindo.setBackgroundDrawable(new BitmapDrawable());
            pwindo.setFocusable(true);
            pwindo.setOutsideTouchable(false);
            pwindo.showAtLocation(layout, Gravity.TOP, 0, 150);
            // Removes default background.
            final EditText btnClosePopup = (EditText) layout.findViewById(R.id.searchEt);
            btnClosePopup.setText(JaribhaPrefrence.getPref(this, Constants.SEARCH_TEXT, ""));
            ImageView searchIv = (ImageView) layout.findViewById(R.id.searchIv);

            searchIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String searchStr = btnClosePopup.getText().toString().trim();
                    if (TextUtils.isNullOrEmpty(searchStr)) {
                        //pwindo.dismiss();
                        showToast(getString(R.string.error_field_required));
                    }
                    pwindo.dismiss();

                }
            });
            btnClosePopup.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (s.length() != 0) {
                        adapter.getFilter().filter(s);
                        JaribhaPrefrence.setPref(OtherCreatedActivity.this, Constants.SEARCH_TEXT, s.toString().trim());
                    } else {
                        JaribhaPrefrence.setPref(getActivity(), Constants.SEARCH_TEXT, "");
                        adapter = new OtherCreatedProjectsAdapter(getActivity(), creatorsDataArrayList);
                        if (!isTablet())
                            projectListView.setAdapter(adapter);
                        else
                            projectGrid.setAdapter(adapter);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

           /* pwindo.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    adapter = new OtherCreatedProjectsAdapter(getActivity(), creatorsDataArrayList);
                    if(!isTablet())
                        projectListView.setAdapter(adapter);
                    else
                        projectGrid.setAdapter(adapter);
                }
            });
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
