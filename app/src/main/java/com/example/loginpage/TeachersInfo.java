package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.UserDetailsClass;

import java.util.List;

public class TeachersInfo extends AppCompatActivity {

    private TextView tvFirstName, tvFullName, tvLastName, tvContact, tvEmail, tvDOB,uniqueIdTextView;
    private TextView accountInfo, subjectExpertise, education, workExperience, gradesTaught, certifications, awards, promotionalActivities, location, dashboard;
    private static final String TAG = "TeachersInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_info);

        // Initialize UI elements
        accountInfo = findViewById(R.id.textView43);
        subjectExpertise = findViewById(R.id.textView44);
        education = findViewById(R.id.textView45);
        workExperience = findViewById(R.id.textView46);
        certifications = findViewById(R.id.textView57);
        awards = findViewById(R.id.textView59);
        promotionalActivities = findViewById(R.id.textView60);
        location = findViewById(R.id.textView48);
        gradesTaught = findViewById(R.id.textView58);
        ImageView editAbout = findViewById(R.id.imageView44);
        ImageView qrCode = findViewById(R.id.imageView107);
        dashboard = findViewById(R.id.textView48);
        tvFullName = findViewById(R.id.tvFullName);
        tvContact = findViewById(R.id.tvContact);
        tvEmail = findViewById(R.id.tvEmail);
        uniqueIdTextView = findViewById(R.id.uniqueIdTextView);

        fetchReferralCodeFromDB(uniqueIdTextView);

        String selfReferralCode = getIntent().getStringExtra("SELF_REFERRAL_CODE");

