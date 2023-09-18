package com.example.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private CameraListManager cameraListManager;
    private String [] titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    private void handleAddCameraButtonClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Camera");

        // Inflate the layout for the dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_camera, null);
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

                // Get the existing camera names
                Set<String> existingCameraNames = new HashSet<>(cameraListManager.getCameraNames());

                // Add the new camera name to the existing list
                existingCameraNames.add(cameraName);

                // Handle saving camera or any desired action
                Toast.makeText(MainActivity.this, "Camera added successfully", Toast.LENGTH_SHORT).show();
                // Save the updated camera names list
                cameraListManager.saveCameraNames(existingCameraNames);

                recreate();
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
