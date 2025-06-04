package com.example.loginpage;

import static android.content.ContentValues.TAG;

import android.content.Entity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.example.loginpage.models.UserDetailsClass;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.loginpage.MySqliteDatabase.Connection_Class;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.UserDetailsClass;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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

public class TeachersDashboardNew extends AppCompatActivity {

    private TextView welcomeText;
    private UserDetailsClass user;
    private ImageView profileIcon, profileIconTop;
    private ImageView searchButton,notificationButton;
    private ImageButton whatsappButton;
    Handler mainTextHandler = new Handler();
    BarChart barChart;
    PieChart pieChart;
    LineChart lineChart;
    ArrayList<Entry> lineEntries;
    ArrayList<PieEntry> pieEntries;
    ArrayList<BarEntry> barEntries;

    // Bottom Navigation Bar Variables
    BottomNavigationView bottomNavigationView;
    private static final String TAG = "TeachersDashboardNew";
    Map<String, String> subjectMap = new HashMap<>();
    AutoCompleteTextView subjectDropdown;
    AutoCompleteTextView gradeDropdown;
    Map<String, String> gradeMap = new HashMap<>();
    private TextView notificationCountTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_dashboard_new);

//        AutoCompleteTextView autoCompleteGrade = findViewById(R.id.autoCompleteGrade);
//        AutoCompleteTextView autoCompleteSubject = findViewById(R.id.autoCompleteSubject);
        subjectDropdown = findViewById(R.id.autoCompleteSubject); // Assign the AutoCompleteTextView for subjects
        gradeDropdown = findViewById(R.id.autoCompleteGrade); // Initialize grade dropdown
        AutoCompleteTextView autoCompleteLocation = findViewById(R.id.autoCompleteLocation);
        Button searchButton = findViewById(R.id.button37);
        welcomeText = findViewById(R.id.textViewHello); // Corrected TextView ID
        profileIcon = findViewById(R.id.imageView151);
        profileIconTop = findViewById(R.id.imageView151);
        notificationButton = findViewById(R.id.imageView141);
        notificationCountTextView = findViewById(R.id.notificationCount);

        // ✅ Fetch Profile Image from Database
        fetchProfileImageFromDB();

        loadUserName();
        showStudentEnrolledClassBarChart();
        showEnrolledStudentsMonthlyBarChart();
        showChannelViewsChart();
        NavigationBarWorking();
//        MoveToWhatsAppScreen();
        // Grade & Subject Data
//        String[] grades = {"Primary","Secondary","Middle School","9th", "10th", "11th", "12th"};
//        String[] subjects = {"Math", "Science", "English", "History", "Geography","Biology"};

