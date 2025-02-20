package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StudentsDashboard extends AppCompatActivity {

    private TextView welcomeText;
    private ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_dashboard);

        ImageView searchButton = findViewById(R.id.imageView120);
        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsDashboard.this, SearchStudentsDashboard.class);
            startActivity(intent);
        });

        welcomeText = findViewById(R.id.textView91); // "Hello" TextView
        profileIcon = findViewById(R.id.imageView119);
        TextView enrolledCourses = findViewById(R.id.textView99);
        TextView liveClasses = findViewById(R.id.textView100);
        TextView completedCourses = findViewById(R.id.textView104);
        TextView attendedClasses = findViewById(R.id.textView105);
        TextView learningHours = findViewById(R.id.textView106);
        TextView nextClass = findViewById(R.id.textView107);

        // Load fonts
        Typeface lightFont = ResourcesCompat.getFont(this, R.font.work_sans);
        Typeface boldFont = ResourcesCompat.getFont(this, R.font.work_sans_bold);

        // Apply font styles
        enrolledCourses.setTypeface(lightFont);
        liveClasses.setTypeface(lightFont);
        completedCourses.setTypeface(lightFont);
        attendedClasses.setTypeface(lightFont);
        learningHours.setTypeface(lightFont);
        nextClass.setTypeface(lightFont);

        // Format text fields
        formatTextView(enrolledCourses, "Total Enrolled Courses: ", "5", "#0F4D73", boldFont);
        formatTextView(liveClasses, "Live Classes: ", "2", "#D16D6A", boldFont);
        formatTextView(completedCourses, "Courses Completed: ", "3", "#0F4D73", boldFont);
        formatTextView(attendedClasses, "Classes Attended: ", "20", "#0F4D73", boldFont);
        formatTextView(learningHours, "Total Learning Hours: ", "50Hrs", "#0F4D73", boldFont);
        formatTextView(nextClass, "Next Class @ ", "10:30 AM, 5th March", "#0F4D73", boldFont);

        // Load student's name
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("FIRST_NAME", "Student"); // Default "Student"
        welcomeText.setText("Hello, " + firstName);

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsDashboard.this, StudentsInfo.class);
            startActivity(intent);
        });

        // Handle insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Helper method to format TextViews
    private void formatTextView(TextView textView, String prefix, String number, String color, Typeface boldFont) {
        SpannableString spannable = new SpannableString(prefix + number);
        int start = prefix.length();
        int end = start + number.length();

        spannable.setSpan(new CustomTypefaceSpan("", boldFont), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannable);
    }
}
