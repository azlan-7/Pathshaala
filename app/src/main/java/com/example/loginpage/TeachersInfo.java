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

import com.bumptech.glide.Glide;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.UserDetailsClass;

import java.util.List;

public class TeachersInfo extends AppCompatActivity {

    private UserDetailsClass user;

    private TextView tvFirstName, tvFullName, tvLastName, tvContact, tvEmail, tvDOB,uniqueIdTextView;

    private ImageView profileImage;
    private TextView accountInfo, subjectExpertise, education, workExperience, gradesTaught, certifications, awards, promotionalActivities, location, dashboard;
    private static final String TAG = "TeachersInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_info);

        // Initialize UI elements
        TextView aboutText = findViewById(R.id.textView41);
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
        profileImage = findViewById(R.id.imageView55);
        dashboard = findViewById(R.id.textView48);
        tvFullName = findViewById(R.id.tvFullName);
        tvContact = findViewById(R.id.tvContact);
        tvEmail = findViewById(R.id.tvEmail);
        uniqueIdTextView = findViewById(R.id.uniqueIdTextView);


        // Retrieve data from Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ABOUT_YOURSELF")) {
            String aboutYourself = intent.getStringExtra("ABOUT_YOURSELF");
            Log.d("TeachersInfo", "Received About: " + aboutYourself);
            aboutText.setText(aboutYourself);
        } else {
            // If Intent data is missing, fallback to SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String savedAbout = sharedPreferences.getString("ABOUT_YOURSELF", "No info available");
            aboutText.setText(savedAbout);
        }



        fetchUserDetailsFromDB();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        int userId = sharedPreferences.getInt("USER_ID", -1);

//
        Log.d("TeachersInfo","phoneNumber: " + phoneNumber);

        if (phoneNumber == null) phoneNumber = sharedPreferences.getString("phoneNumber", "N/A");

        // Set values immediately
//        tvFullName.setText(firstName + " " + lastName);
        tvContact.setText(phoneNumber);

         {
            Log.e(TAG, "⚠️ Self Referral Code Not Found! Fetching from DB...");
//
//            Log.d("TeachersInfo", "📌 First Name: " + firstName);
//            Log.d("TeachersInfo", "📌 Last Name: " + lastName);

            if (!phoneNumber.isEmpty()) {
                DatabaseHelper.UserDetailsSelect
                        (this, "4", phoneNumber, new DatabaseHelper.UserResultListener() {
                    @Override
                    public void onQueryResult(List<UserDetailsClass> userList) {
                        if (!userList.isEmpty()) {
                            String fetchedReferralCode = userList.get(0).getSelfReferralCode();
                            String fetchedProfileImage = userList.get(0).getUserImageName();
                            Log.d(TAG, "✅ Fetched Referral Code: " + fetchedReferralCode);
                            Log.d(TAG, "✅ Fetched Profile Image: " + fetchedProfileImage);
                            uniqueIdTextView.setText(fetchedReferralCode);
                        } else {
                            Log.e(TAG, "❌ No referral code found in DB!");
                        }
                    }
                });
            }
        }

        // Click listeners
        accountInfo.setOnClickListener(v -> {
            if (user != null) {  // ✅ Ensure user is loaded before navigation
                Log.d("TeachersInfo", "📌 User is not null, proceeding with navigation.");

                Intent newIntent = new Intent(TeachersInfo.this, TeachersBasicInfo.class);
                newIntent.putExtra("USER_ID", user.getUserId());
                newIntent.putExtra("USER_CONTACT", user.getMobileNo());

                Log.d("TeachersInfo", "📌 Passing Data to TeachersBasicInfo -> "
                        + "UserID: " + user.getUserId() + " | Phone: 11" + user.getMobileNo());
                startActivity(newIntent);
            } else {
                Log.e("TeachersInfo", "❌ ERROR: User data is not loaded yet! 1122");
                Toast.makeText(TeachersInfo.this, "User details not available. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        editAbout.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, AboutTeacher.class)));
        qrCode.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, QRCode.class)));
        subjectExpertise.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, SubjectExpertiseNewOne.class)));
        education.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, TeachersEducation.class)));
        workExperience.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, WorkExperience.class)));
        gradesTaught.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, GradesTaught.class)));
        certifications.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, CertificationsAdd.class)));
        awards.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, AddAwards.class)));
        promotionalActivities.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, AddPromotionalMedia.class)));
        location.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, TeachersAddress.class)));
        dashboard.setOnClickListener(v -> startActivity(new Intent(TeachersInfo.this, TeachersDashboardNew.class)));

        if (userId == -1) {
            Log.e(TAG, "❌ ERROR: User ID not found in SharedPreferences!");
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "✅ User ID retrieved from SharedPreferences: " + userId);
        }



        String phoneNum = sharedPreferences.getString("phoneNumber", "");


        // Get phone number from intent
        // Try getting phone number from Intent
        // Try getting phone number from Intent
        // Get phone number from Intent
