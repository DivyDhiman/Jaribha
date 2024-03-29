package com.jaribha.customviews.fonts;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.Switch;

import com.jaribha.customviews.fonts.utilities.TypefaceLoader;
import com.jaribha.customviews.fonts.utilities.Typefaceable;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class FontSwitch extends Switch implements Typefaceable {
    private TypefaceLoader typefaceLoader;
    public FontSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        typefaceLoader = TypefaceLoader.get(this, context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        Pair<CharSequence, BufferType> pair = TypefaceLoader.inject(typefaceLoader, text, type);
        super.setText(pair.first, pair.second);
    }

    public void setFont(String font) {
        typefaceLoader.setFont(font);
    }

    public void setFont(@StringRes int font) {
        typefaceLoader.setFont(getResources().getString(font));
    }
}
