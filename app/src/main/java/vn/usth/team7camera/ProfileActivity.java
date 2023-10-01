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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseStorage storage;
    private StorageReference storageReference;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private ImageView avatarImageView;
    private TextView userNameTextView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (currentUser!= null) {
            String userEmail = currentUser.getEmail();

            RelativeLayout rl_email = findViewById(R.id.RL_email);
            RelativeLayout rl_resetPwd = findViewById(R.id.RL_reset_pwd);
            RelativeLayout rl_deleteAcc = findViewById(R.id.RL_del_acc);

            TextView email = findViewById(R.id.user_email);
            TextView resetPwd = findViewById(R.id.reset_password);
            TextView deleteAcc = findViewById(R.id.del_acc);

            email.setText("E-mail: " + userEmail);
            resetPwd.setText(R.string.change_pwd);
            deleteAcc.setText(R.string.delete_acc);

            // Initialize FirebaseStorage
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            storageReference = FirebaseStorage.getInstance().getReference();
            avatarImageView = findViewById(R.id.avatar_image);
            userNameTextView = findViewById(R.id.user_name);

            loadAvatar();
            fetchUserNameFromDatabase();

            email.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                }
            });
            rl_resetPwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);
                }
            });

            rl_deleteAcc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                }
            });

        } else {
            // Nothing yet
        }
    }

    private void loadAvatar() {
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            StorageReference avatarRef = storageReference.child("users/" + userUid + "/avatar.jpg");

            avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).into(avatarImageView);
            }).addOnFailureListener(exception -> {
                avatarImageView.setImageResource(R.drawable.def_user);
            });
        }
    }

    private void fetchUserNameFromDatabase() {
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userdb/" + userUid + "/fullName");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String fullName = dataSnapshot.getValue(String.class);
                    if (fullName != null) {
                        userNameTextView.setText(getString(R.string.full_name) + fullName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    userNameTextView.setText(getString(R.string.full_name) + getString(R.string.not_available));
                }
            });
        }
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account").setMessage("Are you sure you want to delete your account? This action cannot be undone.").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserAccount();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }
    private void deleteUserAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (user != null) {
            String userUid = user.getUid();
            databaseReference.child("camseecamxa").child(userUid).removeValue();
            databaseReference.child("userdb").child(userUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        deleteFilesInStorage(userUid);
                        deleteFirebaseAuthAccount(user);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to delete user data. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void deleteFilesInStorage(String userUid) {
        StorageReference userFolderRef = FirebaseStorage.getInstance().getReference().child("users").child(userUid);
        userFolderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.delete().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Failed to delete user files. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Failed to list user files. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFirebaseAuthAccount(FirebaseUser user) {
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to delete account. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}