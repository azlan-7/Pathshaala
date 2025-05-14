package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.TimeSlotAdapterTeacher;
import com.example.loginpage.models.TimeSlot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ShowTimeTableNewViewTeacher extends AppCompatActivity {

    private RecyclerView recyclerViewTeacher;
    private TimeSlotAdapterTeacher adapterTeacher;
    private ArrayList<TimeSlot> timeSlotListTeacher;

    private ImageView addButtonTeacher;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_time_table_new_view_teacher);

        addButtonTeacher = findViewById(R.id.imageView103Teacher);
        recyclerViewTeacher = findViewById(R.id.recyclerTimeSlotsTeacher);
        recyclerViewTeacher.setLayoutManager(new LinearLayoutManager(this));
        continueButton = findViewById(R.id.button42Teacher);

        addButtonTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(ShowTimeTableNewViewTeacher.this, TimeTableInsert.class);
            startActivity(intent);
        });

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShowTimeTableNewViewTeacher.this, TeachersDashboardNew.class);
            startActivity(intent);
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);
        Log.d("ShowTimeTableNewViewTeacher", "Fetching timetable for UserID: " + userId);

        DatabaseHelper.getTimeTableByUserId(userId, new DatabaseHelper.ProcedureResultCallback<List<DatabaseHelper.TimeTableEntry>>() {
            // Inside the onSuccess callback of getTimeTableByUserId in ShowTimeTableNewViewTeacher.java
            @Override
            public void onSuccess(List<DatabaseHelper.TimeTableEntry> result) {
                List<TimeSlot> slotList = new ArrayList<>();
                // SharedPreferences sharedPreferences = getSharedPreferences("TimeTableData", MODE_PRIVATE);
                SimpleDateFormat outputDayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();

                for (DatabaseHelper.TimeTableEntry e : result) {
                    if (!e.subjectName.toLowerCase().contains("demo")) {
                        StringBuilder daysBuilder = new StringBuilder();
                        if (e.mon) daysBuilder.append("Monday, ");
                        if (e.tue) daysBuilder.append("Tuesday, ");
                        if (e.wed) daysBuilder.append("Wednesday, ");
                        if (e.thur) daysBuilder.append("Thursday, ");
                        if (e.fri) daysBuilder.append("Friday, ");
                        if (e.sat) daysBuilder.append("Saturday, ");
                        if (e.sun) daysBuilder.append("Sunday, ");

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
                        String duration = ""; // Initialize as empty
                        switch (e.durationType) {
                            case 1:
                                duration = e.durationNo + " Yearly";
                                break;
                            case 2:
                                duration = e.durationNo + " Weekly";
                                break;
                            case 3:
                                duration = e.durationNo + " Daily";
                                break;
                            default:
                                duration = String.valueOf(e.durationNo); //just show the number
                                break;
                        }

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
                }

                runOnUiThread(() -> {
                    if (!slotList.isEmpty()) {
                        TimeSlotAdapterTeacher adapter = new TimeSlotAdapterTeacher(slotList);
                        recyclerViewTeacher.setAdapter(adapter);
                    } else {
                        Toast.makeText(ShowTimeTableNewViewTeacher.this, "No time slots available!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ShowTimeTableNewViewTeacher.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTeacher), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

