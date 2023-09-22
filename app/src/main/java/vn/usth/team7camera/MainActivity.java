package vn.usth.team7camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int PERMISSION_CODE = 100;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private CameraListManager cameraListManager;
    private String [] titles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestRuntimePermission();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            cameraListManager = new CameraListManager(this);

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
                String cameraName = editTextCameraName.getText().toString();
                String camAddress = editTextAddress.getText().toString();

                if (cameraName.isEmpty() || camAddress.isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.antiEmpty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!camAddress.startsWith("http://") && !camAddress.startsWith("https://")) {
                    Toast.makeText(MainActivity.this, R.string.invalidLink, Toast.LENGTH_SHORT).show();
                    return;
                }

                new NetworkOperation().execute(camAddress);

                List<String> existingCameraNames = new ArrayList<>(cameraListManager.getCameraNames());
                List<String> existingCameraLinks = new ArrayList<>(cameraListManager.getCameraLinks());

                if (existingCameraNames.contains(cameraName)) {
                    Toast.makeText(MainActivity.this, R.string.camNameExist, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (existingCameraLinks.contains(camAddress)) {
                    Toast.makeText(MainActivity.this, R.string.camPortIPExist, Toast.LENGTH_SHORT).show();
                    return;
                }

                int index = existingCameraNames.indexOf(cameraName);
                if (index != -1) {
                    // The camera name exists, update it
                    existingCameraNames.set(index, cameraName);
                    existingCameraLinks.set(index, camAddress);
                } else {
                    // The camera name does not exist, add it
                    existingCameraNames.add(cameraName);
                    existingCameraLinks.add(camAddress);
                }
                cameraListManager.saveCameraNames(new HashSet<>(existingCameraNames));
                cameraListManager.saveCameraLinks(new HashSet<>(existingCameraLinks));
                Toast.makeText(MainActivity.this, R.string.camAdded, Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                recreate();
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
