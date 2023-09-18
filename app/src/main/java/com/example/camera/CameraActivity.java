package com.example.camera;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CameraActivity extends AppCompatActivity {
    private CameraListManager cameraListManager;
    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        videoView = findViewById(R.id.videoView);
        cameraListManager = new CameraListManager(this);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.deleteCamera) {
            deleteCamera();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    private void deleteCamera() {
        String cameraName = getIntent().getStringExtra("cameraIndex");

        Set<String> cameraList = new HashSet<>(cameraListManager.getCameraNames());

        // Check if the camera name exists in the list
        if (cameraList.contains(cameraName)) {
            // Remove the camera from the list
            cameraList.remove(cameraName);

            // Save the updated camera list
            cameraListManager.saveCameraNames(cameraList);

            // Display a message indicating the camera was deleted
            Toast.makeText(this, "Camera '" + cameraName + "' deleted", Toast.LENGTH_SHORT).show();
            // Create an Intent to return to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else {
            // If the camera name is not found in the list, show an error message
            Toast.makeText(this, "Camera '" + cameraName + "' not found", Toast.LENGTH_SHORT).show();
        }
    }
}

