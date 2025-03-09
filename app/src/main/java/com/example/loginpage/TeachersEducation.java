package com.example.loginpage;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;

public class TeachersEducation extends AppCompatActivity {

    private List<Education> educationList;
    private EducationAdapter educationAdapter;
    private SharedPreferences sharedPreferences;
    private RecyclerView educationRecyclerView;
    private AutoCompleteTextView educationLevel, textViewYear, courseName;
    private EditText etInstitution;
    private TextView textViewID;

    private Map<String, String> educationLevelMap = new HashMap<>(); // EducationLevelName -> EducationLevelID
    private Map<String, String> coursesMap = new HashMap<>(); // CourseName -> CourseID




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_education);


        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        educationRecyclerView = findViewById(R.id.educationRecyclerView);
        educationLevel = findViewById(R.id.textView38);
        textViewYear = findViewById(R.id.yearDropdown);
        etInstitution = findViewById(R.id.editTextText15);
        textViewID = findViewById(R.id.textView109);
        courseName = findViewById(R.id.educationIdDropdown);
//        ImageView addEducation = findViewById(R.id.imageView73);


//        educationList = loadEducationData(this);
        educationList = new ArrayList<>();
        educationAdapter = new EducationAdapter(this,educationList);
        educationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        educationRecyclerView.setAdapter(educationAdapter);

        Log.d(TAG, "onCreate: Activity Started");

        fetchUserEducationDetails();

        loadEducationLevels();

        Button saveButton = findViewById(R.id.button16);

        // Set Click Listener
        saveButton.setOnClickListener(v -> {

            String institution = etInstitution.getText().toString().trim();
            String degree = educationLevel.getText().toString().trim();
            String year = textViewYear.getText().toString().trim();

            if (institution.isEmpty() || degree.isEmpty() || year.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            educationList.add(new Education(institution, degree, year));
            educationAdapter.notifyDataSetChanged();
            saveEducationData(); // Save the list in SharedPreferences
            Toast.makeText(TeachersEducation.this, "Education saved successfully!", Toast.LENGTH_SHORT).show();
            etInstitution.setText("");
            educationLevel.setText("");
            textViewYear.setText("");

            insertUserEducation();
            // Create Intent to move to ProfessionalDetails.java
            Toast.makeText(TeachersEducation.this, "Education saved successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TeachersEducation.this, TeachersEducationView.class);
            startActivity(intent); // Start the new activity
            finish(); // Optional: Close TeachersEducation so user can't go back
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

        String[] distinctions = {"Doctorate", "Post Graduate", "College Graduate", "High School", "Middle School"};

        ArrayAdapter<String> adapterDegree = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, distinctions);
        etDegree.setAdapter(adapterDegree);

        etDegree.setOnClickListener(v -> etDegree.showDropDown());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void insertUserEducation() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1); // Fetch logged-in User ID

        if (userId == -1) {
            Log.e(TAG, "❌ User ID not found in SharedPreferences!");
            return;
        }

        // Example Data (You can modify based on UI inputs)
        int educationLevelId = 1; // Example: Bachelor's Degree
//        String institutionName = etInstitution.getText().toString().trim();

        String institutionName = "Example University"; // Replace with actual input
        int passingYear = 2025; // Example: Graduation Year
        String selfReferralCode = "ABC123"; // Example Referral Code

        Log.d(TAG, "📌 Inserting Education for UserID: " + userId);

        // ✅ Call `UserWiseEducationInsert` Method
        DatabaseHelper.UserWiseEducationInsert(this, "1", userId, educationLevelId, institutionName, passingYear, selfReferralCode, new DatabaseHelper.DatabaseCallback() {
            @Override
            public void onMessage(String message) {
                Log.d(TAG, "✅ Database Response: " + message);
                runOnUiThread(() -> {
                    Toast.makeText(TeachersEducation.this, message, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onSuccess(List<Map<String, String>> result) {
                // No need for success handling in this case, so leave it empty
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Database Error: " + error);
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

                // Convert fetched data to Education model and update RecyclerView
                educationList.clear();

                for (UserWiseEducation edu : userWiseEducationList) {
                    educationList.add(new Education(
                            edu.getInstitutionName(),
                            edu.getEducationLevelName(),
                            edu.getPassingYear()
                    ));
                }

                educationAdapter.notifyDataSetChanged(); // Update UI
                Log.d(TAG, "✅ Education data updated in RecyclerView.");
            }
        });
    }

    private void loadEducationLevels() {
        String query = "SELECT EducationLevelID, EducationLevelName FROM EducationLevel WHERE active = 'true' ORDER BY EducationLevelName";
        Log.d(TAG, "loadEducationLevels: Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "loadEducationLevels: No education levels found!");
                    Toast.makeText(TeachersEducation.this, "No Education Levels Found!", Toast.LENGTH_SHORT).show();
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(TeachersEducation.this, android.R.layout.simple_dropdown_item_1line, educationLevels);
                educationLevel.setAdapter(adapter);

                Log.d(TAG, "loadEducationLevels: Adapter set with values: " + educationLevels);
            }
        });

        educationLevel.setOnClickListener(v -> {
            Log.d(TAG, "educationLevel clicked - Showing dropdown");
            educationLevel.showDropDown();
        });

        educationLevel.setOnItemClickListener((parent, view, position, id) -> {
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
                    Toast.makeText(TeachersEducation.this, "No courses found!", Toast.LENGTH_SHORT).show();
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(TeachersEducation.this, android.R.layout.simple_dropdown_item_1line, courses);
                courseName.setAdapter(adapter);
                courseName.showDropDown(); // Ensure dropdown displays immediately

                Log.d(TAG, "loadCourses: Adapter set with values: " + courses);
            }
        });

        courseName.setOnClickListener(v -> {
            Log.d(TAG, "courseName clicked - Showing dropdown");
            courseName.showDropDown();
        });

        courseName.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCourse = (String) parent.getItemAtPosition(position);
            String courseID = coursesMap.get(selectedCourse);
            Log.d(TAG, "Course Selected: " + selectedCourse + " (ID: " + courseID + ")");
        });
    }



    private void saveEducationData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(educationList);
        editor.putString("EDUCATION_LIST", json);
        editor.apply();
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



















