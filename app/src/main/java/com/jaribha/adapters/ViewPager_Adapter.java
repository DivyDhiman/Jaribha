package com.jaribha.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jaribha.R;

public class ViewPager_Adapter extends PagerAdapter {

    private LayoutInflater layoutInflater;

    public ViewPager_Adapter(Context context) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.pager_item, container, false);

        // ImageView imageView = (ImageView) itemView.findViewById(R.id.pagerItem);
        //imageView.setImageResource(mResources[position]);

        container.addView(itemView);

        return itemView;
    }
}
