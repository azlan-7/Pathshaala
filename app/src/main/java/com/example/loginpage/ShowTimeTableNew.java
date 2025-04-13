package com.example.loginpage;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.TimeSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShowTimeTableNew extends AppCompatActivity {

    private static final String TAG = "ShowTimeTableNew";
    AutoCompleteTextView subjectDropdown,gradeDropdown, dayDropdown, timeSlotDropdown;
    private Map<String, String> subjectMap = new HashMap<>(); // SubjectName -> SubjectID
    private String selectedSubjectID = null; // Stores the selected Subject ID
    private ArrayList<TimeSlot> timeSlotList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_time_table_new);

        // Find views by ID
        subjectDropdown = findViewById(R.id.editTextText32);
        gradeDropdown = findViewById(R.id.editTextText49); // Grade
        dayDropdown = findViewById(R.id.editTextText50); // Day
        timeSlotDropdown = findViewById(R.id.editTextText51); // Time Slot
        AppCompatButton saveButton = findViewById(R.id.button40);


        gradeDropdown.setOnClickListener(v -> gradeDropdown.showDropDown());
        dayDropdown.setOnClickListener(v -> dayDropdown.showDropDown());
        timeSlotDropdown.setOnClickListener(v -> timeSlotDropdown.showDropDown());


        // Save button logic
        saveButton.setOnClickListener(v -> {
            String subject = subjectDropdown.getText().toString().trim();
            String grade = gradeDropdown.getText().toString().trim();
            String day = dayDropdown.getText().toString().trim();
            String time = timeSlotDropdown.getText().toString().trim();

            if (!subject.isEmpty() && !grade.isEmpty() && !day.isEmpty() && !time.isEmpty()) {
                TimeSlot slot = new TimeSlot(subject, grade, day, time);
                timeSlotList.add(slot);

                Toast.makeText(this, "Time slot added", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ShowTimeTableNew.this, ShowTimeTableNewView.class);
                intent.putExtra("timeslot_list", timeSlotList);
                startActivity(intent);

                // Optional: Clear fields after adding
                subjectDropdown.setText("");
                gradeDropdown.setText("");
                dayDropdown.setText("");
                timeSlotDropdown.setText("");
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        loadSubjects();

        // Grade List
        ArrayList<String> gradeList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            gradeList.add("Grade " + i);
        }

        // Day List
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        // Time Slot List (6:00 AM to 10:00 PM, 30-min intervals)
        ArrayList<String> timeOptions = new ArrayList<>();
        for (int hour = 6; hour < 22; hour++) {
            String startTime = formatTime(hour, 0);
            String endTime = formatTime(hour + 1, 0);
            timeOptions.add(startTime + " - " + endTime);
        }


        // Set adapters
        gradeDropdown.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, gradeList));
        dayDropdown.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, daysOfWeek));
        timeSlotDropdown.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, timeOptions));


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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

        subjectDropdown.setOnClickListener(v -> subjectDropdown.showDropDown());

    }

    private String formatTime(int hour24, int minute) {
        int hour12 = hour24 % 12 == 0 ? 12 : hour24 % 12;
        String amPm = (hour24 < 12 || hour24 == 24) ? "AM" : "PM";
        return String.format(Locale.getDefault(), "%d:%02d %s", hour12, minute, amPm);
    }

}