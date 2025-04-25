package com.example.loginpage;

import static android.content.ContentValues.TAG;

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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.loginpage.MySqliteDatabase.Connection_Class;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentsDashboard extends AppCompatActivity {

    Handler mainTextHandler = new Handler();
    private TextView welcomeText;
    private ImageView profileIcon, profileIconTop, notificationBell;
    private Button searchButton;
    private BarChart barChart;
    private PieChart pieChart;
    BottomNavigationView bottomNavigationView;
    private static final String TAG = "StudentsDashboard";
    AutoCompleteTextView subjectDropdown;
    AutoCompleteTextView gradeDropdown;
    Map<String, String> subjectMap = new HashMap<>();
    Map<String, String> gradeMap = new HashMap<>();

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
        notificationBell = findViewById(R.id.imageView141);
        subjectDropdown = findViewById(R.id.autoCompleteSubject);
        gradeDropdown = findViewById(R.id.autoCompleteGrade); // Initialize gradeDropdown HERE

//        Toolbar toolbar = findViewById(R.id.toolbar2);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Pathshaala");

        setupCharts();
        loadGrades(); // Load grades from the database
        loadSubjects(); // Load subjects from the database
        setupDropdowns();
        loadUserName();
        NavigationBarWorking();

        // Load profile picture
        loadProfilePicture();

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsDashboard.this, StudentsInfo.class);
            startActivity(intent);
        });

        notificationBell.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsDashboard.this, NotificationStudents.class);
            startActivity(intent);
        });

        profileIconTop.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsDashboard.this, StudentsInfo.class);
            startActivity(intent);
        });

        searchButton.setOnClickListener(v -> {
            String selectedGrade = gradeDropdown.getText().toString();
            String selectedSubject = subjectDropdown.getText().toString();

            Intent intent = new Intent(this, SearchStudentsDashboard.class);
            intent.putExtra("GRADE", selectedGrade);
            intent.putExtra("SUBJECT", selectedSubject);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadProfilePicture() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String storedImageName = sharedPreferences.getString("USER_PROFILE_IMAGE", "");
        if (!storedImageName.isEmpty()) {
            String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + storedImageName;
            Log.d(TAG, "✅ Loaded Image from SharedPreferences: " + imageUrl);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.generic_avatar)
                    .error(R.drawable.generic_avatar)
                    .apply(RequestOptions.circleCropTransform()) // Apply the circle transformation here
                    .into(profileIcon);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.generic_avatar)
                    .error(R.drawable.generic_avatar)
                    .apply(RequestOptions.circleCropTransform()) // Apply the circle transformation here
                    .into(profileIconTop);
        } else {
            Log.e(TAG, "❌ No profile image found in SharedPreferences");
            // Set default images if no image is found.
            profileIcon.setImageResource(R.drawable.generic_avatar);
            profileIconTop.setImageResource(R.drawable.generic_avatar);
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

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, grades);
            gradeDropdown.setAdapter(adapter);
        });
    }

    private void loadSubjects() {
        String query = "SELECT SubjectID, SubjectName FROM Subject WHERE active = 'true' ORDER BY SubjectName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
            if (result == null || result.isEmpty()) {
                Log.e(TAG, "No subjects found in the database.");
                Toast.makeText(this, "No Subjects Found!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> subjects = new ArrayList<>();
            subjectMap.clear();

            for (Map<String, String> row : result) {
                String id = row.get("SubjectID");
                String name = row.get("SubjectName");
                Log.d(TAG, "Subject Retrieved - ID: " + id + ", Name: " + name);
                subjects.add(name);
                subjectMap.put(name, id);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects);
            subjectDropdown.setAdapter(adapter);
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
            } else if (itemId == R.id.goLive) {
                Intent intent = new Intent(StudentsDashboard.this, SampleGoLiveZegoStudent.class);
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
                    handler.postDelayed(this, 150); // Delay between each letter
                }
            }
        };
        handler.post(runnable); // Start the animation immediately
    }


    private void setupCharts() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        // Define grade levels for the bar chart
        String[] gradeLevels = {"Pre", "Pri", "Sec", "Mid", "9th", "10th", "11th", "12th", "UG"};

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


    private void setupDropdowns() {
        AutoCompleteTextView autoCompleteGrade = findViewById(R.id.autoCompleteGrade);

        AutoCompleteTextView autoCompleteSubject = findViewById(R.id.autoCompleteSubject);
        AutoCompleteTextView autoCompleteLocation = findViewById(R.id.autoCompleteLocation);


        autoCompleteGrade.setOnClickListener(v -> autoCompleteGrade.showDropDown());
        autoCompleteSubject.setOnClickListener(v -> autoCompleteSubject.showDropDown());
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
        loadProfilePicture();
        // loadUserName(); // Ensure name updates when returning to this activity
    }
}

