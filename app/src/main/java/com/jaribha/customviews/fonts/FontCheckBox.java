package com.jaribha.customviews.fonts;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.CheckBox;

import com.jaribha.customviews.fonts.utilities.TypefaceLoader;


public class FontCheckBox extends CheckBox {
    private TypefaceLoader typefaceLoader;
    public FontCheckBox(Context context, AttributeSet attrs) {
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
