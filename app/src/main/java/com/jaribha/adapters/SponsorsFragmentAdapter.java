package com.jaribha.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.Sponsors;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Utils;

import java.util.ArrayList;

public class SponsorsFragmentAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Sponsors> data;

    public SponsorsFragmentAdapter(Context c, ArrayList<Sponsors> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.sponsors_fragment_item, parent, false);
            viewHolder.sponsTitle = (TextView) convertView.findViewById(R.id.sponsTitle);
            viewHolder.sponsDesc = (TextView) convertView.findViewById(R.id.sponsDesc);

            viewHolder.sponsorImg = (ImageView) convertView.findViewById(R.id.sponsorImg);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Sponsors item = getItem(position);

        if (context.getResources().getBoolean(R.bool.isTablet)) {
            viewHolder.sponsorImg.getLayoutParams().width = Utils.getDeviceWidth(context) / 2;
            viewHolder.sponsorImg.getLayoutParams().height = Utils.getDeviceWidth(context) / 4;
        }

        viewHolder.sponsTitle.setText(item.sponsorname);
        viewHolder.sponsDesc.setText(item.sponsorbio);
        if (!TextUtils.isNullOrEmpty(item.pictureurl))
            ((BaseAppCompatActivity) context).displayImage(context, viewHolder.sponsorImg, item.pictureurl, ContextCompat.getDrawable(context, R.drawable.server_error_placeholder));

        return convertView;
    }

    class ViewHolder {
        ImageView sponsorImg;
        TextView sponsTitle;
        TextView sponsDesc;
    }
}
