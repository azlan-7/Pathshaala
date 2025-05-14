package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.TimeSlotAdapter;
import com.example.loginpage.models.TimeSlot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ShowTimeTableNewView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TimeSlotAdapter adapter;
    private ArrayList<TimeSlot> timeSlotList;
    private AppCompatButton continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_time_table_new_view);

        continueButton = findViewById(R.id.button42);
        recyclerView = findViewById(R.id.recyclerTimeSlots);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShowTimeTableNewView.this, SearchStudentsDashboard.class);
            startActivity(intent);
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userIdToFetch;
        int userIdFromSharedPrefs = sharedPreferences.getInt("USER_ID", -1);
        int userIdFromIntent = getIntent().getIntExtra("USER_ID", -1);

        if (userIdFromIntent != -1) {
            userIdToFetch = userIdFromIntent;
            Log.d("ShowTimeTableNewView", "Fetching timetable for UserID from Intent: " + userIdToFetch);
        } else {
            userIdToFetch = userIdFromSharedPrefs;
            Log.d("ShowTimeTableNewView", "Fetching timetable for UserID from SharedPreferences: " + userIdToFetch);
        }

        DatabaseHelper.getTimeTableByUserId(userIdToFetch, new DatabaseHelper.ProcedureResultCallback<List<DatabaseHelper.TimeTableEntry>>() {
            @Override
            public void onSuccess(List<DatabaseHelper.TimeTableEntry> result) {
                List<TimeSlot> slotList = new ArrayList<>();
                SimpleDateFormat outputDayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();

                for (DatabaseHelper.TimeTableEntry e : result) {
                    StringBuilder daysBuilder = new StringBuilder();
                    if ("true".equalsIgnoreCase(e.mon)) daysBuilder.append("Monday, ");
                    if ("true".equalsIgnoreCase(e.tue)) daysBuilder.append("Tuesday, ");
                    if ("true".equalsIgnoreCase(e.wed)) daysBuilder.append("Wednesday, ");
                    if ("true".equalsIgnoreCase(e.thur)) daysBuilder.append("Thursday, ");
                    if ("true".equalsIgnoreCase(e.fri)) daysBuilder.append("Friday, ");
                    if ("true".equalsIgnoreCase(e.sat)) daysBuilder.append("Saturday, ");
                    if ("true".equalsIgnoreCase(e.sun)) daysBuilder.append("Sunday, ");

                    String dayString = "";
                    if (daysBuilder.length() > 0) {
                        dayString = daysBuilder.substring(0, daysBuilder.length() - 2); // Remove trailing ", "
                    }

                    // You can directly use the values from the TimeTableEntry object
                    String subject = e.subjectName;
                    String grade = e.gradeName;
                    String time = e.startTime + " - " + e.endTime;
                    String courseFee = String.valueOf(e.courseFee);
                    String batchCapacity = String.valueOf(e.noOfStudents);
                    String duration = e.durationType;

                    slotList.add(new TimeSlot(
                            subject,
                            grade,
                            dayString,
                            time,
                            courseFee,
                            batchCapacity,
                            duration
                    ));
                }

                runOnUiThread(() -> {
                    if (!slotList.isEmpty()) {
                        TimeSlotAdapter adapter = new TimeSlotAdapter(slotList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(ShowTimeTableNewView.this, "No time slots available for this user!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ShowTimeTableNewView.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
