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

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import vn.usth.team7camera.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CameraActivity extends AppCompatActivity {
    private int videoId;
    private CameraListManager cameraListManager;
    private VideoView videoView;
    private MediaPlayer mediaPlayer;
    private LibVLC libVLC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraListManager = new CameraListManager(this);
        String cameraName = getIntent().getStringExtra("cameraIndex");
        String videoPath = getIntent().getStringExtra("videoPath");
        setTitle(cameraName);
        ArrayList<String> options = new ArrayList<>();
        options.add("-vvv"); // verbosity
        libVLC = new LibVLC(getApplicationContext(), options);
        mediaPlayer = new MediaPlayer(libVLC);

        // Create a media object
        Media media = new Media(libVLC, Uri.parse(videoPath));
        mediaPlayer.setMedia(media);

        // Bind the player to the view
        VLCVideoLayout videoLayout = findViewById(R.id.videoLayout);
        mediaPlayer.attachViews(videoLayout, null, true, true);
        mediaPlayer.play();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.modifyCamera) {
            modifyCamera();
            return true;
        } else if (id == R.id.deleteCamera) {
            deleteCamera();
            return true;
        }
        else if (id == R.id.snapshot) {
//            captureSnapshot();
            new CaptureSnapshotTask().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume() {
        super.onResume();
//        videoView.start();
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//        if (libVLC != null) {
//            libVLC.release();
//            libVLC = null;
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (libVLC != null) {
            libVLC.release();
            libVLC = null;
        }
        recreate();
    }
    private void deleteCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.deleteCamera));

        // Inflate the layout for the dialog
        builder.setMessage(R.string.deleteCamConfirm);
        builder.setPositiveButton(getResources().getString(R.string.ok), null); // Set to null. We override the onclick

        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
                String cameraAddress = getIntent().getStringExtra("videoPath");

                Set<String> cameraList = new HashSet<>(cameraListManager.getCameraNames());
                Set<String> cameraLinks = new HashSet<>(cameraListManager.getCameraLinks());

                // Check if the camera name, IP, and port exist in the list
                if (cameraList.contains(cameraName) && cameraLinks.contains(cameraAddress)) {
                    // Remove the camera from the list
                    cameraList.remove(cameraName);
                    cameraLinks.remove(cameraAddress);

                    // Save the updated camera list
                    cameraListManager.saveCameraNames(cameraList);
                    cameraListManager.saveCameraLinks(cameraLinks);

                    // Display a message indicating the camera was deleted
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.camera) + " '" + cameraName + "' " + getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();

                    // Create an Intent to return to MainActivity
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);

                } else {
                    // If the camera name, IP, or port is not found in the list, show an error message
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.camera) +" '" + cameraName + "' " + getResources().getString(R.string.notFound), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }


    private void modifyCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.modifyCamera));

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_camera, null);
        builder.setView(dialogView);

        final TextView editTextCameraName = dialogView.findViewById(R.id.editTextCameraName);
        final TextView editTextAddress = dialogView.findViewById(R.id.editTextAddress);

        builder.setPositiveButton(getResources().getString(R.string.save), null); // Set to null. We override the onclick

        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
                String cameraAddress = getIntent().getStringExtra("videoPath");

                String cameraNewName = editTextCameraName.getText().toString();
                String cameraNewAddress = editTextAddress.getText().toString();

                Set<String> cameraList = new HashSet<>(cameraListManager.getCameraNames());
                Set<String> cameraLinks = new HashSet<>(cameraListManager.getCameraLinks());

                if (cameraNewName.isEmpty() || cameraNewAddress.isEmpty()) {
                    Toast.makeText(CameraActivity.this, R.string.antiEmpty, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the camera name and address exist in the list
                if (cameraNewName.equals(cameraName) && cameraNewAddress.equals(cameraAddress)) {
                    Toast.makeText(CameraActivity.this, "No changes were made.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (cameraList.contains(cameraNewName) && !cameraNewName.equals(cameraName)) {
                    Toast.makeText(CameraActivity.this, R.string.camNameExist, Toast.LENGTH_SHORT).show();
                    return;
                } else if (cameraLinks.contains(cameraNewAddress) && !cameraNewAddress.equals(cameraAddress)) {
                    Toast.makeText(CameraActivity.this, R.string.camPortIPExist, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // Temporary remove the camera from the list
                    cameraList.remove(cameraName);
                    cameraLinks.remove(cameraAddress);

                    // Save the updated camera list
                    cameraListManager.saveCameraNames(cameraList);
                    cameraListManager.saveCameraLinks(cameraLinks);

                    // Re-add the camera to the list
                    cameraList.add(cameraNewName);
                    cameraLinks.add(cameraNewAddress);

                    // Save the updated camera list
                    cameraListManager.saveCameraNames(cameraList);
                    cameraListManager.saveCameraLinks(cameraLinks);

                    // Display a message indicating the camera was deleted
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.changeCamInfo) + cameraNewName + "'.", Toast.LENGTH_SHORT).show();

                    // Create an Intent to return to MainActivity
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }
//                } else {
//                    // If the camera name, IP, or port is not found in the list, show an error message
//                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.camera)+ " '" + cameraName + "' " +getResources().getString(R.string.notFound), Toast.LENGTH_SHORT).show();
//                }
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                finish();
                startActivity(intent);
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

        // Set the data source to the live link
        String videoPath = getIntent().getStringExtra("videoPath");
        retriever.setDataSource(videoPath, new HashMap<String, String>());

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

