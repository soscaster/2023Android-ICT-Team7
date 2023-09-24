package vn.usth.team7camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

        Preference loginPreference = findPreference("pref_login");
        if (loginPreference != null) {
            loginPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Open LoginActivity here
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
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
                "The application is written in Java and using some libraries for media encode/decode.\n\n" +
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