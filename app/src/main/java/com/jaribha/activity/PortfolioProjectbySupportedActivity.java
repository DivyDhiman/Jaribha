package com.jaribha.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.jaribha.adapters.PortfolioProjectbySupportedActivityAdapter;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.models.GetProjects;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;

import java.util.ArrayList;

public class PortfolioProjectbySupportedActivity extends BaseAppCompatActivity implements View.OnClickListener {

    ImageView iv_close, searchImg;

    TextView tv_title;

    BezelImageView img_user_image;

    ListView projectListView;

    GridView projectGrid;

    private PortfolioProjectbySupportedActivityAdapter adapter;

    private ArrayList<GetProjects> supportersDataArrayList = new ArrayList<>();

    ArrayList<GetProjects> tempList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_projectby_supported);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        searchImg = (ImageView) findViewById(R.id.iv_search);
        searchImg.setVisibility(View.VISIBLE);
        searchImg.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.supported1));

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setOnClickListener(this);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.GONE);
        displayUserImage(img_user_image);
//        iv_share = (ImageView) findViewById(R.id.iv_share);
        Intent intent = getIntent();
        supportersDataArrayList = (ArrayList<GetProjects>) intent.getSerializableExtra(Constants.DATA);

        adapter = new PortfolioProjectbySupportedActivityAdapter(getActivity(), supportersDataArrayList);

        boolean tabletSize = getActivity().getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            projectGrid = (GridView) findViewById(R.id.projectsGrid);
            projectGrid.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            projectGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ProjectDetailsTabs.class);
                    intent.putExtra("detail_object", supportersDataArrayList.get(position));
                    JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_TITLE, supportersDataArrayList.get(position).title);
                    JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_ID, supportersDataArrayList.get(position).id);
                    startActivity(intent);
                }
            });
        } else {
            projectListView = (ListView) findViewById(R.id.projectsList);
            projectListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ProjectDetailsTabs.class);
                    intent.putExtra("detail_object", supportersDataArrayList.get(position));
                    JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_TITLE, supportersDataArrayList.get(position).title);
                    JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_ID, supportersDataArrayList.get(position).id);
                    startActivity(intent);
                }
            });

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_close:
                finish();
                break;
            case R.id.iv_search:
                initiatePopupWindow();
                break;

            default:
                break;
        }
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
                        JaribhaPrefrence.setPref(PortfolioProjectbySupportedActivity.this, Constants.SEARCH_TEXT, s.toString().trim());
                    } else {
                        JaribhaPrefrence.setPref(PortfolioProjectbySupportedActivity.this, Constants.SEARCH_TEXT, "");
                        adapter = new PortfolioProjectbySupportedActivityAdapter(getActivity(), supportersDataArrayList);
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
                    Utils.hideKeyboard(getActivity());
                    adapter = new PortfolioProjectbySupportedActivityAdapter(getActivity(), supportersDataArrayList);
                    if(!isTablet())
                        projectListView.setAdapter(adapter);
                    else
                        projectGrid.setAdapter(adapter);
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

