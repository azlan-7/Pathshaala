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

import java.util.ArrayList;
import java.util.List;

public class TeachersEducation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_education);


        Button saveButton = findViewById(R.id.button16);

        // Set Click Listener
        saveButton.setOnClickListener(v -> {
            // Create Intent to move to ProfessionalDetails.java
            Intent intent = new Intent(TeachersEducation.this, ProfessionalDetails.class);
            startActivity(intent); // Start the new activity
            finish(); // Optional: Close TeachersEducation so user can't go back
        });

        AutoCompleteTextView etDegree = findViewById(R.id.textView38);
        AutoCompleteTextView yearDropdown = findViewById(R.id.yearDropdown);

        List<String> years = new ArrayList<>();
        for (int i = 2025; i >= 1960; i--) {
            years.add(String.valueOf(i));
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years);
        yearDropdown.setAdapter(adapter);

        yearDropdown.setOnClickListener(v -> yearDropdown.showDropDown());

        String[] distinctions = {"Doctorate", "Post Graduate", "College Graduate", "High School", "Middle School"};

        ArrayAdapter<String> adapterDegree = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, distinctions);
        etDegree.setAdapter(adapterDegree);

        etDegree.setOnClickListener(v -> etDegree.showDropDown());




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}