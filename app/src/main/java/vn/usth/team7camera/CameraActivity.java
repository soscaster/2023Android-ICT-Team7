package vn.usth.team7camera;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerControlView;
import androidx.media3.ui.PlayerView;

//import org.videolan.libvlc.LibVLC;
//import org.videolan.libvlc.Media;
//import org.videolan.libvlc.MediaPlayer;
//import org.videolan.libvlc.util.VLCVideoLayout;
import vn.usth.team7camera.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UnstableApi
public class CameraActivity extends AppCompatActivity {
    private CameraListManager cameraListManager;
    private ExoPlayer player;
    private PlayerView playerView;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraListManager = new CameraListManager(this);
        String cameraName = getIntent().getStringExtra("cameraIndex");
        videoPath = getIntent().getStringExtra("videoPath");
        setTitle(cameraName);
        player = new ExoPlayer.Builder(getApplicationContext()).build();
        playerView = (PlayerView) findViewById(R.id.videoLayout);
        playerView.setPlayer(player);
        playerView.setControllerShowTimeoutMs(1000);
        player.setMediaItem(MediaItem.fromUri(videoPath));
        player.prepare();
        player.play();
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
        } else if (id == R.id.snapshot) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        player.stop();
        player.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
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
                    finish();
                    startActivity(intent);
                    recreate();

                } else {
                    // If the camera name, IP, or port is not found in the list, show an error message
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.camera) + " '" + cameraName + "' " + getResources().getString(R.string.notFound), Toast.LENGTH_SHORT).show();
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

                List<String> cameraList = new ArrayList<>(cameraListManager.getCameraNames());
                List<String> cameraLinks = new ArrayList<>(cameraListManager.getCameraLinks());

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

                    int index = cameraList.indexOf(cameraName);
                    cameraList.set(index, cameraNewName);
                    cameraLinks.set(index, cameraNewAddress);
                    cameraListManager.saveCameraNames(new HashSet<>(cameraList));
                    cameraListManager.saveCameraLinks(new HashSet<>(cameraLinks));

                    // Display a message indicating the camera was deleted
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.changeCamInfo) + cameraNewName + "'.", Toast.LENGTH_SHORT).show();

                    // Create an Intent to return to MainActivity
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }
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

    public Bitmap captureBitmap() {
        View view = playerView.getVideoSurfaceView();
        if (view instanceof TextureView) {
            TextureView textureView = (TextureView) view;
            return textureView.getBitmap();
        } else {
            return null;
        }
    }

    private void captureSnapshot() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = captureBitmap();
                if (bitmap == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, "Failed to capture bitmap", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                String cameraName = getIntent().getStringExtra("cameraIndex");
                String fileName = "snapshot_" + cameraName + "_" + System.currentTimeMillis() + ".jpg";
                File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures/Team7Camera");

                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }

                File file = new File(storageDir, fileName);

                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, getString(R.string.savedToDir) + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, getString(R.string.snapFailed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}

