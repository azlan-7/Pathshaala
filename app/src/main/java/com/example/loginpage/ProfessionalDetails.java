package com.example.loginpage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfessionalDetails extends AppCompatActivity {

    private EditText startDate, endDate;
    private RadioGroup radioGroup;
    private RadioButton radioYes, radioNo;
    private Calendar startCalendar, endCalendar;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_professional_details);

        // Initialize UI elements with correct IDs
        startDate = findViewById(R.id.editTextText19);
        endDate = findViewById(R.id.editTextText20);
        radioGroup = findViewById(R.id.radioGroup);
        radioYes = findViewById(R.id.radioYes);
        radioNo = findViewById(R.id.radioNo);


        // Default: Yes is selected, End Date is disabled
        radioYes.setChecked(true);
        endDate.setEnabled(false);

        // Set different colors when selected
        radioYes.setTextColor(ContextCompat.getColor(this, R.color.blue));
        radioNo.setTextColor(ContextCompat.getColor(this, R.color.blue));

        // Calendar instances
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        // Set up Start Date Picker
        startDate.setOnClickListener(v -> showDatePicker(startCalendar, startDate));

        // Set up End Date Picker (Only if "No" is selected)
        endDate.setOnClickListener(v -> showEndDatePicker());

        // Listen for changes in RadioGroup
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioNo) {
                endDate.setEnabled(true); // Enable End Date
                endDate.setHint("End Date");

                // Change color of selected option
                radioYes.setTextColor(ContextCompat.getColor(this, R.color.blue));
                radioNo.setTextColor(ContextCompat.getColor(this, R.color.blue));
            } else {
                endDate.setEnabled(false); // Disable End Date
                endDate.setText(""); // Clear previous End Date
                endDate.setHint("Disabled");
                endDate.setTextColor(ContextCompat.getColor(this, R.color.blue)); // Disabled color

                // Change color of selected option
                radioYes.setTextColor(ContextCompat.getColor(this, R.color.blue));
                radioNo.setTextColor(ContextCompat.getColor(this, R.color.blue));
            }
        });

        Button saveButton = findViewById(R.id.button17); // Replace with your actual Save button ID
        saveButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfessionalDetails.this, TeachersInfo.class);
            startActivity(intent);
            finish(); // Finish the current activity so the user doesn’t come back to it on pressing back
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Show Date Picker for Start Date
    private void showDatePicker(Calendar calendar, EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    updateLabel(editText, calendar);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Show Date Picker for End Date with Validation
    private void showEndDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    endCalendar.set(year, month, dayOfMonth);
                    if (endCalendar.after(startCalendar)) {
                        updateLabel(endDate, endCalendar);
                    } else {
                        endDate.setError("End date must be after Start date");
                        endDate.setText(""); // Clear invalid selection
                    }
                },
                endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Update EditText with selected date
    private void updateLabel(EditText editText, Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        editText.setText(dateFormat.format(calendar.getTime()));
    }



}
