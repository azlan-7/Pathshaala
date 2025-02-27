package com.example.loginpage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


import com.example.loginpage.MySqliteDatabase.Connection_Class;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TeachersBasicInfo extends AppCompatActivity {


    private EditText etDOB;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private ImageView cameraIcon;

    private AutoCompleteTextView autoCompleteCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_basic_info);

        autoCompleteCity = findViewById(R.id.autoCompleteCity);


        profileImageView = findViewById(R.id.imageView50);
        cameraIcon = findViewById(R.id.imageView54);

        cameraIcon.setOnClickListener(v -> openGallery());
        loadCitiesFromDatabase();



        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);


        // Retrieve stored user data
        String firstName = sharedPreferences.getString("FIRST_NAME", "");
        String lastName = sharedPreferences.getString("LAST_NAME", "");
        String contact = sharedPreferences.getString("CONTACT", "");
        String email = sharedPreferences.getString("EMAIL", "");
        String dob = sharedPreferences.getString("DOB", "");

        String savedCity = sharedPreferences.getString("CITY", "");
        if (!savedCity.isEmpty()) {
            autoCompleteCity.setText(savedCity, false);
        }

        // Set values to EditText fields
        EditText etFirstName = findViewById(R.id.editTextText9);
        EditText etLastName = findViewById(R.id.editTextText11);
        EditText etContact = findViewById(R.id.editTextText10);
        EditText etEmail = findViewById(R.id.editTextText12);
        EditText etDOB = findViewById(R.id.editTextText13);



        // Populate fields if data exists
        if (!firstName.isEmpty()) etFirstName.setText(firstName);
        if (!lastName.isEmpty()) etLastName.setText(lastName);
        if (!contact.isEmpty()) etContact.setText(contact);
        if (!email.isEmpty()) etEmail.setText(email);
        if (!dob.isEmpty()) etDOB.setText(dob);




        RadioButton radioMale = findViewById(R.id.radioMale);
        RadioButton radioFemale = findViewById(R.id.radioFemale);
        RadioButton radioOther = findViewById(R.id.radioOther);

        int colorChecked = ContextCompat.getColor(this, R.color.radio_selected);
        int colorUnchecked = ContextCompat.getColor(this, R.color.gray); // Change to your default color

        radioMale.setButtonTintList(ColorStateList.valueOf(colorChecked));
        radioFemale.setButtonTintList(ColorStateList.valueOf(colorChecked));
        radioOther.setButtonTintList(ColorStateList.valueOf(colorChecked));

        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            etContact.setText(phoneNumber);
        } else {
            etContact.setHint("Contact No.");
        }
        etContact.setEnabled(true);

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
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("FIRST_NAME", etFirstName.getText().toString().trim());
            editor.putString("LAST_NAME", etLastName.getText().toString().trim());
            editor.putString("CONTACT", etContact.getText().toString().trim());
            editor.putString("EMAIL", etEmail.getText().toString().trim());
            editor.putString("DOB", etDOB.getText().toString().trim());
            editor.putString("CITY", autoCompleteCity.getText().toString().trim()); // Save city
            editor.apply();
            Intent intent = new Intent(TeachersBasicInfo.this, TeachersInfo.class);
            startActivity(intent);
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        autoCompleteCity.setOnClickListener(v -> autoCompleteCity.showDropDown());
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
                profileImageView.setImageBitmap(bitmap);  // Set the chosen image as profile picture

                // Save image URI to SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("PROFILE_IMAGE_URI", imageUri.toString());
                editor.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void loadCitiesFromDatabase() {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                List<String> cityList = new ArrayList<>();
                try {
                    Connection_Class connectionClass = new Connection_Class();
                    Connection connection = connectionClass.CONN();
                    if (connection != null) {
                        String query = "SELECT city_nm FROM city";
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {
                            cityList.add(rs.getString("city_nm"));
                        }
                        rs.close();
                        stmt.close();
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("TeachersBasicInfo", "Error fetching city data: " + e.getMessage());
                }
                return cityList;
            }

            @Override
            protected void onPostExecute(List<String> cities) {
                if (cities != null && !cities.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(TeachersBasicInfo.this, android.R.layout.simple_dropdown_item_1line, cities);
                    autoCompleteCity.setAdapter(adapter);
                } else {
                    Log.e("TeachersBasicInfo", "No cities found. Check database connection!");
                }
            }
        }.execute();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDOB.setText(formattedDate);
                }, year, month, day);

        // Set maximum selectable date (User should be at least 18 years old)
        Calendar minAdultAge = Calendar.getInstance();
        minAdultAge.add(Calendar.YEAR, -18);  // Subtract 18 years (youngest age)

        // Set minimum selectable date (User should not be older than 90 years)
        Calendar maxAgeLimit = Calendar.getInstance();
        maxAgeLimit.add(Calendar.YEAR, -90);  // Subtract 90 years (oldest age)

        // Apply the min and max limits to the date picker
        datePickerDialog.getDatePicker().setMaxDate(minAdultAge.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(maxAgeLimit.getTimeInMillis());

        datePickerDialog.show();
    }

}