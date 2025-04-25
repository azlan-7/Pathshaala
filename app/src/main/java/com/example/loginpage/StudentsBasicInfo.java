package com.example.loginpage;

import static android.content.ContentValues.TAG;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentsBasicInfo extends AppCompatActivity {

    private EditText etDOB, etFirstName, etLastName, etContactNo, etEmail, etCity;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private ImageView cameraIcon;
    private AutoCompleteTextView autoCompleteCity; // City Dropdown
    private UserDetailsClass user;
    private Map<Integer, String> cityMap = new HashMap<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_basic_info);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        profileImageView = findViewById(R.id.imageViewProfile);
        cameraIcon = findViewById(R.id.imageViewCamera);
        cameraIcon.setOnClickListener(v -> openGallery());
        etFirstName = findViewById(R.id.editTextFirstName);
        etLastName = findViewById(R.id.editTextLastName);
        etContactNo = findViewById(R.id.editTextPhone);
        etEmail = findViewById(R.id.editTextEmail);
        etCity = findViewById(R.id.autoCompleteCity1); // Correct ID to etCity

        autoCompleteCity = findViewById(R.id.autoCompleteCity1); // Initialize city dropdown - CORRECT ID
        loadCitiesFromDatabase();

        // Retrieve user details from SharedPreferences
        String firstName = sharedPreferences.getString("USER_NAME", "");
        String lastName = sharedPreferences.getString("lastName", "");
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        String email = sharedPreferences.getString("USER_EMAIL", "");
        String dob = sharedPreferences.getString("DOB", "");

        Log.d("StudentsBasicInfo", "Info received through sharedpref: " + "FirstName: " + firstName + " " + "LastName: " + lastName + " " + "PhoneNumber " + phoneNumber + " Email: " + email);

        fetchUserDetailsFromDB(phoneNumber);


        etEmail.setText(email);
        etContactNo.setText(phoneNumber);


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
            editor.putString("CITY", autoCompleteCity.getText().toString().trim());  //changed to get the selected city from autocomplete
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

        autoCompleteCity.setOnClickListener(v -> autoCompleteCity.showDropDown());
    }


    private void fetchUserDetailsFromDB(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Log.e("StudentsBasicInfo", "❌ ERROR: Phone number missing from SharedPreferences!");
            return;
        }

        DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, userList -> {
            if (!userList.isEmpty()) {
                UserDetailsClass user = userList.get(0);
                Log.d("StudentsInfo", "✅ Loaded Correct User: " + user.getName());

                runOnUiThread(() -> {
                    etFirstName.setText(user.getName());
                    etLastName.setText(user.getLastName());
                    etContactNo.setText(user.getMobileNo());
                    etEmail.setText(user.getEmailId());
                    etDOB.setText(user.getDateOfBirth().split("\\s+")[0]);

                    String imageName = user.getUserImageName();
                    if (imageName != null && !imageName.isEmpty()) {
                        String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + imageName;
                        Log.d("StudentsInfo", "✅ Profile image URL: " + imageUrl);
                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.generic_avatar)
                                .error(R.drawable.generic_avatar)
                                .apply(RequestOptions.circleCropTransform()) // Apply circle transformation
                                .into(profileImageView);

                        // ✅ Save to SharedPreferences for future use
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USER_PROFILE_IMAGE", imageName);
                        editor.apply();
                    } else {
                        Log.e("StudentsInfo", "❌ No profile image found in DB or empty value.");
                        profileImageView.setImageResource(R.drawable.generic_avatar); // Set default avatar
                    }

                    // Load city name
                    if (user.getCityId() != null) {
                        String cityName = getCityNameFromId(user.getCityId());
                        if (cityName != null) {
                            etCity.setText(cityName);
                        } else {
                            etCity.setText("");
                            Log.e(TAG, "❌ City Name not found for City ID: " + user.getCityId());
                        }
                    } else {
                        etCity.setText("");
                        Log.e(TAG, "❌ City ID is null");
                    }
                });
            } else {
                Log.e("StudentsInfo", "❌ No user found in DB for phone: " + phoneNumber);
            }
        });
    }

    private String getCityNameFromId(int cityId) {
        if (cityMap.containsKey(cityId)) {
            return cityMap.get(cityId);
        }
        return null;
    }


    private void insertProfileImageIntoDB(Uri imageUri) {
        if (imageUri == null) {
            Log.e("StudentsBasicInfo", "⚠️ No image selected.");
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);
        String selfReferralCode = sharedPreferences.getString("SELF_REFERRAL_CODE", "");

        if (userId <= 0) {
            Log.e("StudentsBasicInfo", "⚠️ User ID not found in SharedPreferences.");
            return;
        }

        if (selfReferralCode.isEmpty()) {
            Log.e("StudentsBasicInfo", "⚠️ Self Referral Code not available.");
            return;
        }

        File imageFile = FileUploader.renameFileForTeachers(this, imageUri, userId, "U_S"); // Changed to U_S
        if (imageFile == null) {
            Log.e("StudentsBasicInfo", "❌ Failed to copy and rename file.");
            return;
        }

        // Upload image asynchronously
        FileUploader.uploadImage(imageFile, this, "U_S", new FileUploader.UploadCallback() { // Changed to U_S
            @Override
            public void onUploadComplete(boolean success) {
                if (success) {
                    String uploadedFileName = imageFile.getName();
                    DatabaseHelper.updateUserProfileImage(userId, uploadedFileName);
                    Log.d("StudentsBasicInfo", "✅ Image uploaded and DB updated successfully.  Filename: " + uploadedFileName); //Added Log
                    // Update the image view after successful upload
                    runOnUiThread(() -> {
                        String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + uploadedFileName;
                        Glide.with(StudentsBasicInfo.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.generic_avatar)
                                .error(R.drawable.generic_avatar)
                                .apply(RequestOptions.circleCropTransform()) // Apply circle transformation
                                .into(profileImageView);
                        // Update shared preferences.
                        sharedPreferences.edit().putString("USER_PROFILE_IMAGE", uploadedFileName).apply();

                    });
                } else {
                    Log.e("StudentsBasicInfo", "❌ Image upload failed.");
                    runOnUiThread(()->{
                        profileImageView.setImageResource(R.drawable.generic_avatar); // set default.
                    });
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
            if(imageUri != null){
                insertProfileImageIntoDB(imageUri);
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
                        String query = "SELECT cityid, city_nm FROM city"; //changed the query to fetch both
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {
                            int cityId = rs.getInt("cityid");  //changed to get the id and name
                            String cityName = rs.getString("city_nm");
                            cityList.add(cityName);
                            cityMap.put(cityId, cityName); // Store in the map
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
                    autoCompleteCity.setOnClickListener(v -> autoCompleteCity.showDropDown());
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

