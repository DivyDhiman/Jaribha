package com.jaribha.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.models.ActivityData;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.TimeConstants;
import com.jaribha.utility.Utils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class OtherActivityAdapter extends ArrayAdapter<ActivityData> {

    public OtherActivityAdapter(Context context, List<ActivityData> objects) {
        super(context, R.layout.portfolio_projectby_activity_items, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.portfolio_projectby_activity_items, parent, false);

            holder.tv_header = (TextView) convertView.findViewById(R.id.tv_header);
            holder.tv_title_portfolio = (TextView) convertView.findViewById(R.id.tv_title_portfolio);
            holder.tv_cost_portfolio = (TextView) convertView.findViewById(R.id.tv_cost_portfolio);
            holder.tv_time_portfolio = (TextView) convertView.findViewById(R.id.tv_time_portfolio);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ActivityData activityData = getItem(position);
        holder.tv_title_portfolio.setText(activityData.title);
        double amount = Double.parseDouble(activityData.support_amount);
        holder.tv_cost_portfolio.setText("$" + new DecimalFormat("##.##").format(amount));
        if (!TextUtils.isNullOrEmpty(activityData.created)) {
            Date time = Utils.getStringToDate(getContext(), activityData.created);
            holder.tv_time_portfolio.setText(TimeConstants.getJodaTimeAgo(time, getContext()));
        }

        return convertView;
    }

    class ViewHolder {
        TextView tv_header;
        TextView tv_title_portfolio;
        TextView tv_cost_portfolio;
        TextView tv_time_portfolio;
    }
}
