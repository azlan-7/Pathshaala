package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.UserDetailsClass;
import com.example.loginpage.adapters.CustomExpandableListAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeachersInfoSubSection extends AppCompatActivity {

    private UserDetailsClass user;

    private TextView tvFirstName, tvFullName, tvLastName, tvContact, tvEmail, tvDOB,uniqueIdTextView;

    private ImageView profileImage, backButton;
    private TextView accountInfo, subjectExpertise, education, workExperience, gradesTaught, certifications, awards, promotionalActivities, location, dashboard;
    private static final String TAG = "TeachersInfoSubSection";
    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;

    private List<String> sectionTitles;
    private HashMap<String, List<String>> sectionItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_info_sub_section);

        // Initialize UI elements
        TextView aboutText = findViewById(R.id.textView411);
        backButton = findViewById(R.id.imageView1401);
        accountInfo = findViewById(R.id.textView431);
        subjectExpertise = findViewById(R.id.textView441);
        education = findViewById(R.id.textView451);
        workExperience = findViewById(R.id.textView461);
        certifications = findViewById(R.id.textView571);
        awards = findViewById(R.id.textView591);
        promotionalActivities = findViewById(R.id.textView601);
        location = findViewById(R.id.textView481);
        gradesTaught = findViewById(R.id.textView581);
        ImageView editAbout = findViewById(R.id.imageView441);
        ImageView qrCode = findViewById(R.id.imageView1071);
        profileImage = findViewById(R.id.imageView551);
        dashboard = findViewById(R.id.textView481);
        tvFullName = findViewById(R.id.tvFullName1);
        tvContact = findViewById(R.id.tvContact1);
        tvEmail = findViewById(R.id.tvEmail1);
        uniqueIdTextView = findViewById(R.id.uniqueIdTextView1);

        expandableListView = findViewById(R.id.expandableListView);

        setupExpandableList();

        // Add the Group Expand Listener here
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < expandableListView.getExpandableListAdapter().getGroupCount(); i++) {
                    if (i != groupPosition) {
                        expandableListView.collapseGroup(i); // Collapse all other groups
                    }
                }
            }
        });


        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String selectedItem = sectionItems.get(sectionTitles.get(groupPosition)).get(childPosition);
            navigateToActivity(selectedItem);
            return true;
        });


        // Retrieve data from Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ABOUT_YOURSELF")) {
            String aboutYourself = intent.getStringExtra("ABOUT_YOURSELF");
            Log.d("TeachersInfoSubSection", "Received About: " + aboutYourself);
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
        Log.d("TeachersInfoSubSection","phoneNumber: " + phoneNumber);

        if (phoneNumber == null) phoneNumber = sharedPreferences.getString("phoneNumber", "N/A");

        // Set values immediately
