package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
import com.example.loginpage.adapters.TimeSlotAdapter;
import com.example.loginpage.models.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public class ShowTimeTableNewView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TimeSlotAdapter adapter;
    private ArrayList<TimeSlot> timeSlotList;

    private ImageView addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_time_table_new_view);

        recyclerView = findViewById(R.id.recyclerTimeSlots);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<TimeSlot> receivedList = (ArrayList<TimeSlot>) getIntent().getSerializableExtra("timeslot_list");

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("USER_ID", 0); // or hardcode for test

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
                        TimeSlotAdapter adapter = new TimeSlotAdapter(slotList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(ShowTimeTableNewView.this, "No time slots received!", Toast.LENGTH_SHORT).show();
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