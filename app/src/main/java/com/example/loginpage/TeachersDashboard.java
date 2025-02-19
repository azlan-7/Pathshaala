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

        // Set OnClickListener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start SearchTeachersDashboard activity
                Intent intent = new Intent(TeachersDashboard.this, SearchTeachersDashboard.class);
                startActivity(intent);
            }
        });


        welcomeText = findViewById(R.id.textView91); // Reference to "Hello" TextView
        profileIcon = findViewById(R.id.imageView119);
        TextView totalStudents = findViewById(R.id.textView99);
        TextView classesLive = findViewById(R.id.textView100);

        // Define SpannableString for both TextViews
        SpannableString spannable = new SpannableString("Total Enrolled Students: 100,000");
        SpannableString spannable1 = new SpannableString("Classes Live: 1000");

        // Load fonts
        Typeface lightFont = ResourcesCompat.getFont(this, R.font.work_sans);
        Typeface boldFont = ResourcesCompat.getFont(this, R.font.work_sans_bold);

        // Apply light font to the entire text
        totalStudents.setTypeface(lightFont);
        classesLive.setTypeface(lightFont);

        // Find start and end positions of numbers
        int start = spannable.toString().indexOf("100,000");
        int end = start + "100,000".length();

        int start1 = spannable1.toString().indexOf("1000");
        int end1 = start1 + "1000".length(); // FIXED: Should be start1, not start

        // Apply bold font and color to "100,000"
        spannable.setSpan(new CustomTypefaceSpan("", boldFont), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#0F4D73")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply bold font and red color (#D16D6A) to "1000"
        spannable1.setSpan(new CustomTypefaceSpan("", boldFont), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable1.setSpan(new ForegroundColorSpan(Color.parseColor("#D16D6A")), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set formatted text to TextViews
        totalStudents.setText(spannable);
        classesLive.setText(spannable1);



        // Retrieve the first name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("FIRST_NAME", "User"); // Default "User" if no name saved

        // Update the TextView with the user's name
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
}
