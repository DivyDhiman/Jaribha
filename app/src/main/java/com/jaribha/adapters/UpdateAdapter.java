package com.jaribha.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.models.ProjectUpdates;
import com.jaribha.utility.TimeConstants;
import com.jaribha.utility.Utils;

import java.util.ArrayList;
import java.util.Date;

public class UpdateAdapter extends BaseAdapter {

    private Context ctx;

    private ArrayList<ProjectUpdates> dataList;

    public UpdateAdapter(Context context, ArrayList<ProjectUpdates> data) {
        this.ctx = context;
        this.dataList = data;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public ProjectUpdates getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.update_list_item, null);

        TextView updateDesc = (TextView) view.findViewById(R.id.updateDesc);
        updateDesc.setText(Html.fromHtml(dataList.get(position).description));

        TextView updateTime = (TextView) view.findViewById(R.id.updateTime);
        Date time = Utils.getStringToDate(ctx, dataList.get(position).created);

        updateTime.setText(TimeConstants.getJodaTimeAgo(time, ctx));
//        updateTime.setText(Utils.ConvertDateTimeToTimeStamp(ctx, dataList.get(position).created));

        TextView updateTitle = (TextView) view.findViewById(R.id.updateTitle);
        updateTitle.setText(ctx.getResources().getString(R.string.update) + "#" + (position + 1) + "");

        return view;
    }
}

