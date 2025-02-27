package com.example.loginpage;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkExperienceDropdown extends AppCompatActivity {
    private static final String TAG = "WorkExperienceDropdown";
    private AutoCompleteTextView workExperienceDropdown;
    private Map<String, String> workExperienceMap = new HashMap<>(); // WorkExperience -> WorkExperienceID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_experience_dropdown);

        workExperienceDropdown = findViewById(R.id.workExperienceDropdown);
        Log.d(TAG, "onCreate: Activity Started");

        loadWorkExperience();
    }

    private void loadWorkExperience() {
        String query = "SELECT WorkExperienceID, WorkExperience FROM WorkExperience WHERE active = 'true' ORDER BY WorkExperience";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "No Work Experience records found!");
                    Toast.makeText(WorkExperienceDropdown.this, "No Work Experience Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> workExperiences = new ArrayList<>();
                workExperienceMap.clear();

                for (Map<String, String> row : result) {
                    String id = row.get("WorkExperienceID");
                    String name = row.get("WorkExperience");

                    Log.d(TAG, "Work Experience Retrieved - ID: " + id + ", Name: " + name);
                    workExperiences.add(name);
                    workExperienceMap.put(name, id);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(WorkExperienceDropdown.this, android.R.layout.simple_dropdown_item_1line, workExperiences);
                workExperienceDropdown.setAdapter(adapter);
                Log.d(TAG, "Adapter set with values: " + workExperiences);
            }
        });

        workExperienceDropdown.setOnClickListener(v -> {
            Log.d(TAG, "workExperienceDropdown clicked - Showing dropdown");
            workExperienceDropdown.showDropDown();
        });

        workExperienceDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedExperience = (String) parent.getItemAtPosition(position);
            String experienceID = workExperienceMap.get(selectedExperience);
            Log.d(TAG, "Work Experience Selected: " + selectedExperience + " (ID: " + experienceID + ")");
        });
    }
}
