package com.example.loginpage;

import static android.content.ContentValues.TAG;


import static im.zego.connection.internal.ZegoConnectionImpl.context;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.TimeTableExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowTimeTable extends AppCompatActivity {

    private static final String TAG = "ShowTimeTable";
    private ExpandableListView expandableListView;
    private TimeTableExpandableListAdapter adapter;

    private List<String> sectionTitles;
//    private HashMap<String, List<String>> sectionItems;

    HashMap<String, List<DatabaseHelper.TimeTableEntry>> sectionItems;

    AutoCompleteTextView subjectDropdown,gradeDropdown;

    private Map<String, String> subjectMap = new HashMap<>(); // SubjectName -> SubjectID
    private Map<String, String> selectedTimeSlots = new HashMap<>();

    private Button saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_time_table);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views by ID
        subjectDropdown = findViewById(R.id.editTextText52);
        gradeDropdown = findViewById(R.id.editTextText53); // Grade
        expandableListView = findViewById(R.id.expandableListView);
        expandableListView.setBackground(ContextCompat.getDrawable(this, R.drawable.expandable_list_background));
        saveButton = findViewById(R.id.saveTimeTableBtn);
        saveButton.setOnClickListener(v -> {
            String selectedSubject = subjectDropdown.getText().toString();
            String selectedGrade = gradeDropdown.getText().toString();

            if (selectedSubject.isEmpty() || selectedGrade.isEmpty()) {
                Toast.makeText(this, "Please select both subject and grade", Toast.LENGTH_SHORT).show();
                return;
            }

            String subjectId = subjectMap.get(selectedSubject); // You'll need subject ID for DB
            if (subjectId == null) {
                Toast.makeText(this, "Invalid subject selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Example: Insert timetable slots for each day
            for (String day : sectionTitles) {
                List<DatabaseHelper.TimeTableEntry> classList = sectionItems.get(day);  // ✅ Correct


                for (DatabaseHelper.TimeTableEntry classSlot : classList) {
                    if ("Edit Timetable".equals(classSlot.subjectName)) continue;
                    if (classSlot.remark == null || !classSlot.remark.contains("✓")) continue;


                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    int userId = sharedPreferences.getInt("USER_ID", -1);

                    String startTime = classSlot.startTime;
                    String endTime = classSlot.endTime;

                    int createdByUserId = userId;

                    int subjectIdInt = Integer.parseInt(subjectId.replaceAll("[^0-9]", ""));
                    int gradeId = Integer.parseInt(selectedGrade.replaceAll("[^0-9]", ""));
                    int dayInt = convertDayToInt(day);  // assuming day = "Monday" etc.

                    Log.d("DEBUG", "Context in callback: " + context);
                    DatabaseHelper.insertOrUpdateTimeTable(
                            context,
                            0,
                            userId,
                            subjectIdInt,
                            gradeId,
                            dayInt,
                            startTime,
                            endTime,
                            "",
                            "",
                            createdByUserId,
                            new DatabaseHelper.ProcedureCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    Log.d("ShowTimeTable","Message from db:  " + message);
                                    Log.d("ShowTimeTable", "subjectId=" + subjectId + ", grade=" + selectedGrade + ", day=" + day + ", startTime=" + startTime + ", endTime=" + endTime);
                                    Log.d("ShowTimeTable","Time table saved for userId: "+ createdByUserId);
                                    Toast.makeText(ShowTimeTable.this, "Saved successfully: " + message, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(ShowTimeTable.this, "Failed to save: " + error, Toast.LENGTH_LONG).show();
                                    Log.e("SP_ERROR", error);
                                }
                            }
                    );

                }

            }

            Toast.makeText(this, "Time Table saved successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ShowTimeTable.this, ShowTimeTableNewViewTeacher.class);
            startActivity(intent);
        });


        gradeDropdown.setOnClickListener(v -> gradeDropdown.showDropDown());

        loadSubjects();

        // Grade List
        ArrayList<String> gradeList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            gradeList.add("Grade " + i);
        }


        gradeDropdown.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, gradeList));


        setupExpandableList();

        // Collapse previously expanded group
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup) {
                    expandableListView.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });

        // Handle edit clicks
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String day = sectionTitles.get(groupPosition);
            List<DatabaseHelper.TimeTableEntry> slots = sectionItems.get(day);  // ✅ Correct type

            DatabaseHelper.TimeTableEntry clickedItem = slots.get(childPosition);

            // Skip if "Edit Timetable"
            if ("Edit Timetable".equals(clickedItem.subjectName)) {
                Toast.makeText(this, "Edit clicked for " + day, Toast.LENGTH_SHORT).show();
                return true;
            }

            // Toggle ✓ marker in the remark field (or create a new field like isSelected)
            if (clickedItem.remark == null) clickedItem.remark = "";

            if (clickedItem.remark.contains("✓")) {
                clickedItem.remark = clickedItem.remark.replace("✓", "").trim();
            } else {
                clickedItem.remark = (clickedItem.remark + " ✓").trim();
            }

            ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
            return true;
        });


    }
    public static int convertDayToInt(String day) {
        switch (day.toLowerCase()) {
            case "monday": return 1;
            case "tuesday": return 2;
            case "wednesday": return 3;
            case "thursday": return 4;
            case "friday": return 5;
            case "saturday": return 6;
            case "sunday": return 7;
            default: return 0; // invalid
        }
    }



    private void setupExpandableList() {
        sectionTitles = new ArrayList<>();
        sectionItems = new HashMap<>();



        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday","Sunday"};
        for (String day : days) {
            sectionTitles.add(day);
            List<DatabaseHelper.TimeTableEntry> dayEntries = new ArrayList<>();

            dayEntries.add(new DatabaseHelper.TimeTableEntry("Class 1", "09:00 AM", "10:00 AM", "", ""));
            dayEntries.add(new DatabaseHelper.TimeTableEntry("Class 2", "10:00 AM", "11:00 AM", "", ""));
            dayEntries.add(new DatabaseHelper.TimeTableEntry("Class 3", "11:00 AM", "12:00 PM", "", ""));
            dayEntries.add(new DatabaseHelper.TimeTableEntry("Edit Timetable", "", "", "", ""));

            sectionItems.put(day, dayEntries);
        }

        adapter = new TimeTableExpandableListAdapter(this, sectionTitles, sectionItems);
        expandableListView.setAdapter(adapter);


        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            adapter.setSelectedItem(groupPosition, childPosition);

            DatabaseHelper.TimeTableEntry entry = sectionItems.get(groupPosition).get(childPosition);
            String selectedTimeSlot = entry.getStartTime() + " - " + entry.getEndTime();

            Log.d("SelectedTimeSlot", "Selected: " + selectedTimeSlot);

            // Save to memory / variable
            selectedTimeSlots.put(sectionTitles.get(groupPosition), selectedTimeSlot);

            return true;
        });



        for (String day : sectionTitles) {
            List<DatabaseHelper.TimeTableEntry> entries = sectionItems.get(day);
            for (DatabaseHelper.TimeTableEntry entry : entries) {
                Log.d("TimeTableDebug", day + ": " + entry.subjectName + " " + entry.startTime + " - " + entry.endTime);
            }
        }

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

}