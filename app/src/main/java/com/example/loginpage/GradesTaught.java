package com.example.loginpage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.UserWiseGrades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradesTaught extends AppCompatActivity {

    private AutoCompleteTextView subjectsDropdown, gradesDropdown, topicsDropdown;
    private Button saveButton;
    private static final String TAG = "GradesTaught";

    private Map<String, String> subjectMap = new HashMap<>(); // SubjectName -> SubjectID
    private Map<String, String> gradeMap = new HashMap<>(); // GradeName -> GradeID


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades_taught);

        subjectsDropdown = findViewById(R.id.subjectsDropdown);
        gradesDropdown = findViewById(R.id.gradesDropdown);
//        topicsDropdown = findViewById(R.id.topicsDropdown);
        saveButton = findViewById(R.id.button29);

        // Load data into dropdowns
        loadSubjects();
        loadGrades();
//        loadTopics();
        fetchUserGradesTaught();

        saveButton.setOnClickListener(v -> insertUserGradesTaught());

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
                        // Not required in this case
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

                // ✅ Populate Dropdowns based on retrieved data
                for (UserWiseGrades grade : userWiseGradesList) {
                    Log.d(TAG, "✅ Retrieved Grade: " + grade.getGradename() + " | UserID: " + grade.getUserId());

                    subjectsDropdown.setText(grade.getSubjectName(), false);
                    gradesDropdown.setText(grade.getGradename(), false);
                }
            }
        });
    }

    private void loadSubjects() {
        String query = "SELECT SubjectID, SubjectName FROM Subject WHERE active = 'true' ORDER BY SubjectName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
            if (result == null || result.isEmpty()) {
                Log.e(TAG, "No subjects found!");
                Toast.makeText(this, "No Subjects Found!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> subjects = new ArrayList<>();
            subjectMap.clear();

            for (Map<String, String> row : result) {
                subjects.add(row.get("SubjectName"));
                subjectMap.put(row.get("SubjectName"), row.get("SubjectID"));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects);
            subjectsDropdown.setAdapter(adapter);
        });

        subjectsDropdown.setOnClickListener(v -> subjectsDropdown.showDropDown());
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

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, grades);
            gradesDropdown.setAdapter(adapter);
        });

        gradesDropdown.setOnClickListener(v -> gradesDropdown.showDropDown());
    }
}
