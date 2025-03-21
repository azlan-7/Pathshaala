package com.example.loginpage;

import static android.content.ContentValues.TAG;

import static com.example.loginpage.MySqliteDatabase.DatabaseHelper.getConnection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.loginpage.adapters.EducationAdapter;
import com.example.loginpage.models.Education;
import com.example.loginpage.models.UserWiseEducation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;

public class StudentsAcademicDetails extends AppCompatActivity {

    private List<Education> educationList;
    private EducationAdapter educationAdapter;
    private SharedPreferences sharedPreferences;
    private RecyclerView educationRecyclerView;
    private AutoCompleteTextView educationLevel, textViewYear, courseName;
    private EditText etInstitution;
    private TextView textViewID;

    private String selectedEducationId;
    private String selectedCourseId;


    private Map<String, String> educationLevelMap = new HashMap<>(); // EducationLevelName -> EducationLevelID
    private Map<String, String> coursesMap = new HashMap<>(); // CourseName -> CourseID




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_academic_details);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        educationRecyclerView = findViewById(R.id.educationRecyclerView);
        educationLevel = findViewById(R.id.textView38);
        textViewYear = findViewById(R.id.yearDropdown);
        etInstitution = findViewById(R.id.editTextText15);
        courseName = findViewById(R.id.educationIdDropdown);

        educationList = new ArrayList<>();
        educationAdapter = new EducationAdapter(this, educationList);
        educationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        educationRecyclerView.setAdapter(educationAdapter);

        // ✅ Ensure dropdown opens on click
        educationLevel.setOnClickListener(v -> {
            Log.d(TAG, "📌 Education dropdown clicked!");
            educationLevel.showDropDown();
        });

        courseName.setOnClickListener(v -> {
            Log.d(TAG, "📌 Course dropdown clicked!");
            courseName.showDropDown();
        });

        // ✅ Ensure dropdown opens when focused
        educationLevel.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Log.d(TAG, "📌 Education dropdown focused!");
                educationLevel.showDropDown();
            }
        });

        courseName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Log.d(TAG, "📌 Course dropdown focused!");
                courseName.showDropDown();
            }
        });




        Log.d(TAG, "onCreate: Activity Started");

        fetchUserEducationDetails(); // ✅ Make sure UI updates in runOnUiThread()

        loadEducationLevels();

        Button saveButton = findViewById(R.id.button16);
        saveButton.setOnClickListener(v -> {
            String institution = etInstitution.getText().toString().trim();
            String degree = educationLevel.getText().toString().trim();
            String course = courseName.getText().toString().trim();
            String year = textViewYear.getText().toString().trim();

            if (institution.isEmpty() || degree.isEmpty() || year.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Insert into DB first, then save locally
            insertUserEducation();

            educationList.add(new Education(institution, degree, year));
            educationAdapter.notifyDataSetChanged();
            saveEducationData();

            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String json = sharedPreferences.getString("EDUCATION_LIST", "");
            Log.d(TAG, "Saved Education Data: " + json);
            Toast.makeText(StudentsAcademicDetails.this, "Education saved successfully!", Toast.LENGTH_SHORT).show();

            etInstitution.setText("");
            educationLevel.setText("");
            courseName.setText("");
            textViewYear.setText("");

            Intent intent = new Intent(StudentsAcademicDetails.this, StudentsAcademicDetailsView.class);
            startActivity(intent);
            finish();
        });

        AutoCompleteTextView etDegree = findViewById(R.id.textView38);
        AutoCompleteTextView yearDropdown = findViewById(R.id.yearDropdown);

        List<String> years = new ArrayList<>();
        for (int i = 2025; i >= 1960; i--) {
            years.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years);
        yearDropdown.setAdapter(adapter);
        yearDropdown.setOnClickListener(v -> yearDropdown.showDropDown());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void insertUserEducation() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Log.e(TAG, "❌ User ID not found in SharedPreferences!");
            return;
        }

        String institution = etInstitution.getText().toString().trim();
        String year = textViewYear.getText().toString().trim();

        if (institution.isEmpty() || selectedEducationId == null || selectedCourseId == null || year.isEmpty()) {
            Log.e(TAG, "❌ Cannot insert. Fields are empty.");
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "📌 Inserting into DB: UserID=" + userId + ", Institution=" + institution +
                ", EducationID=" + selectedEducationId + ", CourseID=" + selectedCourseId + ", Year=" + year);

        DatabaseHelper.UserWiseEducationInsert(this, "1", userId, Integer.parseInt(selectedEducationId), institution,
                Integer.parseInt(year), selectedCourseId, new DatabaseHelper.DatabaseCallback() {

                    @Override
                    public void onSuccess(List<Map<String, String>> result) {
                        Log.d(TAG, "✅ Insert Successful. " + result.size() + " records inserted.");
                    }

                    @Override
                    public void onMessage(String message) {
                        Log.d(TAG, "✅ Insert Response: " + message);
                        Toast.makeText(StudentsAcademicDetails.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "❌ Insert Error: " + error);
                        Toast.makeText(StudentsAcademicDetails.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void fetchUserEducationDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1); // ✅ Fetch as Integer

        if (userId == -1) {
            Log.e(TAG, "❌ User ID not found in SharedPreferences!");
            return;
        }

        Log.d(TAG, "📌 Fetching education data for user: " + userId);

        DatabaseHelper.UserWiseEducationSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseEducationResultListener() {
            @Override
            public void onQueryResult(List<UserWiseEducation> userWiseEducationList) {
                if (userWiseEducationList.isEmpty()) {
                    Log.e(TAG, "⚠️ No education records found in DB!");
                    return;
                }

                // Clear the existing list
                educationList.clear();

                // ✅ Iterate properly
                for (UserWiseEducation edu : userWiseEducationList) {
                    educationList.add(new Education(
                            edu.getInstitutionName(),
                            edu.getEducationLevelName(),
                            String.valueOf(edu.getPassingYear())  // Convert int to String
                    ));
                }

                // ✅ Notify the adapter on UI thread
                runOnUiThread(() -> {
                    educationAdapter.notifyDataSetChanged();
                    Log.d(TAG, "✅ Education data updated in RecyclerView.");
                });
            }
        });
    }


    private void loadEducationLevels() {
        DatabaseHelper.getEducationLevels(this, new DatabaseHelper.EducationLevelsCallback() {
            @Override
            public void onSuccess(List<Map<String, String>> educationLevels) {
                List<String> educationNames = new ArrayList<>();
                final Map<String, String> educationMap = new HashMap<>(); // Store name-ID mapping

                for (Map<String, String> education : educationLevels) {
                    String id = education.get("EducationLevelId");
                    String name = education.get("EducationLevelName");

                    educationNames.add(name);
                    educationMap.put(name, id); // Map name to ID
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(StudentsAcademicDetails.this,
                            android.R.layout.simple_dropdown_item_1line, educationNames);
                    educationLevel.setAdapter(adapter);
                    educationLevel.showDropDown(); // ✅ Force dropdown to open
                    Log.d(TAG, "✅ Education Level dropdown updated with " + educationNames.size() + " values.");
                });

                // Handle dropdown selection
                educationLevel.setOnItemClickListener((parent, view, position, id) -> {
                    selectedEducationId = educationMap.get(educationNames.get(position)); // Get corresponding ID
                    Log.d(TAG, "📌 Selected Education ID: " + selectedEducationId);

                    // Load courses based on the selected education level
                    loadCourses(selectedEducationId);
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Failed to load education levels: " + error);
            }
        });
    }




    private void loadCourses(String educationLevelID) {
        DatabaseHelper.getCourses(this, educationLevelID, new DatabaseHelper.CoursesCallback() {
            @Override
            public void onSuccess(List<Map<String, String>> courses) {
                List<String> courseNames = new ArrayList<>();
                final Map<String, String> courseMap = new HashMap<>(); // Store name-ID mapping

                for (Map<String, String> course : courses) {
                    String id = course.get("CourseID");
                    String name = course.get("CourseName");

                    courseNames.add(name);
                    courseMap.put(name, id); // Map name to ID
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(StudentsAcademicDetails.this,
                            android.R.layout.simple_dropdown_item_1line, courseNames);
                    courseName.setAdapter(adapter);
                    courseName.showDropDown(); // ✅ Force dropdown to open
                    Log.d(TAG, "✅ Course dropdown updated with " + courseNames.size() + " courses.");
                });

                // Handle selection
                courseName.setOnItemClickListener((parent, view, position, id) -> {
                    selectedCourseId = courseMap.get(courseNames.get(position)); // ✅ Set selectedCourseId
                    Log.d(TAG, "📌 Selected Course ID: " + selectedCourseId);
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Failed to load courses: " + error);
            }
        });
    }





    private void saveEducationData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Retrieve existing data
        String json = sharedPreferences.getString("EDUCATION_LIST", "");
        Type type = new TypeToken<List<Education>>() {}.getType();
        List<Education> existingList = new Gson().fromJson(json, type);

        if (existingList == null) {
            existingList = new ArrayList<>();
        }

        // ✅ Append new entries instead of overwriting
        existingList.addAll(educationList);

        // Save back to SharedPreferences
        String updatedJson = new Gson().toJson(existingList);
        editor.putString("EDUCATION_LIST", updatedJson);
        editor.apply();

        Log.d(TAG, "✅ Updated EDUCATION_LIST: " + updatedJson);
    }


    public List<Education> loadEducationData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("EDUCATION_LIST", "");

        if (!json.isEmpty()) {
            Type type = new TypeToken<List<Education>>() {}.getType();
            return new Gson().fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }
}



