package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class SubjectExpertise extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject_expertise);


        Button saveButton = findViewById(R.id.button15);

        // Set Click Listener
        saveButton.setOnClickListener(v -> {
            // Create Intent to move to TeachersEducation.java
            Intent intent = new Intent(SubjectExpertise.this, TeachersInfo.class);
            startActivity(intent); // Start the new activity
            finish(); // Optional: Finish current activity so user can't go back
        });

        FlexboxLayout selectedContainer = findViewById(R.id.selectedSubjectContainer);
        TextView selectedCount = findViewById(R.id.tvSelectedCount);
        ImageView editIcon = findViewById(R.id.editIcon); // Pencil icon

        ArrayList<String> selectedSubjects = getIntent().getStringArrayListExtra("selectedSubjects");


        if (selectedSubjects != null && !selectedSubjects.isEmpty()) {
            selectedCount.setText(selectedSubjects.size() + " Selected");

            for (String subject : selectedSubjects) {
                Chip chip = new Chip(this);
                chip.setPadding(30, 10, 30, 10);  // Increase inner padding
                chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);  // Center align text
                chip.setChipStrokeWidth(0f);  // Removes the border
                chip.setTextAppearance(R.style.ChipTextStyle);
                chip.setText(subject);
                chip.setCheckable(false);
                chip.setChipBackgroundColorResource(R.color.blue);
                chip.setTextColor(ContextCompat.getColor(this, R.color.white));// Selected background
                selectedContainer.addView(chip);
            }
        }

        // Click edit icon to go back to previous screen
        editIcon.setOnClickListener(v -> finish());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}