package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LearningPreferences extends AppCompatActivity {

    private AutoCompleteTextView subjectDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_learning_preferences);

        Button btnSave = findViewById(R.id.button34);
        subjectDropdown = findViewById(R.id.editTextText47);

        // List of subjects
        String[] subjects = {
                "Mathematics", "Physics", "Chemistry", "Biology",
                "History", "Geography", "English", "Computer Science",
                "Economics", "Psychology", "Political Science", "Business Studies",
                "Art", "Music", "Environmental Science", "Philosophy"
        };

        // Set up ArrayAdapter for AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects);
        subjectDropdown.setAdapter(adapter);

        // Show dropdown when clicked
        subjectDropdown.setOnClickListener(v -> subjectDropdown.showDropDown());

        // Save button click -> Navigate to StudentsInfo
        btnSave.setOnClickListener(v -> {
            Intent intent = new Intent(LearningPreferences.this, StudentsInfo.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
