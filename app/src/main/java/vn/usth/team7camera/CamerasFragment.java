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
//import androidx.media3.common.MediaItem;
//import androidx.media3.common.Player;
//import androidx.media3.common.util.UnstableApi;
//import androidx.media3.datasource.DataSource;
//import androidx.media3.datasource.DefaultHttpDataSource;
//import androidx.media3.exoplayer.ExoPlayer;
//import androidx.media3.exoplayer.SimpleExoPlayer;
//import androidx.media3.exoplayer.hls.HlsMediaSource;
//import androidx.media3.exoplayer.source.MediaSource;
//import androidx.media3.ui.PlayerView;

import vn.usth.team7camera.R;

import org.jetbrains.annotations.Nullable;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;
import java.util.List;

public class CamerasFragment extends Fragment {
    private CameraListManager cameraListManager;
    private boolean isFullscreen = false;
    private String addCameraText1;
    private String addCameraText2;
    private MediaPlayer mediaPlayer;
    private LibVLC libVLC;
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


//        for (int i = 0; i < cameraNames.length; i++) {
//            View cameraItemView = inflater.inflate(R.layout.camera_items, null);
//            TextView cameraName = cameraItemView.findViewById(R.id.titleTextView);
//
////            VideoView videoView = cameraItemView.findViewById(R.id.videoView);
//            final String videoPath = cameraLinks[i];
//            cameraName.setText(cameraNames[i]);
//
////            // Initialize the ExoPlayer
////            // Create a data source factory.
////            DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
////            // Create a HLS media source pointing to a playlist uri.
////            FfmpegMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(false).createMediaSource(MediaItem.fromUri(videoPath));
////            ExoPlayer player = new ExoPlayer.Builder(requireContext()).build();
////            PlayerView playerView = cameraItemView.findViewById(R.id.playerView);
////
////            // Set the media source and start playing
////            player.setMediaSource(hlsMediaSource);
////            player.prepare();
////            player.setVolume(0f); // Mute
////            player.play();
////
////            // Bind the player to the view
////            playerView.setPlayer(player);
//            // Initialize the VLC player
//            ArrayList<String> options = new ArrayList<>();
//            options.add("-vvv"); // verbosity
//            final LibVLC[] libVLC = {new LibVLC(requireContext(), options)};
//            final MediaPlayer[] mediaPlayer = {new MediaPlayer(libVLC[0])};
//
//            // Create a media object
//            Media media = new Media(libVLC[0], Uri.parse(videoPath));
//            mediaPlayer[0].setMedia(media);
//
//            // Bind the player to the view
//            VLCVideoLayout videoLayout = cameraItemView.findViewById(R.id.videoLayout);
//            mediaPlayer[0].attachViews(videoLayout, null, true, true);
//
//            // Start playing
//            mediaPlayer[0].play();
//            Toast.makeText(getContext(), "RUN VLC", Toast.LENGTH_SHORT).show();
//
////            videoView.setVideoURI(Uri.parse(videoPath));
////            videoView.setOnPreparedListener(mp -> {
////                mp.setLooping(false);
////                mp.setVolume(0f, 0f);
////                mp.start();
////            });
//            final int cameraIndex = i;
//            cameraItemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    for (int j=0; j < mediaPlayer.length ; j++) {
//                        if (mediaPlayer[j] != null) {
//                            mediaPlayer[j].stop();
//                            mediaPlayer[j].release();
//                            mediaPlayer[j] = null;
//                        }
//                    }
//
//                    for (int j=0; j < libVLC.length ; j++) {
//                        if (libVLC[j] != null) {
//                            libVLC[j].release();
//                            libVLC[j] = null;
//                        }
//                    }
//                    Intent cameraActivityIntent = new Intent(requireContext(), CameraActivity.class);
//                    cameraActivityIntent.putExtra("cameraIndex", cameraNames[cameraIndex]);
//                    cameraActivityIntent.putExtra("videoPath",  cameraLinks[cameraIndex]); // Changed from "videoIndex"
//                    startActivity(cameraActivityIntent);
//                }
//            });
//            camerasContainer.addView(cameraItemView);
//        }
//        return view;
//    }

        // Create lists to store all MediaPlayer and LibVLC instances
        List<MediaPlayer> mediaPlayers = new ArrayList<>();
        List<LibVLC> libVLCs = new ArrayList<>();

        for (int i = 0; i < cameraNames.length; i++) {
            View cameraItemView = inflater.inflate(R.layout.camera_items, null);
            TextView cameraName = cameraItemView.findViewById(R.id.titleTextView);

            final String videoPath = cameraLinks[i];
            cameraName.setText(cameraNames[i]);

            ArrayList<String> options = new ArrayList<>();
            options.add("-vvv");
            final LibVLC libVLC = new LibVLC(requireContext(), options);
            final MediaPlayer mediaPlayer = new MediaPlayer(libVLC);

            // Add the instances to the lists
            libVLCs.add(libVLC);
            mediaPlayers.add(mediaPlayer);

            Media media = new Media(libVLC, Uri.parse(videoPath));
            mediaPlayer.setMedia(media);

            VLCVideoLayout videoLayout = cameraItemView.findViewById(R.id.videoLayout);
            mediaPlayer.attachViews(videoLayout, null, true, true);

            mediaPlayer.play();
            Toast.makeText(getContext(), "RUN VLC", Toast.LENGTH_SHORT).show();

            final int cameraIndex = i;
            cameraItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Stop and release all MediaPlayer and LibVLC instances
                    for (MediaPlayer mp : mediaPlayers) {
                        if (mp != null) {
                            mp.release();
                        }
                    }
                    for (LibVLC lv : libVLCs) {
                        if (lv != null) {
                            lv.release();
                        }
                    }

                    Intent cameraActivityIntent = new Intent(requireContext(), CameraActivity.class);
                    cameraActivityIntent.putExtra("cameraIndex", cameraNames[cameraIndex]);
                    cameraActivityIntent.putExtra("videoPath",  cameraLinks[cameraIndex]); // Changed from "videoIndex"
                    startActivity(cameraActivityIntent);
                }
            });
            camerasContainer.addView(cameraItemView);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (libVLC != null) {
            libVLC.release();
            libVLC = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (libVLC != null) {
            libVLC.release();
            libVLC = null;
        }
    }

//    private void toggleFullscreen() {
//        if (isFullscreen) {
//            // Exit fullscreen
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            functionButton.setVisibility(View.VISIBLE);
//            isFullscreen = false;
//        } else {
//            // Enter fullscreen
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            functionButton.setVisibility(View.INVISIBLE);
//            isFullscreen = true;
//        }
//    }
//
}
