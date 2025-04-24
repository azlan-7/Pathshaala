package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.UserWiseGrades;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradesTaught extends AppCompatActivity {

    private static final String TAG = "GradesTaught";

    private AutoCompleteTextView subjectsDropdown, gradesDropdown;
    private Button saveButton;
    private ImageView backButton;

    private Map<String, String> subjectMap = new HashMap<>(); // SubjectName -> SubjectID
    private Map<String, String> gradeMap = new HashMap<>(); // GradeName -> GradeID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades_taught);

        subjectsDropdown = findViewById(R.id.subjectsDropdown);
        gradesDropdown = findViewById(R.id.gradesDropdown);
        saveButton = findViewById(R.id.button29);
        backButton = findViewById(R.id.imageView152);

        // Clear dropdowns initially
        clearDropdown(subjectsDropdown);
        clearDropdown(gradesDropdown);

        // Load data into dropdowns
        loadAndPopulateDropdowns();
        fetchUserGradesTaught();

        saveButton.setOnClickListener(v -> insertUserGradesTaught());
        backButton.setOnClickListener(v -> startActivity(new Intent(GradesTaught.this, TeachersInfoSubSection.class)));
    }

    private void loadAndPopulateDropdowns() {
        loadSubjects();
        loadGrades();
    }

    private void insertUserGradesTaught() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1); // Fetch logged-in User ID

        if (userId == -1) {
            Log.e(TAG, "❌ User ID not found in SharedPreferences!");
            return;
        }

        String selectedSubject = subjectsDropdown.getText().toString().trim();
        String selectedGrade = gradesDropdown.getText().toString().trim();

        if (selectedSubject.isEmpty() || selectedGrade.isEmpty()) {
            Toast.makeText(this, "Please select both Subject and Grade!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch corresponding IDs for Subject and Grade from dropdown data (stored in a map)
        Integer subjectId = subjectMap.get(selectedSubject) != null ? Integer.parseInt(subjectMap.get(selectedSubject)) : null;
        Integer gradeId = gradeMap.get(selectedGrade) != null ? Integer.parseInt(gradeMap.get(selectedGrade)) : null;

        if (subjectId == null || gradeId == null) {
            Log.e(TAG, "❌ Could not find SubjectID or GradeID for the selected values!");
            return;
        }

        int currentProfession = 1; // Example: 1 = Current Profession
        String selfReferralCode = "ABC123"; // Example Self Referral Code

        Log.d(TAG, "📌 Inserting Grades Taught for UserID: " + userId);

        // ✅ Call `UserWiseGradesInsert` Method
        DatabaseHelper.UserWiseGradesInsert(this, "1", userId, currentProfession, gradeId, subjectId, selfReferralCode,
                new DatabaseHelper.DatabaseCallback() {
                    @Override
                    public void onMessage(String message) {
                        Log.d(TAG, "✅ Database Response: " + message);
                        runOnUiThread(() -> {
                            Toast.makeText(GradesTaught.this, message, Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onSuccess(List<Map<String, String>> result) {
                        // Not required here
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "❌ Database Error: " + error);
                    }
                });
    }

    private void fetchUserGradesTaught() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1); // Fetch logged-in User ID

        if (userId == -1) {
            Log.e(TAG, "❌ User ID not found in SharedPreferences!");
            return;
        }

        Log.d(TAG, "📌 Fetching Grades Taught for UserID: " + userId);

        DatabaseHelper.UserWiseGradesSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseGradesResultListener() {
            @Override
            public void onQueryResult(List<UserWiseGrades> userWiseGradesList) {
                if (userWiseGradesList == null || userWiseGradesList.isEmpty()) {
                    Log.d(TAG, "⚠️ No grades data found for UserID: " + userId);
                    Toast.makeText(GradesTaught.this, "No Grades Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ Clear previous entries before adding new ones
                List<String> subjectNames = new ArrayList<>();
                List<String> gradeNames = new ArrayList<>();

                for (UserWiseGrades grade : userWiseGradesList) {
                    Log.d(TAG, "✅ Retrieved Subject: " + grade.getSubjectName() + " | Grade: " + grade.getGradename() + " | UserID: " + grade.getUserId());
                    // Add only the SubjectName to the list, and make sure to add each subject only once.
                    if (!subjectNames.contains(grade.getSubjectName())) {
                        subjectNames.add(grade.getSubjectName());
                    }
                    if (!gradeNames.contains(grade.getGradename())) {
                        gradeNames.add(grade.getGradename());
                    }
                }
                populateDropdown(subjectsDropdown, subjectNames);
                populateDropdown(gradesDropdown, gradeNames);
            }
        });
    }

    private void loadSubjects() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1); // Fetch logged-in User ID

        if (userId != -1) {
            // Updated query to fetch subjects based on userid
            String query = "SELECT UserWiseSubject.subjectid, subject.SubjectName as description " +
                    "FROM UserWiseSubject " +
                    "INNER JOIN subject ON UserWiseSubject.subjectid = subject.subjectid " +
                    "WHERE userid = " + userId + " AND UserWiseSubject.Active = 1";
            Log.d(TAG, "Executing query: " + query);

            DatabaseHelper.loadDataFromDatabase(this, query, result -> {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "No subjects found for user: " + userId);
                    Toast.makeText(this, "No Subjects Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> subjects = new ArrayList<>();
                subjectMap.clear();

                for (int i = 0; i < result.size(); i++) {
                    Map<String, String> row = result.get(i);
                    String subjectName = row.get("description");
                    String subjectId = row.get("subjectid");

                    subjects.add(subjectName);
                    subjectMap.put(subjectName, subjectId);
                }

                populateDropdown(subjectsDropdown, subjects);
                subjectsDropdown.setOnClickListener(v -> subjectsDropdown.showDropDown());
            });
        } else {
            Log.e(TAG, "User ID not found in SharedPreferences!");
            Toast.makeText(this, "Please login to select subjects.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadGrades() {
        String query = "SELECT GradeID, GradeName FROM Grades WHERE active = 'true' ORDER BY GradeName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
            if (result == null || result.isEmpty()) {
                Log.e(TAG, "No grades found!");
                Toast.makeText(this, "No Grades Found!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> grades = new ArrayList<>();
            gradeMap.clear();

            for (Map<String, String> row : result) {
                grades.add(row.get("GradeName"));
                gradeMap.put(row.get("GradeName"), row.get("GradeID"));
            }

            populateDropdown(gradesDropdown, grades);
            gradesDropdown.setOnClickListener(v -> gradesDropdown.showDropDown());
        });
    }

    private void populateDropdown(AutoCompleteTextView dropdown, List<String> data) {
        // Clear the adapter and the text before setting new data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, data);
        dropdown.setAdapter(adapter);
    }

    private void clearDropdown(AutoCompleteTextView dropdown) {
        dropdown.setText(""); // Clear the text
        dropdown.setAdapter(null); // Clear the adapter
    }
}
