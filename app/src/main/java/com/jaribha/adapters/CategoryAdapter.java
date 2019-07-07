package com.jaribha.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.Category;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {

    public CategoryAdapter(Context context, List<Category> objects) {
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

        Category countryBean = getItem(position);

        if (((BaseAppCompatActivity) getContext()).isArabic()) {
            viewHolder.textView.setText(countryBean.name_ara);
        } else {
            viewHolder.textView.setText(countryBean.name_eng);
        }
        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
