package vn.usth.team7camera;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class CameraListManager {
    private static final String PREFS_NAME = "CameraPrefs";
    private static final String CAMERA_NAMES_KEY = "cameraNames";
    private static final String CAMERA_PORTS_KEY = "cameraPorts";
    private static final String CAMERA_IPS_KEY = "cameraIPs";

    private final Context context;

    public CameraListManager(Context context) {
        this.context = context;
    }

    public void addCameraName(String cameraName) {
        Set<String> cameraNames = getCameraNames();
        cameraNames.add(cameraName);
        saveCameraNames(cameraNames);
    }

    public String[] getCameraNamesArray() {
        Set<String> cameraNames = getCameraNames();
        return cameraNames.toArray(new String[0]);
    }

    Set<String> getCameraNames() {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getStringSet(CAMERA_NAMES_KEY, new HashSet<>());
    }

    void saveCameraNames(Set<String> cameraNames) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(CAMERA_NAMES_KEY, cameraNames);
        editor.apply();
    }

    Set<String> getCameraPorts() {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getStringSet(CAMERA_PORTS_KEY, new HashSet<>());
    }

    void saveCameraPorts(Set<String> cameraNames) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(CAMERA_PORTS_KEY, cameraNames);
        editor.apply();
    }

    Set<String> getCameraIPs() {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getStringSet(CAMERA_IPS_KEY, new HashSet<>());
    }

    void saveCameraIPs(Set<String> cameraNames) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(CAMERA_IPS_KEY, cameraNames);
        editor.apply();
    }
}
