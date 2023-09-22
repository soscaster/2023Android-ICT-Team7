//package vn.usth.team7camera;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.res.Configuration;
//import android.content.res.Resources;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.SeekBar;
//import android.widget.Spinner;
//import android.widget.Switch;
//import android.widget.TextView;
//
//import androidx.fragment.app.Fragment;
//
//import java.util.Locale;
//
//public class SettingsFragment extends Fragment {
//
//    private Spinner spinnerVideoQuality;
//    private SeekBar seekBarAudio;
//    private SeekBar seekBarSensitivity;
//    private String SensivityText;
//    private Spinner spinnerLanguage;
//    private Button buttonAbout;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_settings, container, false);
//
//        Switch switchMotionDetection = view.findViewById(R.id.switchMotionDetection);
//        spinnerVideoQuality = view.findViewById(R.id.spinnerVideoQuality);
//        setupSpinner();
//        seekBarAudio = view.findViewById(R.id.seekBarAudio);
//        seekBarSensitivity = view.findViewById(R.id.seekBarSensitivity);
//        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);
//        setupLanguageSpinner();
//        buttonAbout = view.findViewById(R.id.buttonAbout);
//
//        SensivityText = getResources().getString(R.string.sensitivity);
//        TextView sensitivity = view.findViewById(R.id.SensitivityText);
//        sensitivity.setText(SensivityText);
//        sensitivity.setVisibility(View.GONE);
//        seekBarSensitivity.setVisibility(View.GONE);
//
//
//
//        switchMotionDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                handleMotionDetectionSetting(isChecked);
//
//                // Show or hide sensitivity bar based on switch state
//                if (isChecked) {
//                    sensitivity.setVisibility(View.VISIBLE);
//                    seekBarSensitivity.setVisibility(View.VISIBLE);
//                } else {
//                    sensitivity.setVisibility(View.GONE);
//                    seekBarSensitivity.setVisibility(View.GONE);
//                }
//            }
//        });
//
//
//        buttonAbout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle "About" button click (e.g., open a dialog with app information)
//                showAboutDialog();
//            }
//        });
//
//        seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                handleAudioVolumeSetting(progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // Do nothing
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // Do nothing
//            }
//        });
//
//        return view;
//    }
//
//    private void setupSpinner() {
//        // Example data for the spinner
//        String[] videoQualityOptions = getResources().getStringArray(R.array.video_quality_options);
//
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, videoQualityOptions);
//
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        // Apply the adapter to the spinner
//        spinnerVideoQuality.setAdapter(adapter);
//
//        // Set a listener to handle spinner item selection
//        spinnerVideoQuality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                // Handle the selected item
//                String selectedQuality = parent.getItemAtPosition(position).toString();
//                handleVideoQualitySetting(selectedQuality);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Do nothing
//            }
//        });
//    }
//
//    private void handleVideoQualitySetting(String quality) {
//        // Handle video quality setting here
//        // Example: Update a TextView with the selected quality
//        // TextView qualityTextView = view.findViewById(R.id.textViewQuality);
//        // qualityTextView.setText("Selected Quality: " + quality);
//    }
//
//    private void handleAudioVolumeSetting(int progress) {
//        // Handle audio volume setting here
//        // Example: Update a TextView with the audio volume
//        // TextView volumeTextView = view.findViewById(R.id.textViewVolume);
//        // volumeTextView.setText("Audio Volume: " + progress);
//    }
//
//    private void handleMotionDetectionSetting(boolean isChecked) {
//        // Handle motion detection setting here
//        // Example: Show a toast based on the motion detection state
//        // String message = isChecked ? "Motion detection enabled" : "Motion detection disabled";
//        // Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
//    }
//
//    private void setupLanguageSpinner() {
//        // Example data for the spinner
//        String[] languageOptions = getResources().getStringArray(R.array.languages);
//
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languageOptions);
//
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        // Apply the adapter to the spinner
//        spinnerLanguage.setAdapter(adapter);
//
//        if (getCurrentLanguage().equals("vi")){
//        int spinnerPosition = adapter.getPosition("Tiếng Việt");
//        spinnerLanguage.setSelection(spinnerPosition);}
//
//        // Set a listener to handle spinner item selection
//        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                // Handle the selected item
//                Log.d("lang", getCurrentLanguage());
//                if (getCurrentLanguage().equals("vi")) {
//                    int spinnerPosition = adapter.getPosition("Tiếng Việt");
//                    spinnerLanguage.setSelection(spinnerPosition);
//                }
//                else {
//                    int spinnerPosition = adapter.getPosition("English");
//                    spinnerLanguage.setSelection(spinnerPosition);
//                    }
//                String selectedLanguage = parent.getItemAtPosition(position).toString();
//                if (selectedLanguage.equals(spinnerLanguage.getSelectedItem().toString())==false) {
//                    Log.d("lang2", selectedLanguage);
//                    if (selectedLanguage.equals("English")) {
//                        int spinnerPosition = adapter.getPosition(selectedLanguage);
//                        spinnerLanguage.setSelection(spinnerPosition);
//                        setLocale("");
//                    } else if (selectedLanguage.equals("Tiếng Việt")) {
//                        int spinnerPosition = adapter.getPosition(selectedLanguage);
//                        spinnerLanguage.setSelection(spinnerPosition);
//                        setLocale("vi");
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Do nothing
//            }
//        });
//    }
//
//    private void setLocale(String languageCode) {
//        if (!getCurrentLanguage().equals(languageCode)) {
//            ((MainActivity) getActivity()).saveLanguagePreference(languageCode);
//            Locale newLocale = new Locale(languageCode);
//            Locale.setDefault(newLocale);
//
//            Configuration configuration = getResources().getConfiguration();
//            configuration.setLocale(newLocale);
//            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
//
//            // Restart the activity to apply changes
//            requireActivity().recreate();
//        }
//    }
//
//
//    private String getCurrentLanguage() {
//        Configuration configuration = getResources().getConfiguration();
//        return configuration.locale.getLanguage();
//    }
//
//    private void showAboutDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        builder.setTitle("About App");
//        builder.setMessage("This is a description of your app.");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.create().show();
//    }
//}
package vn.usth.team7camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Locale;


