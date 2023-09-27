package vn.usth.team7camera;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
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

public class RegistrationActivity extends AppCompatActivity {
    private EditText emailTextView, passwordTextView, re_passwordTextView;
    private Button btn;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.reg_email);
        passwordTextView = findViewById(R.id.reg_password);
        re_passwordTextView = findViewById(R.id.reg_re_password);
        btn = findViewById(R.id.reg_btn);
        progressbar = findViewById(R.id.progressbar);

        // Set on Click Listener on Registration button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerNewUser();
            }
        });
    }

    private void registerNewUser() {
        // show the visibility of progress bar to show loading
        progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password,re_password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();
        re_password = re_passwordTextView.getText().toString();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Please enter password!!", Toast.LENGTH_LONG).show();
                progressbar.setVisibility(View.GONE);
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(getApplicationContext(),"The password needs to be at least 6 characters", Toast.LENGTH_LONG).show();
                progressbar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(re_password) || !re_password.equals(password)) {
                Toast.makeText(getApplicationContext(), "Please match passwords!!", Toast.LENGTH_LONG).show();
                progressbar.setVisibility(View.GONE);
                return;
            }

            // create new user or register new user
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        final FirebaseUser user = mAuth.getCurrentUser();
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RegistrationActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@androidx.annotation.NonNull Exception e) {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(RegistrationActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // hide the progress bar
                        progressbar.setVisibility(View.GONE);
                        // if the user created intent to login activity
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // Registration failed
                        Toast.makeText(getApplicationContext(), "Registration failed!!" + " Please try again later", Toast.LENGTH_LONG).show();
                        // hide the progress bar
                        progressbar.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            progressbar.setVisibility(View.GONE);
            Toast.makeText(this, "Enter valid Email address !", Toast.LENGTH_SHORT).show();
        }
    }
}