package com.abhaybmicoc.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.VideoView;

import com.abhaybmicoc.app.R;

public class TutorialVideosActivity extends AppCompatActivity {

    Context context = TutorialVideosActivity.this;

    VideoView videoViewTutorialVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_videos);

        setupUI();
    }

    private void setupUI() {
        videoViewTutorialVideos = findViewById(R.id.videoView_TutorialVideos);

        videoViewTutorialVideos.setVideoPath("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
//        videoViewTutorialVideos.setVideoPath("https://www.youtube.com/watch?v=oytjIwEQ_us&feature=youtu.be");
        videoViewTutorialVideos.start();
    }

}
