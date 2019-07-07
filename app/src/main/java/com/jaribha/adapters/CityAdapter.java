package com.jaribha.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.models.CityBean;

import java.util.List;

public class CityAdapter extends ArrayAdapter<CityBean> {

    public CityAdapter(Context context, List<CityBean> objects) {
        super(context, R.layout.item_textview, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_textview, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CityBean countryBean = getItem(position);

        viewHolder.textView.setText(countryBean.name_eng);

        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
