package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;
import java.util.HashSet;

import java.util.Set;

public class SubjectExpertiseNew extends AppCompatActivity {

    private FlexboxLayout majorSubjectContainer, minorSubjectContainer;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject_expertise_new);



        majorSubjectContainer = findViewById(R.id.majorsubjectContainer);
        minorSubjectContainer = findViewById(R.id.minorSubjectContainer);
        Button editSubjects = findViewById(R.id.button22);


        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        loadSubjects();

        editSubjects.setOnClickListener(v -> {
            Intent intent = new Intent(SubjectExpertiseNew.this, SubjectExpertiseNewEdit.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void loadSubjects() {
        majorSubjectContainer.removeAllViews();
        minorSubjectContainer.removeAllViews();

        Set<String> majorSubjects = sharedPreferences.getStringSet("MAJOR_SUBJECTS", new HashSet<>());
        Set<String> minorSubjects = sharedPreferences.getStringSet("MINOR_SUBJECTS", new HashSet<>());

        // Load major subjects
        for (String subject : majorSubjects) {
            addChip(subject, majorSubjectContainer, "MAJOR_SUBJECTS");
        }

        // Load minor subjects
        for (String subject : minorSubjects) {
            addChip(subject, minorSubjectContainer, "MINOR_SUBJECTS");
        }

    }

        private void addChip (String subject, FlexboxLayout container, String category){
            Chip chip = new Chip(this);
            chip.setText(subject);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                Set<String> updatedSubjects = new HashSet<>(sharedPreferences.getStringSet(category, new HashSet<>()));
                updatedSubjects.remove(subject);
                sharedPreferences.edit().putStringSet(category, updatedSubjects).apply();
                loadSubjects();
            });

            container.addView(chip);
        }
}