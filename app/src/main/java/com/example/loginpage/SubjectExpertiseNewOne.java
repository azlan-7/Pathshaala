package com.example.loginpage;
import com.example.loginpage.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.adapters.SubjectAdapterOne;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import java.util.HashSet;
import java.util.Set;

public class SubjectExpertiseNewOne extends AppCompatActivity {

    private FlexboxLayout subjectContainer;
    private RecyclerView subjectsRecyclerView;
    private SharedPreferences sharedPreferences;
    private Button saveButton;
    private Set<String> selectedSubjects;
    private SubjectAdapterOne adapter; // Adapter reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject_expertise_new_one);

        subjectContainer = findViewById(R.id.subjectContainer);
        subjectsRecyclerView = findViewById(R.id.subjectsRecycler);
        saveButton = findViewById(R.id.button21);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        selectedSubjects = new HashSet<>(sharedPreferences.getStringSet("SELECTED_SUBJECTS", new HashSet<>()));

        // Setup RecyclerView for all subjects
        setupRecyclerView();

        // Load selected subjects into the FlexboxLayout
        loadSelectedSubjects();

        // Save subjects on button click
        saveButton.setOnClickListener(v -> saveSubjects());

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void loadSelectedSubjects() {
        subjectContainer.removeAllViews(); // Only remove views, not the data

        selectedSubjects = new HashSet<>(sharedPreferences.getStringSet("SELECTED_SUBJECTS", new HashSet<>())); // Re-load stored subjects

        for (String subject : selectedSubjects) {
            addChip(subject);
        }

        // Ensure checkboxes in RecyclerView match stored subjects
        adapter.updateSelection(selectedSubjects);
    }


    private void addChip(String subject) {
        Chip chip = new Chip(this);
        chip.setText(subject);
        chip.setTextSize(18);
        chip.setPadding(30, 10, 30, 10);
        chip.setTypeface(ResourcesCompat.getFont(this, R.font.work_sans_bold));
        chip.setCloseIconVisible(true);

        // Set colors
        chip.setChipBackgroundColorResource(R.color.light_blue);
        chip.setTextColor(ContextCompat.getColor(this, R.color.blue));
        chip.setCloseIconTintResource(R.color.blue);

        // Correctly remove the single chip and update UI
        chip.setOnCloseIconClickListener(v -> {
            if (selectedSubjects.contains(subject)) {
                selectedSubjects.remove(subject);  // Remove the specific subject from memory

                // Update SharedPreferences
                sharedPreferences.edit().putStringSet("SELECTED_SUBJECTS", new HashSet<>(selectedSubjects)).apply();

                // Remove chip from the layout
                subjectContainer.removeView(chip);

                // **Update the RecyclerView checkboxes**
                adapter.updateSelection(selectedSubjects);
            }
        });

        subjectContainer.addView(chip);
    }




    private void setupRecyclerView() {
        String[] subjects = {
                "Mathematics", "Physics", "Chemistry", "Biology", "History", "Music",
                "Art", "English", "Geography", "Economics", "Political Science", "Psychology",
                "Sociology", "Philosophy", "Physical Education", "Computer Science", "Literature",
                "Law", "Business Studies", "Civics", "Environmental Science", "Agriculture",
                "Engineering", "Medicine", "Nursing", "Accounting"
        };

        adapter = new SubjectAdapterOne(this, subjects, selectedSubjects);
        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        subjectsRecyclerView.setAdapter(adapter);
    }

    private void saveSubjects() {
        sharedPreferences.edit().putStringSet("SELECTED_SUBJECTS", selectedSubjects).apply();
        Intent intent = new Intent(SubjectExpertiseNewOne.this, TeachersInfo.class);
        startActivity(intent);
        finish();
    }
}
