package vn.usth.team7camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;


public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load your preferences from XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        Preference accPreference = findPreference("pref_logged");

        if (accPreference != null) {
            FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (isAdded()) { // Check if the fragment is attached to an activity
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            if (firebaseUser.isEmailVerified()) {
                                String email = firebaseUser.getEmail();
                                if (email != null) {
                                    // Update the summary text of pref_logged to the email string
                                    accPreference.setSummary(email);
                                }
                            } else {
                                accPreference.setSummary(getString(R.string.notLoggedIn));
                            }
                        } else {
                            // If the user is not logged in, set the summary to "Not logged in yet."
                            accPreference.setSummary(getString(R.string.notLoggedIn));
                        }
                    }
                }
            };
            // Add the AuthStateListener to FirebaseAuth
            auth.addAuthStateListener(authListener);
        }


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
        Preference logoutPreference = findPreference("pref_logout");
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                // User is logged in, show logout preference and hide login preference
                if (loginPreference != null) {
                    loginPreference.setVisible(false);
                }
                if (logoutPreference != null) {
                    logoutPreference.setVisible(true);
                }
            } else {
                if (loginPreference != null) {
                    loginPreference.setVisible(true);
                }
                if (logoutPreference != null) {
                    logoutPreference.setVisible(false);
                }
            }
        } else {
            // User is not logged in, show login preference and hide logout preference
            if (loginPreference != null) {
                loginPreference.setVisible(true);
            }
            if (logoutPreference != null) {
                logoutPreference.setVisible(false);
            }
        }

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

        if (logoutPreference != null) {
            logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // Log out the user here using Firebase Authentication
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
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