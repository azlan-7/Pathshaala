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


import com.bumptech.glide.Glide;
import com.example.loginpage.MySqliteDatabase.Connection_Class;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.UserDetailsClass;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TeachersBasicInfo extends AppCompatActivity {

    private static final String TAG = "TeachersBasicInfo";

    private EditText etDOB,etFirstName,etLastName,etContact,etEmail;
    private UserDetailsClass user;
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
        etFirstName = findViewById(R.id.editTextText9);
        etLastName = findViewById(R.id.editTextText11);
        etContact = findViewById(R.id.editTextText10);
        etEmail = findViewById(R.id.editTextText12);
        etDOB = findViewById(R.id.editTextText13);
        profileImageView = findViewById(R.id.imageView50);
        cameraIcon = findViewById(R.id.imageView54);

        cameraIcon.setOnClickListener(v -> openGallery());
        loadCitiesFromDatabase();

        Log.d("TeachersBasicInfo", "📌 onCreate() Started!"); // Debug ✅

        // ✅ Get data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            int userId = intent.getIntExtra("USER_ID", -1);
            String phoneNumber = intent.getStringExtra("USER_CONTACT");


            Log.d("TeachersBasicInfo", "📌 Received Data -> UserID: 11"
                    + " | Phone: " + phoneNumber);

            fetchUserDetails12(phoneNumber);

        } else {
            Log.e("TeachersBasicInfo", "❌ ERROR: Intent is null!");
        }

        RadioButton radioMale = findViewById(R.id.radioMale);
        RadioButton radioFemale = findViewById(R.id.radioFemale);
        RadioButton radioOther = findViewById(R.id.radioOther);

        int colorChecked = ContextCompat.getColor(this, R.color.radio_selected);
        int colorUnchecked = ContextCompat.getColor(this, R.color.gray); // Change to your default color

        radioMale.setButtonTintList(ColorStateList.valueOf(colorChecked));
        radioFemale.setButtonTintList(ColorStateList.valueOf(colorChecked));
        radioOther.setButtonTintList(ColorStateList.valueOf(colorChecked));

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
            // Reuse the existing intent by setting a new destination
            intent.setClass(TeachersBasicInfo.this, TeachersInfo.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        autoCompleteCity.setOnClickListener(v -> autoCompleteCity.showDropDown());
    }

    private void fetchUserDetails12(String phoneNumber) {


        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Log.e("TeachersBasicInfo", "❌ ERROR: Phone number missing from Intent!");
            return;
        }

        Log.d("TeachersBasicInfo", "📌 Fetching user details for phone number: 545" + phoneNumber);

        DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, userList -> {
            if (userList == null) {
                Log.e("TeachersBasicInfo", "❌ ERROR: userList is NULL!");
                return;
            }

            if (userList.isEmpty()) {
                Log.e("TeachersBasicInfo", "❌ No user found in DB for phone: " + phoneNumber);
                return;
            }

            user = userList.get(0);  // ✅ Store user object
            Log.d("TeachersBasicInfo", "✅ Loaded Correct User: " + user.getName() + " " + user.getLastName());

            runOnUiThread(() -> {
                etFirstName.setText(user.getName() != null ? user.getName() : "N/A");
                etLastName.setText(user.getLastName() != null ? user.getLastName() : "N/A");
                etContact.setText(user.getMobileNo() != null ? user.getMobileNo() : "N/A");
                etEmail.setText(user.getEmailId() != null ? user.getEmailId() : "N/A");
                etDOB.setText(user.getDateOfBirth());


                String imageName = user.getUserImageName();
//                String referralCode = user.getSelfReferralCode();


                if (imageName != null && !imageName.isEmpty()) {
                    String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + imageName;
                    Log.d(TAG, "✅ Profile image URL: " + imageUrl);

                    runOnUiThread(() -> Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.generic_avatar) // Default profile image
                            .error(R.drawable.generic_avatar) // Show default if error
                            .into(profileImageView));
                } else {
                    Log.e(TAG, "❌ No profile image found in DB or empty value.");
                }


                Log.d("TeachersBasicInfo", "✅ UI Updated Successfully with User Details");
            });
        });
    }



    private Uri pendingImageUri = null; // Store image if self-referral code isn't available yet

    private void insertProfileImageIntoDB(Uri imageUri) {
        if (imageUri == null) {
            Log.e("TeachersBasicInfo", "⚠️ No image selected.");
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);
        String selfReferralCode = sharedPreferences.getString("SELF_REFERRAL_CODE", "");

//        if (userId == -1) {
        if (userId <= 0) {
            Log.e("TeachersBasicInfo", "⚠️ User ID not found in SharedPreferences.");
            return;
        }

        if (selfReferralCode.isEmpty()) {
            Log.e("TeachersBasicInfo", "⚠️ Self Referral Code not available.");
            return;
        }

        File imageFile = FileUploader.renameFileForTeachers(this, imageUri, userId, "U_T");
        if (imageFile == null) {
            Log.e("TeachersBasicInfo", "❌ Failed to copy and rename file.");
            return;
        }

        // Upload image asynchronously
        FileUploader.uploadImage(imageFile, this, "U_T", new FileUploader.UploadCallback() {
            @Override
            public void onUploadComplete(boolean success) {
                if (success) {
                    String uploadedFileName = imageFile.getName();
                    DatabaseHelper.updateUserProfileImage(userId, uploadedFileName);
                    Log.d("TeachersBasicInfo", "✅ Image uploaded and DB updated successfully.");
                } else {
                    Log.e("TeachersBasicInfo", "❌ Image upload failed.");
                }
            }
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
                profileImageView.setImageBitmap(bitmap);  // Set the chosen image as profile picture

                // Upload and save image in DB
                insertProfileImageIntoDB(imageUri);

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