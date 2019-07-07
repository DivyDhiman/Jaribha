package com.djhs16.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.djhs16.utils.TextUtils;

public class TwoStageImageCache implements com.android.volley.toolbox.ImageLoader.ImageCache {
	private static TwoStageImageCache instance = null;
	private Context mContext;
	private final Bitmap.CompressFormat a = Bitmap.CompressFormat.PNG;
	private LruCache<String, Bitmap> e;
	private DiskLruImageCache f;

	private TwoStageImageCache(Context context) {
		mContext = context;

		if (this.e == null) {
			// this.e = new s(this, 1048576 * ((ActivityManager) mContext.getSystemService("activity")).getMemoryClass() / 8);
		}

		this.f = new DiskLruImageCache(context, "data/fka/images", 15728640, this.a, 80);
	}

	private String a(String paramString) {
		if ((paramString != null) && (paramString.length() != 0))
			return String.valueOf(paramString.trim().hashCode());
		return null;
	}

	private String b(String paramString) {
		String str = paramString.substring(1 + paramString.lastIndexOf("."));
		if (str == null)
			str = "";
		return str;
	}

	public static TwoStageImageCache getInstance(Context context) {
		if (instance == null)
			instance = new TwoStageImageCache(context);
		return instance;
	}

	public Bitmap getBitmap(String paramString) {
		// try {
		// String str = a(paramString);
		// boolean bool = TextUtils.isNullOrEmpty(str);
		// Bitmap localObject2 = null;
		// if (!bool) {
		// localObject2 = (Bitmap) this.e.get(str);
		// if (localObject2 == null) {
		// Bitmap localBitmap = this.f.getBitmap(str);
		// localObject2 = localBitmap;
		// }
		// }
		// return localObject2;
		// } finally {
		// }
		return null;
	}

	public void putBitmap(String paramString, Bitmap paramBitmap) {
		// try
		// {
		// String str = a(paramString);
		// this.e.put(str, paramBitmap);
		// this.f.put(str, paramBitmap, b(paramString));
		// return;
		// }
		// finally
		// {
		// localObject = finally;
		// throw localObject;
		// }
	}
}
