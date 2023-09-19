package vn.usth.team7camera;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import vn.usth.team7camera.R;

import org.jetbrains.annotations.Nullable;

public class CamerasFragment extends Fragment {
    private CameraListManager cameraListManager;
    private boolean isFullscreen = false;

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

        for (int i = 0; i < cameraNames.length; i++) {
            // Create a new instance of camera_items.xml for each camera
            View cameraItemView = inflater.inflate(R.layout.camera_items, null);

            // Find views within the camera_items.xml layout
            TextView cameraName = cameraItemView.findViewById(R.id.titleTextView);
            VideoView videoView = cameraItemView.findViewById(R.id.videoView);

            //Set video base on id instead of R.raw.FILE_NAME
            final String videoName = "sample"+((i%2)+1);
            Resources res = getActivity().getResources();
            int videoId = res.getIdentifier(videoName, "raw", getActivity().getPackageName());

            // Set camera name
            cameraName.setText(cameraNames[i]);

            // Set video source and start playing (same video for all cameras)
            String videoPath = "android.resource://" + requireActivity().getPackageName() + "/" + videoId;
            videoView.setVideoPath(videoPath);
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true); // Set looping to true to play the video in a loop
                mp.setVolume(0f, 0f);
                mp.start();
            });

            // Add click behavior for each camera
            final int cameraIndex = i; // Capture the current camera index for the click listener
            cameraItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cameraActivityIntent = new Intent(requireContext(), CameraActivity.class);
                    cameraActivityIntent.putExtra("cameraIndex", cameraNames[cameraIndex]);
                    cameraActivityIntent.putExtra("videoIndex",  "sample"+((cameraIndex%2)+1));
                    startActivity(cameraActivityIntent);
                }
            });

            // Add the cameraItemView to the camerasContainer
            camerasContainer.addView(cameraItemView);

        }

        return view;
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