//26th Feb 17:44
//package com.example.loginpage;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//
//import com.example.loginpage.adapters.EducationAdapter;
//import com.example.loginpage.models.Education;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
//
//public class TeachersEducation extends AppCompatActivity {
//
//    private List<Education> educationList;
//    private EducationAdapter educationAdapter;
//    private SharedPreferences sharedPreferences;
//    private RecyclerView educationRecyclerView;
//    private AutoCompleteTextView textViewDegree, textViewYear;
//    private EditText etInstitution;
//    private TextView textViewID;
//
//
//
//
//        @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_teachers_education);
//
//
//        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        educationRecyclerView = findViewById(R.id.educationRecyclerView);
//        textViewDegree = findViewById(R.id.textView38);
//        textViewYear = findViewById(R.id.yearDropdown);
//        etInstitution = findViewById(R.id.editTextText15);
//        textViewID = findViewById(R.id.textView109);
////        ImageView addEducation = findViewById(R.id.imageView73);
//
//        String educationQuery = "SELECT EducationLevelID,EducationLevelName from EducationLevel where active = 'true' order by EducationLevelName";
//        DatabaseHelper.loadDataFromDatabase(this, educationQuery,textViewDegree,textViewID);
//
//
//
//        educationList = loadEducationData(this);
//        educationAdapter = new EducationAdapter(this,educationList);
//        educationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        educationRecyclerView.setAdapter(educationAdapter);
////
////        addEducation.setOnClickListener(v -> {
////            String institution = etInstitution.getText().toString().trim();
////            String degree = textViewDegree.getText().toString().trim();
////            String year = textViewYear.getText().toString().trim();
////
////            if (institution.isEmpty() || degree.isEmpty() || year.isEmpty()) {
////                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
////                return;
////            }
////
////            educationList.add(new Education(institution, degree, year));
////            educationAdapter.notifyDataSetChanged();
////            saveEducationData(); // Save the list in SharedPreferences
////            Toast.makeText(TeachersEducation.this, "Education saved successfully!", Toast.LENGTH_SHORT).show();
////            etInstitution.setText("");
////            textViewDegree.setText("");
////            textViewYear.setText("");
////        });
//
//
//
//
//
//        Button saveButton = findViewById(R.id.button16);
//
//        // Set Click Listener
//        saveButton.setOnClickListener(v -> {
//
//            String institution = etInstitution.getText().toString().trim();
//            String degree = textViewDegree.getText().toString().trim();
//            String year = textViewYear.getText().toString().trim();
//
//            if (institution.isEmpty() || degree.isEmpty() || year.isEmpty()) {
//                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            educationList.add(new Education(institution, degree, year));
//            educationAdapter.notifyDataSetChanged();
//            saveEducationData(); // Save the list in SharedPreferences
//            Toast.makeText(TeachersEducation.this, "Education saved successfully!", Toast.LENGTH_SHORT).show();
//            etInstitution.setText("");
//            textViewDegree.setText("");
//            textViewYear.setText("");
//            // Create Intent to move to ProfessionalDetails.java
//            Toast.makeText(TeachersEducation.this, "Education saved successfully!", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(TeachersEducation.this, TeachersEducationView.class);
//            startActivity(intent); // Start the new activity
//            finish(); // Optional: Close TeachersEducation so user can't go back
//        });
//
//        AutoCompleteTextView etDegree = findViewById(R.id.textView38);
//        AutoCompleteTextView yearDropdown = findViewById(R.id.yearDropdown);
//
//        List<String> years = new ArrayList<>();
//        for (int i = 2025; i >= 1960; i--) {
//            years.add(String.valueOf(i));
//        }
//
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years);
//        yearDropdown.setAdapter(adapter);
//
//        yearDropdown.setOnClickListener(v -> yearDropdown.showDropDown());
//
//        String[] distinctions = {"Doctorate", "Post Graduate", "College Graduate", "High School", "Middle School"};
//
//        ArrayAdapter<String> adapterDegree = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, distinctions);
//        etDegree.setAdapter(adapterDegree);
//
//        etDegree.setOnClickListener(v -> etDegree.showDropDown());
//
//
////    // Navigate to next activity
////            Intent intent = new Intent(TeachersEducation.this, TeachersEducationView.class);
////            startActivity(intent);
////            finish(); // Optional: Close TeachersEducation so the user can't go back
//
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//    }
//
//    private void saveEducationData() {
//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        Gson gson = new Gson();
//        String json = gson.toJson(educationList);
//        editor.putString("EDUCATION_LIST", json);
//        editor.apply();
//    }
//    public List<Education> loadEducationData(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
//        String json = sharedPreferences.getString("EDUCATION_LIST", "");
//
//        if (!json.isEmpty()) {
//            Type type = new TypeToken<List<Education>>() {}.getType();
//            return new Gson().fromJson(json, type);
//        } else {
//            return new ArrayList<>();
//        }
//    }
//}

