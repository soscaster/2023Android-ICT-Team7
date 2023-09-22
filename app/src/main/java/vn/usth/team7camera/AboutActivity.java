package vn.usth.team7camera;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Set the project description content
        TextView projectDescriptionContent = findViewById(R.id.textProjectDescriptionContent);
        projectDescriptionContent.setText("This project is a GUI-friendly application that allows users to watch and manage security camera(s). The application is written in (Java or Kotlin) and using some libraries for media encode/decode.");

        // Set the project status content
        TextView projectStatusContent = findViewById(R.id.textProjectStatusContent);
        projectStatusContent.setText("We started this project from Sep 11, 2023 and have not yet finished. The project was initially planned to be finished in 2 weeks with basic GUI and functions. We're in the development stage, so lots of bugs will exist.");
    }
}
