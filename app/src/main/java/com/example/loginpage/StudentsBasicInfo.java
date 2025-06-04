package com.example.loginpage;

import static android.content.ContentValues.TAG; // Note: You might want to remove this if you use your own TAG constant

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
// import android.database.Cursor; // Not used, can remove
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
import android.widget.TextView; // Import TextView
import android.widget.Toast;

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

    private static final String LOG_TAG = "StudentsBasicInfo"; // Use a specific TAG
    private EditText etDOB, etFirstName, etLastName, etContactNo, etEmail, etCity, etAbout; // Added etAbout
    private TextView charCount; // Added charCount
    private static final int MAX_CHAR = 150; // Max character limit for About Us

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private ImageView cameraIcon;
    private AutoCompleteTextView autoCompleteCity; // City Dropdown
    private UserDetailsClass user; // This will hold the currently loaded user details
    private Map<Integer, String> cityMap = new HashMap<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_basic_info);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // --- Initialize UI elements ---
        profileImageView = findViewById(R.id.imageViewProfile);
        cameraIcon = findViewById(R.id.imageViewCamera);
        etFirstName = findViewById(R.id.editTextFirstName);
        etLastName = findViewById(R.id.editTextLastName);
        etContactNo = findViewById(R.id.editTextPhone);
        etEmail = findViewById(R.id.editTextEmail);
        etDOB = findViewById(R.id.editTextDOB);
        etCity = findViewById(R.id.autoCompleteCity1); // Correct ID to etCity
        autoCompleteCity = findViewById(R.id.autoCompleteCity1); // Initialize city dropdown - CORRECT ID

        // --- About Us and Counter ---
        etAbout = findViewById(R.id.editTextTextAbout); // Initialize your About Us EditText
        charCount = findViewById(R.id.textView55Counter); // Initialize your Counter TextView
        charCount.setText(0 + "/" + MAX_CHAR); // Set initial counter text

        // Character counter logic
        etAbout.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                charCount.setText(length + "/" + MAX_CHAR);
                if (length > MAX_CHAR) {
                    charCount.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    charCount.setTextColor(getResources().getColor(android.R.color.black));
                }
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        // --- End About Us and Counter ---

        cameraIcon.setOnClickListener(v -> openGallery());
        loadCitiesFromDatabase();

        // Retrieve phone number from SharedPreferences to fetch user data
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        Log.d(LOG_TAG, "Info received through sharedpref: " + "PhoneNumber: " + phoneNumber);

        // Fetch all user details from DB based on phone number
        fetchUserDetailsFromDB(phoneNumber);

        // Gender Selection
        RadioButton radioMale = findViewById(R.id.radioMale);
        RadioButton radioFemale = findViewById(R.id.radioFemale);
        RadioButton radioOther = findViewById(R.id.radioOther);

        int colorChecked = ContextCompat.getColor(this, R.color.radio_selected); // Ensure this color exists in your resources
        ColorStateList colorStateList = ColorStateList.valueOf(colorChecked);
        radioMale.setButtonTintList(colorStateList);
        radioFemale.setButtonTintList(colorStateList);
        radioOther.setButtonTintList(colorStateList);

        // Contact Number field (make it editable but ensure it's loaded from DB/prefs)
        etContactNo.setEnabled(true); // Assuming this is editable

        // Date Picker
        etDOB.setOnClickListener(v -> showDatePicker());
        etDOB.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; // Right-side drawable index
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (etDOB.getCompoundDrawables()[DRAWABLE_RIGHT] != null && // Check if drawable exists
                        event.getRawX() >= (etDOB.getRight() - etDOB.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    showDatePicker();
                    v.performClick();
                    return true;
                }
            }
            return false;
        });

        // Save Button Click - Update DB and Redirect
        Button btnSave = findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(v -> {
            // --- CRITICAL NULL CHECK for the loaded 'user' object ---
            if (user == null) {
                Log.e(LOG_TAG, "Error: User object is NULL. Cannot proceed with save. Data not loaded yet or failed to load.");
                Toast.makeText(StudentsBasicInfo.this, "User data not loaded. Please wait or restart.", Toast.LENGTH_LONG).show();
                return; // Exit the click listener if user data isn't loaded
            }

            // Extract all current values from EditTexts
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String contactNo = etContactNo.getText().toString().trim();
            String emailId = etEmail.getText().toString().trim();
            String dob = etDOB.getText().toString().trim();
            String aboutText = ""; // Initialize to empty string
            if (etAbout != null && etAbout.getText() != null) {
                aboutText = etAbout.getText().toString().trim();
            } else {
                Log.e(LOG_TAG, "EditText for About Us (etAbout) is null or its text is null. Cannot save.");
                // Optionally show a toast or prevent save if this field is mandatory
            }
            String selectedCityName = autoCompleteCity.getText().toString().trim();

            // Create a new UserDetailsClass instance to hold the updated data
            UserDetailsClass currentUserToUpdate = new UserDetailsClass();

            // *** IMPORTANT: Copy all existing fields from the 'user' object ***
            // This ensures all parameters for the stored procedure are populated,
            // even if they are not being changed by this screen.
            currentUserToUpdate.setUserId(user.getUserId());
            currentUserToUpdate.setUsername(user.getUsername());
            currentUserToUpdate.setPassword(user.getPassword());
            currentUserToUpdate.setUserType(user.getUserType());
            currentUserToUpdate.setSignUpType(user.getSignUpType());
            currentUserToUpdate.setCountryCode(user.getCountryCode());
            currentUserToUpdate.setSecurityKey(user.getSecurityKey());
            currentUserToUpdate.setSelfReferralCode(user.getSelfReferralCode());
            currentUserToUpdate.setCustReferralCode(user.getCustReferralCode());
            currentUserToUpdate.setLatitude(user.getLatitude());
            currentUserToUpdate.setLongitude(user.getLongitude());
            currentUserToUpdate.setUserImageName(user.getUserImageName());
            currentUserToUpdate.setStateId(user.getStateId());
            currentUserToUpdate.setPinCode(user.getPinCode());


            // Determine CityId from selectedCityName
            Integer selectedCityId = null;
            for (Map.Entry<Integer, String> entry : cityMap.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(selectedCityName)) {
                    selectedCityId = entry.getKey();
                    break;
                }
            }
            // Set CityId: Use the newly selected one, or keep the old one if no match/change
            currentUserToUpdate.setCityId(selectedCityId != null ? selectedCityId : user.getCityId());


            // *** Now, set the fields that are being updated from the UI ***
            currentUserToUpdate.setName(firstName);
            currentUserToUpdate.setLastName(lastName);
            currentUserToUpdate.setMobileNo(contactNo);
            currentUserToUpdate.setEmailId(emailId);
            currentUserToUpdate.setDateOfBirth(dob);
            currentUserToUpdate.setAboutUs(aboutText); // Set the about us text here


            // --- Add Logs before DB update ---
            Log.d(LOG_TAG, "--- Data prepared for UserDetailsInsert (Student) ---");
            Log.d(LOG_TAG, "QryStatus: 2 (Update)");
            Log.d(LOG_TAG, "UserId: " + currentUserToUpdate.getUserId());
            Log.d(LOG_TAG, "FirstName (from ET): \"" + firstName + "\"");
            Log.d(LOG_TAG, "LastName (from ET): \"" + lastName + "\"");
            Log.d(LOG_TAG, "Contact No (from ET): \"" + contactNo + "\"");
            Log.d(LOG_TAG, "Email ID (from ET): \"" + emailId + "\"");
            Log.d(LOG_TAG, "DOB (from ET): \"" + dob + "\"");
            Log.d(LOG_TAG, "About Us (from ET): \"" + aboutText + "\"");
            Log.d(LOG_TAG, "Selected City Name (from AutoComplete): \"" + selectedCityName + "\"");
            Log.d(LOG_TAG, "Calculated City ID: " + currentUserToUpdate.getCityId());
            Log.d(LOG_TAG, "Username (from user obj): " + currentUserToUpdate.getUsername());
            Log.d(LOG_TAG, "Password (from user obj): " + (currentUserToUpdate.getPassword() != null ? "********" : "NULL")); // Mask password
            Log.d(LOG_TAG, "UserType (from user obj): " + currentUserToUpdate.getUserType());
            Log.d(LOG_TAG, "Self Referral Code (from user obj): " + currentUserToUpdate.getSelfReferralCode());
            Log.d(LOG_TAG, "State ID (from user obj): " + currentUserToUpdate.getStateId());
            Log.d(LOG_TAG, "Pin Code (from user obj): " + currentUserToUpdate.getPinCode());
            Log.d(LOG_TAG, "--- End Data prepared for UserDetailsInsert (Student) ---");


            // *** Call DatabaseHelper to update the user details ***
            DatabaseHelper.UserDetailsInsert(
                    StudentsBasicInfo.this,
                    "2", // QryStatus "2" for update
                    currentUserToUpdate, // The UserDetailsClass object with updated data
                    new DatabaseHelper.UserResultListener() {
                        @Override
                        public void onQueryResult(List<UserDetailsClass> userList) {
                            Log.d(LOG_TAG, "Database operation completed.");
                            if (userList != null && !userList.isEmpty()) {
                                UserDetailsClass updatedUser = userList.get(0);
                                Log.d(LOG_TAG, "User details updated successfully. About Us: " + updatedUser.getAboutUs());
                                user = updatedUser; // Update the class-level 'user' object with the latest data

                                // Update SharedPreferences with the latest fetched data (if needed, or just specific updated fields)
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("USER_NAME", updatedUser.getName());
                                editor.putString("lastName", updatedUser.getLastName());
                                editor.putString("USER_EMAIL", updatedUser.getEmailId());
                                editor.putString("phoneNumber", updatedUser.getMobileNo());
                                editor.putString("DOB", updatedUser.getDateOfBirth());
                                // editor.putString("ABOUT_US", updatedUser.getAboutUs()); // Save aboutUs to shared preferences
                                // You might want to save city name/id as well.
                                editor.apply();

                                Toast.makeText(StudentsBasicInfo.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                                // Navigate to StudentsInfo after successful update
                                Intent intent = new Intent(StudentsBasicInfo.this, StudentsInfo.class);
                                startActivity(intent);
                                finish(); // Finish StudentsBasicInfo
                            } else {
                                Log.e(LOG_TAG, "Failed to update user details. UserList empty or null from DB.");
                                Toast.makeText(StudentsBasicInfo.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
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
            Log.e(LOG_TAG, "❌ ERROR: Phone number missing from SharedPreferences!");
            // Consider redirecting to login or showing an error
            return;
        }

        DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, userList -> {
            runOnUiThread(() -> { // Ensure UI updates on the main thread
                if (userList != null && !userList.isEmpty()) {
                    user = userList.get(0); // Assign to the class-level 'user' object
                    Log.d(LOG_TAG, "✅ Loaded Correct User: " + user.getName() + " " + user.getLastName());

                    // Populate UI elements from the loaded 'user' object with null checks
                    etFirstName.setText(user.getName() != null ? user.getName() : "");
                    etLastName.setText(user.getLastName() != null ? user.getLastName() : "");
                    etContactNo.setText(user.getMobileNo() != null ? user.getMobileNo() : "");
                    etEmail.setText(user.getEmailId() != null ? user.getEmailId() : "");
                    // Handle DOB: split only if not null and contains space
                    String dobFromDb = user.getDateOfBirth();
                    etDOB.setText(dobFromDb != null && dobFromDb.contains(" ") ? dobFromDb.split("\\s+")[0] : (dobFromDb != null ? dobFromDb : ""));
                    etAbout.setText(user.getAboutUs() != null ? user.getAboutUs() : ""); // Set About Us here

                    String imageName = user.getUserImageName();
                    if (imageName != null && !imageName.isEmpty()) {
                        String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + imageName;
                        Log.d(LOG_TAG, "✅ Profile image URL: " + imageUrl);
                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.generic_avatar)
                                .error(R.drawable.generic_avatar)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profileImageView);
                    } else {
                        Log.e(LOG_TAG, "❌ No profile image found in DB or empty value.");
                        profileImageView.setImageResource(R.drawable.generic_avatar);
                    }

                    // Load city name
                    if (user.getCityId() != null) {
                        String cityName = getCityNameFromId(user.getCityId());
                        if (cityName != null) {
                            etCity.setText(cityName);
                        } else {
                            etCity.setText("");
                            Log.e(LOG_TAG, "❌ City Name not found for City ID: " + user.getCityId());
                        }
                    } else {
                        etCity.setText("");
                        Log.e(LOG_TAG, "❌ City ID is null");
                    }
                    Log.d(LOG_TAG, "✅ UI populated with user data.");
                } else {
                    Log.e(LOG_TAG, "❌ No user found in DB for phone: " + phoneNumber);
                    Toast.makeText(StudentsBasicInfo.this, "User data not found.", Toast.LENGTH_LONG).show();
                    user = null; // Ensure user is null if not found
                }
            });
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
            Log.e(LOG_TAG, "⚠️ No image selected.");
            return;
        }

        // Get userId and selfReferralCode from the loaded 'user' object (more reliable)
        String userIdStr = (user != null && user.getUserId() != null) ? user.getUserId() : null;
        String selfReferralCode = (user != null && user.getSelfReferralCode() != null) ? user.getSelfReferralCode() : null;

        if (userIdStr == null || userIdStr.isEmpty()) {
            Log.e(LOG_TAG, "⚠️ User ID not found/loaded in 'user' object for image upload.");
            Toast.makeText(this, "Error: User ID not available for image upload.", Toast.LENGTH_SHORT).show();
            return;
        }
        int userId = Integer.parseInt(userIdStr); // Convert to int for FileUploader

        if (selfReferralCode == null || selfReferralCode.isEmpty()) {
            Log.e(LOG_TAG, "⚠️ Self Referral Code not available in 'user' object for image upload.");
            Toast.makeText(this, "Error: Self Referral Code not available for image upload.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use U_S for Students
        File imageFile = FileUploader.renameFileForTeachers(this, imageUri, userId, "U_S");
        if (imageFile == null) {
            Log.e(LOG_TAG, "❌ Failed to copy and rename file.");
            Toast.makeText(this, "Failed to prepare image for upload.", Toast.LENGTH_SHORT).show();
            return;
        }

        FileUploader.uploadImage(imageFile, this, "U_S", new FileUploader.UploadCallback() {
            @Override
            public void onUploadComplete(boolean success) {
                if (success) {
                    String uploadedFileName = imageFile.getName();
                    // Pass userId (int) and uploadedFileName to DatabaseHelper
                    DatabaseHelper.updateUserProfileImage(userId, uploadedFileName);
                    Log.d(LOG_TAG, "✅ Image uploaded and DB updated successfully. Filename: " + uploadedFileName);
                    runOnUiThread(() -> {
                        String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + uploadedFileName;
                        Glide.with(StudentsBasicInfo.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.generic_avatar)
                                .error(R.drawable.generic_avatar)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profileImageView);
                        Toast.makeText(StudentsBasicInfo.this, "Profile image updated!", Toast.LENGTH_SHORT).show();
                        // Update shared preferences with the new image name
                        sharedPreferences.edit().putString("USER_PROFILE_IMAGE", uploadedFileName).apply();
                    });
                } else {
                    Log.e(LOG_TAG, "❌ Image upload failed.");
                    runOnUiThread(()->{
                        profileImageView.setImageResource(R.drawable.generic_avatar);
                        Toast.makeText(StudentsBasicInfo.this, "Image upload failed.", Toast.LENGTH_SHORT).show();
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
                        String query = "SELECT cityid, city_nm FROM city";
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {
                            int cityId = rs.getInt("cityid");
                            String cityName = rs.getString("city_nm");
                            cityList.add(cityName);
                            cityMap.put(cityId, cityName);
                        }
                        rs.close();
                        stmt.close();
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error fetching city data: " + e.getMessage(), e);
                }
                return cityList;
            }

            @Override
            protected void onPostExecute(List<String> cities) {
                if (cities != null && !cities.isEmpty()) { // Added null check for cities list
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(StudentsBasicInfo.this, android.R.layout.simple_dropdown_item_1line, cities);
                    autoCompleteCity.setAdapter(adapter);
                    autoCompleteCity.setOnClickListener(v -> autoCompleteCity.showDropDown());
                } else {
                    Log.e(LOG_TAG, "No cities found or error loading cities.");
                    Toast.makeText(StudentsBasicInfo.this, "Could not load cities data.", Toast.LENGTH_SHORT).show();
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

        // Optional: Set min/max dates if you have age restrictions for students
        // Example: Students must be at least 5 years old and not older than 25
        Calendar minStudentAge = Calendar.getInstance();
        minStudentAge.add(Calendar.YEAR, -5); // Max date of birth (oldest student allowed)

        Calendar maxStudentAge = Calendar.getInstance();
        maxStudentAge.add(Calendar.YEAR, -25); // Min date of birth (youngest student allowed)

        datePickerDialog.getDatePicker().setMaxDate(minStudentAge.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(maxStudentAge.getTimeInMillis());


        datePickerDialog.show();
    }
}