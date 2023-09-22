package vn.usth.team7camera;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
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
//import org.videolan.libvlc.LibVLC;
//import org.videolan.libvlc.Media;
//import org.videolan.libvlc.MediaPlayer;
//import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;
import java.util.List;

public class CamerasFragment extends Fragment {
    private CameraListManager cameraListManager;
    private boolean isFullscreen = false;
    private String addCameraText1;
    private String addCameraText2;
    private ExoPlayer player;
    private PlayerView playerView;
    private List<ExoPlayer> exoPlayers;


    public CamerasFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraListManager = new CameraListManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cameras, container, false);

        LinearLayout camerasContainer = view.findViewById(R.id.camerasContainer);

        String[] cameraNames = cameraListManager.getCameraNamesArray();
        String[] cameraLinks = cameraListManager.getCameraLinksArray();

        addCameraText1 = getResources().getString(R.string.addCameraText1);
        TextView addCamera1 = view.findViewById(R.id.noCamera1);
        addCamera1.setText(addCameraText1);
        addCameraText2 = getResources().getString(R.string.addCameraText2);
        TextView addCamera2 = view.findViewById(R.id.noCamera2);
        addCamera2.setText(addCameraText2);

        ImageView noCameraIcon = view.findViewById(R.id.noCameraIcon);
        noCameraIcon.setImageResource(R.drawable.baseline_add_2);

        if (cameraNames.length == 0)
        {
            addCamera1.setVisibility(View.VISIBLE);
            addCamera2.setVisibility(View.VISIBLE);
            noCameraIcon.setVisibility(View.VISIBLE);}
        else {
            addCamera1.setVisibility(View.INVISIBLE);
            addCamera2.setVisibility(View.INVISIBLE);
            noCameraIcon.setVisibility(View.INVISIBLE);
        }

        // Create a list to store all ExoPlayer instances
        exoPlayers = new ArrayList<>();

        for (int i = 0; i < cameraNames.length; i++) {
            View cameraItemView = inflater.inflate(R.layout.camera_items, null);
            TextView cameraName = cameraItemView.findViewById(R.id.titleTextView);

            final String videoPath = cameraLinks[i];
            cameraName.setText(cameraNames[i]);

            player = new ExoPlayer.Builder(requireContext()).build();
            playerView = cameraItemView.findViewById(R.id.videoLayout);
            playerView.setPlayer(player);
            player.setMediaItem(MediaItem.fromUri(videoPath));
            player.setVolume(0);
            player.prepare();
            player.play();

            // Add the instance to the list
            exoPlayers.add(player);

            final int cameraIndex = i;
            cameraItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cameraActivityIntent = new Intent(requireContext(), CameraActivity.class);
                    cameraActivityIntent.putExtra("cameraIndex", cameraNames[cameraIndex]);
                    cameraActivityIntent.putExtra("videoPath",  cameraLinks[cameraIndex]);
                    startActivity(cameraActivityIntent);
                }
            });
            camerasContainer.addView(cameraItemView);
        }
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (exoPlayers != null) {
            for (ExoPlayer player : exoPlayers) {
                if (player != null && player.isPlaying()) {
                    player.stop();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (exoPlayers != null) {
            for (ExoPlayer player : exoPlayers) {
                if (player != null) {
                    player.stop();
                }
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
