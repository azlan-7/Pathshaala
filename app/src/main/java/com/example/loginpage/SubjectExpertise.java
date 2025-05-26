package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.UserWiseSubject;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubjectExpertise extends AppCompatActivity {

    private static final String TAG = "SubjectExpertise";
    private FlexboxLayout selectedContainer;
    private TextView selectedCount;
    private SharedPreferences sharedPreferences;
    private int userId;
    private Set<String> selectedSubjects = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject_expertise);

        Button saveButton = findViewById(R.id.button15);
        selectedContainer = findViewById(R.id.selectedSubjectContainer);
        selectedCount = findViewById(R.id.tvSelectedCount);
        ImageView editIcon = findViewById(R.id.editIcon); // Pencil icon
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("USER_ID", -1);

        // Initialize selectedSubjects from shared preferences
        selectedSubjects = new HashSet<>(sharedPreferences.getStringSet("SELECTED_SUBJECTS", new HashSet<>()));

        // Get selected subjects from Intent and DB
        loadAndDisplaySubjects();

        // Navigate back to edit subjects
        editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(SubjectExpertise.this, SubjectExpertiseNewOne.class);
            startActivity(intent);
            finish();
        });

        // Navigate to TeachersInfoSubSection
        saveButton.setOnClickListener(v -> {
            Intent intent = new Intent(SubjectExpertise.this, TeachersInfoSubSection.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadAndDisplaySubjects() {
        if (userId == -1) {
            Log.e(TAG, "Cannot load subjects: User ID not found.");
            selectedCount.setText("No subjects selected");
            return;
        }

        DatabaseHelper.UserWiseSubjectSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseSubjectResultListener() {
            @Override
            public void onQueryResult(List<UserWiseSubject> userSubjects) {
                if (userSubjects == null || userSubjects.isEmpty()) {
                    Log.d(TAG, "⚠️ No subjects found for the user.");
                    if (selectedSubjects.isEmpty()) {
                        selectedCount.setText("No subjects selected");
                    } else {
                        displaySubjects(selectedSubjects);
                    }
                    return;
                }

                // Use a Set to store unique subject names
                Set<String> uniqueSubjects = new HashSet<>();
                selectedContainer.removeAllViews(); // Clear previous chips

                // Add subjects from the database
                for (UserWiseSubject subject : userSubjects) {
                    uniqueSubjects.add(subject.getSubjectName());
                }

                // Add subjects from SharedPreferences, if any
                uniqueSubjects.addAll(selectedSubjects);

                //display
                if(uniqueSubjects.isEmpty()){
                    selectedCount.setText("No subjects selected");
                    return;
                }

                selectedCount.setText(uniqueSubjects.size() + " Selected");
                displaySubjects(uniqueSubjects);
            }
        });
    }

    private void displaySubjects(Set<String> subjects) {
        selectedContainer.removeAllViews();
        for (String subject : subjects) {
            addRetrievedChip(subject);
        }
    }

    private void addRetrievedChip(String subjectName) {
        Chip chip = new Chip(this);
        chip.setPadding(30, 10, 30, 10);
        chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        chip.setTextAppearance(R.style.ChipTextStyle);
        chip.setText(subjectName);
        chip.setCheckable(false);
        chip.setChipBackgroundColorResource(R.color.blue);
        chip.setTextColor(ContextCompat.getColor(this, R.color.white));
        selectedContainer.addView(chip);
    }
}

