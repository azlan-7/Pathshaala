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

public class TeachersDashboard extends AppCompatActivity {

    private TextView welcomeText;
    private ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_dashboard);

        ImageView searchButton = findViewById(R.id.imageView120);
        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeachersDashboard.this, SearchTeachersDashboard.class);
            startActivity(intent);
        });

        welcomeText = findViewById(R.id.textView91); // Reference to "Hello" TextView
        profileIcon = findViewById(R.id.imageView119);
        TextView totalStudents = findViewById(R.id.textView99);
        TextView classesLive = findViewById(R.id.textView100);
        TextView totalTaught = findViewById(R.id.textView104);
        TextView classesTaught = findViewById(R.id.textView105);
        TextView durationTaught = findViewById(R.id.textView106);
        TextView nextLecture = findViewById(R.id.textView107);

        // Load fonts
        Typeface lightFont = ResourcesCompat.getFont(this, R.font.work_sans);
        Typeface boldFont = ResourcesCompat.getFont(this, R.font.work_sans_bold);

        // Apply light font to the entire text
        totalStudents.setTypeface(lightFont);
        classesLive.setTypeface(lightFont);
        totalTaught.setTypeface(lightFont);
        classesTaught.setTypeface(lightFont);
        durationTaught.setTypeface(lightFont);
        nextLecture.setTypeface(lightFont);

        // Format TextViews
        formatTextView(totalStudents, "Total Enrolled Students: ", "100,000", "#0F4D73", boldFont);
        formatTextView(classesLive, "Classes Live: ", "1000", "#D16D6A", boldFont);
        formatTextView(totalTaught, "Total Students Taught: ", "49", "#0F4D73", boldFont);
        formatTextView(classesTaught, "Total Classes Taught: ", "11", "#0F4D73", boldFont);
        formatTextView(durationTaught, "Total Duration Taught: ", "22Hrs50Mins", "#0F4D73", boldFont);
        formatTextView(nextLecture, "History @ ", "18:30, 22nd June", "#0F4D73", boldFont);

        // Retrieve the first name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("FIRST_NAME", "User"); // Default "User" if no name saved
        welcomeText.setText("Hello, " + firstName);

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(TeachersDashboard.this, TeachersInfo.class);
            startActivity(intent);
        });

        // Handle insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Helper method to format TextViews (Apply bold font & color to numbers)
    private void formatTextView(TextView textView, String prefix, String number, String color, Typeface boldFont) {
        SpannableString spannable = new SpannableString(prefix + number);
        int start = prefix.length();
        int end = start + number.length();

        // Apply bold font and color to the number
        spannable.setSpan(new CustomTypefaceSpan("", boldFont), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannable);
    }
}
