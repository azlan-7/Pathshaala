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

public class GradesStudentAdd extends AppCompatActivity {

    private AutoCompleteTextView subjectsDropdown, gradesDropdown, topicDropdown;
    private Button saveButton;
    private ImageView backButton;
    private static final String TAG = "GradesStudentAdd";

    private Map<String, String> subjectMap = new HashMap<>(); // SubjectName -> SubjectID
    private Map<String, String> gradeMap = new HashMap<>(); // GradeName -> GradeID
    private Map<String, String> topicMap = new HashMap<>(); // TopicName -> TopicID


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades_student_add);

        subjectsDropdown = findViewById(R.id.subjectsDropdown);
        gradesDropdown = findViewById(R.id.gradesDropdown);
//        topicDropdown = findViewById(R.id.topicsDropdown);
        saveButton = findViewById(R.id.button29);
        backButton = findViewById(R.id.imageView152);

        String selectedSubject = subjectsDropdown.getText().toString().trim();
        String selectedGrade = gradesDropdown.getText().toString().trim();

        String subjectID = subjectMap.get(selectedSubject);
        String gradeID = gradeMap.get(selectedGrade);

//        if (subjectID != null && gradeID != null) {
//            loadTopics(subjectID, gradeID); // ✅ Correct: Pass subjectID and gradeID
//        } else {
//            Log.e(TAG, "❌ Subject ID or Grade ID is missing!");
//        }


        // Load data into dropdowns
        loadSubjects();
        loadGrades();
        fetchUserGradesTaught();

        saveButton.setOnClickListener(v -> insertUserGradesTaught());

        backButton.setOnClickListener(v -> startActivity(new Intent(GradesStudentAdd.this, StudentsInfo.class)));

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
                            Toast.makeText(GradesStudentAdd.this, message, Toast.LENGTH_SHORT).show();

                            // Navigate to GradesStudentView after inserting successfully
                            Intent intent = new Intent(GradesStudentAdd.this, GradesStudentView.class);
                            startActivity(intent);
                            finish();
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
                    Toast.makeText(GradesStudentAdd.this, "No Grades Found!", Toast.LENGTH_SHORT).show();
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

//    private void loadTopics(String subjectID, String gradeID) {
//        String query = "SELECT topicsID, topicsName FROM topics WHERE active = 'true' AND SubjectID = '" + subjectID + "' AND GradesID = '" + gradeID + "' ORDER BY topicsName";
//        Log.d(TAG, "Executing query: " + query);
//
//        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
//            if (result == null || result.isEmpty()) {
//                Log.e(TAG, "No topics found for Subject ID: " + subjectID + " and Grade ID: " + gradeID);
//                Toast.makeText(this, "No Topics Found!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            List<String> topics = new ArrayList<>();
//            topicMap.clear();
//
//            for (Map<String, String> row : result) {
//                String id = row.get("topicsID");
//                String name = row.get("topicsName");
//                Log.d(TAG, "Topic Retrieved - ID: " + id + ", Name: " + name);
//                topics.add(name);
//                topicMap.put(name, id);
//            }
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, topics);
//            topicDropdown.setAdapter(adapter);
//            topicDropdown.showDropDown();
//        });
//
//        topicDropdown.setOnClickListener(v -> topicDropdown.showDropDown());
//
//        topicDropdown.setOnItemClickListener((parent, view, position, id) -> {
//            String selectedTopic = (String) parent.getItemAtPosition(position);
//            String topicID = topicMap.get(selectedTopic);
//            Log.d(TAG, "Selected Topic: " + selectedTopic + " (ID: " + topicID + ")");
//        });
//    }


}
