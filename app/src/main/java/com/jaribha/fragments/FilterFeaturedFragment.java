package com.jaribha.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseFragment;
import com.jaribha.models.FeaturedFilterBean;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Utils;

import java.util.ArrayList;

public class FilterFeaturedFragment extends BaseFragment implements View.OnClickListener {

    private ListView list_filter_featured;

    private ArrayList<FeaturedFilterBean> list = new ArrayList<>();

    private FeaturedCategoryAdapter adapter;

    private TextView tv_select_all, tv_featured_reset;

    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_featured_filter, container, false);
        String[] filter = new String[]{
                activity.getResources().getString(R.string.all_features),
                activity.getResources().getString(R.string.staff_picks),
                activity.getResources().getString(R.string.most_popular),
                activity.getResources().getString(R.string.recently),
                activity.getResources().getString(R.string.ending_soon),
                activity.getResources().getString(R.string.small_projects),
                activity.getResources().getString(R.string.most_supported),
                activity.getResources().getString(R.string.community)
        };

        for (String aFilter : filter) {
            list.add(new FeaturedFilterBean(aFilter));
        }

        tv_select_all = (TextView) view.findViewById(R.id.tv_select_all);
        tv_select_all.setOnClickListener(this);

        tv_featured_reset = (TextView) view.findViewById(R.id.tv_featured_reset);
        tv_featured_reset.setOnClickListener(this);

        adapter = new FeaturedCategoryAdapter(activity, list);
        list_filter_featured = (ListView) view.findViewById(R.id.list_filter_featured);
        list_filter_featured.setAdapter(adapter);
        list_filter_featured.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FeaturedFilterBean bean = list.get(position);
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setSelected(false);
                }
                if (bean.isSelected()) {
                    bean.setSelected(false);
                } else {

                    if (bean.getFilterText().equals(activity.getResources().getString(R.string.all_features))) {
                        JaribhaPrefrence.setPref(activity, Constants.FEATURE_ID_SEARCH, "");
                    } else if (bean.getFilterText().equals(activity.getResources().getString(R.string.staff_picks))) {
                        JaribhaPrefrence.setPref(activity, Constants.FEATURE_ID_SEARCH, "staffpicks");
                    } else if (bean.getFilterText().equals(activity.getResources().getString(R.string.most_popular))) {
                        JaribhaPrefrence.setPref(activity, Constants.FEATURE_ID_SEARCH, "mostpopular");
                    } else if (bean.getFilterText().equals(activity.getResources().getString(R.string.recently))) {
                        JaribhaPrefrence.setPref(activity, Constants.FEATURE_ID_SEARCH, "recentlylaunched");
                    } else if (bean.getFilterText().equals(activity.getResources().getString(R.string.ending_soon))) {
                        JaribhaPrefrence.setPref(activity, Constants.FEATURE_ID_SEARCH, "endingsoon");
                    } else if (bean.getFilterText().equals(activity.getResources().getString(R.string.small_projects))) {
                        JaribhaPrefrence.setPref(activity, Constants.FEATURE_ID_SEARCH, "smallproject");
                    } else if (bean.getFilterText().equals(activity.getResources().getString(R.string.most_supported))) {
                        JaribhaPrefrence.setPref(activity, Constants.FEATURE_ID_SEARCH, "mostsupported");
                    } else if (bean.getFilterText().equals(activity.getResources().getString(R.string.community))) {
                        JaribhaPrefrence.setPref(activity, Constants.FEATURE_ID_SEARCH, "community");
                    }
                    JaribhaPrefrence.setPref(activity, Constants.FEATURE_NAME, bean.getFilterText());
                    bean.setSelected(true);
                }
                adapter.notifyDataSetChanged();
            }
        });

        for (int i = 0; i < list.size(); i++) {

            if (!JaribhaPrefrence.getPref(activity, Constants.FEATURE_NAME, "").equals(activity.getResources().getString(R.string.all_features))) {
                if (list.get(i).getFilterText().equals(JaribhaPrefrence.getPref(activity, Constants.FEATURE_NAME, ""))) {
                    list.get(i).setSelected(true);
                }
            } else {
                list.get(0).setSelected(true);
            }


        }

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_featured_reset:
                for (int i = 0; i < list.size(); i++) {
                    FeaturedFilterBean bean = list.get(i);
                    bean.setSelected(false);
                }
                JaribhaPrefrence.setPref(activity, Constants.FEATURE_ID, "");
                JaribhaPrefrence.setPref(activity, Constants.FEATURE_NAME, getString(R.string.all_features));
                adapter.notifyDataSetChanged();
                break;

            case R.id.tv_select_all:
                for (int i = 0; i < list.size(); i++) {
                    FeaturedFilterBean bean = list.get(i);
                    bean.setSelected(true);
                }
                JaribhaPrefrence.setPref(activity, Constants.FEATURE_NAME, getString(R.string.all_features));
                JaribhaPrefrence.setPref(activity, Constants.FEATURE_ID, "");
                adapter.notifyDataSetChanged();
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
                holder.tv_filter.setTypeface(Utils.getBoldFont(getActivity()), Typeface.BOLD);
                holder.tv_filter.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_theme_color));
            } else {
                holder.img_checked.setVisibility(View.INVISIBLE);
                holder.tv_filter.setTypeface(Utils.getRegularFont(getActivity()), Typeface.NORMAL);
                holder.tv_filter.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_grey_500));
            }

            return convertView;
        }

        class ViewHolder {
            ImageView img_checked;
            TextView tv_filter;
        }
    }
}
