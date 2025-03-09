package com.example.loginpage;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.loginpage.MySqliteDatabase.Connection_Class;
import com.example.loginpage.models.UserDetailsClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import android.app.DatePickerDialog;
import android.widget.AutoCompleteTextView;


import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.MySqliteDatabase.UserDatabaseHelper;
import com.example.loginpage.models.UserDetailsClass;


public class UserOnboardingRadio extends AppCompatActivity {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    public EditText etFirstName, etLastName, etContact, etEmail, etDOB, etUsername,etPassword,etConfirmPassword;

    private TextView textViewID;

    private RadioGroup radioGroup;
    private Button buttonSave;
    private String userType = "Teacher"; // Default selection
    private AutoCompleteTextView autoCompleteCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_onboarding_radio);

        // Initialize Views
        etFirstName = findViewById(R.id.editTextText9);
        etLastName = findViewById(R.id.editTextText11);
        etEmail = findViewById(R.id.editTextText12);
        etDOB = findViewById(R.id.editTextText13);
        etContact = findViewById(R.id.editTextText10);
        autoCompleteCity = findViewById(R.id.autoCompleteCity);  // Ensure initialization here
        etUsername = findViewById(R.id.userName);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.confirmPassword);


        fetchCityData(autoCompleteCity);

        // ✅ Show dropdown when clicked
        autoCompleteCity.setOnClickListener(v -> autoCompleteCity.showDropDown());
        autoCompleteCity.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) autoCompleteCity.showDropDown();
        });


        // Retrieve phone number from Intent
        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            Log.d("UserOnboardingRadio", "Phone number received: " + phoneNumber);
            etContact.setText(phoneNumber);
            etContact.setEnabled(false);  // Disable editing
        } else {
            Log.e("UserOnboardingRadio", "Phone number NOT received in Intent!");
            etContact.setHint("Enter your phone number");
        }

        // Ensure autoCompleteCity is not null before setting text
        if (autoCompleteCity != null) {
            autoCompleteCity.setHint("Select City"); // Show hint instead of default text
        } else {
            Log.e("UserOnboardingRadio", "autoCompleteCity is NULL!");
        }


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
        String savedPhoneNumber = getIntent().getStringExtra("phoneNumber"); // Get from Intent first

        if (savedPhoneNumber == null || savedPhoneNumber.isEmpty()) {
            savedPhoneNumber = sharedPreferences.getString("phoneNumber", ""); // Fallback to SharedPreferences
        }

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


        final String phoneNumberFinal = savedPhoneNumber;


        String savedCity = sharedPreferences.getString("CITY", "");
        if (!savedCity.isEmpty()) {
            autoCompleteCity.setText("");  // Ensure no default city is selected
            autoCompleteCity.setHint("Select City"); // Show hint instead
        }


        buttonSave.setOnClickListener(v -> {
            createUser();  // ✅ Ensure User is Created First

            new android.os.Handler().postDelayed(() -> {  // ✅ Wait before fetching data
                DatabaseHelper.UserDetailsSelect(UserOnboardingRadio.this, "4", etContact.getText().toString(), new DatabaseHelper.UserResultListener() {
                    @Override
                    public void onQueryResult(List<UserDetailsClass> userList) {
                        if (!userList.isEmpty()) {
                            UserDetailsClass user = userList.get(0);

                            // ✅ Save user details in SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("USER_NAME", user.getName() + " " + user.getLastName()); // ✅ Save full name
                            editor.putString("USER_EMAIL", user.getEmailId());  // ✅ Save email
                            editor.putString("USER_PHONE", user.getMobileNo()); // ✅ Save phone number
                            editor.putInt("USER_ID", Integer.parseInt(user.getUserId())); // ✅ Save user ID

                            editor.apply(); // ✅ Save changes
                            Log.d("UserOnboardingRadio", "✅ User Data Saved in SharedPreferences");

                            // ✅ Navigate to `TeachersInfo` **AFTER** saving data
                            Intent intent = new Intent(UserOnboardingRadio.this, TeachersInfo.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e("UserOnboardingRadio", "❌ No user found to retrieve details.");
                        }
                    }
                });
            }, 2000);  // ✅ Wait for 2 seconds to allow DB to update
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

    private void getUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // Log to Logcat
                                Log.d("USER_LOCATION", "Latitude: " + latitude + ", Longitude: " + longitude);

                            } else {
                                Log.e("USER_LOCATION", "Failed to get location.");
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createUser() {
        DatabaseHelper.UserResultListener listener = new DatabaseHelper.UserResultListener() {
            @Override
            public void onQueryResult(List<UserDetailsClass> userList) {
                Log.d("UserInfo", "UserList received in createUser: " + userList.size());

                if (!userList.isEmpty()) {
                    UserDetailsClass user = userList.get(0);
                    Log.d("UserInfo", "✅ User Created: " + user.getName() + ", Email: " + user.getEmailId());

                    // ✅ Save user details in SharedPreferences for future retrieval
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    String firstName = user.getName();
                    String lastName = user.getLastName();

                    // Prevent "null" from appearing
                    if (lastName == null || lastName.equalsIgnoreCase("null") || lastName.trim().isEmpty()) {
                        lastName = "";  // Ensure it doesn't get stored as "null"
                    }

                    // If last name is empty, store only first name
                    String fullName = lastName.isEmpty() ? firstName : firstName + " " + lastName;

                    editor.putString("USER_NAME", fullName);
                    Log.d("UserOnboardingRadio", "✅ Fixed Full Name Before Saving: " + fullName);



//                  editor.putString("USER_NAME", user.getName() + " " + user.getLastName()); // ✅ Save full name
                    editor.putString("USER_EMAIL", user.getEmailId());  // ✅ Save email
                    editor.putString("USER_PHONE", user.getMobileNo()); // ✅ Save phone number
                    editor.putInt("USER_ID", Integer.parseInt(user.getUserId())); // ✅ Save user ID

                    editor.apply(); // ✅ Save changes
                    Log.d("UserOnboardingRadio", "First Name: " + user.getName());
                    Log.d("UserOnboardingRadio", "Last Name: " + user.getLastName());
                    Log.d("UserInfo", "✅ User Data Saved in SharedPreferences");
                    Log.d("UserOnboardingRadio", "Last Name Retrieved: " + user.getLastName());


                } else {
                    Log.d("UserInfo", "❌ User creation failed.");
                }
            }
        };

        // Get values from EditText fields
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String dob = etDOB.getText().toString().trim();
        String contactNumber = etContact.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String userType = (radioGroup.getCheckedRadioButtonId() == R.id.radioTeacher) ? "T" : "S";
        String password = etPassword.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate input fields (Optional)
        if (username.isEmpty() || contactNumber.isEmpty() || email.isEmpty()) {
            Log.e("UserCreation", "❌ Required fields are missing!");
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new UserDetailsClass object
        UserDetailsClass user = new UserDetailsClass();
        user.setUserId("0"); // Default ID
        user.setUsername(username);
        user.setPassword(password);
        user.setName(firstName);
        user.setLastName(lastName);
        user.setDateOfBirth(dob);
        user.setUserType(userType);
        user.setSignUpType("");
        user.setCountryCode("+91");
        user.setMobileNo(contactNumber);
        user.setEmailId(email);
        user.setSecurityKey("");
        user.setSelfReferralCode("");
        user.setCustReferralCode("");
        user.setLatitude("");
        user.setLongitude("");
        user.setUserImageName("");

        Log.d("UserOnboardingRadio", "First Name: " + user.getName());
        Log.d("UserOnboardingRadio", "Last Name: " + user.getLastName());
        Log.d("UserInfo", "🚀 Sending User Data to Database: " + user.getUsername());

        // Call method to insert the user into the database
        DatabaseHelper.UserDetailsInsert(this, "1", user, listener);
    }


    private void fetchCityData(AutoCompleteTextView autoCompleteLocation) {
        new Thread(() -> {
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
                Log.e("UserOnboardingRadio", "❌ Error fetching city data: " + e.getMessage());
            }

            // ✅ Ensure UI is updated on the main thread
            runOnUiThread(() -> {
                if (!cityList.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(UserOnboardingRadio.this, android.R.layout.simple_dropdown_item_1line, cityList);
                    autoCompleteLocation.setAdapter(adapter);
                } else {
                    Log.e("UserOnboardingRadio", "⚠️ No cities found in DB!");
                }
            });
        }).start();
    }

}
