package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.UUID;
import android.app.DatePickerDialog;

public class UserOnboardingRadio extends AppCompatActivity {

    private EditText etFirstName, etLastName, etContact, etEmail, etDOB;

    private RadioGroup radioGroup;
    private Button buttonSave;
    private String userType = "Teacher"; // Default selection


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_onboarding_radio);

        etFirstName = findViewById(R.id.editTextText9);
        etLastName = findViewById(R.id.editTextText11);
        etEmail = findViewById(R.id.editTextText12);
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



        radioGroup = findViewById(R.id.teacherorstudent);
        buttonSave = findViewById(R.id.generateUID);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedPhoneNumber = sharedPreferences.getString("phoneNumber", "");
        Log.d("UserOnboardingRadio", "Stored Phone Number: " + savedPhoneNumber);

        // Set the phone number in the contact field
        EditText etContact = findViewById(R.id.editTextText10);
        if (!savedPhoneNumber.isEmpty()) {
            etContact.setText(savedPhoneNumber);
            etContact.setEnabled(false); // Disable editing
            Log.d("UserOnboardingRadio", "Phone Number Retrieved: " + savedPhoneNumber);
        } else {
            etContact.setHint("Contact No.");
            Log.e("UserOnboardingRadio", "Phone Number NOT FOUND in SharedPreferences!");
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioTeacher) {
                userType = "Teacher";
            } else if (checkedId == R.id.radioStudent) {
                userType = "Student";
            }
        });

        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        buttonSave.setOnClickListener(v -> {

            SharedPreferences.Editor editor = sharedPreferences.edit();

            TextView contactTextView = findViewById(R.id.editTextText10);
            contactTextView.setText(savedPhoneNumber);

            editor.putString("FIRST_NAME", etFirstName.getText().toString().trim());
            editor.putString("LAST_NAME", etLastName.getText().toString().trim());
            editor.putString("CONTACT", etContact.getText().toString().trim());
            editor.putString("EMAIL", etEmail.getText().toString().trim());
            editor.putString("DOB", etDOB.getText().toString().trim());
            editor.apply();
            editor.putString("USER_TYPE", userType);
            editor.apply();

            String uniqueID = UUID.randomUUID().toString().substring(0, 8).toUpperCase(); // Generate 8-character ID
            Toast.makeText(this, "ID: " + uniqueID, Toast.LENGTH_SHORT).show();

            // Pass userType and uniqueID to next screen
            Intent intent = new Intent(UserOnboardingRadio.this, ShowUniqueID.class);
            intent.putExtra("USER_TYPE", userType);
            intent.putExtra("UNIQUE_ID", uniqueID);
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