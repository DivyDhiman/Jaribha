package com.jaribha.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.NavDrawerItem;

import java.util.ArrayList;

public class MenuAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<NavDrawerItem> navDrawerItems;

    TypedArray navMenuIconsTopList, navMenuIconsBottomList;

    private Boolean isTopList;

    public MenuAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems, Boolean isTop) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        this.isTopList = isTop;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.menu_items, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

        navMenuIconsBottomList = context.getResources().obtainTypedArray(R.array.nav_drawer_icons_bottom_unselected);
        navMenuIconsTopList = context.getResources().obtainTypedArray(R.array.nav_drawer_icons_top_unselected);

        boolean tabletSize = context.getResources().getBoolean(R.bool.isTablet);

        txtTitle.setText(navDrawerItems.get(position).getTitle());

        if (navDrawerItems.get(position).getTitle().equalsIgnoreCase(context.getString(R.string.rewards)) && !tabletSize)
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        if (navDrawerItems.get(position).getTitle().equalsIgnoreCase(context.getString(R.string.guidelines)) && !tabletSize)
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        if (navDrawerItems.get(position).getTitle().equalsIgnoreCase(context.getString(R.string.contact_us)) && !tabletSize)
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        if (navDrawerItems.get(position).getTitle().equalsIgnoreCase(context.getString(R.string.subscribe_to_newsletter)) && !tabletSize)
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

        // displaying count
        // check whether it set visible or not
        if (((BaseAppCompatActivity) context).getUser() != null) {
            if (navDrawerItems.get(position).getCounterVisibility()) {
                txtCount.setText(navDrawerItems.get(position).getCount());
            } else {
                // hide the counter view
                txtCount.setVisibility(View.GONE);
            }
        } else {
            txtCount.setVisibility(View.GONE);
        }

        if (navDrawerItems.get(position).isSelected()) {
            txtTitle.setTextColor(ContextCompat.getColor(context, R.color.menu_text_color));
            if (isTopList) {
                imgIcon.setImageResource(navMenuIconsTopList.getResourceId(position, -1));
            } else {
                imgIcon.setImageResource(navMenuIconsBottomList.getResourceId(position, -1));
            }
        } else {
            txtTitle.setTextColor(ContextCompat.getColor(context, R.color.white));
            imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
        }

        return convertView;
    }

}
