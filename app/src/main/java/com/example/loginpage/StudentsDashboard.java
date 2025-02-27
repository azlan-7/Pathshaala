package com.example.loginpage;

import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.example.loginpage.MySqliteDatabase.Connection_Class;

public class StudentsDashboard extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteGrade, autoCompleteSubject, autoCompleteLocation;
    private ImageView profileIcon;
    private ImageView searchButton;

    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_dashboard);

        welcomeText = findViewById(R.id.textViewHello1);


        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pathshaala");

        profileIcon = findViewById(R.id.imageViewProfile1);
        searchButton = findViewById(R.id.imageView1461);


        autoCompleteGrade = findViewById(R.id.autoCompleteGrade1);
        autoCompleteSubject = findViewById(R.id.autoCompleteSubject1);
        autoCompleteLocation = findViewById(R.id.autoCompleteLocation1);


        setupDropdowns();
        loadUserName();
        fetchCityData(autoCompleteLocation);

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsDashboard.this, StudentsInfo.class);
            startActivity(intent);
        });

        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchStudentsDashboard.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupDropdowns() {
        List<String> grades = Arrays.asList("Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5");
        List<String> subjects = Arrays.asList("Math", "Science", "English", "History", "Geography");
        List<String> locations = Arrays.asList("New York", "Los Angeles", "Chicago", "Houston", "Phoenix");

        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, grades);
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);

        autoCompleteGrade.setAdapter(gradeAdapter);
        autoCompleteSubject.setAdapter(subjectAdapter);
        autoCompleteLocation.setAdapter(locationAdapter);

        autoCompleteGrade.setOnClickListener(v -> autoCompleteGrade.showDropDown());
        autoCompleteSubject.setOnClickListener(v -> autoCompleteSubject.showDropDown());
        autoCompleteLocation.setOnClickListener(v -> autoCompleteLocation.showDropDown());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserName(); // Ensure name updates when returning to this activity
    }

    private void loadUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("FIRST_NAME", "User"); // Default "User"
        welcomeText.setText("Hello, " + firstName);
    }


    private void fetchCityData(AutoCompleteTextView autoCompleteLocation) {
        new Thread(() -> {
            List<String> cityList = new ArrayList<>();
            try {
                Connection_Class connectionClass = new Connection_Class();
                Connection connection = connectionClass.CONN();
                if (connection != null) {
                    String query = "SELECT city_nm FROM city";
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        cityList.add(rs.getString("city_nm"));
                    }
                    rs.close();
                    stmt.close();
                    connection.close();
                }
            } catch (Exception e) {
                Log.e("TeachersDashboardNew", "Error fetching city data: " + e.getMessage());
            }

            runOnUiThread(() -> {
                autoCompleteLocation.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cityList));
                autoCompleteLocation.setOnClickListener(v -> autoCompleteLocation.showDropDown());
            });
        }).start();
    }

}
