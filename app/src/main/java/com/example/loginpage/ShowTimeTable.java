package com.example.loginpage;

import static android.content.ContentValues.TAG;
import static im.zego.connection.internal.ZegoConnectionImpl.context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
    private HashMap<String, List<DatabaseHelper.TimeTableEntry>> sectionItems;

    private AutoCompleteTextView subjectDropdown, gradeDropdown, dayDropdown, durationDropdown;
    private CheckBox disableSliderCheckBox;
    private Button saveButton;
    private EditText editTextNumber,etCourseFee;

    private Map<String, String> subjectMap = new HashMap<>(); // SubjectName -> SubjectID
    private Map<String, String> gradeMap = new HashMap<>(); // GradeName -> GradeID

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

        // Initialize views
        dayDropdown = findViewById(R.id.editTextTime);
        durationDropdown = findViewById(R.id.editTextDuration);
        disableSliderCheckBox = findViewById(R.id.disableSliderCheckBox);
        subjectDropdown = findViewById(R.id.editTextText52);
        gradeDropdown = findViewById(R.id.editTextText53);
        expandableListView = findViewById(R.id.expandableListView);
        saveButton = findViewById(R.id.saveTimeTableBtn);
        editTextNumber = findViewById(R.id.editTextNumber);
        etCourseFee = findViewById(R.id.editTextCourseFee);


        etCourseFee.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && etCourseFee.getText().toString().isEmpty()) {
                etCourseFee.setHint("   INR -/");
            } else if (!hasFocus && etCourseFee.getText().toString().isEmpty()) {
                etCourseFee.setHint("   Set Course Fee");
            }
        });

        etCourseFee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No specific action needed while text is changing if only updating hint on focus
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    String currentText = s.toString();
                    if (!currentText.startsWith("INR ")) {
                        String formattedText = "INR " + currentText + " -/";
                        etCourseFee.removeTextChangedListener(this); // To avoid infinite loop
                        etCourseFee.setText(formattedText);
                        etCourseFee.setSelection(formattedText.indexOf(currentText) + currentText.length()); // Move cursor to the end of user input
                        etCourseFee.addTextChangedListener(this); // Re-attach the listener
                    } else if (!currentText.endsWith(" -/")) {
                        String trimmedTextWithoutPrefix = currentText.substring("INR ".length());
                        String formattedText = "INR " + trimmedTextWithoutPrefix + " -/";
                        etCourseFee.removeTextChangedListener(this);
                        etCourseFee.setText(formattedText);
                        etCourseFee.setSelection(formattedText.indexOf(trimmedTextWithoutPrefix) + trimmedTextWithoutPrefix.length());
                        etCourseFee.addTextChangedListener(this);
                    }
                } else {
                    etCourseFee.setHint("   INR -/");
                }
            }
        });


        expandableListView.setBackground(ContextCompat.getDrawable(this, R.drawable.expandable_list_background));

        // Populate Day Dropdown
        List<String> daysList = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            daysList.add(i + " day" + (i > 1 ? "s" : ""));
        }
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, daysList);
        dayDropdown.setAdapter(dayAdapter);
        dayDropdown.setOnClickListener(v -> dayDropdown.showDropDown());
        dayDropdown.setEnabled(false);
        dayDropdown.setAlpha(0.5f);

        // Populate Duration Dropdown
        List<String> durationList = new ArrayList<>();
        durationList.add("1 hour");
        durationList.add("2 hours");
        durationList.add("3 hours");
        durationList.add("4 hours");
        durationList.add("5+ hours");
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, durationList);
        durationDropdown.setAdapter(durationAdapter);
        durationDropdown.setOnClickListener(v -> durationDropdown.showDropDown());
        durationDropdown.setEnabled(false);
        durationDropdown.setAlpha(0.5f);

        // Set CheckBox Listener to Enable/Disable Dropdowns and Update Alpha
        disableSliderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dayDropdown.setEnabled(isChecked);
            durationDropdown.setEnabled(isChecked);
            float alpha = isChecked ? 1.0f : 0.5f;
            dayDropdown.setAlpha(alpha);
            durationDropdown.setAlpha(alpha);
        });

        // Save button listener
        saveButton.setOnClickListener(v -> {
            String selectedSubject = subjectDropdown.getText().toString();
            String selectedGrade = gradeDropdown.getText().toString();
            String selectedDay = dayDropdown.getText().toString();
            String selectedDuration = durationDropdown.getText().toString();

            if (selectedSubject.isEmpty() || selectedGrade.isEmpty()) {
                Toast.makeText(this, "Please select both subject and grade", Toast.LENGTH_SHORT).show();
                return;
            }

            String subjectId = subjectMap.get(selectedSubject);
            if (subjectId == null) {
                Toast.makeText(this, "Invalid subject selected", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("USER_ID", -1);

            for (String day : sectionTitles) {
                List<DatabaseHelper.TimeTableEntry> classList = sectionItems.get(day);
                for (DatabaseHelper.TimeTableEntry classSlot : classList) {
                    if ("Edit Timetable".equals(classSlot.subjectName)) continue;
                    if (classSlot.remark == null || !classSlot.remark.contains("✓")) continue;

                    String startTime = classSlot.startTime;
                    String endTime = classSlot.endTime;
                    int createdByUserId = userId;
                    int subjectIdInt = Integer.parseInt(subjectId.replaceAll("[^0-9]", ""));
                    int gradeId = Integer.parseInt(selectedGrade.replaceAll("[^0-9]", ""));
                    int dayInt = convertDayToInt(day);

                    DatabaseHelper.insertOrUpdateTimeTable(
                            context,
                            0,
                            userId,
                            subjectIdInt,
                            gradeId,
                            dayInt,
                            startTime,
                            endTime,
                            selectedDay,
                            selectedDuration,
                            createdByUserId,
                            new DatabaseHelper.ProcedureCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    Log.d("ShowTimeTable", "Saved successfully: " + message);
                                    Intent intent = new Intent(ShowTimeTable.this, ShowTimeTableNewViewTeacher.class);
                                    intent.putExtra("subjectName", selectedSubject);
                                    intent.putExtra("gradeName", selectedGrade);
                                    intent.putExtra("dayOfWeek", day);
                                    intent.putExtra("timeSlot", startTime + " - " + endTime);
                                    startActivity(intent);
                                    finish();
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
        });

        gradeDropdown.setOnClickListener(v -> gradeDropdown.showDropDown());

        loadSubjects();
        loadGrades();
        setupExpandableList();

        // Collapse previously expanded group
        expandableListView.setOnGroupExpandListener(groupPosition -> {
            int previousGroup = -1;
            if (groupPosition != previousGroup) {
                expandableListView.collapseGroup(previousGroup);
            }
            previousGroup = groupPosition;
        });

        // Handle edit clicks in expandable list
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String day = sectionTitles.get(groupPosition);
            List<DatabaseHelper.TimeTableEntry> slots = sectionItems.get(day);
            DatabaseHelper.TimeTableEntry clickedItem = slots.get(childPosition);

            if ("Edit Timetable".equals(clickedItem.subjectName)) {
                Toast.makeText(this, "Edit clicked for " + day, Toast.LENGTH_SHORT).show();
                return true;
            }

            if (clickedItem.remark == null) clickedItem.remark = "";
            clickedItem.remark = clickedItem.remark.contains("✓") ? clickedItem.remark.replace("✓", "").trim() : (clickedItem.remark + " ✓").trim();

            ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
            return true;
        });
    }

    public static int convertDayToInt(String day) {
        String numericPart = day.split(" ")[0];
        try {
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return 0; // Invalid day format
        }
    }

    public static int convertDurationToInt(String duration) {
        String numericPart = duration.split(" ")[0];
        if (numericPart.contains("+")) {
            return 5; // Represents 5+ hours
        }
        try {
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return 0; // Invalid duration format
        }
    }

    private void setupExpandableList() {
        sectionTitles = new ArrayList<>();
        sectionItems = new HashMap<>();

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
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
            return true;
        });
    }

    private void loadSubjects() {
        String query = "SELECT SubjectID, SubjectName FROM Subject WHERE active = 'true' ORDER BY SubjectName";
        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
            if (result != null && !result.isEmpty()) {
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
                subjectDropdown.setOnClickListener(v -> subjectDropdown.showDropDown());
            } else {
                Log.e(TAG, "No subjects found in the database.");
                Toast.makeText(this, "No Subjects Found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGrades() {
        String query = "SELECT GradeID, GradeName FROM Grades WHERE active = 'true' ORDER BY GradeName";
        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
            if (result != null && !result.isEmpty()) {
                List<String> grades = new ArrayList<>();
                gradeMap.clear();
                for (Map<String, String> row : result) {
                    grades.add(row.get("GradeName"));
                    gradeMap.put(row.get("GradeName"), row.get("GradeID"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, grades);
                gradeDropdown.setAdapter(adapter);
                gradeDropdown.setOnClickListener(v -> gradeDropdown.showDropDown());
            } else {
                Log.e(TAG, "No grades found!");
                Toast.makeText(this, "No Grades Found!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}