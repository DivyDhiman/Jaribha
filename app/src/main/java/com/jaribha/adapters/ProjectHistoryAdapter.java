package com.jaribha.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.fonts.FontTextView;
import com.jaribha.models.ProjectHistoryBean;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Utils;

import java.util.List;

public class ProjectHistoryAdapter extends ArrayAdapter<ProjectHistoryBean> {

    public ProjectHistoryAdapter(Context context, List<ProjectHistoryBean> objects) {
        super(context, R.layout.item_history_sponsor, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_history_sponsor, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.transaction_id = (FontTextView) convertView.findViewById(R.id.transaction_id);
            viewHolder.date_time = (FontTextView) convertView.findViewById(R.id.date_time);
            viewHolder.title = (FontTextView) convertView.findViewById(R.id.title);
            viewHolder.by_name = (FontTextView) convertView.findViewById(R.id.by_name);
            viewHolder.pay_type = (FontTextView) convertView.findViewById(R.id.pay_type);
            viewHolder.reward_detail = (FontTextView) convertView.findViewById(R.id.reward_detail);
            viewHolder.currency_rate = (FontTextView) convertView.findViewById(R.id.currency_rate);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ProjectHistoryBean projectHistoryBean = getItem(position);

        viewHolder.transaction_id.setText(projectHistoryBean.tracking_id);

        String date = Utils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", projectHistoryBean.created);
        String time = Utils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "hh:mm a", projectHistoryBean.created);
        //String time = Utils.formateDateFromstring(getContext(), "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", projectHistoryBean.created);
        viewHolder.date_time.setText(String.format("%s %s %s", date, getContext().getString(R.string.at), time));

        viewHolder.title.setText(projectHistoryBean.title);

        viewHolder.by_name.setText(getContext().getString(R.string.by) + projectHistoryBean.name);

        viewHolder.pay_type.setText(projectHistoryBean.pay_type);

        viewHolder.reward_detail.setText(projectHistoryBean.description);

        viewHolder.currency_rate.setText(String.format("$%s", projectHistoryBean.support_amount_usd));

        if (TextUtils.isNullOrEmpty(projectHistoryBean.image)) {
            viewHolder.image.setImageResource(R.drawable.server_error_placeholder);
        } else {
            ((BaseAppCompatActivity) getContext()).displayImage(getContext(), viewHolder.image, projectHistoryBean.image,
                    ContextCompat.getDrawable(getContext(), R.drawable.server_error_placeholder));
        }

        return convertView;
    }

    class ViewHolder {
        FontTextView transaction_id;
        FontTextView date_time;
        FontTextView title;
        FontTextView by_name;
        FontTextView pay_type;
        FontTextView reward_detail;
        FontTextView currency_rate;
        ImageView image;
    }
}
