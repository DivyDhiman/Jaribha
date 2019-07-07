package com.djhs16.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class SizeUtils {
	@SuppressWarnings({ "deprecation", "unused" })
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public static Point getScreenSize(Context context) {

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		Point size = new Point();

		if (VersionUtils.hasHoneycombMR2()) {
			display.getSize(size);
			int width = size.x;
			int height = size.y;
		} else {
			int width = display.getWidth();
			int height = display.getHeight();
			size.x = width;
			size.y = height;
		}
		return size;
	}

	public static int getScreenWidth(Context context) {
		return SizeUtils.getScreenSize(context).x;
	}

	public static int getScreenHeight(Context context) {
		return SizeUtils.getScreenSize(context).y;
	}

	public static int getResourceWidth(Context context, int resId) {
		BitmapFactory.Options dimensions = new BitmapFactory.Options();
		dimensions.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), resId, dimensions);
		// int height = dimensions.outHeight;
		int width = dimensions.outWidth;

		return width;
	}
}
