package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginpage.TeachersRepository;
import com.example.loginpage.models.SharedViewModel;
import com.example.loginpage.models.TeachersList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LearningPreferences extends AppCompatActivity {

    private static final String PREFS_NAME = "LearningPreferences";
    private static final String SELECTED_SUBJECTS_KEY = "selected_subjects";

    private AutoCompleteTextView subjectDropdown;
    private TextView selectedSubjectsText;
    private TextView teacherRecommendations;
    private SharedViewModel sharedViewModel;
    private SharedPreferences sharedPreferences;

    private final String[] subjects = {
            "Accounting", "Art", "Biology", "Business Studies",
            "Chemistry", "Computer Science", "Economics", "English",
            "Environmental Science", "Geography", "History", "Mathematics",
            "Music", "Philosophy", "Physics", "Political Science",
            "Psychology", "Sociology", "Statistics"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_learning_preferences);

        Button btnSave = findViewById(R.id.button34);
        Button clearBtn = findViewById(R.id.clearPreferenceBtn);
        subjectDropdown = findViewById(R.id.editTextText47);
        selectedSubjectsText = findViewById(R.id.selectedSubjectsText);
        teacherRecommendations = findViewById(R.id.teacherRecommendations);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Initialize ViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Set up dropdown adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects);
        subjectDropdown.setAdapter(adapter);

        // Show dropdown when clicked
        subjectDropdown.setOnClickListener(v -> subjectDropdown.showDropDown());

        // Handle subject selection
        subjectDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSubject = parent.getItemAtPosition(position).toString();
            sharedViewModel.addSubject(selectedSubject);
            saveSubjectToPreferences(selectedSubject);
            Toast.makeText(this, selectedSubject + " added!", Toast.LENGTH_SHORT).show();
            subjectDropdown.setText(""); // Clear input
        });

        // Observe ViewModel to update UI
        sharedViewModel.getSelectedSubjects().observe(this, this::updateSelectedSubjectsText);

        // Load saved preferences
        loadSubjectsFromPreferences();
        updateRecommendedTeachers();

        // Save button action
        btnSave.setOnClickListener(v -> {
            List<String> selectedSubjects = sharedViewModel.getSelectedSubjects().getValue();
            if (selectedSubjects != null && !selectedSubjects.isEmpty()) {
                Toast.makeText(this, "Subjects saved: " + selectedSubjects, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No subjects selected!", Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(LearningPreferences.this, StudentsInfo.class));
            finish();
        });

        // Clear button action
        clearBtn.setOnClickListener(v -> {
            sharedViewModel.clearSubjects(); // Clear ViewModel
            clearSubjectsFromPreferences();  // Clear SharedPreferences
            updateRecommendedTeachers();     // Refresh recommendations
            Toast.makeText(this, "Preferences cleared!", Toast.LENGTH_SHORT).show();
        });
    }

    /** Saves a subject to SharedPreferences */
    private void saveSubjectToPreferences(String subject) {
        Set<String> subjectsSet = new HashSet<>(sharedPreferences.getStringSet(SELECTED_SUBJECTS_KEY, new HashSet<>()));
        subjectsSet.add(subject);
        sharedPreferences.edit().putStringSet(SELECTED_SUBJECTS_KEY, subjectsSet).apply();
        updateSelectedSubjectsText(new ArrayList<>(subjectsSet));
        updateRecommendedTeachers();
    }

    /** Loads subjects from SharedPreferences into ViewModel */
    private void loadSubjectsFromPreferences() {
        Set<String> savedSubjects = sharedPreferences.getStringSet(SELECTED_SUBJECTS_KEY, new HashSet<>());
        sharedViewModel.setSubjects(new ArrayList<>(savedSubjects));
    }

    /** Clears subjects from SharedPreferences */
    private void clearSubjectsFromPreferences() {
        sharedPreferences.edit().remove(SELECTED_SUBJECTS_KEY).apply();
        updateSelectedSubjectsText(new ArrayList<>()); // Update UI
    }

    /** Updates the selected subjects TextView */
    private void updateSelectedSubjectsText(List<String> subjects) {
        if (subjects.isEmpty()) {
            selectedSubjectsText.setText("No subjects selected");
        } else {
            selectedSubjectsText.setText("Selected: " + String.join(", ", subjects));
        }
    }

    /** Fetches recommended teachers based on selected subjects */
    private List<TeachersList> recommendTeachers() {
        List<String> preferredSubjects = new ArrayList<>(sharedPreferences.getStringSet(SELECTED_SUBJECTS_KEY, new HashSet<>()));
        List<TeachersList> allTeachers = TeachersRepository.getTeachersList();
        List<TeachersList> matchedTeachers = new ArrayList<>();

        for (TeachersList teacher : allTeachers) {
            for (String subject : preferredSubjects) {
                if (teacher.getSubjects().contains(subject)) {
                    matchedTeachers.add(teacher);
                    break;
                }
            }
        }
        return matchedTeachers;
    }

    /** Updates recommended teachers UI */
    private void updateRecommendedTeachers() {
        List<TeachersList> recommendedTeachers = recommendTeachers();
        if (recommendedTeachers.isEmpty()) {
            teacherRecommendations.setText("No matching teachers found!");
        } else {
            StringBuilder builder = new StringBuilder("Recommended Teachers:\n");
            for (TeachersList teacher : recommendedTeachers) {
                builder.append("- ").append(teacher.getName()).append("\n");
            }
            teacherRecommendations.setText(builder.toString());
        }
    }
}