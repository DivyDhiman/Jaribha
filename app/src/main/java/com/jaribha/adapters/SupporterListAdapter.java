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
import com.jaribha.models.Supporters;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.TimeConstants;
import com.jaribha.utility.Utils;

import java.util.ArrayList;
import java.util.Date;

public class SupporterListAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<Supporters> data;

    public SupporterListAdapter(Context c, ArrayList<Supporters> list) {
        this.context = c;
        this.data = list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Supporters getItem(int position) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.supporter_list_item, parent, false);
            viewHolder.count = (TextView) convertView.findViewById(R.id.supportCount);
            viewHolder.title = (TextView) convertView.findViewById(R.id.supportName);
            viewHolder.time = (TextView) convertView.findViewById(R.id.supportTime);
            viewHolder.type = (TextView) convertView.findViewById(R.id.supportType);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.supportImage);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Supporters item = getItem(position);
        Date time = Utils.getStringToDate(context, item.created);
        viewHolder.time.setText(TimeConstants.getJodaTimeAgo(time, context));

        viewHolder.title.setText(item.supportername);

        viewHolder.type.setText("$" + item.total_support_amount + "\n " + context.getString(R.string.supporters));

        viewHolder.count.setText(item.support_project_count + " " + context.getString(R.string.projects_supported));

        if (!TextUtils.isNullOrEmpty(item.pictureurl))
            ((BaseAppCompatActivity) context).displayImage(context,
                    viewHolder.img,
                    item.pictureurl,
                    ContextCompat.getDrawable(context, R.drawable.user_icon));

        return convertView;
    }

    class ViewHolder {
        TextView time;
        TextView count;
        TextView type;
        TextView title;
        ImageView img;
    }
}
