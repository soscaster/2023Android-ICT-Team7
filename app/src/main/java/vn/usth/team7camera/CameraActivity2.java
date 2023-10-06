package vn.usth.team7camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import vn.usth.team7camera.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

@UnstableApi
public class CameraActivity2 extends AppCompatActivity {
    private ExoPlayer player;
    private PlayerView playerView;
    private String videoPath;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private boolean durationSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //Get selected Time
        long selectedDateTimeMillis = getIntent().getLongExtra("selectedDateTime", 0);
        long currentTimeMillis = Calendar.getInstance().getTimeInMillis();
        long rewindTimeMillis = currentTimeMillis - selectedDateTimeMillis;

        String cameraName = getIntent().getStringExtra("cameraIndex");
        videoPath = getIntent().getStringExtra("videoPath");
        setTitle(cameraName);
        player = new ExoPlayer.Builder(getApplicationContext()).build();
        playerView = (PlayerView) findViewById(R.id.videoLayout);
        playerView.setPlayer(player);
        playerView.setControllerShowTimeoutMs(1000);

        player.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_READY && !durationSet) {
                    // Media is ready, and duration has not been set yet
                    long realDurationMillis = player.getDuration();
                    durationSet = true;

                    // Now you can use realDurationMillis as needed
                    // Calculate the seek position based on your requirements
                    long seekPosition = realDurationMillis - rewindTimeMillis;
                    player.seekTo(seekPosition);
                    player.setPlayWhenReady(true); // Start playback
                }
            }

            // Implement other event listener methods as needed
        });

        // Set the media item
        player.setMediaItem(MediaItem.fromUri(videoPath));

        // Prepare the player
        player.prepare();
        player.play();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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

        builder.setMessage(R.string.deleteCamConfirm);
        builder.setPositiveButton(getResources().getString(R.string.ok), null);

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
                final String cameraName = getIntent().getStringExtra("cameraIndex");
                final String cameraAddress = getIntent().getStringExtra("videoPath");

                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference camerasRef = FirebaseDatabase.getInstance().getReference("camseecamxa").child(currentUserUid);
                camerasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean cameraFound = false;

                        for (DataSnapshot cameraSnapshot : dataSnapshot.getChildren()) {
                            String existingCameraName = cameraSnapshot.child("cameraName").getValue(String.class);
                            String existingCameraLink = cameraSnapshot.child("cameraLink").getValue(String.class);

                            if (existingCameraName != null && existingCameraLink != null &&
                                    existingCameraName.equals(cameraName) && existingCameraLink.equals(cameraAddress)) {
                                cameraSnapshot.getRef().removeValue();
                                cameraFound = true;
                                break;
                            }
                        }

                        if (cameraFound) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.camera) + " '" + cameraName + "' " + getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            finish();
                            startActivity(intent);
                            recreate();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.camera) + " '" + cameraName + "' " + getResources().getString(R.string.notFound), Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
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

        builder.setPositiveButton(getResources().getString(R.string.save), null);

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
                final String cameraName = getIntent().getStringExtra("cameraIndex");
                final String cameraAddress = getIntent().getStringExtra("videoPath");
                final String cameraNewName = editTextCameraName.getText().toString();
                final String cameraNewAddress = editTextAddress.getText().toString();

                if (cameraNewAddress.isEmpty()) {
                    Toast.makeText(CameraActivity2.this, R.string.antiEmpty, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cameraNewName.equals(cameraName) && cameraNewAddress.equals(cameraAddress)) {
                    Toast.makeText(CameraActivity2.this, getString(R.string.no_change), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                DatabaseReference camerasRef = FirebaseDatabase.getInstance().getReference("camseecamxa");
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                camerasRef.child(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot cameraSnapshot : dataSnapshot.getChildren()) {
                            String existingCameraName = cameraSnapshot.child("cameraName").getValue(String.class);
                            String existingCameraLink = cameraSnapshot.child("cameraLink").getValue(String.class);

                            if (existingCameraName != null && existingCameraName.equals(cameraName)
                                    && existingCameraLink != null && existingCameraLink.equals(cameraAddress)) {
                                cameraSnapshot.getRef().child("cameraName").setValue(cameraNewName);
                                cameraSnapshot.getRef().child("cameraLink").setValue(cameraNewAddress);
                                break;
                            }
                        }
                        Toast.makeText(getApplicationContext(), getString(R.string.changeCamInfo) + cameraNewName + "'.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }
    private boolean isCameraNameExist(final String cameraName) {
        DatabaseReference camerasRef = FirebaseDatabase.getInstance().getReference("camseecamxa");
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final boolean[] exists = {false};

        camerasRef.child(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot cameraSnapshot : dataSnapshot.getChildren()) {
                    String existingCameraName = cameraSnapshot.child("cameraName").getValue(String.class);
                    if (existingCameraName != null && existingCameraName.equals(cameraName)) {
                        exists[0] = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), "Database Error!", Toast.LENGTH_SHORT).show();
            }
        });

        return exists[0];
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
                            Toast.makeText(CameraActivity2.this, getString(R.string.capture_failed), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CameraActivity2.this, getString(R.string.savedToDir) + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity2.this, getString(R.string.snapFailed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                String userUid = currentUser.getUid();
                StorageReference userFolderRef = storageReference.child("snapshots").child(userUid);
                StorageReference imageRef = userFolderRef.child(fileName);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = imageRef.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CameraActivity2.this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CameraActivity2.this, getString(R.string.upload_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }
}

