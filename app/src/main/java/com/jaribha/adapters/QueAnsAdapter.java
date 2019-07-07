package com.jaribha.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.models.AddFAQ;

import java.util.List;

public class QueAnsAdapter extends ArrayAdapter<AddFAQ> {

    public QueAnsAdapter(Context context, List<AddFAQ> objects) {
        super(context, R.layout.item_que_ans, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_que_ans, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.tv_ans = (TextView) convertView.findViewById(R.id.tv_ans);

            viewHolder.tv_que = (TextView) convertView.findViewById(R.id.tv_que);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final AddFAQ addFAQ = getItem(position);

        viewHolder.tv_que.setTextColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
        viewHolder.tv_que.setText(addFAQ.question);

        viewHolder.tv_ans.setText(addFAQ.answer);

        return convertView;
    }

    class ViewHolder {
        TextView tv_que, tv_ans;
    }
}
