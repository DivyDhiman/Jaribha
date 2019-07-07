package com.jaribha.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.activity.SponserDetailActivity;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.Sponsors;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;

import java.util.ArrayList;

public class SponsorListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Sponsors> data;

    public SponsorListAdapter(Context c, ArrayList<Sponsors> list) {
        this.context = c;
        this.data = list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Sponsors getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.detail_sponsorlist_item, parent, false);
            viewHolder.desc = (TextView) convertView.findViewById(R.id.sponsorDesc);
            viewHolder.title = (TextView) convertView.findViewById(R.id.sponsorTitle);
            viewHolder.moreInfo = (TextView) convertView.findViewById(R.id.sponsorInfo);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.sponsorImage);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Sponsors item = getItem(position);
        viewHolder.desc.setText(item.sponsorbio);
        viewHolder.title.setText(item.sponsorname);
        viewHolder.moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SponserDetailActivity.class);
                JaribhaPrefrence.setPref(context, Constants.SPONSOR_ID, getItem(position).sponsor_id);
                context.startActivity(intent);
            }
        });

        if (!TextUtils.isNullOrEmpty(item.pictureurl))
        ((BaseAppCompatActivity) context).displayImage(context, viewHolder.img, item.pictureurl, ContextCompat.getDrawable(context, R.drawable.server_error_placeholder));

        return convertView;
    }

    class ViewHolder {
        TextView moreInfo;
        TextView desc;
        TextView title;
        ImageView img;
    }
}
