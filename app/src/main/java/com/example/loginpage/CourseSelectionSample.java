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

public class CourseSelectionSample extends AppCompatActivity {
    private static final String TAG = "CourseSelectionSample";
    private AutoCompleteTextView educationDropdown, coursesDropdown;
    private Map<String, String> educationLevelMap = new HashMap<>(); // EducationLevelName -> EducationLevelID
    private Map<String, String> coursesMap = new HashMap<>(); // CourseName -> CourseID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_selection_sample);

        educationDropdown = findViewById(R.id.educationDropdown);
        coursesDropdown = findViewById(R.id.coursesDropdown);

        Log.d(TAG, "onCreate: Activity Started");
        loadEducationLevels();
    }

    private void loadEducationLevels() {
        String query = "SELECT EducationLevelID, EducationLevelName FROM EducationLevel WHERE active = 'true' ORDER BY EducationLevelName";
        Log.d(TAG, "loadEducationLevels: Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "loadEducationLevels: No education levels found!");
                    Toast.makeText(CourseSelectionSample.this, "No Education Levels Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> educationLevels = new ArrayList<>();
                educationLevelMap.clear();

                for (Map<String, String> row : result) {
                    String id = row.get("EducationLevelID");
                    String name = row.get("EducationLevelName");

                    Log.d(TAG, "Education Level Retrieved - ID: " + id + ", Name: " + name);
                    educationLevels.add(name);
                    educationLevelMap.put(name, id);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(CourseSelectionSample.this, android.R.layout.simple_dropdown_item_1line, educationLevels);
                educationDropdown.setAdapter(adapter);

                Log.d(TAG, "loadEducationLevels: Adapter set with values: " + educationLevels);
            }
        });

        educationDropdown.setOnClickListener(v -> {
            Log.d(TAG, "educationDropdown clicked - Showing dropdown");
            educationDropdown.showDropDown();
        });

        educationDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedEducationLevel = (String) parent.getItemAtPosition(position);
            String educationLevelID = educationLevelMap.get(selectedEducationLevel);
            Log.d(TAG, "Education Level Selected: " + selectedEducationLevel + " (ID: " + educationLevelID + ")");
            if (educationLevelID != null) {
                loadCourses(educationLevelID);
            }
        });
    }

    private void loadCourses(String educationLevelID) {
        String query = "SELECT CourseID, CourseName FROM Courses WHERE EducationLevelID = '" + educationLevelID + "' AND active = 'true' ORDER BY CourseName";
        Log.d(TAG, "loadCourses: Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "loadCourses: No courses found for EducationLevelID: " + educationLevelID);
                    Toast.makeText(CourseSelectionSample.this, "No courses found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> courses = new ArrayList<>();
                coursesMap.clear();

                for (Map<String, String> row : result) {
                    String id = row.get("CourseID");
                    String name = row.get("CourseName");

                    Log.d(TAG, "Course Retrieved - ID: " + id + ", Name: " + name);
                    courses.add(name);
                    coursesMap.put(name, id);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(CourseSelectionSample.this, android.R.layout.simple_dropdown_item_1line, courses);
                coursesDropdown.setAdapter(adapter);
                coursesDropdown.showDropDown(); // Ensure dropdown displays immediately

                Log.d(TAG, "loadCourses: Adapter set with values: " + courses);
            }
        });

        coursesDropdown.setOnClickListener(v -> {
            Log.d(TAG, "coursesDropdown clicked - Showing dropdown");
            coursesDropdown.showDropDown();
        });

        coursesDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCourse = (String) parent.getItemAtPosition(position);
            String courseID = coursesMap.get(selectedCourse);
            Log.d(TAG, "Course Selected: " + selectedCourse + " (ID: " + courseID + ")");
        });
    }

}



