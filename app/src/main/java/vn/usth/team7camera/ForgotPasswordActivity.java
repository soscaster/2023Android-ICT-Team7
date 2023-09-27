package vn.usth.team7camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView resetEmail;
    private MaterialButton resetBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        resetEmail = findViewById(R.id.reset_email);
        resetBtn = findViewById(R.id.reset_btn);

        resetBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {sendResetPasswordEmail();}
        });
    }
    private void sendResetPasswordEmail() {
        String email;
        email = resetEmail.getText().toString();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(),getString(R.string.email_null), Toast.LENGTH_SHORT).show();
                return;
            }
            
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Password reset E-mail sent!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Reset E-mail failed to sent.", Toast.LENGTH_SHORT).show();
                        }
                    }
            });
        } else {
            Toast.makeText(this, getString(R.string.email_not_valid), Toast.LENGTH_SHORT).show();
        }
    }
}