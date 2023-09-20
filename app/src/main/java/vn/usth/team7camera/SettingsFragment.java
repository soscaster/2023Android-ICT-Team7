package vn.usth.team7camera;

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
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private Spinner spinnerVideoQuality;
    private SeekBar seekBarAudio;
    private SeekBar seekBarSensitivity;
    private String SensivityText;
    private Spinner spinnerLanguage;
    private Button buttonAbout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Switch switchMotionDetection = view.findViewById(R.id.switchMotionDetection);
        spinnerVideoQuality = view.findViewById(R.id.spinnerVideoQuality);
        seekBarAudio = view.findViewById(R.id.seekBarAudio);
        seekBarSensitivity = view.findViewById(R.id.seekBarSensitivity);
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);
        buttonAbout = view.findViewById(R.id.buttonAbout);

        SensivityText = getResources().getString(R.string.sensitivity);
        TextView sensitivity = view.findViewById(R.id.SensitivityText);
        sensitivity.setText(SensivityText);
        sensitivity.setVisibility(View.GONE);
        seekBarSensitivity.setVisibility(View.GONE);

        setupLanguageSpinner();

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

        buttonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "About" button click (e.g., open a dialog with app information)
                showAboutDialog();
            }
        });

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
        // Example data for the spinner
        String[] videoQualityOptions = {"Low", "Medium", "High"};

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, videoQualityOptions);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerVideoQuality.setAdapter(adapter);

        // Set a listener to handle spinner item selection
        spinnerVideoQuality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selected item
                String selectedQuality = parent.getItemAtPosition(position).toString();
                handleVideoQualitySetting(selectedQuality);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void handleVideoQualitySetting(String quality) {
        // Handle video quality setting here
        // Example: Update a TextView with the selected quality
        // TextView qualityTextView = view.findViewById(R.id.textViewQuality);
        // qualityTextView.setText("Selected Quality: " + quality);
    }

    private void handleAudioVolumeSetting(int progress) {
        // Handle audio volume setting here
        // Example: Update a TextView with the audio volume
        // TextView volumeTextView = view.findViewById(R.id.textViewVolume);
        // volumeTextView.setText("Audio Volume: " + progress);
    }

    private void handleMotionDetectionSetting(boolean isChecked) {
        // Handle motion detection setting here
        // Example: Show a toast based on the motion detection state
        // String message = isChecked ? "Motion detection enabled" : "Motion detection disabled";
        // Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void setupLanguageSpinner() {
        // Add your code to set up the language spinner here
        // You'll need to provide data for the spinner and set up a listener to handle selection
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("About App");
        builder.setMessage("This is a description of your app.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
