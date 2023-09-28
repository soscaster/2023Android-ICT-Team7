package vn.usth.team7camera;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import vn.usth.team7camera.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int PERMISSION_CODE = 100;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private String [] titles;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the saved language preference
        String selectedLanguage = getSavedLanguagePreference();

        String title = getResources().getString(R.string.app_name);
        setTitle(title);
        // Set the locale based on the saved language preference
        if (TextUtils.isEmpty(selectedLanguage)) {
            selectedLanguage = getCurrentLanguage();
        }
        setLocale(selectedLanguage);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestRuntimePermission();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            titles = getResources().getStringArray(R.array.tab_titles);
            PagerAdapter adapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), titles);
            ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
            pager.setOffscreenPageLimit(1);
            pager.setAdapter(adapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
            tabLayout.setupWithViewPager(pager);

            int[] tabIcons = { R.drawable.baseline_videocam_24, R.drawable.baseline_event_24,
                    R.drawable.baseline_image_24, R.drawable.baseline_settings_24 };

            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setCustomView(R.layout.custom_tab);
                    ImageView tabIcon = tab.getCustomView().findViewById(R.id.tabIcon);
                    TextView tabText = tab.getCustomView().findViewById(R.id.tabText);

                    // Set icon and text for the tab
                    tabIcon.setImageResource(tabIcons[i]);
                    tabText.setText(titles[i]); // titles is an array of your tab text
                }
            }
        } else {
            TextView textView = new TextView(this);
            textView.setText(R.string.check_Internet);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            setContentView(textView);
        }
    }

    //Save last language configuration
    private String getSavedLanguagePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        Log.d("language", getSystemLanguage());
        return sharedPreferences.getString("selectedLanguage", getSystemLanguage());
    }
    public void saveLanguagePreference(String languageCode) {
        SharedPreferences sharedPreferences = getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedLanguage", languageCode);
        editor.apply();
    }
    private String getCurrentLanguage() {
        Configuration configuration = getResources().getConfiguration();
        return configuration.locale.getLanguage();
    }

    //set language
    private void setLocale(String languageCode) {
        if (!getCurrentLanguage().equals(languageCode)) {
            saveLanguagePreference(languageCode);
            Locale newLocale = new Locale(languageCode);
            Locale.setDefault(newLocale);

            Resources resources = getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(newLocale);
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());

            recreate();
        }
    }

    // Get the system language
    private String getSystemLanguage() {
        Configuration configuration = Resources.getSystem().getConfiguration();
        return configuration.locale.getLanguage();
    }

    private void requestRuntimePermission(){
        if (ActivityCompat.checkSelfPermission(this, PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, R.string.granted_notice, Toast.LENGTH_SHORT).show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION_STORAGE)){
            AlertDialog.Builder reqbuild = new AlertDialog.Builder(this);
            reqbuild.setMessage(R.string.ask_permission_text)
                    .setTitle(R.string.ask_permission_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.OK_text, (dialog, which) ->{
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{PERMISSION_STORAGE}, PERMISSION_CODE);
                    })
                    .setNegativeButton(R.string.cancel_text, ((dialog, which) -> dialog.dismiss()));
            reqbuild.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_STORAGE}, PERMISSION_CODE);
        }
    }

    private void handleAddCameraButtonClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.addCamera));
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_camera, null);
        builder.setView(dialogView);
        final TextView editTextCameraName = dialogView.findViewById(R.id.editTextCameraName);
        final TextView editTextAddress = dialogView.findViewById(R.id.editTextAddress);
        builder.setPositiveButton(getResources().getString(R.string.save), null);
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String cameraName = editTextCameraName.getText().toString();
                final String cameraLink = editTextAddress.getText().toString();

                if (cameraName.isEmpty() || cameraLink.isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.antiEmpty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!cameraLink.startsWith("http://") && !cameraLink.startsWith("https://")) {
                    Toast.makeText(MainActivity.this, R.string.invalidLink, Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference camerasRef = FirebaseDatabase.getInstance().getReference("camseecamxa");
                camerasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot cameraSnapshot : userSnapshot.getChildren()) {
                                String existingCameraLink = cameraSnapshot.child("cameraLink").getValue(String.class);
                                String existingCameraName = cameraSnapshot.child("cameraName").getValue(String.class);

                                if (existingCameraLink != null && existingCameraLink.equals(cameraLink)) {
                                    Toast.makeText(MainActivity.this, getString(R.string.camPortIPExist), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (existingCameraName != null && existingCameraName.equals(cameraName)) {
                                    Toast.makeText(MainActivity.this, getString(R.string.camNameExist), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }

                        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference userCamerasRef = camerasRef.child(currentUserUid);
                        String cameraKey = userCamerasRef.push().getKey();
                        userCamerasRef.child(cameraKey).child("cameraName").setValue(cameraName);
                        userCamerasRef.child(cameraKey).child("cameraLink").setValue(cameraLink);
                        Toast.makeText(MainActivity.this, R.string.camAdded, Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        recreate();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getBaseContext(), getString(R.string.database_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private class NetworkOperation extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... urls) {
            int responseCode = 0;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                responseCode = connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseCode;
        }

        protected void onPostExecute(Integer result) {
            if (result != HttpURLConnection.HTTP_OK) {
                Toast.makeText(MainActivity.this, R.string.unreachableLink, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar1, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addCamera) {
            handleAddCameraButtonClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
