package com.example.loginpage;

import static android.content.ContentValues.TAG;
import static im.zego.connection.internal.ZegoConnectionImpl.context;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.AnalogClock;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.TimeTableExpandableListAdapter;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimeTableInsert extends AppCompatActivity {

    private static final String TAG = "TimeTableInsert";
    private TimeTableExpandableListAdapter adapter;

    private List<String> sectionTitles;
    private HashMap<String, List<DatabaseHelper.TimeTableEntry>> sectionItems;

    private AutoCompleteTextView subjectDropdown, gradeDropdown;
    private EditText durationDropdownDemo, durationDropdownRegular; // Changed to EditText
    private CheckBox disableSliderCheckBox;
    private Button saveButton;
    private EditText editTextNumber, etCourseFee, editTextStartTime, editTextEndTime;
    private RadioGroup durationRadioGroupYearsDaysHoursDemo, durationRadioGroupDaysHoursRegular;
    private RadioButton radioButtonYearsDemo, radioButtonDaysDemo, radioButtonHoursDemo, radioButtonDurationYearsRegular, radioButtonDurationHoursRegular;

    private Map<String, String> subjectMap = new HashMap<>(); // SubjectName -> SubjectID
    private Map<String, String> gradeMap = new HashMap<>(); // GradeName -> GradeID

    private AnalogClock analogClockStartTime, analogClockEndTime;
    private Calendar calendarStart = Calendar.getInstance();
    private Calendar calendarEnd = Calendar.getInstance();
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    MaterialCheckBox checkBoxMon, checkBoxTue, checkBoxWed, checkBoxThu, checkBoxFri, checkBoxSat, checkBoxSun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_time_table_insert);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        durationDropdownRegular = findViewById(R.id.editTextText59); // Now EditText
        disableSliderCheckBox = findViewById(R.id.disableSliderCheckBox);
        subjectDropdown = findViewById(R.id.editTextText52);
        gradeDropdown = findViewById(R.id.editTextText53);
        saveButton = findViewById(R.id.saveTimeTableBtn);
        editTextNumber = findViewById(R.id.editTextNumber);
        etCourseFee = findViewById(R.id.editTextCourseFee);
        durationDropdownDemo = findViewById(R.id.editTextText61); // Now EditText
        editTextStartTime = findViewById(R.id.editTextTimeSlot);
        editTextEndTime = findViewById(R.id.editTextEndTime);
        analogClockStartTime = findViewById(R.id.analogClockStartTime);
        analogClockEndTime = findViewById(R.id.analogClockEndTime);

        durationRadioGroupYearsDaysHoursDemo = findViewById(R.id.radioGroupYearsDaysHours);
        radioButtonYearsDemo = findViewById(R.id.radioButton);
        radioButtonDaysDemo = findViewById(R.id.radioButton2);
        radioButtonHoursDemo = findViewById(R.id.radioButton3);

        durationRadioGroupDaysHoursRegular = findViewById(R.id.radioGroupDaysHours);
        radioButtonDurationYearsRegular = findViewById(R.id.radioButton4);
        radioButtonDurationHoursRegular = findViewById(R.id.radioButton5);
        radioButtonDaysDemo = findViewById(R.id.radioButton6);


        editTextStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        analogClockStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        editTextEndTime.setOnClickListener(v -> showTimePickerDialog(false));
        analogClockEndTime.setOnClickListener(v -> showTimePickerDialog(false));


        checkBoxMon = findViewById(R.id.checkBoxMon);
        checkBoxTue = findViewById(R.id.checkBoxTue);
        checkBoxWed = findViewById(R.id.checkBoxWed);
        checkBoxThu = findViewById(R.id.checkBoxThu);
        checkBoxFri = findViewById(R.id.checkBoxFri);
        checkBoxSat = findViewById(R.id.checkBoxSat);
        checkBoxSun = findViewById(R.id.checkBoxSun);

        // Populate Day Dropdown with an array
        String[] daysOfWeek = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, daysOfWeek);


        // Logic for single selection in Demo Duration radio group
        durationRadioGroupYearsDaysHoursDemo.setOnCheckedChangeListener((group, checkedId) -> {
            // No need to populate a dropdown anymore, the value will be in the EditText
        });

        // Disable Regular Duration Dropdown and Radio Buttons initially
        durationDropdownRegular.setEnabled(false);
        durationDropdownRegular.setAlpha(0.5f);
        radioButtonDurationYearsRegular.setEnabled(false);
        radioButtonDurationHoursRegular.setEnabled(false);
        radioButtonDaysDemo.setEnabled(false);
        radioButtonDurationYearsRegular.setAlpha(0.5f);
        radioButtonDurationHoursRegular.setAlpha(0.5f);
        radioButtonDaysDemo.setAlpha(0.5f);

        // Populate Regular Duration Dropdown initially (not needed anymore)

        // Set CheckBox Listener to Enable/Disable Regular Duration elements and set listener for radio group
        disableSliderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            durationDropdownRegular.setEnabled(isChecked);
            durationDropdownRegular.setAlpha(isChecked ? 1.0f : 0.5f);
            radioButtonDurationYearsRegular.setEnabled(isChecked);
            radioButtonDurationHoursRegular.setEnabled(isChecked);
            radioButtonDaysDemo.setEnabled(isChecked);
            radioButtonDurationYearsRegular.setAlpha(isChecked ? 1.0f : 0.5f);
            radioButtonDurationHoursRegular.setAlpha(isChecked ? 1.0f : 0.5f);
            radioButtonDaysDemo.setAlpha(isChecked ? 1.0f : 0.5f);

            // Set listener for the regular duration radio group only when the checkbox is checked
            if (isChecked) {
                durationRadioGroupDaysHoursRegular.setOnCheckedChangeListener((group, checkedId) -> {
                    // No need to populate a dropdown anymore, the value will be in the EditText
                });
                // Initial population if a radio button is already checked (not needed)
            } else {
                // Remove the listener when the checkbox is unchecked to avoid unexpected behavior
                durationRadioGroupDaysHoursRegular.setOnCheckedChangeListener(null);
                // Optionally clear the text when disabled (if you want)
                durationDropdownRegular.setText("");
            }
        });


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


        // Save button listener
        saveButton.setOnClickListener(v -> {
            String selectedSubject = subjectDropdown.getText().toString();
            String selectedGrade = gradeDropdown.getText().toString();
            final String selectedDuration; // Declare as final
            String startTimeStr = editTextStartTime.getText().toString();
            String endTimeStr = editTextEndTime.getText().toString();

            // Get references to the day checkboxes

            // Build a string of selected days
            StringBuilder selectedDaysBuilder = new StringBuilder();
            if (checkBoxMon.isChecked()) selectedDaysBuilder.append("Mon,");
            if (checkBoxTue.isChecked()) selectedDaysBuilder.append("Tue,");
            if (checkBoxWed.isChecked()) selectedDaysBuilder.append("Wed,");
            if (checkBoxThu.isChecked()) selectedDaysBuilder.append("Thu,");
            if (checkBoxFri.isChecked()) selectedDaysBuilder.append("Fri,");
            if (checkBoxSat.isChecked()) selectedDaysBuilder.append("Sat,");
            if (checkBoxSun.isChecked()) selectedDaysBuilder.append("Sun,");

            final String selectedDays; // Declare as final
            if (selectedDaysBuilder.length() > 0) {
                selectedDays = selectedDaysBuilder.substring(0, selectedDaysBuilder.length() - 1); // Remove the trailing comma
            } else {
                selectedDays = ""; // Initialize even if no days are selected
            }

            String subjectId = subjectMap.get(selectedSubject);
            if (subjectId == null) {
                Toast.makeText(this, "Invalid subject selected", Toast.LENGTH_SHORT).show();
                return;
            }


            if (disableSliderCheckBox.isChecked()) {
                if (radioButtonDurationYearsRegular.isChecked()) {
                    selectedDuration = durationDropdownRegular.getText().toString(); // Get text from EditText
                } else if (radioButtonDurationHoursRegular.isChecked()) {
                    selectedDuration = durationDropdownRegular.getText().toString(); // Get text from EditText
                } else if(radioButtonDaysDemo.isChecked()){
                    selectedDuration = durationDropdownRegular.getText().toString();
                } else {
                    Toast.makeText(this, "Please select a duration (Days/Hours) for regular class", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                selectedDuration = durationDropdownRegular.getText().toString(); // Get text from EditText
            }

            if (selectedSubject.isEmpty() || selectedGrade.isEmpty()) {
                Toast.makeText(this, "Please select both subject and grade", Toast.LENGTH_SHORT).show();
                return;
            }

            if (startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
                Toast.makeText(this, "Please select both start and end times", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date startTime = sdf.parse(startTimeStr);
                Date endTime = sdf.parse(endTimeStr);

                if (startTime != null && endTime != null) {
                    long diffInMillis = endTime.getTime() - startTime.getTime();
                    long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
                    long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);

                    if (startTime.equals(endTime)) {
                        Toast.makeText(this, "Start and end time cannot be the same.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (diffInMinutes < 30) {
                        Toast.makeText(this, "Start and end time must have at least 30 minutes difference.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (diffInHours >= 5) {
                        Toast.makeText(this, "The maximum time difference allowed is less than 5 hours.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(this, "Invalid time format.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error parsing time.", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("USER_ID", -1);

            int createdByUserId = userId;
            int subjectIdInt = Integer.parseInt(subjectId.replaceAll("[^0-9]", ""));
            int gradeId = Integer.parseInt(selectedGrade.replaceAll("[^0-9]", ""));

            int dayInt = -1;
            if (!selectedDays.isEmpty()) {
                String firstDay = selectedDays.split(",")[0];
                dayInt = convertDayToInt(firstDay);
            }

            // Database insertion
            DatabaseHelper.insertOrUpdateTimeTable(
                    context,
                    0,
                    userId,
                    subjectIdInt,
                    gradeId,
                    dayInt,
                    startTimeStr,
                    endTimeStr,
                    selectedDays,
                    selectedDuration,
                    createdByUserId,
                    new DatabaseHelper.ProcedureCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Log.d("TimeTableInsert", "Saved successfully: " + message);

                            // Save to SharedPreferences
                            SharedPreferences sp = getSharedPreferences("TimeTableData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();

                            editor.putString("subject_" + subjectIdInt + "_" + startTimeStr.replace(":", ""), selectedSubject);
                            editor.putString("grade_" + gradeId + "_" + startTimeStr.replace(":", ""), selectedGrade);
                            editor.putString("days_" + startTimeStr.replace(":", ""), selectedDays);
                            editor.putString("time_" + startTimeStr.replace(":", ""), startTimeStr + " - " + endTimeStr);
                            editor.putString("courseFee_" + subjectIdInt + "_" + startTimeStr.replace(":", ""), etCourseFee.getText().toString());
                            editor.putString("batchCapacity_" + subjectIdInt + "_" + startTimeStr.replace(":", ""), editTextNumber.getText().toString());
                            editor.putString("duration_" + subjectIdInt + "_" + startTimeStr.replace(":", ""), selectedDuration);
                            editor.apply();

                            Intent intent = new Intent(TimeTableInsert.this, ShowTimeTableNewViewTeacher.class);
                            intent.putExtra("subjectName", selectedSubject);
                            intent.putExtra("gradeName", selectedGrade);
                            intent.putExtra("dayOfWeek", selectedDays);
                            intent.putExtra("timeSlot", startTimeStr + " - " + endTimeStr);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(TimeTableInsert.this, "Failed to save: " + error, Toast.LENGTH_LONG).show();
                            Log.e("SP_ERROR", error);
                        }
                    }
            );
            Toast.makeText(this, "Time Table saved successfully!", Toast.LENGTH_SHORT).show();
        });

        gradeDropdown.setOnClickListener(v -> gradeDropdown.showDropDown());

        loadSubjects();
        loadGrades();

        // Initial population for Demo Duration (Years selected by default)
        radioButtonYearsDemo.setChecked(true);
        // No need to call populateDemoDurationDropdown to set adapter
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

    private void showTimePickerDialog(boolean isStart) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, hourOfDay, minute) -> {
            if (isStart) {
                calendarStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarStart.set(Calendar.MINUTE, minute);
                editTextStartTime.setText(timeFormat.format(calendarStart.getTime()));
            } else {
                calendarEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarEnd.set(Calendar.MINUTE, minute);
                editTextEndTime.setText(timeFormat.format(calendarEnd.getTime()));
            }
        };

        int hour = isStart ? calendarStart.get(Calendar.HOUR_OF_DAY) : calendarEnd.get(Calendar.HOUR_OF_DAY);
        int minute = isStart ? calendarStart.get(Calendar.MINUTE) : calendarEnd.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle(isStart ? "Select Start Time" : "Select End Time");
        timePickerDialog.show();
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