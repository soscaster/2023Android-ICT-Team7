package vn.usth.team7camera;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView; // Add this import

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {
    private String addCameraText1;
    private String addCameraText2;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference camerasRef = database.getReference("camseecamxa");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private RecyclerView recyclerView; // Declare recyclerView
    private CameraAdapter cameraAdapter; // Declare cameraAdapter
    private static final int REQUEST_CODE_DATE_TIME_PICKER = 123;

    private String cameraName1;
    private String cameraLink1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DATE_TIME_PICKER && resultCode == Activity.RESULT_OK) {
            boolean isInvalidTime = data.getBooleanExtra("invalidTime", false);

            if (isInvalidTime) {
                // Handle the invalid time condition here, e.g., show a toast
                Toast.makeText(requireContext(), getResources().getString(R.string.invalidTime), Toast.LENGTH_SHORT).show();
            }
            else {
                long selectedDateTimeMillis = data.getLongExtra("selectedDateTime", 0);
                Log.d("selected", String.valueOf(selectedDateTimeMillis));
                Intent intent = new Intent(requireContext(), CameraActivity2.class);
                intent.putExtra("selectedDateTime", selectedDateTimeMillis);
                intent.putExtra("cameraIndex", cameraName1);
                intent.putExtra("videoPath", cameraLink1);
                startActivity(intent);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize Firebase references (already created in Step 1)
        addCameraText1 = getResources().getString(R.string.addCameraText1);
        TextView addCamera1 = view.findViewById(R.id.noCamera1);
        addCamera1.setText(addCameraText1);
        addCameraText2 = getResources().getString(R.string.addCameraText2);
        TextView addCamera2 = view.findViewById(R.id.noCamera2);
        addCamera2.setText(addCameraText2);

        ImageView noCameraIcon = view.findViewById(R.id.noCameraIcon);
        noCameraIcon.setImageResource(R.drawable.baseline_add_2);
        addCamera1.setVisibility(View.GONE);
        addCamera2.setVisibility(View.GONE);
        noCameraIcon.setVisibility(View.GONE);
        // Fetch camera names from Firebase
        fetchCameraNames(addCamera1, addCamera2, noCameraIcon);

        return view;
    }

    private void fetchCameraNames(TextView addCamera1,TextView addCamera2, ImageView noCameraIcon) {
        if (currentUser != null) {
            camerasRef.child(String.valueOf(currentUser.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        addCamera1.setVisibility(View.GONE);
                        addCamera2.setVisibility(View.GONE);
                        noCameraIcon.setVisibility(View.GONE);

                        List<String> cameraNamesList = new ArrayList<>();
                        List<String> cameraLinksList = new ArrayList<>();

                        for (DataSnapshot cameraSnapshot : snapshot.getChildren()) {
                            String cameraName = cameraSnapshot.child("cameraName").getValue(String.class);
                            String cameraLink = cameraSnapshot.child("cameraLink").getValue(String.class);
                            cameraNamesList.add(cameraName);
                            cameraLinksList.add(cameraLink);
                        }

                        cameraAdapter = new CameraAdapter(cameraNamesList, cameraLinksList, getChildFragmentManager());
                        recyclerView.setAdapter(cameraAdapter);

                        // Set item click listener for the RecyclerView
                        cameraAdapter.setOnCameraItemClickListener(new CameraAdapter.OnCameraItemClickListener() {
                            @Override
                            public void onCameraItemClick(String cameraName, String cameraLink) {
                                // Display the date and time picker dialog
                                cameraName1 = cameraName;
                                cameraLink1 = cameraLink;
                                DateTimePickerDialogFragment dateTimePickerDialogFragment = new DateTimePickerDialogFragment();
                                dateTimePickerDialogFragment.setTargetFragment(EventsFragment.this, REQUEST_CODE_DATE_TIME_PICKER);
                                dateTimePickerDialogFragment.show(getFragmentManager(), "DateTimePicker");
                            }
                        });
                    }
                    else {
                        addCamera1.setVisibility(View.VISIBLE);
                        addCamera2.setVisibility(View.VISIBLE);
                        noCameraIcon.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                    Toast.makeText(getActivity(), getString(R.string.database_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            addCamera1.setVisibility(View.VISIBLE);
            addCamera2.setVisibility(View.VISIBLE);
            noCameraIcon.setVisibility(View.VISIBLE);
        }
    }
}
