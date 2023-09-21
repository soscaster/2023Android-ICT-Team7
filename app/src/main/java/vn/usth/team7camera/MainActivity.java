package vn.usth.team7camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.HashSet;
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
//
//        TextView username =(TextView) findViewById(R.id.username);
//        TextView password =(TextView) findViewById(R.id.password);
//
//        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);
//
//        //admin and admin
//
//        loginbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(username.getText().toString().equals("admin") && password.getText().toString().equals("admin")){
//                    //correct
//                    Toast.makeText(MainActivity.this,"LOGIN SUCCESSFUL",Toast.LENGTH_SHORT).show();
//                }else
//                    //incorrect
//                    Toast.makeText(MainActivity.this,"LOGIN FAILED !!!",Toast.LENGTH_SHORT).show();
//            }
//        });

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
        builder.setTitle("Add Camera");

        // Inflate the layout for the dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_camera, null);
        builder.setView(dialogView);

        final TextView editTextCameraName = dialogView.findViewById(R.id.editTextCameraName);
        final TextView editTextAddress = dialogView.findViewById(R.id.editTextAddress);

        builder.setPositiveButton("Save", null); // Set to null. We override the onclick

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

                // Get the existing camera names, IPs and ports
                Set<String> existingCameraNames = new HashSet<>(cameraListManager.getCameraNames());
                Set<String> existingCameraLinks = new HashSet<>(cameraListManager.getCameraLinks());

                // Check if the new camera name already exists
                if (existingCameraNames.contains(cameraName)) {
                    Toast.makeText(MainActivity.this, R.string.camNameExist, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if a camera with the same IP already exists
                if (existingCameraLinks.contains(camAddress)) {
                    Toast.makeText(MainActivity.this, R.string.camPortIPExist, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the new camera name, IP and port to the existing list
                existingCameraNames.add(cameraName);
                existingCameraLinks.add(camAddress);

                // Save the updated camera names, IPs and ports list
                cameraListManager.saveCameraNames(existingCameraNames);
                cameraListManager.saveCameraLinks(existingCameraLinks);
                Toast.makeText(MainActivity.this, R.string.camAdded, Toast.LENGTH_SHORT).show();


                recreate();
                dialog.dismiss();
            }
        });
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
