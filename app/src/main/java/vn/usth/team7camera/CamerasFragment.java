package vn.usth.team7camera;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import vn.usth.team7camera.R;

import org.jetbrains.annotations.Nullable;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CamerasFragment extends Fragment {
    private String addCameraText1;
    private String addCameraText2;
    private List<ExoPlayer> exoPlayers;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference camerasRef = database.getReference("camseecamxa");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    public CamerasFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exoPlayers = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cameras, container, false);
        LinearLayout camerasContainer = view.findViewById(R.id.camerasContainer);

        addCameraText1 = getResources().getString(R.string.addCameraText1);
        TextView addCamera1 = view.findViewById(R.id.noCamera1);
        addCamera1.setText(addCameraText1);
        addCameraText2 = getResources().getString(R.string.addCameraText2);
        TextView addCamera2 = view.findViewById(R.id.noCamera2);
        addCamera2.setText(addCameraText2);

        ImageView noCameraIcon = view.findViewById(R.id.noCameraIcon);
        noCameraIcon.setImageResource(R.drawable.baseline_add_2);
        addCamera1.setVisibility(View.GONE);
        addCamera2.setVisibility(View.GONE);
        noCameraIcon.setVisibility(View.GONE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            camerasRef.child(String.valueOf(currentUser.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot cameraSnapshot : snapshot.getChildren()) {
                            addCamera1.setVisibility(View.GONE);
                            addCamera2.setVisibility(View.GONE);
                            noCameraIcon.setVisibility(View.GONE);

                            String cameraName = cameraSnapshot.child("cameraName").getValue(String.class);
                            String cameraLink = cameraSnapshot.child("cameraLink").getValue(String.class);

                            View cameraItemView = inflater.inflate(R.layout.camera_items, null);
                            TextView cameraNameTextView = cameraItemView.findViewById(R.id.titleTextView);
                            cameraNameTextView.setText(cameraName);

                            final ExoPlayer player = new ExoPlayer.Builder(requireContext()).build();
                            PlayerView playerView = cameraItemView.findViewById(R.id.videoLayout);
                            playerView.setPlayer(player);
                            player.setVolume(0);
                            exoPlayers.add(player);
                            cameraItemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent cameraActivityIntent = new Intent(requireContext(), CameraActivity.class);
                                    cameraActivityIntent.putExtra("cameraIndex", cameraName);
                                    cameraActivityIntent.putExtra("videoPath", cameraLink);
                                    startActivity(cameraActivityIntent);
                                }
                            });
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean isReachable = false;
                                    try {
                                        HttpURLConnection.setFollowRedirects(false);
                                        HttpURLConnection con = (HttpURLConnection) new URL(cameraLink).openConnection();
                                        con.setRequestMethod("HEAD");
                                        isReachable = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    final boolean finalIsReachable = isReachable;
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!finalIsReachable) {
                                                cameraNameTextView.setText(cameraName + " - Camera Offline");
                                            } else {
                                                player.setMediaItem(MediaItem.fromUri(cameraLink));
                                                player.prepare();
                                                player.play();
                                            }
                                        }
                                    });
                                }
                            }).start();
                            camerasContainer.addView(cameraItemView);
                        }
                    } else {
                        addCamera1.setVisibility(View.VISIBLE);
                        addCamera2.setVisibility(View.VISIBLE);
                        noCameraIcon.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), getString(R.string.database_error), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            addCamera1.setVisibility(View.VISIBLE);
            addCamera2.setVisibility(View.VISIBLE);
            noCameraIcon.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Resume ExoPlayers if they were paused
        for (ExoPlayer player : exoPlayers) {
            if (player != null && !player.isPlaying()) {
                player.play();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        for (ExoPlayer player : exoPlayers) {
            if (player != null && player.isPlaying()) {
                player.pause();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (ExoPlayer player : exoPlayers) {
            if (player != null) {
                player.release();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (exoPlayers != null) {
            for (ExoPlayer player : exoPlayers) {
                if (player != null && !player.isPlaying()) {
                    player.play();
                }
            }
        }
    }
}
