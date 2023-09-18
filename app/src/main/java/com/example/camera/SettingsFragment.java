package com.example.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

    private Spinner spinnerVideoQuality;
    private SeekBar seekBarAudio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Switch switchMotionDetection = view.findViewById(R.id.switchMotionDetection);
        spinnerVideoQuality = view.findViewById(R.id.spinnerVideoQuality);
        seekBarAudio = view.findViewById(R.id.seekBarAudio);
        Button btnAddCamera = view.findViewById(R.id.btnAddCamera);

        switchMotionDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleMotionDetectionSetting(isChecked);
            }
        });

        setupSpinner();

        seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                handleAudioVolumeSetting(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        btnAddCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddCameraButtonClick();
            }
        });

        return view;
    }

    private void setupSpinner() {
        // Existing code for setting up the spinner
    }

    private void handleVideoQualitySetting(String quality) {
        // Existing code for handling video quality setting
    }

    private void handleAudioVolumeSetting(int progress) {
        // Existing code for handling audio volume setting
    }

    private void handleMotionDetectionSetting(boolean isChecked) {
        // Existing code for handling motion detection setting
    }

    private void handleAddCameraButtonClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Camera");

        // Inflate the layout for the dialog
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_camera, null);
        builder.setView(dialogView);

        final TextView editTextCameraName = dialogView.findViewById(R.id.editTextCameraName);
        final TextView editTextIpAddress = dialogView.findViewById(R.id.editTextIpAddress);
        final TextView editTextPort = dialogView.findViewById(R.id.editTextPort);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cameraName = editTextCameraName.getText().toString();
                String ipAddress = editTextIpAddress.getText().toString();
                String port = editTextPort.getText().toString();

                // Handle saving camera or any desired action
                Toast.makeText(requireContext(), "Camera added: " + cameraName + ", " + ipAddress + ", " + port, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}



