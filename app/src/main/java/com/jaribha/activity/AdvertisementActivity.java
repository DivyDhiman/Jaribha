package com.jaribha.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;

public class AdvertisementActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private TextView skeep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_advertisement);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        skeep = (TextView) findViewById(R.id.skeep);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        skeep.setOnClickListener(this);
    }

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 6000L;
    long updatedTime = 0L;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    int second = 5;

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff - timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            second = secs;

            skeep.setText("" + mins + ":0" + secs);
            if (second <= 0) {
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
                skeep.setText(getString(R.string.skip));
            } else {
                customHandler.postDelayed(this, 0);
            }

        }

    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.skeep:
                String skipTxt = skeep.getText().toString();
                if (skipTxt.equalsIgnoreCase(getString(R.string.skip))) {
                    Intent intent = new Intent(AdvertisementActivity.this, LanguageOptionActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();
                }
                break;

            default:
                break;

        }
    }

}
