package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.MySqliteDatabase.Connection_Class;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TeachersDashboardNew extends AppCompatActivity {

    private TextView welcomeText;
    private ImageView profileIcon;
    private ImageView searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_dashboard_new);

        AutoCompleteTextView autoCompleteGrade = findViewById(R.id.autoCompleteGrade);
        AutoCompleteTextView autoCompleteSubject = findViewById(R.id.autoCompleteSubject);
        AutoCompleteTextView autoCompleteLocation = findViewById(R.id.autoCompleteLocation);
        ImageView searchButton = findViewById(R.id.imageView146);
        welcomeText = findViewById(R.id.textViewHello); // Corrected TextView ID
        profileIcon = findViewById(R.id.imageView151);

        loadUserName();

        // Grade & Subject Data
        String[] grades = {"9th", "10th", "11th", "12th"};
        String[] subjects = {"Math", "Science", "History"};

        autoCompleteGrade.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, grades));
        autoCompleteSubject.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects));

        autoCompleteGrade.setOnClickListener(v -> autoCompleteGrade.showDropDown());
        autoCompleteSubject.setOnClickListener(v -> autoCompleteSubject.showDropDown());

        // Fetch Cities from Database
        fetchCityData(autoCompleteLocation);

        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchTeachersDashboard.class);
            startActivity(intent);
        });

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(TeachersDashboardNew.this, TeachersInfo.class);
            startActivity(intent);
        });


        // Handle insets for layout adjustments
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
