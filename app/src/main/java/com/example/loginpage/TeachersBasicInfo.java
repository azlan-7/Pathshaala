package com.example.loginpage;

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
import android.widget.Toast;


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

public class TeachersBasicInfo extends AppCompatActivity {

    private static final String TAG = "TeachersBasicInfo";

    private EditText etDOB, etFirstName, etLastName, etContact, etEmail, etAbout;
    private UserDetailsClass user; // This is the class-level member for the loaded user details
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private ImageView cameraIcon, backButton;
    private TextView charCount;
    private static final int MAX_CHAR = 150; // Max character limit

    private AutoCompleteTextView autoCompleteCity;
    private Map<Integer, String> cityMap = new HashMap<>(); // Store CityId -> CityName

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_basic_info);

        // Initialize UI elements FIRST
        autoCompleteCity = findViewById(R.id.autoCompleteCity);
        etFirstName = findViewById(R.id.editTextText9);
        etLastName = findViewById(R.id.editTextText11);
        etContact = findViewById(R.id.editTextText10);
        etEmail = findViewById(R.id.editTextText12);
        etDOB = findViewById(R.id.editTextText13);
        profileImageView = findViewById(R.id.imageView50);
        cameraIcon = findViewById(R.id.imageView54);
        backButton = findViewById(R.id.imageView142);
        etAbout = findViewById(R.id.editTextText22);
        charCount = findViewById(R.id.textView55);

        charCount.setText(0 + "/" + MAX_CHAR);

        cameraIcon.setOnClickListener(v -> openGallery());
        loadCitiesFromDatabase();

        Log.d(TAG, "📌 onCreate() Started!"); // Debug ✅

        // Retrieve data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");

        // Only call fetchUserDetails once to load the 'user' object
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            fetchUserDetails(phoneNumber); // This will load 'user' and update UI
        } else {
            Log.e(TAG, "Phone number not found in SharedPreferences for loading user.");
            Toast.makeText(this, "Error: Login data missing.", Toast.LENGTH_LONG).show();
            // Consider disabling save button or showing an error state
        }

        // The following lines should eventually be replaced by the data loaded from 'user' object in fetchUserDetails
        // For now, they might be setting default/stale data if fetchUserDetails is async and not completed.
        // Once fetchUserDetails fully populates UI, these lines can be removed if they are redundant.
        String firstNameFromPrefs = sharedPreferences.getString("USER_NAME", "");
        String lastNameFromPrefs = sharedPreferences.getString("lastName", "");
        String emailFromPrefs = sharedPreferences.getString("USER_EMAIL", "");
        String dobWithTime = sharedPreferences.getString("DOB", "");
        String dobOnly = "";

        if (dobWithTime.contains(" ")) {
            dobOnly = dobWithTime.split(" ")[0]; // Split at the space and take the first part
        } else {
            dobOnly = dobWithTime; // If there's no space, it might already be just the date
        }

        // These initial sets might be overwritten later by fetchUserDetails or provide defaults
        // Consider if these should be set *after* the 'user' object is loaded, or if they are truly fallbacks.
        etDOB.setText(dobOnly);
        etFirstName.setText(firstNameFromPrefs.trim().split("\\s+")[0]);
        etLastName.setText(lastNameFromPrefs);
        etContact.setText(phoneNumber);
        etEmail.setText(emailFromPrefs);


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

        // Radio button tinting (can be done directly in XML with app:buttonTint)
        RadioButton radioMale = findViewById(R.id.radioMale);
        RadioButton radioFemale = findViewById(R.id.radioFemale);
        RadioButton radioOther = findViewById(R.id.radioOther);

        int colorChecked = ContextCompat.getColor(this, R.color.radio_selected); // Make sure @color/radio_selected exists
        // int colorUnchecked = ContextCompat.getColor(this, R.color.gray); // Not needed for setButtonTintList

        ColorStateList colorStateList = ColorStateList.valueOf(colorChecked);
        radioMale.setButtonTintList(colorStateList);
        radioFemale.setButtonTintList(colorStateList);
        radioOther.setButtonTintList(colorStateList);

        etContact.setEnabled(true); // Is this always true? Consider based on app flow.

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

            // --- CRITICAL NULL CHECK for the loaded 'user' object ---
            if (user == null) {
                Log.e(TAG, "Error: User object is NULL. Cannot proceed with save. Data not loaded yet or failed to load.");
                Toast.makeText(TeachersBasicInfo.this, "User data not loaded. Please wait or restart.", Toast.LENGTH_LONG).show();
                return; // Exit the click listener if user data isn't loaded
            }

            // --- Defensive check for etAbout and its text ---
            String aboutText = ""; // Initialize to empty string
            if (etAbout != null && etAbout.getText() != null) {
                aboutText = etAbout.getText().toString().trim();
            } else {
                Log.e(TAG, "EditText for About Us (etAbout) is null or its text is null. Cannot save.");
                Toast.makeText(TeachersBasicInfo.this, "About Us field is missing or invalid.", Toast.LENGTH_SHORT).show();
                return; // Stop the save operation
            }


            // --- Capture values from all EditTexts ---
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String contactNo = etContact.getText().toString().trim();
            String emailId = etEmail.getText().toString().trim();
            Log.d(TAG, "DEBUG_EMAIL: Value read from etEmail: \"" + emailId + "\""); // Log after reading from ET

            String dob = etDOB.getText().toString().trim();
            String selectedCityName = autoCompleteCity.getText().toString().trim();


            // Create a new UserDetailsClass to hold the updated data
            UserDetailsClass currentUserToUpdate = new UserDetailsClass();

            // **Crucially, copy ALL existing fields from the 'user' object, then apply changes**
            // This ensures all parameters for the stored procedure are populated,
            // even if they are not being changed by this screen.
            currentUserToUpdate.setUserId(user.getUserId());
            currentUserToUpdate.setUsername(user.getUsername());
            currentUserToUpdate.setPassword(user.getPassword());
            currentUserToUpdate.setUserType(user.getUserType());
            currentUserToUpdate.setName(firstName);
            currentUserToUpdate.setLastName(lastName);
            currentUserToUpdate.setDateOfBirth(dob);
            currentUserToUpdate.setSignUpType(user.getSignUpType());
            currentUserToUpdate.setCountryCode(user.getCountryCode());
            currentUserToUpdate.setMobileNo(contactNo);
            currentUserToUpdate.setEmailId(emailId);
            currentUserToUpdate.setSecurityKey(user.getSecurityKey());
            currentUserToUpdate.setSelfReferralCode(user.getSelfReferralCode());
            currentUserToUpdate.setCustReferralCode(user.getCustReferralCode());
            currentUserToUpdate.setLatitude(user.getLatitude());
            currentUserToUpdate.setLongitude(user.getLongitude());
            currentUserToUpdate.setUserImageName(user.getUserImageName());
            currentUserToUpdate.setStateId(user.getStateId());
            currentUserToUpdate.setCityId(user.getCityId());
            currentUserToUpdate.setPinCode(user.getPinCode());

            // Set the 'aboutUs' field from the EditText (this is the one being updated)
            currentUserToUpdate.setAboutUs(aboutText);


            Log.d(TAG, "FirstName (from ET): \"" + firstName + "\"");
            Log.d(TAG, "LastName (from ET): \"" + lastName + "\"");
            Log.d(TAG, "Contact No (from ET): \"" + contactNo + "\"");
            Log.d(TAG, "Email ID (from ET): \"" + emailId + "\"");
            Log.d(TAG, "DOB (from ET): \"" + dob + "\"");
            Log.d(TAG, "About Us (from ET): \"" + aboutText + "\"");
            Log.d(TAG, "Selected City Name (from AutoComplete): \"" + selectedCityName + "\"");


            // **Add these log statements before calling DatabaseHelper.UserDetailsInsert**
            Log.d(TAG, "Attempting to update user with UserId: " + currentUserToUpdate.getUserId());
            Log.d(TAG, "Updating aboutUs with value: \"" + currentUserToUpdate.getAboutUs() + "\"");
            Log.d(TAG, "SelfReferralCode for update: " + currentUserToUpdate.getSelfReferralCode());
            Log.d(TAG, "StateId for update: " + currentUserToUpdate.getStateId());
            Log.d(TAG, "CityId for update: " + currentUserToUpdate.getCityId());
            Log.d(TAG, "PinCode for update: " + currentUserToUpdate.getPinCode());
            Log.d(TAG, "MobileNo for update: " + currentUserToUpdate.getMobileNo());
            Log.d(TAG, "EmailId for update: " + currentUserToUpdate.getEmailId());
            Log.d(TAG, "Username for update: " + currentUserToUpdate.getUsername());
            // Log other relevant fields as well

            DatabaseHelper.UserDetailsInsert(
                    TeachersBasicInfo.this,
                    "2", // Use "2" for update
                    currentUserToUpdate, // The fully populated object
                    new DatabaseHelper.UserResultListener() {
                        @Override
                        public void onQueryResult(List<UserDetailsClass> userList) {
                            // Handle the result of the database update
                            Log.d(TAG, "Database operation completed.");

                            if (userList != null && !userList.isEmpty()) {
                                UserDetailsClass updatedUser = userList.get(0);
                                String updatedAboutUs = updatedUser.getAboutUs();

                                Log.d(TAG, "About us updated successfully. Retrieved value from object: " + updatedAboutUs);

                                // You can now use updatedAboutUs, e.g., to update UI or save to SharedPreferences
                                // If your TeachersBasicInfo needs to update the 'user' member variable, do it here:
                                user = updatedUser; // Update the class member variable with latest data

                                try {
                                    Intent intent = new Intent(TeachersBasicInfo.this, TeachersInfoSubSection.class);
                                    startActivity(intent);
                                    finish(); // Finish TeachersBasicInfo if you don't want to come back to it
                                } catch (Exception e) {
                                    Log.e(TAG, "Error starting TeachersInfoSubSection: " + e.getMessage());
                                }
                            } else {
                                Log.e(TAG, "Failed to update about us. UserList empty or null from DB.");
                                Toast.makeText(TeachersBasicInfo.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
        });

        backButton.setOnClickListener(v -> startActivity(new Intent(TeachersBasicInfo.this, TeachersInfoSubSection.class)));


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        autoCompleteCity.setOnClickListener(v -> autoCompleteCity.showDropDown());
    }

    private void fetchUserDetails(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Log.e(TAG, "❌ ERROR: Phone number missing from Intent!");
            return;
        }

        Log.d(TAG, "📌 Fetching user details for phone number: " + phoneNumber);

        DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, userList -> {
            // Ensure this runs on the UI thread
            runOnUiThread(() -> { // Encapsulate UI updates in runOnUiThread
                if (userList == null) {
                    Log.e(TAG, "❌ ERROR: userList is NULL from DatabaseHelper.UserDetailsSelect!");
                    Toast.makeText(TeachersBasicInfo.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userList.isEmpty()) {
                    Log.e(TAG, "❌ No user found in DB for phone: " + phoneNumber);
                    Toast.makeText(TeachersBasicInfo.this, "User not found.", Toast.LENGTH_SHORT).show();
                    user = null; // Ensure user is null if not found
                    return;
                }

                user = userList.get(0);  // ✅ Store user object to the class-level member
                Log.d(TAG, "✅ Loaded Correct User: " + user.getName() + " " + user.getLastName());

                // Now populate UI elements using the loaded 'user' object with null checks
                etFirstName.setText(user.getName() != null ? user.getName().trim().split("\\s+")[0] : "");
                etLastName.setText(user.getLastName() != null ? user.getLastName() : "");
                etContact.setText(user.getMobileNo() != null ? user.getMobileNo() : "");
                etEmail.setText(user.getEmailId() != null ? user.getEmailId() : "");
                etDOB.setText(user.getDateOfBirth() != null ? user.getDateOfBirth() : "");
                etAbout.setText(user.getAboutUs() != null ? user.getAboutUs() : ""); // Set aboutUs here too on load

                String imageName = user.getUserImageName();
                if (imageName != null && !imageName.isEmpty()) {
                    String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + imageName;
                    Log.d(TAG, "✅ Profile image URL: " + imageUrl);

                    Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.generic_avatar)
                            .error(R.drawable.generic_avatar)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profileImageView);
                } else {
                    Log.e(TAG, "❌ No profile image found in DB or empty value.");
                    profileImageView.setImageResource(R.drawable.generic_avatar); // Ensure a default image is set
                }

                // Set the city name in the AutoCompleteTextView
                if (user.getCityId() != null) {
                    String cityName = getCityNameFromId(user.getCityId());
                    if (cityName != null) {
                        autoCompleteCity.setText(cityName);
                    } else {
                        autoCompleteCity.setText("");
                        Log.e(TAG, "❌ City Name not found for City ID: " + user.getCityId());
                    }
                } else {
                    autoCompleteCity.setText("");
                    Log.e(TAG, "❌ City ID is null.");
                }
                Log.d(TAG, "✅ UI Updated Successfully with User Details");
            });
        });
    }

    private String getCityNameFromId(int cityId) {
        if (cityMap.containsKey(cityId)) {
            return cityMap.get(cityId);
        }
        return null;
    }

    private Uri pendingImageUri = null; // Store image if self-referral code isn't available yet

    private void insertProfileImageIntoDB(Uri imageUri) {
        if (imageUri == null) {
            Log.e(TAG, "⚠️ No image selected.");
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        // int userId = sharedPreferences.getInt("USER_ID", -1); // This is not reliable for String UserId
        String userIdStr = user != null ? user.getUserId() : null; // Get UserId from the loaded 'user' object
        String selfReferralCode = user != null ? user.getSelfReferralCode() : null; // Get from the loaded 'user' object


        if (userIdStr == null || userIdStr.isEmpty()) {
            Log.e(TAG, "⚠️ User ID not found/loaded in 'user' object.");
            Toast.makeText(this, "Error: User ID not available for image upload.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selfReferralCode == null || selfReferralCode.isEmpty()) {
            Log.e(TAG, "⚠️ Self Referral Code not available in 'user' object.");
            Toast.makeText(this, "Error: Self Referral Code not available for image upload.", Toast.LENGTH_SHORT).show();
            return;
        }

        File imageFile = FileUploader.renameFileForTeachers(this, imageUri, Integer.parseInt(userIdStr), "U_T"); // Convert UserId string to int if needed
        if (imageFile == null) {
            Log.e(TAG, "❌ Failed to copy and rename file.");
            Toast.makeText(this, "Failed to prepare image for upload.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image asynchronously
        FileUploader.uploadImage(imageFile, this, "U_T", new FileUploader.UploadCallback() {
            @Override
            public void onUploadComplete(boolean success) {
                if (success) {
                    String uploadedFileName = imageFile.getName();
                    DatabaseHelper.updateUserProfileImage(Integer.parseInt(userIdStr), uploadedFileName); // Convert UserId string to int if needed
                    Log.d(TAG, "✅ Image uploaded and DB updated successfully.");
                    Toast.makeText(TeachersBasicInfo.this, "Profile image updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "❌ Image upload failed.");
                    Toast.makeText(TeachersBasicInfo.this, "Image upload failed.", Toast.LENGTH_SHORT).show();
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
                Log.e(TAG, "Error processing selected image: " + e.getMessage(), e);
                Toast.makeText(this, "Error loading image.", Toast.LENGTH_SHORT).show();
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
                    Log.e(TAG, "Error fetching city data: " + e.getMessage(), e);
                }
                return cityList;
            }

            @Override
            protected void onPostExecute(List<String> cities) {
                if (cities != null && !cities.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(TeachersBasicInfo.this, android.R.layout.simple_dropdown_item_1line, cities);
                    autoCompleteCity.setAdapter(adapter);
                } else {
                    Log.e(TAG, "No cities found. Check database connection!");
                    Toast.makeText(TeachersBasicInfo.this, "Could not load cities.", Toast.LENGTH_SHORT).show();
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

        Calendar minAdultAge = Calendar.getInstance();
        minAdultAge.add(Calendar.YEAR, -18);

        Calendar maxAgeLimit = Calendar.getInstance();
        maxAgeLimit.add(Calendar.YEAR, -90);

        datePickerDialog.getDatePicker().setMaxDate(minAdultAge.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(maxAgeLimit.getTimeInMillis());

        datePickerDialog.show();
    }
}