package vn.usth.team7camera;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import vn.usth.team7camera.R;

import org.jetbrains.annotations.Nullable;

public class CamerasFragment extends Fragment {
    private CameraListManager cameraListManager;
    private boolean isFullscreen = false;

    private String addCameraText1;
    private String addCameraText2;

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

        for (int i = 0; i < cameraNames.length; i++) {
            View cameraItemView = inflater.inflate(R.layout.camera_items, null);
            TextView cameraName = cameraItemView.findViewById(R.id.titleTextView);
            VideoView videoView = cameraItemView.findViewById(R.id.videoView);
            final String videoPath = cameraLinks[i];
            cameraName.setText(cameraNames[i]);
            videoView.setVideoURI(Uri.parse(videoPath));
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(false);
                mp.setVolume(0f, 0f);
                mp.start();
            });
            final int cameraIndex = i;
            cameraItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
