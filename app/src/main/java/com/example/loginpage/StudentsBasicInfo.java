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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.MySqliteDatabase.Connection_Class;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StudentsBasicInfo extends AppCompatActivity {

    private EditText etDOB;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private ImageView cameraIcon;
    private AutoCompleteTextView autoCompleteCity; // City Dropdown

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_basic_info);

        profileImageView = findViewById(R.id.imageViewProfile);
        cameraIcon = findViewById(R.id.imageViewCamera);
        cameraIcon.setOnClickListener(v -> openGallery());

        autoCompleteCity = findViewById(R.id.autoCompleteCity1); // Initialize city dropdown

        // Load City Data
        loadCitiesFromDatabase();

        // Retrieve user details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Prepopulate city if saved
        String savedCity = sharedPreferences.getString("CITY", "");
        if (!savedCity.isEmpty()) {
            autoCompleteCity.setText(savedCity, false);
        }

        // Gender Selection
        RadioButton radioMale = findViewById(R.id.radioMale);
        RadioButton radioFemale = findViewById(R.id.radioFemale);
        RadioButton radioOther = findViewById(R.id.radioOther);

        int colorChecked = ContextCompat.getColor(this, R.color.radio_selected);
        radioMale.setButtonTintList(ColorStateList.valueOf(colorChecked));
        radioFemale.setButtonTintList(ColorStateList.valueOf(colorChecked));
        radioOther.setButtonTintList(ColorStateList.valueOf(colorChecked));

        // Load Phone Number if available
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
        btnSave.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Save user data
            editor.putString("FIRST_NAME", ((EditText) findViewById(R.id.editTextFirstName)).getText().toString().trim());
            editor.putString("LAST_NAME", ((EditText) findViewById(R.id.editTextLastName)).getText().toString().trim());
            editor.putString("EMAIL", ((EditText) findViewById(R.id.editTextEmail)).getText().toString().trim());
            editor.putString("CONTACT", ((EditText) findViewById(R.id.editTextPhone)).getText().toString().trim());
            editor.putString("DOB", etDOB.getText().toString().trim());
            editor.putString("CITY", autoCompleteCity.getText().toString().trim()); // Save city
            editor.apply();

            // Retrieve the existing Unique ID if available
            String uniqueID = sharedPreferences.getString("UNIQUE_ID", null);
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

        // Show dropdown when clicked
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
                profileImageView.setImageBitmap(bitmap);
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
                    Log.e("StudentsBasicInfo", "Error fetching city data: " + e.getMessage());
                }
                return cityList;
            }

            @Override
            protected void onPostExecute(List<String> cities) {
                if (!cities.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(StudentsBasicInfo.this, android.R.layout.simple_dropdown_item_1line, cities);
                    autoCompleteCity.setAdapter(adapter);
                    autoCompleteCity.setOnClickListener(v -> autoCompleteCity.showDropDown()); // Show dropdown when clicked
                }
            }
        }.execute();
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
