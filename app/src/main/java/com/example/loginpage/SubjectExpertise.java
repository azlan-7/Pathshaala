package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "SubjectExpertise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject_expertise);

        Button saveButton = findViewById(R.id.button15);
        FlexboxLayout selectedContainer = findViewById(R.id.selectedSubjectContainer);
        TextView selectedCount = findViewById(R.id.tvSelectedCount);
        ImageView editIcon = findViewById(R.id.editIcon); // Pencil icon

        // Retrieve selected subjects from Intent
        ArrayList<String> selectedSubjects = getIntent().getStringArrayListExtra("selectedSubjects");

        // Debug log
        Log.d(TAG, "📥 Received subjects from Intent: " + selectedSubjects);

        if (selectedSubjects != null && !selectedSubjects.isEmpty()) {
            selectedCount.setText(selectedSubjects.size() + " Selected");

            for (String subject : selectedSubjects) {
                Chip chip = new Chip(this);
                chip.setPadding(30, 10, 30, 10); // Increase inner padding
                chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                chip.setTextAppearance(R.style.ChipTextStyle);
                chip.setText(subject);
                chip.setCheckable(false);
                chip.setChipBackgroundColorResource(R.color.blue);
                chip.setTextColor(ContextCompat.getColor(this, R.color.white));
                selectedContainer.addView(chip);
            }
        } else {
            Log.e(TAG, "⚠️ No subjects received! Showing default message.");
            selectedCount.setText("No subjects selected");
        }

        // Navigate back to edit subjects
        editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(SubjectExpertise.this, SubjectExpertiseNewOne.class);
            startActivity(intent);
            finish();
        });

        // Navigate to TeachersInfo
        saveButton.setOnClickListener(v -> {
            Intent intent = new Intent(SubjectExpertise.this, TeachersInfo.class);
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
