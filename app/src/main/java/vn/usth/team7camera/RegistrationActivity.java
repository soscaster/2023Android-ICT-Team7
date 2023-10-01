package vn.usth.team7camera;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {
    private EditText nameTextView, emailTextView, passwordTextView, re_passwordTextView;
    private Button btn;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView avatarImageView;
    private Uri avatarUri;
    private Bitmap avatarBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();

        nameTextView = findViewById(R.id.reg_name);
        emailTextView = findViewById(R.id.reg_email);
        passwordTextView = findViewById(R.id.reg_password);
        re_passwordTextView = findViewById(R.id.reg_re_password);
        btn = findViewById(R.id.reg_btn);
        progressbar = findViewById(R.id.progressbar);


        avatarImageView = findViewById(R.id.avatar_image);
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerNewUser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                avatarUri = data.getData();
                if (avatarUri != null) {
                    try {
                        avatarBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), avatarUri);
                        avatarBitmap = cropImage(avatarBitmap);
                        avatarBitmap = resizeImage(avatarBitmap, 256, 256);
                        avatarImageView.setImageBitmap(avatarBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private Bitmap cropImage(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        return Bitmap.createBitmap(source, 0, 0, size, size);
    }

    private Bitmap resizeImage(Bitmap source, int targetWidth, int targetHeight) {
        return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);
    }

    private void uploadAvatar() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userUid = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("userdb");
            DatabaseReference userTableReference = databaseReference.child(userUid);
            String name = nameTextView.getText().toString();
            userTableReference.child("fullName").setValue(name);

            if (avatarUri != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference avatarRef = storageRef.child("users/" + userUid + "/avatar.jpg");

                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    avatarRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(RegistrationActivity.this, "Avatar uploaded", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(RegistrationActivity.this, "Avatar upload failed", Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference defaultAvatarRef = storageRef.child("users/" + userUid + "/avatar.jpg");

                Bitmap defaultAvatarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.def_user);
                defaultAvatarBitmap = cropImage(defaultAvatarBitmap);
                defaultAvatarBitmap = resizeImage(defaultAvatarBitmap, 256, 256);

                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    defaultAvatarBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] data = baos.toByteArray();

                    defaultAvatarRef.putBytes(data)
                            .addOnSuccessListener(taskSnapshot -> {
//                                Toast.makeText(RegistrationActivity.this, "Default avatar uploaded", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
//                                Toast.makeText(RegistrationActivity.this, "Default avatar upload failed", Toast.LENGTH_SHORT).show();
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void registerNewUser() {
        progressbar.setVisibility(View.VISIBLE);

        String name, email, password, re_password;
        name = nameTextView.getText().toString();
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();
        re_password = re_passwordTextView.getText().toString();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (name.isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.name_null), Toast.LENGTH_SHORT).show();
                progressbar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), getString(R.string.pwd_null), Toast.LENGTH_SHORT).show();
                progressbar.setVisibility(View.GONE);
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(getApplicationContext(), getString(R.string.pwd_char_miss), Toast.LENGTH_SHORT).show();
                progressbar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(re_password) || !re_password.equals(password)) {
                Toast.makeText(getApplicationContext(), getString(R.string.pwd_not_match), Toast.LENGTH_SHORT).show();
                progressbar.setVisibility(View.GONE);
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        final FirebaseUser user = mAuth.getCurrentUser();
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RegistrationActivity.this, getString(R.string.email_verify_sent) + user.getEmail(), Toast.LENGTH_SHORT).show();
                                String userUid = user.getUid();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("camseecamxa");
                                DatabaseReference userTableReference = databaseReference.child(userUid);
                                userTableReference.setValue("");
                                uploadAvatar();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@androidx.annotation.NonNull Exception e) {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(RegistrationActivity.this, getString(R.string.email_verify_sent_failed), Toast.LENGTH_SHORT).show();
                            }
                        });

                        // hide the progress bar
                        progressbar.setVisibility(View.GONE);
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.reg_failed), Toast.LENGTH_SHORT).show();
                        progressbar.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            progressbar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.email_not_valid), Toast.LENGTH_SHORT).show();
        }
    }
}