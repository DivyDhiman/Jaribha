package com.jaribha.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.CountryBean;

import java.util.List;

public class CountryAdapter extends ArrayAdapter<CountryBean> {

    public CountryAdapter(Context context, List<CountryBean> objects) {
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

        CountryBean countryBean = getItem(position);

        if (!((BaseAppCompatActivity) getContext()).isArabic())
            viewHolder.textView.setText(countryBean.name_eng);
        else
            viewHolder.textView.setText(countryBean.name_ara);
        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