public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load your preferences from XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Find and handle the "About" preference
        Preference aboutPreference = findPreference("pref_about");
        if (aboutPreference != null) {
            aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Handle the "About" preference click event here
                    // For example, show an About dialog
                    showAboutDialog();
                    return true;
                }
            });
        }
        // Find and handle the language preference
        ListPreference languagePreference = findPreference("pref_app_language");
        if (languagePreference != null) {
            languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Handle the language preference change event here
                    String selectedLanguage = (String) newValue;
                    setLocale(selectedLanguage);
                    return true;
                }
            });
        }
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("About App");

        // Build the message with the project description and status
        String aboutMessage = "Description:\n" +
                "This project is a GUI-friendly application that allows users to watch and manage security camera(s). " +
                "The application is written in (Java or Kotlin) and using some libraries for media encode/decode.\n\n" +
                "Project Status:\n" +
                "We started this project from Sep 11, 2023 and have not yet finished. " +
                "The project was initially planned to be finished in 2 weeks with basic GUI and functions. " +
                "We're in the development stage, so lots of bugs will exist.\n\n" +
                "Project Development Details:\n" +
                "The commit history will not show the real contribution of each member. " +
                "We had a lot of discussions at the library to decide what to do for each project's progress. " +
                "In order for the members not to create many structurally different pieces of code, " +
                "we decided to focus on a single machine (leader's potato machine) during almost all development stages.";

        builder.setMessage(aboutMessage);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }


    private void setLocale(String languageCode) {
        if (!getCurrentLanguage().equals(languageCode)) {
            ((MainActivity) getActivity()).saveLanguagePreference(languageCode);
            Locale newLocale = new Locale(languageCode);
            Locale.setDefault(newLocale);

            Configuration configuration = getResources().getConfiguration();
            configuration.setLocale(newLocale);
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

            // Restart the activity to apply changes
            requireActivity().recreate();
        }
    }


    private String getCurrentLanguage() {
        Configuration configuration = getResources().getConfiguration();
        return configuration.locale.getLanguage();
    }
}