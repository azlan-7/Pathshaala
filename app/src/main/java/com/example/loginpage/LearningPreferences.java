package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginpage.models.SharedViewModel;

import java.util.Arrays;
import java.util.List;

public class LearningPreferences extends AppCompatActivity {

    private AutoCompleteTextView subjectDropdown;
    private TextView selectedSubjectsText;
    private SharedViewModel sharedViewModel;

    private final String[] subjects = {
            "Accounting", "Art", "Biology", "Business Studies",
            "Chemistry", "Computer Science", "Economics", "English",
            "Environmental Science", "Geography", "History", "Mathematics",
            "Music", "Philosophy", "Physics", "Political Science",
            "Psychology", "Sociology", "Statistics"
    }; // Lexicographically sorted subjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_learning_preferences);

        Button btnSave = findViewById(R.id.button34);
        subjectDropdown = findViewById(R.id.editTextText47);
        selectedSubjectsText = findViewById(R.id.selectedSubjectsText);

        // Initialize ViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Set up ArrayAdapter for AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects);
        subjectDropdown.setAdapter(adapter);

        // Show dropdown when clicked
        subjectDropdown.setOnClickListener(v -> subjectDropdown.showDropDown());

        // Handle selection from dropdown
        subjectDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSubject = parent.getItemAtPosition(position).toString();

            // Add to ViewModel
            sharedViewModel.addSubject(selectedSubject);
            Toast.makeText(this, selectedSubject + " added!", Toast.LENGTH_SHORT).show();

            // Clear the dropdown input after selection
            subjectDropdown.setText("");
        });

        // Observe ViewModel data to update UI automatically
        sharedViewModel.getSelectedSubjects().observe(this, this::updateSelectedSubjectsText);

        // Save button click -> Navigate to StudentsInfo
        btnSave.setOnClickListener(v -> {
            List<String> selectedSubjects = sharedViewModel.getSelectedSubjects().getValue();
            if (selectedSubjects != null && !selectedSubjects.isEmpty()) {
                Toast.makeText(this, "Subjects saved: " + Arrays.toString(selectedSubjects.toArray()), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No subjects selected!", Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(LearningPreferences.this, StudentsInfo.class);
            startActivity(intent);
            finish();
        });
    }

    // Function to update UI with selected subjects
    private void updateSelectedSubjectsText(List<String> subjects) {
        if (subjects.isEmpty()) {
            selectedSubjectsText.setText("No subjects selected");
        } else {
            selectedSubjectsText.setText("Selected: " + String.join(", ", subjects));
        }
    }
}
