package vn.usth.team7camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import vn.usth.team7camera.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CameraActivity extends AppCompatActivity {
    private int videoId;
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
        videoId = res.getIdentifier(videoName, "raw", this.getPackageName());

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
        } else if (id == R.id.snapshot) {
//            captureSnapshot();
            new CaptureSnapshotTask().execute();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Camera");

        // Inflate the layout for the dialog
        builder.setMessage(R.string.deleteCamConfirm);
        builder.setPositiveButton("Yes", null); // Set to null. We override the onclick

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cameraName = getIntent().getStringExtra("cameraIndex");
                String cameraIP = getIntent().getStringExtra("cameraIP");
                String cameraPort = getIntent().getStringExtra("cameraPort");

                Set<String> cameraList = new HashSet<>(cameraListManager.getCameraNames());
                Set<String> existingCameraIPs = new HashSet<>(cameraListManager.getCameraIPs());
                Set<String> existingCameraPorts = new HashSet<>(cameraListManager.getCameraPorts());

                // Check if the camera name, IP, and port exist in the list
                if (cameraList.contains(cameraName) && existingCameraIPs.contains(cameraIP) && existingCameraPorts.contains(cameraPort)) {
                    // Remove the camera from the list
                    cameraList.remove(cameraName);
                    existingCameraIPs.remove(cameraIP);
                    existingCameraPorts.remove(cameraPort);

                    // Save the updated camera list
                    cameraListManager.saveCameraNames(cameraList);
                    cameraListManager.saveCameraIPs(existingCameraIPs);
                    cameraListManager.saveCameraPorts(existingCameraPorts);

                    // Display a message indicating the camera was deleted
                    Toast.makeText(getApplicationContext(), "Camera '" + cameraName + "' deleted", Toast.LENGTH_SHORT).show();

                    // Create an Intent to return to MainActivity
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);

                } else {
                    // If the camera name, IP, or port is not found in the list, show an error message
                    Toast.makeText(getApplicationContext(), "Camera '" + cameraName + "' not found", Toast.LENGTH_SHORT).show();
                }
                recreate();
                dialog.dismiss();
            }

        });
    }

    private class CaptureSnapshotTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            captureSnapshot();
            return null;
        }
    }

    private void captureSnapshot() {
        String cameraName = getIntent().getStringExtra("cameraIndex");

        // Get the current position of the video
        int currentPosition = videoView.getCurrentPosition();

        // Create a MediaMetadataRetriever
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        // Set the data source to the video path
        String videoPath = "android.resource://" + this.getPackageName() + "/" + videoId;
        retriever.setDataSource(this, Uri.parse(videoPath));

        // Get the bitmap of the current frame
        Bitmap bitmap = retriever.getFrameAtTime(currentPosition * 1000, MediaMetadataRetriever.OPTION_CLOSEST); // timeUs is in microseconds

        String toastMessage = "";

        // Check if the bitmap is not null
        if (bitmap != null) {
            // Create a file name based on the current time
            String fileName = "snapshot_" + cameraName + "_" + System.currentTimeMillis() + ".jpg";

            File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures/Team7Camera");
            if (!storageDir.exists())
            {
                storageDir.mkdirs();
            }

            // Create a file object for the snapshot
            File file = new File(storageDir, fileName);

            try {
                // Write the bitmap to the file using JPEG format
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();

                // Show a toast message to indicate success
                toastMessage = getString(R.string.savedToDir) + file.getAbsolutePath();
            } catch (IOException e) {
                // Show a toast message to indicate failure
                Toast.makeText(this, getString(R.string.snapFailed) + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show a toast message to indicate no frame available
            toastMessage = getString(R.string.frameNo);
        }
        final String finalToastMessage = toastMessage;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CameraActivity.this, finalToastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

