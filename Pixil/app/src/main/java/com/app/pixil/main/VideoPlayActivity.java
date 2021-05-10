package com.app.pixil.main;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

import com.app.pixil.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;

public class VideoPlayActivity extends AppCompatActivity {

    VideoView videoView;
    ImageView masked;
    AVLoadingIndicatorView loadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        videoView = (VideoView)findViewById(R.id.videoView);
        masked = (ImageView)findViewById(R.id.masked);
        loadingIndicatorView = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        String path = "android.resource://" + getPackageName() + "/" + R.raw.videofile;
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();
        loadingIndicatorView.setVisibility(View.VISIBLE);
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    // Here the video starts
                    videoView.setBackground(null);
                    masked.setVisibility(View.GONE);
                    loadingIndicatorView.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });

    }

    public void back(View view){
        onBackPressed();
    }
}


























