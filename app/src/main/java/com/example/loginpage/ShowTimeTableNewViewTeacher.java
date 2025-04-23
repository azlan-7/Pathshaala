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
import com.example.loginpage.adapters.TimeSlotAdapterTeacher; // Create a new Adapter for teachers
import com.example.loginpage.models.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public class ShowTimeTableNewViewTeacher extends AppCompatActivity {

    private RecyclerView recyclerViewTeacher; // Changed variable name
    private TimeSlotAdapterTeacher adapterTeacher; // Changed adapter type and name
    private ArrayList<TimeSlot> timeSlotListTeacher; // Changed variable name

    private ImageView addButtonTeacher; // Changed variable name

    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_time_table_new_view_teacher); // Use the teacher's layout

        addButtonTeacher = findViewById(R.id.imageView103Teacher); // Use teacher's ID
        recyclerViewTeacher = findViewById(R.id.recyclerTimeSlotsTeacher); // Use teacher's ID
        recyclerViewTeacher.setLayoutManager(new LinearLayoutManager(this));
        continueButton = findViewById(R.id.button42Teacher);

        addButtonTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(ShowTimeTableNewViewTeacher.this, ShowTimeTable.class);
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
            @Override
            public void onSuccess(List<DatabaseHelper.TimeTableEntry> result) {
                List<TimeSlot> slotList = new ArrayList<>();
                for (DatabaseHelper.TimeTableEntry e : result) {
                    slotList.add(new TimeSlot(
                            e.subjectName,
                            e.gradeName,
                            e.weekDay,
                            e.startTime + " - " + e.endTime
                    ));
                }

                runOnUiThread(() -> {
                    if (!slotList.isEmpty()) {
                        TimeSlotAdapterTeacher adapter = new TimeSlotAdapterTeacher(slotList); // Use teacher's adapter
                        recyclerViewTeacher.setAdapter(adapter);
                    } else {
                        Toast.makeText(ShowTimeTableNewViewTeacher.this, "No time slots received!", Toast.LENGTH_SHORT).show();
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTeacher), (v, insets) -> { // Use teacher's ID
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}