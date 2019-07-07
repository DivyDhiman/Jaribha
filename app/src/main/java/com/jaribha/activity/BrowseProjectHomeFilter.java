package com.jaribha.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.models.FeaturedFilterBean;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Utils;

import java.util.ArrayList;

public class BrowseProjectHomeFilter extends BaseAppCompatActivity implements View.OnClickListener {

    private ImageView iv_close;

    private TextView tv_title, tv_apply;

    private BezelImageView img_user_image;

    private ListView list_filter_featured;

    private ArrayList<FeaturedFilterBean> list = new ArrayList<>();

    private FeaturedCategoryAdapter adapter;

    FeaturedFilterBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_project_home_filter);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        String[] filter = new String[]{
                getString(R.string.recently_launched),
                getString(R.string.most_popular),
                getString(R.string.ending_soon),
                getString(R.string.most_supported),
        };

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.GONE);
        img_user_image.setOnClickListener(this);

        displayUserImage(img_user_image);

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.close_icon);
        iv_close.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.filter));

        tv_apply = (TextView) findViewById(R.id.tv_apply);
        tv_apply.setVisibility(View.VISIBLE);
        tv_apply.setOnClickListener(this);
        tv_apply.setText(getString(R.string.apply));

        for (String aFilter : filter) {
            list.add(new FeaturedFilterBean(aFilter));
        }

//        bean = list.get(0);

        for (int i = 0; i < list.size(); i++) {
            //list.get(0).setSelected(true);
            if (JaribhaPrefrence.getPref(this, Constants.FEATURE, "").equals(list.get(i).getFilterText())) {
                list.get(i).setSelected(true);
            }
        }

        adapter = new FeaturedCategoryAdapter(getActivity(), list);
        list_filter_featured = (ListView) findViewById(R.id.list_filter_featured);
        list_filter_featured.setAdapter(adapter);

        list_filter_featured.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bean = list.get(position);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_close:
                setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.tv_apply:
                if (!TextUtils.isNull(bean)) {
                    if (bean.getFilterText().equals(getString(R.string.most_popular))) {
                        JaribhaPrefrence.setPref(getActivity(), Constants.FEATURE_ID, "mostpopular");
                    } else if (bean.getFilterText().equals(getString(R.string.recently_launched))) {
                        JaribhaPrefrence.setPref(getActivity(), Constants.FEATURE_ID, "recentlylaunched");
                    } else if (bean.getFilterText().equals(getString(R.string.ending_soon))) {
                        JaribhaPrefrence.setPref(getActivity(), Constants.FEATURE_ID, "endingsoon");
                    } else if (bean.getFilterText().equals(getString(R.string.most_supported))) {
                        JaribhaPrefrence.setPref(getActivity(), Constants.FEATURE_ID, "mostsupported");
                    }
                    JaribhaPrefrence.setPref(getActivity(), Constants.FEATURE, bean.getFilterText());
                    setResult(RESULT_OK);
                    finish();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                break;

            case R.id.img_user_image:
                startActivity(new Intent(getActivity(), MyPortfolioActivity.class));
                break;

            default:
                break;

        }
    }

    public class FeaturedCategoryAdapter extends ArrayAdapter<FeaturedFilterBean> {

        public FeaturedCategoryAdapter(Context context, ArrayList<FeaturedFilterBean> beans) {
            super(context, R.layout.item_featured_filter, beans);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_featured_filter, parent, false);

                holder.img_checked = (ImageView) convertView.findViewById(R.id.img_checked);
                holder.img_checked.setVisibility(View.INVISIBLE);

                holder.tv_filter = (TextView) convertView.findViewById(R.id.tv_featured_filter);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            FeaturedFilterBean mBean = getItem(position);

            holder.tv_filter.setText(mBean.getFilterText());

            if (mBean.isSelected()) {
                holder.img_checked.setVisibility(View.VISIBLE);
                holder.tv_filter.setTypeface(Utils.getRegularFont(getActivity()), Typeface.BOLD);
            } else {
                holder.img_checked.setVisibility(View.INVISIBLE);
                holder.tv_filter.setTypeface(Utils.getRegularFont(getActivity()), Typeface.NORMAL);
            }

            return convertView;
        }

        class ViewHolder {
            ImageView img_checked;
            TextView tv_filter;
        }
    }
}
