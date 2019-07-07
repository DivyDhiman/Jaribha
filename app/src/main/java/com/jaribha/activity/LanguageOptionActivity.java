package com.jaribha.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Utils;

public class LanguageOptionActivity extends BaseAppCompatActivity implements View.OnClickListener {

    ImageView btnEnglish, btnArabic, back;

    TextView txtChooseYourLanguage, orText;

    Button btnContinue;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_option);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        orText = (TextView) findViewById(R.id.orTv);

        btnEnglish = (ImageView) findViewById(R.id.btnEnglish);
        btnEnglish.setOnClickListener(this);

        btnArabic = (ImageView) findViewById(R.id.btnArabic);
        btnArabic.setOnClickListener(this);

        back = (ImageView) findViewById(R.id.back);
        back.setImageResource(R.drawable.btn_back);
        back.setOnClickListener(this);

        txtChooseYourLanguage = (TextView) findViewById(R.id.txtChooseYourLanguage);
        txtChooseYourLanguage.setText(getString(R.string.choose_your_language));
        txtChooseYourLanguage.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtChooseYourLanguage.setOnClickListener(this);

        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setText(getString(R.string.continue_to_explainer_video));
        btnContinue.setOnClickListener(this);

        if (isArabic()) {
            btnEnglish.setImageResource(R.drawable.english_language_screen_unselect);
            btnArabic.setImageResource(R.drawable.btn_arabic_language_screen);
        } else {
            btnEnglish.setImageResource(R.drawable.btn_eng_language_screen);
            btnArabic.setImageResource(R.drawable.arabic_language_screen_unselect);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back:
                Intent intent = new Intent(LanguageOptionActivity.this, AdvertisementActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                finish();
                break;

            case R.id.btnEnglish:
                btnEnglish.setImageResource(R.drawable.btn_eng_language_screen);
                btnArabic.setImageResource(R.drawable.arabic_language_screen_unselect);
                JaribhaPrefrence.setPref(this, Constants.PROJECT_LANGUAGE, "en");
                Utils.changeLangauge("en", this);
                txtChooseYourLanguage.setText(getString(R.string.choose_your_language));
                btnContinue.setText(getString(R.string.continue_to_explainer_video));
                orText.setText(getString(R.string.or_caps));
                break;
            case R.id.btnArabic:
                Utils.changeLangauge("ar", this);
                JaribhaPrefrence.setPref(this, Constants.PROJECT_LANGUAGE, "ar");
                btnEnglish.setImageResource(R.drawable.english_language_screen_unselect);
                btnArabic.setImageResource(R.drawable.btn_arabic_language_screen);

                txtChooseYourLanguage.setText(getString(R.string.choose_your_language));
                btnContinue.setText(getString(R.string.continue_to_explainer_video));
                orText.setText(getString(R.string.or_caps));
                break;
            case R.id.btnContinue:
                Utils.setDefaultValues(this);

                intent = new Intent(LanguageOptionActivity.this, TutorialVideoActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
                break;

            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(LanguageOptionActivity.this, AdvertisementActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }
}
