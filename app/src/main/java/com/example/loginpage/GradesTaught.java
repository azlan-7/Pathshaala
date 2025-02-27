package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginpage.models.GradesTaughtModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GradesTaught extends AppCompatActivity {

    private AutoCompleteTextView etGradeLevel, etMedium;
    private EditText etSubject, etTopic;
    private Button btnSave;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grades_taught);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        etGradeLevel = findViewById(R.id.editTextText29);
        etMedium = findViewById(R.id.editTextText32);
        etSubject = findViewById(R.id.editTextText30);
        etTopic = findViewById(R.id.editTextText31);
        btnSave = findViewById(R.id.button29);

        // Initialize dropdown options
        setupDropdownMenus();

        // Save Button Click
        btnSave.setOnClickListener(v -> saveGrades());
    }

    private void setupDropdownMenus() {
        // Grade Level Options
        String[] gradeLevels = {
                "Primary", "Secondary", "High School Secondary (10th)",
                "High School Senior (12th)", "Diploma", "Undergraduate",
                "Postgraduate", "Doctorate"
        };

        // Medium of Instruction Options
        String[] mediums = {"English", "Hindi", "Bengali", "Marathi", "Tamil"};

        // Set Adapter for Grade Level
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, gradeLevels);
        etGradeLevel.setAdapter(gradeAdapter);
        etGradeLevel.setOnClickListener(v -> etGradeLevel.showDropDown());

        // Set Adapter for Medium of Instruction
        ArrayAdapter<String> mediumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mediums);
        etMedium.setAdapter(mediumAdapter);
        etMedium.setOnClickListener(v -> etMedium.showDropDown());
    }

    private void saveGrades() {
        String subject = etSubject.getText().toString().trim();
        String topic = etTopic.getText().toString().trim();
        String gradeLevel = etGradeLevel.getText().toString().trim();
        String medium = etMedium.getText().toString().trim();

        if (subject.isEmpty() || topic.isEmpty() || gradeLevel.isEmpty() || medium.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<GradesTaughtModel> gradesList = loadGrades();
        gradesList.add(new GradesTaughtModel(subject, topic, gradeLevel, medium));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString("GRADES_TAUGHT_LIST", gson.toJson(gradesList));
        editor.apply();

        Toast.makeText(this, "Grades Taught Saved!", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, GradesTaughtView.class));
        finish();
    }

    private List<GradesTaughtModel> loadGrades() {
        String json = sharedPreferences.getString("GRADES_TAUGHT_LIST", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<GradesTaughtModel>>() {}.getType();
        return (json == null) ? new ArrayList<>() : gson.fromJson(json, type);
    }
}
