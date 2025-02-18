package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TeachersDashboard extends AppCompatActivity {

    private TextView welcomeText;
    private ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_dashboard);

        welcomeText = findViewById(R.id.textView91); // Reference to "Hello" TextView
        profileIcon = findViewById(R.id.imageView119);

        // Retrieve the first name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("FIRST_NAME", "User"); // Default "User" if no name saved

        // Update the TextView with the user's name
        welcomeText.setText("Hello, " + firstName);

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(TeachersDashboard.this, TeachersInfo.class);
            startActivity(intent);
        });

        // Handle insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
