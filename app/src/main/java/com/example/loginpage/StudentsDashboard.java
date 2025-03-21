package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.MySqliteDatabase.Connection_Class;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudentsDashboard extends AppCompatActivity {

    Handler mainTextHandler = new Handler();
    private TextView welcomeText;
    private ImageView profileIcon,profileIconTop;
    private Button searchButton;
    private BarChart barChart;
    private PieChart pieChart;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_dashboard);

        welcomeText = findViewById(R.id.textViewHello);
        profileIcon = findViewById(R.id.imageView151);
        searchButton = findViewById(R.id.button37);
        barChart = findViewById(R.id.barChartStudentEnrolledClass);
        pieChart = findViewById(R.id.barChartStudentEnrolledMonth);
        profileIconTop = findViewById(R.id.imageView151);

//        Toolbar toolbar = findViewById(R.id.toolbar2);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Pathshaala");

        setupCharts();
        setupDropdowns();
        loadUserName();
        NavigationBarWorking();

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsDashboard.this, StudentsInfo.class);
            startActivity(intent);
        });

        profileIconTop.setOnClickListener(v -> {
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


    public void NavigationBarWorking() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.chatBot) {
                Intent intent = new Intent(StudentsDashboard.this, ChatActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.home) {
                Intent intent = new Intent(StudentsDashboard.this, StudentsDashboard.class);
                startActivity(intent);
            } else if (itemId == R.id.profile) {
                Intent intent = new Intent(StudentsDashboard.this, StudentsInfo.class);
                startActivity(intent);
            }
            return true;
        });
    }

    private void loadUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("FIRST_NAME", "User"); // Default "User"
        final String text = "Welcome Back, " + firstName + "\uD83D\uDC4B ";
        welcomeText.setText("");

        Handler handler = new Handler(); // Single handler instance

        Runnable runnable = new Runnable() {
            int i = 0; // Now 'i' is a member of Runnable, preserving its state.

            @Override
            public void run() {
                if (i < text.length()) {
                    char chr = text.charAt(i);
                    welcomeText.append(String.valueOf(chr));
                    i++;
                    handler.postDelayed(this,150); // Delay between each letter
                }
            }
        };
        handler.post(runnable); // Start the animation immediately
    }


    private void setupCharts() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        // Define grade levels for the bar chart
        String[] gradeLevels = {"Pre","Pri","Sec","Mid","9th","10th","11th","12th","UG"};

        // Define subjects for the pie chart
        String[] subjects = {"Math", "Science", "English", "History", "Geography", "Physics", "Chemistry", "Biology", "Computer Science"};

        for (int i = 0; i < gradeLevels.length; i++) {
            float value = (float) ((i + 1) * 10.0);

            // Bar Chart Entry with Grade Levels
            barEntries.add(new BarEntry(i, value));

            // Pie Chart Entry with Subjects (Now Corrected)
            if (i < subjects.length) {  // Ensure we don't go out of bounds
                pieEntries.add(new PieEntry(value, subjects[i]));
            }
        }

        // Set Bar Chart with Grade Level Labels
        BarDataSet barDataSet = new BarDataSet(barEntries, "Grade Levels");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setDrawValues(true);
        barChart.setData(new BarData(barDataSet));
        barChart.animateY(2000);
        barChart.getDescription().setText("Student Distribution by Grade");
        barChart.getDescription().setTextColor(Color.BLACK);

        // Customize X-Axis Labels
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(gradeLevels));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setLabelCount(gradeLevels.length);

        // Set Pie Chart with Subject Labels
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Subjects");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setData(new PieData(pieDataSet));
        pieChart.animateXY(2000, 2000);
        pieChart.getDescription().setEnabled(false);
    }





//    private void setupCharts() {
//        ArrayList<BarEntry> barEntries = new ArrayList<>();
//        ArrayList<PieEntry> pieEntries = new ArrayList<>();
//
//        for (int i = 1; i < 10; i++) {
//            float value = (float) (i * 10.0);
//            barEntries.add(new BarEntry(i, value));
//            pieEntries.add(new PieEntry(value, "Item " + i));
//        }
//
//        BarDataSet barDataSet = new BarDataSet(barEntries, "Students Data");
//        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        barDataSet.setDrawValues(false);
//        barChart.setData(new BarData(barDataSet));
//        barChart.animateY(2000);
//        barChart.getDescription().setText("Students Chart");
//        barChart.getDescription().setTextColor(Color.BLACK);
//
//        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Student Distribution");
//        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        pieChart.setData(new PieData(pieDataSet));
//        pieChart.animateXY(2000, 2000);
//        pieChart.getDescription().setEnabled(false);
//    }

    private void setupDropdowns() {
        List<String> grades = Arrays.asList("Primary","Secondary","Middle School","9th", "10th", "11th", "12th");
        List<String> subjects = Arrays.asList("Math", "Science", "English", "History", "Geography");
//        List<String> locations = Arrays.asList("New York", "Los Angeles", "Chicago", "Houston", "Phoenix");

        AutoCompleteTextView autoCompleteGrade = findViewById(R.id.autoCompleteGrade);
        AutoCompleteTextView autoCompleteSubject = findViewById(R.id.autoCompleteSubject);
        AutoCompleteTextView autoCompleteLocation = findViewById(R.id.autoCompleteLocation);

        autoCompleteGrade.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, grades));
        autoCompleteSubject.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects));
//        autoCompleteLocation.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations));

        autoCompleteGrade.setOnClickListener(v -> autoCompleteGrade.showDropDown());
        autoCompleteSubject.setOnClickListener(v -> autoCompleteSubject.showDropDown());
//        autoCompleteLocation.setOnClickListener(v -> autoCompleteLocation.showDropDown());
        fetchCityData(autoCompleteLocation);
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
                Log.e("StudentsDashboard", "Error fetching city data: " + e.getMessage());
            }

            runOnUiThread(() -> {
                autoCompleteLocation.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cityList));
                autoCompleteLocation.setOnClickListener(v -> autoCompleteLocation.showDropDown());
            });
        }).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mainTextHandler.postDelayed(this::loadUserName, 10000);
        // loadUserName(); // Ensure name updates when returning to this activity
    }

}
