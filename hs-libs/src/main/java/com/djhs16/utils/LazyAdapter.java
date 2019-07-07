package com.djhs16.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.djhs16.image.ImageLoader;

public class LazyAdapter extends BaseAdapter {

	private Activity activity;
	private String[] data;
	public ImageLoader imageLoader;

	public LazyAdapter(Activity a, String[] d) {
		activity = a;
		data = d;
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return data.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
//		if (convertView == null)
//			vi = inflater.inflate(R.layout.item, null);
//
//		TextView text = (TextView) vi.findViewById(R.id.text);
//		ImageView image = (ImageView) vi.findViewById(R.id.image);
//		text.setText("item " + position);
//		imageLoader.DisplayImage(data[position], image);
		return vi;
	}
}