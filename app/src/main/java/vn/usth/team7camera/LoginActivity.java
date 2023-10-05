package vn.usth.team7camera;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;

public class LoginActivity extends AppCompatActivity {
    private TextView emailTextView, passwordTextView, forgotPasswordTextView;
    private MaterialButton Btn;
    private MaterialButton BtnReg;
    private ImageView BtnGoogle;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressBar progressbar;
    private static final int RC_SIGN_IN = 9001; // Request code for Google Sign-In

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        emailTextView = findViewById(R.id.username);
        passwordTextView = findViewById(R.id.password);
        forgotPasswordTextView = findViewById(R.id.forgotpass);
        Btn = findViewById(R.id.loginbtn);
        BtnReg = findViewById(R.id.regnew);
        BtnGoogle = findViewById(R.id.googlebtn);
        progressbar = findViewById(R.id.progressbar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });

        BtnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegistrationActivity.class);
                startActivity(intent);
            }
        });
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent (getBaseContext(),ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        BtnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginGoogleAccount();
            }
        });
    }

    private void loginUserAccount() {
        progressbar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), getString(R.string.email_null), Toast.LENGTH_SHORT).show();
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
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressbar.setVisibility(View.GONE);
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser.isEmailVerified()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),getString(R.string.user_not_yet_verify), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressbar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            progressbar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.email_not_valid), Toast.LENGTH_SHORT).show();
        }
    }

    private void loginGoogleAccount() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, getString(R.string.google_sign_in_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userUid = user.getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("camseecamxa");
                    DatabaseReference userTableReference = databaseReference.child(userUid);
                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("userdb");
                    DatabaseReference userTableReference2 = databaseReference2.child(userUid);

                    // Check if the user already exists in the authentication database
                    boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                    if (isNewUser) {
                        // User is a new user, upload default avatar
                        Bitmap defaultAvatarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.def_user);
                        defaultAvatarBitmap = cropImage(defaultAvatarBitmap);
                        defaultAvatarBitmap = resizeImage(defaultAvatarBitmap, 256, 256);

                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            defaultAvatarBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                            byte[] data = baos.toByteArray();

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            StorageReference defaultAvatarRef = storageRef.child("users/" + userUid + "/avatar.jpg");

                            defaultAvatarRef.putBytes(data)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        // Default avatar uploaded successfully
                                    })
                                    .addOnFailureListener(e -> {
                                        // Default avatar upload failed
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    userTableReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                userTableReference.setValue("");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    userTableReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                userTableReference2.child("fullName").setValue("N/A"); // Create an empty table for the user
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    Toast.makeText(getApplicationContext(), getString(R.string.google_sign_in_success), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    recreate();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.google_sign_in_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}