//        autoCompleteGrade.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, grades));
//        autoCompleteSubject.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects));

        loadSubjects();
        loadGrades();

        loadUnreadNotificationCount();

        gradeDropdown.setOnClickListener(v -> gradeDropdown.showDropDown());
        subjectDropdown.setOnClickListener(v -> subjectDropdown.showDropDown());

        // Fetch Cities from Database
        fetchCityData(autoCompleteLocation);

        searchButton.setOnClickListener(v -> {
            String selectedGrade = gradeDropdown.getText().toString();
//            String selectedSubject = autoCompleteSubject.getText().toString();
            String selectedSubject = subjectDropdown.getText().toString();

            Intent intent = new Intent(this, SearchTeachersDashboard.class);
            intent.putExtra("GRADE", selectedGrade);
            intent.putExtra("SUBJECT", selectedSubject);
            startActivity(intent);
        });

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(TeachersDashboardNew.this, TeachersInfoSubSection.class);
            startActivity(intent);
        });

        profileIconTop.setOnClickListener(v -> {
            Intent intent = new Intent(TeachersDashboardNew.this, TeachersInfoSubSection.class);
            startActivity(intent);
        });

        notificationButton.setOnClickListener(v -> {
            notificationCountTextView.setVisibility(View.GONE);
            Intent intent = new Intent(TeachersDashboardNew.this, NotificationTeachersMessage.class);
            startActivity(intent);
        });


        // Handle insets for layout adjustments
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // bottomNavigationView = findViewById(R.id.bottomNavigationView);
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
            gradeDropdown.setAdapter(adapter); // Use gradeDropdown here
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

    private void fetchProfileImageFromDB() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1); // ✅ Get updated USER_ID

        if (userId <= 0) {
            Log.e("TeachersDashboardNew", "⚠️ User ID not found in SharedPreferences.");
            return;
        }

        DatabaseHelper.fetchUserProfileImage(userId, imageName -> {
            if (imageName != null && !imageName.isEmpty()) {
                String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + imageName;
                Log.d("TeachersDashboardNew", "✅ Profile image URL: " + imageUrl);

                runOnUiThread(() -> Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.generic_avatar) // Default profile image
                        .error(R.drawable.generic_avatar) // Show default if error
                        .apply(RequestOptions.circleCropTransform()) // Makes the image round
                        .into(profileIconTop)); // ✅ Load image into Toolbar Profile
            } else {
                Log.e("TeachersDashboardNew", "❌ No profile image found in DB or empty value.");
            }
        });
    }


    // Working of the BOTTOM NAVIGATION BAR
    public void NavigationBarWorking() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.chatBot) {
                Intent intent = new Intent(TeachersDashboardNew.this, ChatActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.home) {
                Intent intent = new Intent(TeachersDashboardNew.this, TeachersDashboardNew.class);
                startActivity(intent);
            }
            else if(itemId == R.id.goLive){
                Intent intent = new Intent(TeachersDashboardNew.this, SampleGoLiveZego.class);
                startActivity(intent);
            }
            else if (itemId == R.id.timeTable) {
                Intent intent = new Intent(TeachersDashboardNew.this, ShowTimeTableNewViewTeacher.class);
                startActivity(intent);
            }
            return true;
        });
    }

    private void loadUnreadNotificationCount() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);
        Log.d(TAG, "loadUnreadNotificationCount - User ID from SharedPreferences: " + userId); // Add this log

        if (userId != -1) {
            new Thread(() -> {
                int unreadCount = DatabaseHelper.getUnreadNotificationCount(userId);
                runOnUiThread(() -> {
                    Log.d(TAG, "Unread count received: " + unreadCount);
                    if (unreadCount > 0) {
                        notificationCountTextView.setText(String.valueOf(unreadCount));
                        notificationCountTextView.setVisibility(View.VISIBLE);
                        Log.d(TAG, "Notification count TextView is now visible with count: " + unreadCount);
                    } else {
                        notificationCountTextView.setVisibility(View.GONE);
                        Log.d(TAG, "Notification count TextView is now gone.");
                    }
                });
            }).start();
        } else {
            Log.e(TAG, "User ID not found in SharedPreferences.");
        }
    } 


    @Override
    protected void onResume() {
        super.onResume();
        mainTextHandler.postDelayed(this::loadUserName, 10000);
        // loadUserName(); // Ensure name updates when returning to this activity
        loadUnreadNotificationCount();
    }

    // Function to get the username of the logged in user
    private void loadUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("FIRST_NAME", "User"); // Default "User"
