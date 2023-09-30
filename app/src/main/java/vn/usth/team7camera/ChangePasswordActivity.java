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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextView currentPassword;
    private TextView newPassword;

    private MaterialButton confirmBtn;
    private FirebaseAuth mAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();

        currentPassword = findViewById(R.id.current_pwd);
        newPassword = findViewById(R.id.new_pwd);
        confirmBtn = findViewById(R.id.confirm_btn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });
    }

    private void updatePassword() {
        String oldPwd = currentPassword.getText().toString();
        String newPwd = newPassword.getText().toString();

        if (oldPwd.isEmpty()) {
            Toast.makeText(ChangePasswordActivity.this, "Current password field is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPwd.isEmpty()) {
            Toast.makeText(ChangePasswordActivity.this, "New password field is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (oldPwd.equals(newPwd)) {
            Toast.makeText(ChangePasswordActivity.this, "New password cannot match with old password", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPwd);

            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPwd)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.pwd_updated), Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, getString(R.string.pwd_not_correct), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

    }
}
