package vn.usth.team7camera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_KEY = "LOGIN_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(MainIntent);


        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        if (pref.getBoolean(LOGIN_KEY,false)) {
            //has login
            startActivity(new Intent(this, MainActivity.class));
            //must finish this activity(the login activity will not be shown)
            finish();
        }
        else {
            // Mark login
            pref.edit().putBoolean(LOGIN_KEY,true).apply();

            //Do something

            TextView username =(TextView) findViewById(R.id.username);
            TextView password =(TextView) findViewById(R.id.password);

            MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);

            //admin and admin

            loginbtn.setOnClickListener(new View.OnClickListener( )
            {
                @Override
                public void onClick(View v) {
                    if(username.getText().toString().equals("admin") && password.getText().toString().equals("admin")){
                        //correct
                        Toast.makeText(LoginActivity.this,"LOGIN SUCCESSFUL",Toast.LENGTH_SHORT).show();

                    }else
                        //incorrect
                        Toast.makeText(LoginActivity.this,"LOGIN FAILED !!!",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}