//        final String text = "Welcome Back, " + firstName + "\uD83D\uDC4B ";
        final String text = "Welcome Back " +  "\uD83D\uDC4B ";
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


    public void showStudentEnrolledClassBarChart() {
        // Find bar chart view
        barChart = findViewById(R.id.barChartStudentEnrolledClass);

        // Create BarEntry lists for each subject
        ArrayList<BarEntry> englishEntries = new ArrayList<>();
        ArrayList<BarEntry> mathEntries = new ArrayList<>();
        ArrayList<BarEntry> scienceEntries = new ArrayList<>();

        // Sample data: (x-value, y-value)
        englishEntries.add(new BarEntry(0, 40)); // 10th grade
        englishEntries.add(new BarEntry(1, 42)); // 11th grade
        englishEntries.add(new BarEntry(2, 41)); // 12th grade

        mathEntries.add(new BarEntry(0, 18)); // 10th grade
        mathEntries.add(new BarEntry(1, 15)); // 11th grade
        mathEntries.add(new BarEntry(2, 17)); // 12th grade

        scienceEntries.add(new BarEntry(0, 50)); // 10th grade
        scienceEntries.add(new BarEntry(1, 52)); // 11th grade
        scienceEntries.add(new BarEntry(2, 45)); // 12th grade

        // Create datasets for each subject
        BarDataSet englishDataSet = new BarDataSet(englishEntries, "English");
        englishDataSet.setColor(Color.parseColor("#76E4FB")); // Light Blue

        BarDataSet mathDataSet = new BarDataSet(mathEntries, "Math");
        mathDataSet.setColor(Color.parseColor("#4FA3D1")); // Medium Blue

        BarDataSet scienceDataSet = new BarDataSet(scienceEntries, "Science");
        scienceDataSet.setColor(Color.parseColor("#2D5C86")); // Dark Blue

        // Create BarData and group datasets
        BarData barData = new BarData(englishDataSet, mathDataSet, scienceDataSet);

        // Set bar width (ensures proper spacing)
        float groupSpace = 0.3f; // Space between groups
        float barSpace = 0.05f; // Space between bars in a group
        float barWidth = 0.2f; // Width of each bar

        barData.setBarWidth(barWidth); // Set each bar width
        barChart.setData(barData);

        // Configure X-Axis labels
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"10th", "11th", "12th"}));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true); // Center labels
        xAxis.setAxisMinimum(0); // Start from zero

        // Enable grouping
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * 3);
        barChart.groupBars(0, groupSpace, barSpace);

        // Customize Y-axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0); // Start from zero
        barChart.getAxisRight().setEnabled(false); // Hide right Y-axis

        // Chart customization
        barChart.getDescription().setEnabled(false); // Hide description
        barChart.getLegend().setWordWrapEnabled(true); // Enable legend wrapping
        barChart.setFitBars(true); // Fit bars within the chart
        barChart.animateY(2000);
        barChart.invalidate(); // Refresh chart
    }


    // Function to display the statistical charts
    public void showEnrolledStudentsMonthlyBarChart() {
        // Find bar chart view
        barChart = findViewById(R.id.barChartStudentEnrolledMonth);

        // Create BarEntry list for stacked bars (each entry represents a month)
        ArrayList<BarEntry> stackedEntries = new ArrayList<>();

        // Sample data (x-value = month index, y-values = stacked values for Eng, Math, Sci)
        stackedEntries.add(new BarEntry(0, new float[]{10, 20, 30})); // Jan
        stackedEntries.add(new BarEntry(1, new float[]{15, 25, 40})); // Feb
        stackedEntries.add(new BarEntry(2, new float[]{20, 30, 50})); // Mar
        stackedEntries.add(new BarEntry(3, new float[]{25, 35, 55})); // Apr

        // Create BarDataSet for stacked values
        BarDataSet stackedDataSet = new BarDataSet(stackedEntries, "Students Enrolled");
        stackedDataSet.setColors(
                Color.parseColor("#76E4FB"), // English - Light Blue
                Color.parseColor("#A5D904"), // Math - Green
                Color.parseColor("#AFC6E9")  // Science - Light Gray
        );
        stackedDataSet.setStackLabels(new String[]{"Eng", "Math", "Sc."}); // Labels inside bars
        stackedDataSet.setValueTextSize(14f); // Increase text size
        stackedDataSet.setValueTextColor(Color.BLACK); // Text color

        // Create BarData and set dataset
        BarData barData = new BarData(stackedDataSet);
        barChart.setData(barData);

        // Configure X-Axis labels
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Jan", "Feb", "Mar", "Apr"}));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        // Configure Y-Axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0); // Start Y-axis from zero
        barChart.getAxisRight().setEnabled(false); // Hide right Y-axis

        // Customize chart appearance
        barChart.getDescription().setEnabled(false); // Hide description
        barChart.setFitBars(true); // Fit bars in chart
        barChart.getLegend().setWordWrapEnabled(true); // Enable wrapping for legend
        barChart.animateY(2000); // Animation

        // Refresh chart
        barChart.invalidate();
    }

    public void showChannelViewsChart() {
        lineChart = findViewById(R.id.lineChartLectureViews);

        // Create Entries for monthly views
        ArrayList<Entry> viewEntries = new ArrayList<>();

        // Sample data: (x = month index, y = views)
        viewEntries.add(new Entry(0, 1200)); // Jan
        viewEntries.add(new Entry(1, 1500)); // Feb
        viewEntries.add(new Entry(2, 1800)); // Mar
        viewEntries.add(new Entry(3, 2500)); // Apr
        viewEntries.add(new Entry(4, 3000)); // May
        viewEntries.add(new Entry(5, 3500)); // Jun
        viewEntries.add(new Entry(6, 4000)); // Jul
        viewEntries.add(new Entry(7, 3800)); // Aug
        viewEntries.add(new Entry(8, 4500)); // Sep
        viewEntries.add(new Entry(9, 5000)); // Oct
        viewEntries.add(new Entry(10, 5200)); // Nov
        viewEntries.add(new Entry(11, 6000)); // Dec

        // Create LineDataSet
        LineDataSet viewDataSet = new LineDataSet(viewEntries, "Monthly Views");
        viewDataSet.setColor(Color.parseColor("#4285F4")); // Blue
        viewDataSet.setCircleColor(Color.parseColor("#4285F4"));
        viewDataSet.setLineWidth(3f);
        viewDataSet.setValueTextSize(10f);
        viewDataSet.setValueTextColor(Color.BLACK);
        viewDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth curve

        // Create LineData and add dataset
        LineData lineData = new LineData(viewDataSet);
        lineChart.setData(lineData);

        // Configure X-Axis labels
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        }));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        // Configure Y-Axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        lineChart.getAxisRight().setEnabled(false);

        // Customize chart appearance
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setTextSize(14f);
        lineChart.getLegend().setWordWrapEnabled(true);
        lineChart.animateX(2000);

        // Refresh chart
        lineChart.invalidate();
    }


    // Function to navigate through the bottom navigation bar

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