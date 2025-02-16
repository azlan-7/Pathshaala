package com.example.loginpage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.Calendar;

public class StudentsBasicInfo extends AppCompatActivity {

    private EditText etDOB;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private ImageView cameraIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_basic_info);

        profileImageView = findViewById(R.id.imageViewProfile);
        cameraIcon = findViewById(R.id.imageViewCamera);
        cameraIcon.setOnClickListener(v -> openGallery());


        // Gender Selection
        RadioButton radioMale = findViewById(R.id.radioMale);
        RadioButton radioFemale = findViewById(R.id.radioFemale);
        RadioButton radioOther = findViewById(R.id.radioOther);

        int colorChecked = ContextCompat.getColor(this, R.color.radio_selected);
        radioMale.setButtonTintList(ColorStateList.valueOf(colorChecked));
        radioFemale.setButtonTintList(ColorStateList.valueOf(colorChecked));
        radioOther.setButtonTintList(ColorStateList.valueOf(colorChecked));

        // Load Phone Number if available
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        EditText etContact = findViewById(R.id.editTextPhone);
        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            etContact.setText(phoneNumber);
        } else {
            etContact.setHint("Contact No.");
        }
        etContact.setEnabled(true);

        // Date Picker
        etDOB = findViewById(R.id.editTextDOB);
        etDOB.setOnClickListener(v -> showDatePicker());

        etDOB.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; // Right-side drawable index
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etDOB.getRight() - etDOB.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    showDatePicker();
                    v.performClick();
                    return true;
                }
            }
            return false;
        });

        // Save Button Click - Redirect to StudentsInfo
        Button btnSave = findViewById(R.id.buttonSave);
// Remove the duplicate declaration inside btnSave.setOnClickListener()
        btnSave.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit(); // Use existing sharedPreferences

            // Save user data
            editor.putString("FIRST_NAME", ((EditText) findViewById(R.id.editTextFirstName)).getText().toString().trim());
            editor.putString("LAST_NAME", ((EditText) findViewById(R.id.editTextLastName)).getText().toString().trim());
            editor.putString("EMAIL", ((EditText) findViewById(R.id.editTextEmail)).getText().toString().trim());
            editor.putString("CONTACT", ((EditText) findViewById(R.id.editTextPhone)).getText().toString().trim());
            editor.putString("DOB", etDOB.getText().toString().trim());

            // Retrieve the existing Unique ID if available
            String uniqueID = sharedPreferences.getString("UNIQUE_ID", null);
            if (uniqueID != null) {
                editor.putString("UNIQUE_ID", uniqueID); // Ensure Unique ID is retained
            }

            editor.apply(); // Save changes

            // Pass Unique ID when redirecting to StudentsInfo
            Intent intent = new Intent(StudentsBasicInfo.this, StudentsInfo.class);
            intent.putExtra("UNIQUE_ID", uniqueID);
            startActivity(intent);
            finish();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
