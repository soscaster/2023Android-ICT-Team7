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
    private SeekBar seekBarSensitivity;

    private String SensivityText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Switch switchMotionDetection = view.findViewById(R.id.switchMotionDetection);
        spinnerVideoQuality = view.findViewById(R.id.spinnerVideoQuality);
        seekBarAudio = view.findViewById(R.id.seekBarAudio);
        seekBarSensitivity = view.findViewById(R.id.seekBarSensitivity);

        SensivityText = getResources().getString(R.string.sensitivity);
        TextView sensitivity = view.findViewById(R.id.SensitivityText);
        sensitivity.setText(SensivityText);
        sensitivity.setVisibility(View.GONE);
        seekBarSensitivity.setVisibility(View.GONE);

        switchMotionDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleMotionDetectionSetting(isChecked);

                // Show or hide sensitivity bar based on switch state
                if (isChecked) {
                    sensitivity.setVisibility(View.VISIBLE);
                    seekBarSensitivity.setVisibility(View.VISIBLE);
                } else {
                    sensitivity.setVisibility(View.GONE);
                    seekBarSensitivity.setVisibility(View.GONE);
                }
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

}
