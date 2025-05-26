package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

public class ShowTimeTableNewView extends AppCompatActivity implements TimeSlotAdapter.OnOptInOutListener {

    private RecyclerView recyclerView;
    private TimeSlotAdapter adapter;
    private ArrayList<TimeSlot> timeSlotList;
    private AppCompatButton continueButton;
    private int currentUserId; // To store the current student's ID

    private TextView tvTeachersInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_time_table_new_view);

        Intent intentTeacher = getIntent();
        int teacherUserIdFromIntent = intentTeacher.getIntExtra("USER_ID", -1); // Retrieve the teacher's UserId

        if (teacherUserIdFromIntent != -1) {
            Log.d("ShowTimeTableNewView", "Teacher UserID received from Intent: " + teacherUserIdFromIntent);
            // Now you can use teacherUserIdFromIntent when sending the notification
        } else {
            Log.e("ShowTimeTableNewView", "Teacher UserID not received from Intent.");
            // Handle the error appropriately, maybe finish the activity or load a default value
        }

        continueButton = findViewById(R.id.button42);
        recyclerView = findViewById(R.id.recyclerTimeSlots);
        tvTeachersInfo = findViewById(R.id.tvTeachersInfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Retrieve teacher's full name and referral code from Intent
        Intent intentNameRef = getIntent();
        String teacherFullName = intentNameRef.getStringExtra("USER_FIRST_NAME");
        String teacherReferralCode = intentNameRef.getStringExtra("USER_SELF_REFERRAL_CODE");
        Integer teacherSubjectId = intentNameRef.getIntExtra("Subject_ID",-1);
        Integer teacherGradeId = intentNameRef.getIntExtra("Grade_ID",-1);

        // Set the text for tvTeachersInfo if the data is available
        if (teacherFullName != null && teacherReferralCode != null) {
            String teacherInfoText = teacherFullName + " (" + teacherReferralCode + ")";
            tvTeachersInfo.setText(teacherInfoText);
            Log.d("ShowTimeTableNewView", "Teacher Info set: " + teacherInfoText);
        } else {
            // Handle the case where the data might not be passed
            tvTeachersInfo.setText("Teacher Info Not Available");
            Log.w("ShowTimeTableNewView", "Teacher Full Name or Referral Code not found in Intent.");
        }


        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShowTimeTableNewView.this, SearchStudentsDashboard.class);
            startActivity(intent);
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userIdFromSharedPrefs = sharedPreferences.getInt("USER_ID", -1);
        int userIdFromIntent = getIntent().getIntExtra("USER_ID", -1);

        currentUserId = userIdFromSharedPrefs;
        Log.d("ShowTimeTableNewView", "Fetching UserId(currentUserId) " + currentUserId);
        Log.d("ShowTimeTableNewView", "Fetching timetable for UserID from Intent: " + userIdFromIntent);


        DatabaseHelper.getTimeTableByUserId(userIdFromIntent,teacherSubjectId,teacherGradeId, new DatabaseHelper.ProcedureResultCallback<List<DatabaseHelper.TimeTableEntry>>() {
            @Override
            public void onSuccess(List<DatabaseHelper.TimeTableEntry> result) {
                List<TimeSlot> slotList = new ArrayList<>();
                SimpleDateFormat outputDayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();

                for (DatabaseHelper.TimeTableEntry e : result) {
                    StringBuilder daysBuilder = new StringBuilder();
                    if ("Monday".equalsIgnoreCase(e.mon)) daysBuilder.append("Monday, ");
                    if ("Tuesday".equalsIgnoreCase(e.tue)) daysBuilder.append("Tuesday, ");
                    if ("Wednessday".equalsIgnoreCase(e.wed)) daysBuilder.append("Wednesday, ");
                    if ("Thru".equalsIgnoreCase(e.thur)) daysBuilder.append("Thursday, ");
                    if ("Friday".equalsIgnoreCase(e.fri)) daysBuilder.append("Friday, ");
                    if ("Saturday".equalsIgnoreCase(e.sat)) daysBuilder.append("Saturday, ");
                    if ("Sunday".equalsIgnoreCase(e.sun)) daysBuilder.append("Sunday, ");

                    String dayString = "";
                    if (daysBuilder.length() > 0) {
                        dayString = daysBuilder.substring(0, daysBuilder.length() - 2); // Remove trailing ", "
                    }
                    if (dayString.isEmpty()) {
                        dayString = "No Days Selected";
                    }

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
                        // Pass both slotList and result to the adapter
                        adapter = new TimeSlotAdapter(slotList, result);
                        adapter.setOnOptInOutListener(ShowTimeTableNewView.this); // Set the listener
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

    @Override
    public void onOptInOut(TimeSlot timeSlot, boolean isOptedIn) {
        if (isOptedIn) {
            // Student opted in, send notification to the teacher
            sendTimetableOptInNotification(timeSlot);
        } else {
            // Student opted out, you might want to handle this differently
            // For now, we'll just log it.
            Log.d("ShowTimeTableNewView", "Student opted out of: " + timeSlot.getSubject() + " - " + timeSlot.getTime());
        }
    }

    private void sendTimetableOptInNotification(TimeSlot timeSlot) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String studentName = sharedPreferences.getString("firstName", "Unknown");
        String studentGrade = sharedPreferences.getString("grade", "Unknown");

        Intent intentTeacher = getIntent();
        int teacherId = intentTeacher.getIntExtra("USER_ID", -1); // Retrieve teacher's UserId here

        if (teacherId != -1) {
            String title = studentName + " (Grade: " + studentGrade + ") - Timetable Opt-In";
            String message = "Student " + studentName + " (Grade " + studentGrade + ") has opted for the following timetable: " +
                    "Subject: " + timeSlot.getSubject() + ", " +
                    "Day(s): " + timeSlot.getDay() + ", " +
                    "Time: " + timeSlot.getTime() + ", " +
                    "Grade: " + timeSlot.getGrade();

            int notificationId = DatabaseHelper.insertNotification(currentUserId, title, message, "info");

            if (notificationId != -1) {
                DatabaseHelper.insertNotificationRead(notificationId, teacherId);
                Toast.makeText(this, "Notification sent to teacher.", Toast.LENGTH_SHORT).show();
                Log.d("ShowTimeTableNewView", "Timetable opt-in notification sent. Notification ID: " + notificationId + ", Teacher ID: " + teacherId);
            } else {
                Toast.makeText(this, "Failed to send notification.", Toast.LENGTH_SHORT).show();
                Log.e("ShowTimeTableNewView", "Failed to insert timetable opt-in notification.");
            }
        } else {
            Toast.makeText(this, "Error: Teacher ID not available.", Toast.LENGTH_SHORT).show();
            Log.e("ShowTimeTableNewView", "Teacher ID not available from Intent.");
        }
    }
}