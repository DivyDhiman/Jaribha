package com.jaribha.utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;

import com.squareup.picasso.Transformation;


public class ColorTransformation implements Transformation {

    private int color = 0;

    public ColorTransformation() {

    }

    public ColorTransformation(int color) {
        setColor(color);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setColorFromRes(Context context, int colorResId) {
        setColor(ContextCompat.getColor(context, colorResId));
    }

    public int getColor() {
        return color;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        if (color == 0) {
            return source;
        }

        BitmapDrawable drawable = new BitmapDrawable(Resources.getSystem(), source);
        Bitmap result = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        drawable.draw(canvas);
        drawable.setColorFilter(null);
        drawable.setCallback(null);

        if (result != source) {
            source.recycle();
        }

        return result;
    }

    @Override
    public String key() {
        return "DrawableColor:" + color;
    }
}
