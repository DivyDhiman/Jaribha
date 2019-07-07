package com.jaribha.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.utility.SessionManager;

public class TutorialVideoActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private TextView skeep;

    private ProgressBar pDialog;

    private VideoView video_player_view;

    long timeInMilliseconds = 0L;

    long timeSwapBuff = 6000L;

    long updatedTime = 0L;

    private long startTime = 0L;

    private Handler customHandler = new Handler();

    int second = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_tutorial_video);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        skeep = (TextView) findViewById(R.id.skeep);
        skeep.setOnClickListener(this);

        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

        advertisementVideo();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skeep:
                String skipTxt = skeep.getText().toString();
                if (skipTxt.equalsIgnoreCase(getString(R.string.skip))) {
                    SessionManager sessionManager = SessionManager.getInstance(TutorialVideoActivity.this);
                    Object user = sessionManager.getUser();
                    if (user != null) {
                        Intent loginIntent = new Intent(TutorialVideoActivity.this, HomeScreenActivity.class);
                        //loginIntent.putExtra("pos", 0);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        finish();
                    } else {
                        Intent loginIntent = new Intent(TutorialVideoActivity.this, LoginScreenActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        finish();
                    }
                }
                break;

            default:
                break;

        }
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff - timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            second = secs;
            /*skeep.setText("" + mins + ":"
                    + String.format("%02d", secs));*/

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

    public void advertisementVideo() {
        try {
            video_player_view = (VideoView) findViewById(R.id.video_player_view);
            pDialog = (ProgressBar) findViewById(R.id.progressBar);
            // Show progressbar
            pDialog.setVisibility(View.VISIBLE);
            // Start the MediaController
            MediaController mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(video_player_view);
            String path = "android.resource://" + getPackageName() + "/" + R.raw.video;
            video_player_view.setMediaController(mediacontroller);
            //video_player_view.setVideoURI(videoUri);
            video_player_view.setVideoURI(Uri.parse(path));
            video_player_view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return second > 0;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        video_player_view.requestFocus();
        video_player_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.setVisibility(View.GONE);
                video_player_view.start();
            }
        });

        video_player_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                if (pDialog.getVisibility() == View.VISIBLE) {
                    pDialog.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (getIntent().getExtras() != null) {
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            finish();
        } else {
            Intent intent = new Intent(TutorialVideoActivity.this, LanguageOptionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            finish();
        }
    }
}
