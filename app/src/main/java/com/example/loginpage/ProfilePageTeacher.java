package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.api.ApiClient;
import com.example.loginpage.api.ApiInterface;
import com.example.loginpage.models.CertificationModel;
import com.example.loginpage.models.Education;
import com.example.loginpage.models.UserDetailsClass;
import com.example.loginpage.adapters.CustomExpandableListAdapter;
import com.example.loginpage.models.UserInfoItem;
import com.example.loginpage.models.UserWiseEducation;
import com.example.loginpage.models.UserWiseGrades;
import com.example.loginpage.models.UserWiseSubject;
import com.example.loginpage.models.UserWiseWorkExperience;
import com.example.loginpage.models.WorkExperienceModel;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePageTeacher extends AppCompatActivity {

    private UserDetailsClass user;

    private TextView tvFirstName, tvFullName, tvLastName, tvContact, tvEmail, tvDOB,uniqueIdTextView;

    private ImageView profileImage, backButton;
    private TextView accountInfo, subjectExpertise, education, workExperience, gradesTaught, certifications, awards, promotionalActivities, location, dashboard;
    private static final String TAG = "ProfilePageTeacher";
    private ExpandableListView expandableListView;
//    private ExpandableListAdapter adapter;

    private CustomExpandableListAdapter adapter;


    private List<String> sectionTitles;
    private HashMap<String, List<String>> sectionItems;
    private int userId;
    private int lastExpandedPosition = -1;
    private ApiInterface apiService;



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

        apiService = ApiClient.getClient().create(ApiInterface.class);


        Log.d("ProfilePageTeacher", "expandableListView is " + (expandableListView != null));


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


        String userId = getIntent().getStringExtra("USER_ID");
        String phoneNumber = getIntent().getStringExtra("USER_PHONE");


        if (userId == null || userId.isEmpty() || phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Invalid user info", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("ProfilePageTeacher", "UserID received: " + userId + ", Phone: " + phoneNumber);

        Log.d("ProfilePageTeacher", "📌 Loaded via Intent -> UserID: " + userId + ", Phone: " + phoneNumber);





        fetchUserDetailsFromDB();

//
        Log.d("ProfilePageTeacher","phoneNumber: " + phoneNumber);

//        if (phoneNumber == null) phoneNumber = sharedPreferences.getString("phoneNumber", "N/A");

        // Set values immediately
//        tvFullName.setText(firstName + " " + lastName);
        tvContact.setText(phoneNumber);

        {
            Log.e(TAG, "⚠️ Self Referral Code Not Found! Fetching from DB...");


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
//        accountInfo.setOnClickListener(v -> {
//            if (user != null) {  // ✅ Ensure user is loaded before navigation
//                Log.d("ProfilePageTeacher", "📌 User is not null, proceeding with navigation.");
//
//                Intent newIntent = new Intent(ProfilePageTeacher.this, TeachersBasicInfo.class);
//                newIntent.putExtra("USER_ID", user.getUserId());
//                newIntent.putExtra("USER_CONTACT", user.getMobileNo());
//
//                Log.d("ProfilePageTeacher", "📌 Passing Data to TeachersBasicInfo -> "
//                        + "UserID: " + user.getUserId() + " | Phone: 11" + user.getMobileNo());
//                startActivity(newIntent);
//            } else {
//                Log.e("ProfilePageTeacher", "❌ ERROR: User data is not loaded yet! 1122");
//                Toast.makeText(ProfilePageTeacher.this, "User details not available. Please try again.", Toast.LENGTH_SHORT).show();
//            }
//        });

//        editAbout.setOnClickListener(v -> startActivity(new Intent(ProfilePageTeacher.this, AboutTeacher.class)));
//        qrCode.setOnClickListener(v -> startActivity(new Intent(ProfilePageTeacher.this, QRCode.class)));
//        subjectExpertise.setOnClickListener(v -> startActivity(new Intent(ProfilePageTeacher.this, SubjectExpertiseNewOne.class)));
//        education.setOnClickListener(v -> startActivity(new Intent(ProfilePageTeacher.this, TeachersEducation.class)));
//        workExperience.setOnClickListener(v -> startActivity(new Intent(ProfilePageTeacher.this, WorkExperience.class)));
//        gradesTaught.setOnClickListener(v -> startActivity(new Intent(ProfilePageTeacher.this, GradesTaught.class)));
//        certifications.setOnClickListener(v -> startActivity(new Intent(ProfilePageTeacher.this, CertificationsAdd.class)));
//        awards.setOnClickListener(v -> startActivity(new Intent(ProfilePageTeacher.this, AddAwards.class)));
//        promotionalActivities.setOnClickListener(v -> startActivity(new Intent(ProfilePageTeacher.this, AddPromotionalMedia.class)));
//        location.setOnClickListener(v -> startActivity(new Intent(ProfilePageTeacher.this, TeachersAddress.class)));
//        dashboard.setOnClickListener(v -> startActivity(new Intent(ProfilePageTeacher.this, TeachersDashboardNew.class)));
//        backButton.setOnClickListener(v -> startActivity(new Intent(ProfilePageTeacher.this, TeachersDashboardNew.class)));



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

    private void loadAllUserInfoFromApi() {
        apiService.getAllUserInfo(userId).enqueue(new Callback<List<UserInfoItem>>() {
            @Override
            public void onResponse(Call<List<UserInfoItem>> call, Response<List<UserInfoItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateSectionItemsFromData(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Failed or empty response");
                }
            }

            @Override
            public void onFailure(Call<List<UserInfoItem>> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });

    }


    private void updateSectionItemsFromData(List<UserInfoItem> dataList) {
        // Clear placeholders first
        for (String title : sectionTitles) {
            List<String> list = sectionItems.get(title);
            if (list != null) list.clear();
        }

        for (UserInfoItem item : dataList) {
            String heading = item.getHeading();
            String description = item.getDescription();

            if (sectionItems.containsKey(heading)) {
                sectionItems.get(heading).add(description);
            }
        }

        // Fallback if any section is still empty
        for (String title : sectionTitles) {
            List<String> list = sectionItems.get(title);
            if (list != null && list.isEmpty() && !title.equals("Promotional Activities") && !title.equals("Dashboard")) {
                list.add("No Data Found");
            }
        }
    }




    private void setupExpandableList() {
        sectionTitles = new ArrayList<>();
        sectionItems = new HashMap<>();

        // Sections
        sectionTitles.add("Subject Expertise");
        sectionTitles.add("Education");
        sectionTitles.add("Work Experience");
        sectionTitles.add("Certifications");
        sectionTitles.add("Grades Taught");
        sectionTitles.add("Awards/Achievements");

        sectionTitles.add("Promotional Activities");
//        sectionTitles.add("Dashboard");

        // Initialize with "Loading..." for dropdown sections
        for (int i = 0; i < 6; i++) {
            List<String> placeholder = new ArrayList<>();
            placeholder.add("Loading...");
            sectionItems.put(sectionTitles.get(i), placeholder);
        }

        // Non-dropdowns
        sectionItems.put("Promotional Activities", new ArrayList<>());
        sectionItems.put("Dashboard", new ArrayList<>());

        adapter = new CustomExpandableListAdapter(this, sectionTitles, sectionItems);
        expandableListView.setAdapter(adapter);


        String userIdStr = getIntent().getStringExtra("USER_ID");  // String from intent

        if (userIdStr == null || userIdStr.isEmpty()) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);  // ✅ Convert to int
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid user ID format", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ProfilePageTeacher","UserId fetched through sharedpreferences: " + userId);
        getAllTeacherInfoByUserId(userId); // or getTeacherInfoByPhone(phoneNumber)
    }


    private void getAllTeacherInfoByUserId(int userId) {
        new Thread(() -> {
            List<UserInfoItem> userInfoList = DatabaseHelper.getTeacherInfoByUserId(userId);

            runOnUiThread(() -> {
                if (userInfoList != null) {
                    Map<String, List<String>> headingMap = new HashMap<>();
                    headingMap.put("Subject", sectionItems.get("Subject Expertise"));
                    headingMap.put("Education", sectionItems.get("Education"));
                    headingMap.put("Work", sectionItems.get("Work Experience"));
                    headingMap.put("Certificate", sectionItems.get("Certifications"));
                    headingMap.put("Grades", sectionItems.get("Grades Taught"));
                    headingMap.put("Award", sectionItems.get("Awards/Achievements"));

                    for (UserInfoItem item : userInfoList) {
                        String heading = item.getHeading();
                        String description = item.getDescription();

                        if (headingMap.containsKey(heading)) {
                            List<String> list = headingMap.get(heading);
                            if (list != null && list.contains("Loading...")) list.clear();
                            list.add(description);
                        }
                    }

                    for (Map.Entry<String, List<String>> entry : headingMap.entrySet()) {
                        entry.getValue().add("Add");
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("ProfilePageTeacher", "No additional info returned.");
                }
            });
        }).start();
    }




    private void fetchUserDetailsFromDB() {
        String phoneNumber = getIntent().getStringExtra("USER_PHONE");

//        String phoneNumber = getIntent().getStringExtra("phoneNumber");  // Get from Intent
        Log.d("ProfilePageTeacher", "phoneNumberFetched: " + phoneNumber);
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Log.e("ProfilePageTeacher", "❌ ERROR: Phone number missing from Intent!");
            return;
        }

        Log.d("ProfilePageTeacher", "📌 Fetching user details for phone number:89 :: " + phoneNumber);

        DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, userList -> {
            if (!userList.isEmpty()) {
                user = userList.get(0);  // ✅ Store user object
                Log.d("ProfilePageTeacher", "✅ Loaded Correct User: " + user.getName() + " " + user.getLastName());

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
                    Log.d("ProfilePageTeacher", "📌 Calling enableNavigation() after user is loaded.");  // Debug log ✅
                });

            } else {
                Log.e("ProfilePageTeacher", "❌ No user found in DB for phone: " + phoneNumber);
            }
        });
    }

}
