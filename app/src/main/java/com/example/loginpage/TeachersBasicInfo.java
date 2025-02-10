package com.example.loginpage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.MotionEvent;


import java.util.Calendar;

public class TeachersBasicInfo extends AppCompatActivity {

    private EditText etDOB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_basic_info);


        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        EditText etContact = findViewById(R.id.editTextText10);
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            etContact.setText(phoneNumber);
        } else {
            etContact.setHint("Contact No.");
        }
        etContact.setEnabled(true);

        etDOB = findViewById(R.id.editTextText13);
        etDOB.setOnClickListener(v -> showDatePicker());



        etDOB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2; // Right-side drawable index

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etDOB.getRight() - etDOB.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        showDatePicker();
                        v.performClick(); // Fixes warning
                        return true;
                    }
                }
                return false;
            }
        });

        Button btnSave = findViewById(R.id.button12);

        btnSave.setOnClickListener(v -> {
            Intent intent = new Intent(TeachersBasicInfo.this, TeachersInfo.class);
            startActivity(intent);
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDOB.setText(formattedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
}