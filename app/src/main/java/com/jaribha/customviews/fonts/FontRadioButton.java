package com.jaribha.customviews.fonts;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.Button;
import android.widget.RadioButton;

import com.jaribha.customviews.fonts.utilities.TypefaceLoader;
import com.jaribha.customviews.fonts.utilities.Typefaceable;


public class FontRadioButton extends RadioButton implements Typefaceable {
    private TypefaceLoader typefaceLoader;
    public FontRadioButton(Context context, AttributeSet attrs) {
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
