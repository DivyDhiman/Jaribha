package com.djhs16.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * Implementation of volley's ImageCache interface. This manager tracks the application image loader and cache.
 * 
 * @author Himanshu Soni
 * 
 */
public class ImageCacheManager implements ImageCache {

	private static ImageCacheManager mInstance;

	/**
	 * Image cache used for local image storage
	 */
	private DiskLruImageCache mDiskCache;

	/**
	 * @return instance of the cache manager
	 */
	public static ImageCacheManager getInstance() {
		if (mInstance == null)
			mInstance = new ImageCacheManager();

		return mInstance;
	}

	/**
	 * Initializer for the manager. Must be called prior to use.
	 * 
	 * @param context
	 *            application context
	 * @param uniqueName
	 *            name for the cache location
	 * @param cacheSize
	 *            max size for the cache
	 * @param compressFormat
	 *            file type compression format.
	 * @param quality
	 */
	public void init(Context context, String uniqueName, int cacheSize, CompressFormat compressFormat, int quality) {
		mDiskCache = new DiskLruImageCache(context, uniqueName, cacheSize, compressFormat, quality);
	}

	@Override
	public Bitmap getBitmap(String url) {
		try {
			return mDiskCache.getBitmap(createKey(url));
		} catch (NullPointerException e) {
			throw new IllegalStateException("Disk Cache Not initialized");
		}
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		try {
			mDiskCache.put(createKey(url), bitmap);
		} catch (NullPointerException e) {
			throw new IllegalStateException("Disk Cache Not initialized");
		}
	}

	/**
	 * Creates a unique cache key based on a url value
	 * 
	 * @param url
	 *            url to be used in key creation
	 * @return cache key value
	 */
	private String createKey(String url) {
		return String.valueOf(url.hashCode());
	}

}
