package vn.usth.team7camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseStorage storage;
    private StorageReference storageReference;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();

        if (currentUser!= null) {
            String userEmail = currentUser.getEmail();

            RelativeLayout rl_email = findViewById(R.id.RL_email);
            RelativeLayout rl_resetPwd = findViewById(R.id.RL_reset_pwd);
            RelativeLayout rl_deleteAcc = findViewById(R.id.RL_del_acc);

            TextView email = findViewById(R.id.user_email);
            TextView resetPwd = findViewById(R.id.reset_password);
            TextView deleteAcc = findViewById(R.id.del_acc);


//            ImageView profilePic = findViewById(R.id.profile_picture);

//            profilePic.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                            .setDisplayName("Jane Q. User")
//                            .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
//                            .build();
//
//                    currentUser.updateProfile(profileUpdates)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(ProfileActivity.this, "User profile updated", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                }
//            });


            email.setText("E-mail: " + userEmail);
            resetPwd.setText(R.string.change_pwd);
            deleteAcc.setText(R.string.delete_acc);



            email.setOnClickListener(new View.OnClickListener(){
                @Override
                public  void onClick(View v) {

                }
            });
            rl_resetPwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);

                }
            });
//            rl_deleteAcc.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    new AlertDialog.Builder(ProfileActivity.this)
//                            .setTitle("Delete Account")
//                            .setMessage("Are you sure?")
//                            // "Yes" button on the left is pretty cringe
//                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            })
//                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    AuthCredential credential = EmailAuthProvider.getCredential("user@example.com", "password1234");
//
//                                    currentUser.reauthenticate(credential)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    Toast.makeText(ProfileActivity.this, "Reauthenticated", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//
//                                    currentUser.delete()
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        Toast.makeText(ProfileActivity.this, "Account Deleted", Toast.LENGTH_SHORT).show();
//                                                    } else {
//                                                        Toast.makeText(ProfileActivity.this, "Delete account failed", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                }
//                                            });
//                                }
//                            })
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .show();
//                }
//            });


        } else {
            // Nothing yet
        }
    }



}