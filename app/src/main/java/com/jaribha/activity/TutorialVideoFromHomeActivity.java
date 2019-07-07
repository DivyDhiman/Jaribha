package com.jaribha.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class TutorialVideoFromHomeActivity extends BaseAppCompatActivity implements View.OnClickListener {

    TextView skeep;

    private ProgressBar pDialog;

    private VideoView video_player_view;

    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_video);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        path = getIntent().getStringExtra(Constants.DATA);

        skeep = (TextView) findViewById(R.id.skeep);
        skeep.setVisibility(View.GONE);
        skeep.setOnClickListener(this);

        advertisementVideo();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skeep:
                finish();
                break;

            default:
                break;
        }
    }

    MediaController mediacontroller;

    public void advertisementVideo() {
        try {
            video_player_view = (VideoView) findViewById(R.id.video_player_view);

            pDialog = (ProgressBar) findViewById(R.id.progressBar);
            // Show progressbar
            pDialog.setVisibility(View.VISIBLE);
            // Start the MediaController
            mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(video_player_view);
            video_player_view.setMediaController(mediacontroller);

            if (isInternetConnected()) {
                new RTSPAsync().execute(path);
            } else {
                showNetworkDialog();
            }

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
        finish();
    }

    private class RTSPAsync extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String videoUri = "";
            try {
                // String url = "http://www.youtube.com/watch?v=1FJHYqE0RDg";
                videoUri = getUrlVideoRTSP(params[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return videoUri;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.setVisibility(View.GONE);
            Log.e("video url", result + "");
            if (!TextUtils.isNullOrEmpty(result)) {
                video_player_view.setVideoURI(Uri.parse(path));
                video_player_view.requestFocus();
                video_player_view.start();
                mediacontroller.show();
            } else {
                showToast(getString(R.string.Can_not_play_this_video));
            }
        }
    }

    public static String getUrlVideoRTSP(String urlYoutube) {
        try {
            String gdy = "http://gdata.youtube.com/feeds/api/videos/";
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String id = extractYoutubeId(urlYoutube);
            URL url = new URL(gdy + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Document doc = documentBuilder.parse(connection.getInputStream());
            Element el = doc.getDocumentElement();
            NodeList list = el.getElementsByTagName("media:content");///media:content
            String cursor = urlYoutube;
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node != null) {
                    NamedNodeMap nodeMap = node.getAttributes();
                    HashMap<String, String> maps = new HashMap<>();
                    for (int j = 0; j < nodeMap.getLength(); j++) {
                        Attr att = (Attr) nodeMap.item(j);
                        maps.put(att.getName(), att.getValue());
                    }
                    if (maps.containsKey("yt:format")) {
                        String f = maps.get("yt:format");
                        if (maps.containsKey("url")) {
                            cursor = maps.get("url");
                        }
                        if (f.equals("1"))
                            return cursor;
                    }
                }
            }
            return cursor;
        } catch (Exception ex) {
            return urlYoutube;
        }
    }

    protected static String extractYoutubeId(String url) throws MalformedURLException {
        String id = null;
        try {
            String query = new URL(url).getQuery();
            if (query != null) {
                String[] param = query.split("&");
                for (String row : param) {
                    String[] param1 = row.split("=");
                    if (param1[0].equals("v")) {
                        id = param1[1];
                    }
                }
            } else {
                if (url.contains("embed")) {
                    id = url.substring(url.lastIndexOf("/") + 1);
                }
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.toString());
        }
        return id;
    }
}