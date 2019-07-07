package com.djhs16;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.djhs16.image.ImageCacheManager;
import com.djhs16.net.toolbox.MultiPartStack;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

public class HSLibs {

    private static final int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 10;
    private static final CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
    private static final int DISK_IMAGECACHE_QUALITY = 100;

    //    private static RequestQueue mRequestQueue;
    private static RequestQueue mRequestQueueMultipart;
    private static ImageLoader mImageLoader;
    private static DefaultHttpClient httpClient;

    private HSLibs() {
    }

    public static void init(Context context) {
//        mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueueMultipart = Volley.newRequestQueue(context, new MultiPartStack());

        // int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        // int cacheSize = 1024 * 1024 * memClass / 8;

        ImageCacheManager cacheManager = ImageCacheManager.getInstance();
        cacheManager.init(context, context.getPackageCodePath(), DISK_IMAGECACHE_SIZE, DISK_IMAGECACHE_COMPRESS_FORMAT, DISK_IMAGECACHE_QUALITY);
        mImageLoader = new ImageLoader(mRequestQueueMultipart, cacheManager);

        httpClient = new DefaultHttpClient();
        ClientConnectionManager manager = httpClient.getConnectionManager();
        HttpParams params = httpClient.getParams();
        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, manager.getSchemeRegistry()), params);
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueueMultipart != null) {
            return mRequestQueueMultipart;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    /**
     * Returns instance of ImageLoader initialized with {@see FakeImageCache} which effectively means that no memory caching is used. This is useful for images that you know that will be show only once.
     *
     * @return
     */
    public static ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }

    public static DefaultHttpClient getHttpClient() {
        return HSLibs.httpClient;
    }

    public static void setHttpClient(DefaultHttpClient httpClient) {
        HSLibs.httpClient = httpClient;
    }

}
