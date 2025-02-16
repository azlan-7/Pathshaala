package com.example.loginpage;

import com.example.loginpage.EncryptionHelper;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.time.LocalDate;
import java.util.Calendar;
import java.util.UUID;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.Log;

public class UserOnboardingRadio extends AppCompatActivity {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

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



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check and Request Location Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, get location
            getUserLocation();
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




            try {
                String EncryptEmail = etEmail.getText().toString().trim();
                Log.d("EncryptionHelper", "Original Data: " + EncryptEmail);

                // Generate AES Key
                byte[] aesKey = EncryptionHelper.generateAESKey();
                Log.d("EncryptionHelper", "Generated AES Key: " + android.util.Base64.encodeToString(aesKey, android.util.Base64.DEFAULT));

                // Encrypt Data
                String encryptedData = EncryptionHelper.encryptData(EncryptEmail, aesKey);
                Log.d("EncryptionHelper", "Encrypted Data: " + encryptedData);

                // Save encrypted email in SharedPreferences (optional)
                editor.putString("ENCRYPTED_EMAIL", encryptedData);
                editor.apply();

                // Decrypt Data
                String decryptedData = EncryptionHelper.decryptData(encryptedData, aesKey);
                Log.d("EncryptionHelper", "Decrypted Data: " + decryptedData);

                // Encrypt AES Key
                String encryptedAESKey = EncryptionHelper.encryptAESKey(aesKey);
                Log.d("EncryptionHelper", "Encrypted AES Key: " + encryptedAESKey);

                // Decrypt AES Key
                byte[] decryptedAESKey = EncryptionHelper.decryptAESKey(encryptedAESKey);
                Log.d("EncryptionHelper", "Decrypted AES Key Matches Original: " + java.util.Arrays.equals(aesKey, decryptedAESKey));

            } catch (Exception e) {
                Log.e("EncryptionHelper", "Error during encryption/decryption", e);
            }



            editor.putString("FIRST_NAME", etFirstName.getText().toString().trim());
            editor.putString("LAST_NAME", etLastName.getText().toString().trim());
            editor.putString("CONTACT", etContact.getText().toString().trim());
            editor.putString("EMAIL", etEmail.getText().toString().trim());
            editor.putString("DOB", etDOB.getText().toString().trim());
            editor.apply();
            editor.putString("USER_TYPE", userType);
            editor.apply();


            String prefix = userType.equals("Teacher") ? "T" : "S";
            String uniqueID = prefix + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + Calendar.getInstance().get(Calendar.YEAR);
            Log.d("UserOnboardingRadio", "User Type " + userType.substring(1,5) + "-"  + userType);
            Toast.makeText(this, "ID: " + uniqueID, Toast.LENGTH_SHORT).show();



            // Pass userType and uniqueID to next screen
            Intent intent = new Intent(UserOnboardingRadio.this, ShowUniqueID.class);
            intent.putExtra("USER_TYPE", userType);
            intent.putExtra("UNIQUE_ID", uniqueID);
            intent.putExtra("ENCRYPTED_EMAIL", sharedPreferences.getString("ENCRYPTED_EMAIL", "No Data"));
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
}