//        tvFullName.setText(firstName + " " + lastName);
        tvContact.setText(phoneNumber);

        {
            Log.e(TAG, "⚠️ Self Referral Code Not Found! Fetching from DB...");
//
//            Log.d("TeachersInfoSubSection", "📌 First Name: " + firstName);
//            Log.d("TeachersInfoSubSection", "📌 Last Name: " + lastName);

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
                Log.d("TeachersInfoSubSection", "📌 User is not null, proceeding with navigation.");

                Intent newIntent = new Intent(TeachersInfoSubSection.this, TeachersBasicInfo.class);
                newIntent.putExtra("USER_ID", user.getUserId());
                newIntent.putExtra("USER_CONTACT", user.getMobileNo());

                Log.d("TeachersInfoSubSection", "📌 Passing Data to TeachersBasicInfo -> "
                        + "UserID: " + user.getUserId() + " | Phone: 11" + user.getMobileNo());
                startActivity(newIntent);
            } else {
                Log.e("TeachersInfoSubSection", "❌ ERROR: User data is not loaded yet! 1122");
                Toast.makeText(TeachersInfoSubSection.this, "User details not available. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        editAbout.setOnClickListener(v -> startActivity(new Intent(TeachersInfoSubSection.this, AboutTeacher.class)));
        qrCode.setOnClickListener(v -> startActivity(new Intent(TeachersInfoSubSection.this, QRCode.class)));
        subjectExpertise.setOnClickListener(v -> startActivity(new Intent(TeachersInfoSubSection.this, SubjectExpertiseNewOne.class)));
        education.setOnClickListener(v -> startActivity(new Intent(TeachersInfoSubSection.this, TeachersEducation.class)));
        workExperience.setOnClickListener(v -> startActivity(new Intent(TeachersInfoSubSection.this, WorkExperience.class)));
        gradesTaught.setOnClickListener(v -> startActivity(new Intent(TeachersInfoSubSection.this, GradesTaught.class)));
        certifications.setOnClickListener(v -> startActivity(new Intent(TeachersInfoSubSection.this, CertificationsAdd.class)));
        awards.setOnClickListener(v -> startActivity(new Intent(TeachersInfoSubSection.this, AddAwards.class)));
        promotionalActivities.setOnClickListener(v -> startActivity(new Intent(TeachersInfoSubSection.this, AddPromotionalMedia.class)));
        location.setOnClickListener(v -> startActivity(new Intent(TeachersInfoSubSection.this, TeachersAddress.class)));
        dashboard.setOnClickListener(v -> startActivity(new Intent(TeachersInfoSubSection.this, TeachersDashboardNew.class)));
        backButton.setOnClickListener(v -> startActivity(new Intent(TeachersInfoSubSection.this, TeachersDashboardNew.class)));

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
                        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
                            lastName = "";
                        }
                        Log.d(TAG, "✅ Last Name Retrieved from DB: " + user.getLastName());
                        tvFullName.setText(user.getName() + " " + lastName);
                        tvContact.setText(user.getMobileNo());
                        tvEmail.setText(user.getEmailId());
                        tvEmail.setText(user.getEmailId());
                        uniqueIdTextView.setText(user.getSelfReferralCode());
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

    private void setupExpandableList() {
        sectionTitles = new ArrayList<>();
        sectionItems = new HashMap<>();

        // Adding sections
        sectionTitles.add("Subject Expertise");
        sectionTitles.add("Education");
        sectionTitles.add("Work Experience");
        sectionTitles.add("Certifications");
        sectionTitles.add("Grades Taught");
        sectionTitles.add("Awards/Achievements");

        // Standalone sections (No dropdowns)
        sectionTitles.add("Promotional Activities");
        sectionTitles.add("Dashboard");

        // Adding subsections (Dropdown Items)
        List<String> subjectExpertiseOptions = new ArrayList<>();
        subjectExpertiseOptions.add("View your Subject Expertise");
        subjectExpertiseOptions.add("Add Subject Expertise");

        List<String> educationOptions = new ArrayList<>();
        educationOptions.add("View your Education");
        educationOptions.add("Add Education");

        List<String> workExperienceOptions = new ArrayList<>();
        workExperienceOptions.add("View your Work Experience");
        workExperienceOptions.add("Add Work Experience");

        List<String> certificationsOptions = new ArrayList<>();
        certificationsOptions.add("View your Certifications");
        certificationsOptions.add("Add Certifications");

        List<String> gradesTaughtOptions = new ArrayList<>();
        gradesTaughtOptions.add("View your Grades Taught");
        gradesTaughtOptions.add("Add Grades Taught");

        List<String> awardsAchievementsOptions = new ArrayList<>();
        awardsAchievementsOptions.add("View your Achievements");
        awardsAchievementsOptions.add("Add Achievements");

        // Adding sections with dropdowns
        sectionItems.put(sectionTitles.get(0), subjectExpertiseOptions);
        sectionItems.put(sectionTitles.get(1), educationOptions);
        sectionItems.put(sectionTitles.get(2), workExperienceOptions);
        sectionItems.put(sectionTitles.get(3), certificationsOptions);
        sectionItems.put(sectionTitles.get(4), gradesTaughtOptions);
        sectionItems.put(sectionTitles.get(5), awardsAchievementsOptions);

        // For Promotional & Dashboard (No Dropdown, Just a Single Click)
        sectionItems.put(sectionTitles.get(6), new ArrayList<>()); // Empty list to prevent dropdown
        sectionItems.put(sectionTitles.get(7), new ArrayList<>()); // Empty list to prevent dropdown

        adapter = new CustomExpandableListAdapter(this, sectionTitles, sectionItems);
        expandableListView.setAdapter(adapter);
    }



    private void navigateToActivity(String selectedItem) {
        Intent intent = null;
        switch (selectedItem) {
            case "View your Subject Expertise":
                intent = new Intent(this, SubjectExpertiseNewOne.class);
                break;
            case "Add Subject Expertise":
                intent = new Intent(this, SubjectExpertise.class);
                break;
            case "View your Education":
                intent = new Intent(this, TeachersEducationView.class);
                break;
            case "Add Education":
                intent = new Intent(this, TeachersEducation.class);
                break;
            case "View your Work Experience":
                intent = new Intent(this, WorkExperienceView.class);
                break;
            case "Add Work Experience":
                intent = new Intent(this, WorkExperience.class);
                break;
            case "View your Certifications":
                intent = new Intent(this, CertificationsView.class);
                break;
            case "Add Certifications":
                intent = new Intent(this, CertificationsAdd.class);
                break;
            case "View your Grades Taught":
                intent = new Intent(this, GradesTaughtView.class);
                break;
            case "Add Grades Taught":
                intent = new Intent(this, GradesTaught.class);
                break;
            case "View your Achievements":
                intent = new Intent(this, Awards.class);
                break;
            case "Add Achievements":
                intent = new Intent(this, AddAwards.class);
                break;
            case "View your Promotional Media":
                intent = new Intent(this, AddPromotionalMedia.class);
                break;
            case "Go to Dashboard":
                intent = new Intent(this, TeachersDashboardNew.class);
                break;
            default:
                Toast.makeText(this, "Invalid selection!", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
    }


    private void fetchUserDetailsFromDB() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");

//        String phoneNumber = getIntent().getStringExtra("phoneNumber");  // Get from Intent
        Log.e("TeachersInfoSubSection", "phoneNumberFetched: " + phoneNumber);
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Log.e("TeachersInfoSubSection", "❌ ERROR: Phone number missing from Intent!");
            return;
        }

        Log.d("TeachersInfoSubSection", "📌 Fetching user details for phone number:89 :: " + phoneNumber);

        DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, userList -> {
            if (!userList.isEmpty()) {
                user = userList.get(0);  // ✅ Store user object
                Log.d("TeachersInfoSubSection", "✅ Loaded Correct User: " + user.getName() + " " + user.getLastName());

                runOnUiThread(() -> {
                    tvFullName.setText(user.getName() + " " + (user.getLastName() != null ? user.getLastName() : ""));
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
                                .apply(RequestOptions.circleCropTransform()) // Makes the image round
                                .into(profileImage));
                    } else {
                        Log.e(TAG, "❌ No profile image found in DB or empty value.");
                    }

                    // ✅ Enable navigation once data is loaded
                    Log.d("TeachersInfoSubSection", "📌 Calling enableNavigation() after user is loaded.");  // Debug log ✅
                });

            } else {
                Log.e("TeachersInfoSubSection", "❌ No user found in DB for phone: " + phoneNumber);
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