        if (selfReferralCode != null && !selfReferralCode.equals("N/A")) {
            Log.d(TAG, "📌 Self Referral Code: " + selfReferralCode);
            uniqueIdTextView.setText(selfReferralCode);  // ✅ Display Code
        } else {
            Log.e(TAG, "⚠️ Self Referral Code Not Found! Fetching from DB...");

            // ✅ Try fetching again from DB
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String phoneNumber = sharedPreferences.getString("phoneNumber", "");
            String fullName = sharedPreferences.getString("USER_NAME", "N/A");

            // Check if fullName contains both first and last name
// Remove any stored "null" values
            if (fullName.contains("null")) {
                fullName = fullName.replace("null", "").trim();
            }

            if (fullName.contains("null")) {
                fullName = fullName.replace("null", "").trim();
            }

            String[] nameParts = fullName.split("\\s+", 2);
            String firstName = nameParts[0];
            String lastName = (nameParts.length > 1) ? nameParts[1].trim() : "";

            Log.d("TeachersInfo", "📌 Cleaned Full Name: " + fullName);
            Log.d("TeachersInfo", "📌 First Name: " + firstName);
            Log.d("TeachersInfo", "📌 Last Name: " + lastName);


            // Set values in UI
            tvFullName.setText(fullName);
            tvContact.setText(sharedPreferences.getString("USER_PHONE", "N/A"));
            tvEmail.setText(sharedPreferences.getString("USER_EMAIL", "N/A"));

            // Set values to UI
            tvFullName.setText(fullName);
            tvContact.setText(sharedPreferences.getString("USER_PHONE", "N/A"));
            tvEmail.setText(sharedPreferences.getString("USER_EMAIL", "N/A"));
            String email = sharedPreferences.getString("USER_EMAIL", "N/A");
            String phone = sharedPreferences.getString("USER_PHONE", "N/A");

            // ✅ Display the values in TextViews
            tvFullName.setText(fullName);
            tvContact.setText(phone);
            tvEmail.setText(email);

            // ✅ Debugging Log
            Log.d("TeachersInfo", "📌 Fetched from SharedPreferences: " + fullName + " | " + email + " | " + phone);

            if (!phoneNumber.isEmpty()) {
                DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, new DatabaseHelper.UserResultListener() {
                    @Override
                    public void onQueryResult(List<UserDetailsClass> userList) {
                        if (!userList.isEmpty()) {
                            String fetchedReferralCode = userList.get(0).getSelfReferralCode();
                            Log.d(TAG, "✅ Fetched Referral Code: " + fetchedReferralCode);
                            uniqueIdTextView.setText(fetchedReferralCode);
                        } else {
                            Log.e(TAG, "❌ No referral code found in DB!");
                        }
                    }
                });
            }
        }

        // Click listeners
        editAbout.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, AboutTeacher.class)));
        qrCode.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, QRCode.class)));
        accountInfo.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, TeachersBasicInfo.class)));
        subjectExpertise.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, SubjectExpertiseNewOne.class)));
        education.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, TeachersEducation.class)));
        workExperience.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, WorkExperience.class)));
        gradesTaught.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, GradesTaught.class)));
        certifications.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, CertificationsAdd.class)));
        awards.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, AddAwards.class)));
        promotionalActivities.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, AddPromotionalMedia.class)));
        location.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, TeachersAddress.class)));
        dashboard.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, TeachersDashboardNew.class)));

        // Retrieve User ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Log.e(TAG, "❌ ERROR: User ID not found in SharedPreferences!");
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "✅ User ID retrieved from SharedPreferences: " + userId);
        }

        // Get phone number from intent
        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        if (phoneNumber != null) {
            Log.d(TAG, "Fetching user details for phone number: " + phoneNumber);

            DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, new DatabaseHelper.UserResultListener() {
                @Override
                public void onQueryResult(List<UserDetailsClass> userList) {
                    if (!userList.isEmpty()) {
                        UserDetailsClass user = userList.get(0);
                        Log.d(TAG, "✅ User Retrieved: " + user.getName() + ", " + user.getEmailId() + ", " + user.getMobileNo());
                        Log.d(TAG, "✅ User Retrieved: " + user.getName() + ", Last Name: " + user.getLastName());

                        runOnUiThread(() -> {

                            String lastName = user.getLastName();
                            if (lastName == null || lastName.trim().isEmpty()) {
                                lastName = "N/A";
                            }
                            Log.d(TAG, "✅ Last Name Retrieved from DB: " + lastName);
                            tvFullName.setText(user.getName() + " " + lastName);

//                            tvFullName.setText(user.getName() + " " + user.getLastName());
                            tvContact.setText(user.getMobileNo());
                            tvEmail.setText(user.getEmailId());
                        });

                    } else {
                        Log.e(TAG, "⚠️ No users found in DB for phone: " + phoneNumber);
                    }
                }
            });
        } else {
            Log.e(TAG, "❌ ERROR: Phone number not provided in Intent!");
        }

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void fetchReferralCodeFromDB(TextView uniqueIdTextView) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Log.e(TAG, "❌ ERROR: User ID not found in SharedPreferences!");
            uniqueIdTextView.setText("N/A");
            return;
        }

        Log.d(TAG, "📌 Fetching Self Referral Code for User ID: " + userId);

        DatabaseHelper.UserDetailsSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserResultListener() {
            @Override
            public void onQueryResult(List<UserDetailsClass> userList) {
                if (!userList.isEmpty()) {
                    UserDetailsClass user = userList.get(0);
                    String selfReferralCode = user.getSelfReferralCode();

                    // ✅ Log the actual received value
                    Log.d(TAG, "✅ Retrieved Referral Code (Raw): " + selfReferralCode);

                    if (selfReferralCode == null) {
                        Log.e(TAG, "❌ ERROR: Referral Code is NULL in UserDetailsClass!");
                    }

                    if (selfReferralCode != null && !selfReferralCode.trim().isEmpty()) {
                        Log.d(TAG, "✅ Final Referral Code: " + selfReferralCode);
                        uniqueIdTextView.setText(selfReferralCode);
                    } else {
                        Log.e(TAG, "⚠️ Self Referral Code is NULL or Empty! Showing default.");
                        uniqueIdTextView.setText("N/A");
                    }
                } else {
                    Log.e(TAG, "❌ No user found in DB!");
                    uniqueIdTextView.setText("N/A");
                }
            }
        });
    }


}