//        String phoneNum = getIntent().getStringExtra("phoneNumber");

        // If not found in Intent, get it from SharedPreferences
        if (phoneNum == null || phoneNum.isEmpty()) {
            phoneNum = sharedPreferences.getString("USER_PHONE", "");

            if (phoneNum == null || phoneNum.isEmpty()) {
                Log.e(TAG, "❌ ERROR: Phone number not found in Intent or SharedPreferences!");
                return;  // Stop execution if phone number is missing
            }
        }


        Log.d(TAG, "📌 Using Phone Number: " + phoneNumber);

       // Now fetch user details from DB
        DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, new DatabaseHelper.UserResultListener() {
            @Override
            public void onQueryResult(List<UserDetailsClass> userList) {
                if (!userList.isEmpty()) {
                    UserDetailsClass user = userList.get(0);
                    Log.d(TAG, "✅ User Retrieved: " + user.getName() + ", " + user.getEmailId() + ", " + user.getMobileNo());

                    runOnUiThread(() -> {
                        String lastName = user.getLastName();
                        if (lastName == null || lastName.trim().isEmpty()) {
                            lastName = "N/A";
                        }
                        Log.d(TAG, "✅ Last Name Retrieved from DB: " + lastName);
                        tvFullName.setText(user.getName() + " " + lastName);
                        tvContact.setText(user.getMobileNo());
                        tvEmail.setText(user.getEmailId());
                    });
                } else {
//                    Log.e(TAG, "⚠️ No users found in DB for phone: " + phoneNumber);
                }
            }
        });



        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchUserDetailsFromDB() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");

//        String phoneNumber = getIntent().getStringExtra("phoneNumber");  // Get from Intent
        Log.e("TeachersInfo", "phoneNumberFetched: " + phoneNumber);
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Log.e("TeachersInfo", "❌ ERROR: Phone number missing from Intent!");
            return;
        }

        Log.d("TeachersInfo", "📌 Fetching user details for phone number:89 :: " + phoneNumber);

        DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, userList -> {
            if (!userList.isEmpty()) {
                user = userList.get(0);  // ✅ Store user object
                Log.d("TeachersInfo", "✅ Loaded Correct User: " + user.getName() + " " + user.getLastName());

                runOnUiThread(() -> {
                    tvFullName.setText(user.getName() + " " + (user.getLastName() != null ? user.getLastName() : "N/A"));
                    tvContact.setText(user.getMobileNo());
                    tvEmail.setText(user.getEmailId());
                    //uniqueIdTextView.setText(user.getSelfReferralCode());

                    String imageName = user.getUserImageName();
                   // String referralCode = user.getSelfReferralCode();

                    if (imageName != null && !imageName.isEmpty()) {
                        String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + imageName;
                        Log.d(TAG, "✅ Profile image URL: " + imageUrl);

                        runOnUiThread(() -> Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.generic_avatar) // Default profile image
                                .error(R.drawable.generic_avatar) // Show default if error
                                .into(profileImage));
                    } else {
                        Log.e(TAG, "❌ No profile image found in DB or empty value.");
                    }

                    // ✅ Enable navigation once data is loaded
                    Log.d("TeachersInfo", "📌 Calling enableNavigation() after user is loaded.");  // Debug log ✅
                });

            } else {
                Log.e("TeachersInfo", "❌ No user found in DB for phone: " + phoneNumber);
            }
        });
    }


    private void fetchProfileImageFromDB() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1); // ✅ Get updated USER_ID

        if (userId <= 0) {
            Log.e(TAG, "⚠️ User ID not found in SharedPreferences.");
            return;
        }

        DatabaseHelper.fetchUserProfileImage(userId, imageName -> {
            if (imageName != null && !imageName.isEmpty()) {
                String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + imageName;
                Log.d(TAG, "✅ Profile image URL: " + imageUrl);

                runOnUiThread(() -> Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.generic_avatar) // Default profile image
                        .error(R.drawable.generic_avatar) // Show default if error
                        .into(profileImage));
            } else {
                Log.e(TAG, "❌ No profile image found in DB or empty value.");
            }
        });
    }




    private void fetchReferralCodeFromDB(TextView uniqueIdTextView) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1); // ✅ Get updated USER_ID

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
                    String fetchedReferralCode = user.getSelfReferralCode();

                    Log.d(TAG, "✅ Fetched Referral Code from DB: " + fetchedReferralCode);
                    uniqueIdTextView.setText(fetchedReferralCode);
                } else {
                    Log.e(TAG, "❌ No referral code found in DB!");
                }
            }
        });
    }

}
