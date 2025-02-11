package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShowUniqueID extends AppCompatActivity {
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_unique_id);

        TextView userTypeText = findViewById(R.id.textViewUserType);
        TextView uniqueIdText = findViewById(R.id.textViewUniqueId);
        Button buttonContinue = findViewById(R.id.button18);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userType = sharedPreferences.getString("USER_TYPE", "Teacher"); // Default: Teacher

        String userType = getIntent().getStringExtra("USER_TYPE");
        String uniqueId = getIntent().getStringExtra("UNIQUE_ID");

        userTypeText.setText("You Selected: " + userType);
        uniqueIdText.setText("Your ID: " + uniqueId);


        buttonContinue.setOnClickListener(v -> {
            if ("Teacher".equals(userType)) {
                // Redirect to TeachersInfo.java
                Intent intent = new Intent(ShowUniqueID.this, TeachersInfo.class);
                startActivity(intent);
            } else {
                // Redirect to StudentsInfo.java (if you have a different screen for students)
                Intent intent = new Intent(ShowUniqueID.this, StudentsInfo.class);
                startActivity(intent);
            }
            finish();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}