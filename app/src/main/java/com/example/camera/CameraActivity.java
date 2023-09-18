package com.example.camera;

import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class CameraActivity extends AppCompatActivity {

    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        videoView = findViewById(R.id.videoView);

        String cameraName = getIntent().getStringExtra("cameraIndex");
        String videoName = getIntent().getStringExtra("videoIndex");

        //Set video base on id instead of R.raw.FILE_NAME
        Resources res = this.getResources();
        int videoId = res.getIdentifier(videoName, "raw", this.getPackageName());

        setTitle(cameraName);

        String videoPath = "android.resource://" + this.getPackageName() + "/" + videoId;

        // Set a MediaController for video playback control
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setVideoPath(videoPath);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.start();
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }

}

