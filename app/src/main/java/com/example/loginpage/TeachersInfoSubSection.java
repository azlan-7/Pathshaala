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

public class TeachersInfoSubSection extends AppCompatActivity {

    private UserDetailsClass user;

    private TextView tvFirstName, tvFullName, tvLastName, tvContact, tvEmail, tvDOB,uniqueIdTextView;

    private ImageView profileImage, backButton;
    private TextView accountInfo, subjectExpertise, education, workExperience, gradesTaught, certifications, awards, promotionalActivities, location, dashboard;
    private static final String TAG = "TeachersInfoSubSection";
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


        Log.d("TeachersInfoSubSection", "expandableListView is " + (expandableListView != null));


        setupExpandableList();

        // Add the Group Expand Listener here
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int lastExpandedPosition = -1; // To track the last expanded group

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition); // Collapse the previously expanded group
                }
                lastExpandedPosition = groupPosition; // Update the last expanded group
            }
        });

        // Add the Group Collapse Listener (optional but good practice for symmetry)
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                if (groupPosition == expandableListView.getExpandableListAdapter().getGroupCount() - 1 && lastExpandedPosition == groupPosition) {
                    lastExpandedPosition = -1; // Reset if the last group is collapsed
                } else if (groupPosition == lastExpandedPosition) {
                    lastExpandedPosition = -1; // Reset when the currently expanded group is collapsed
                }
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                } else {
                    parent.expandGroup(groupPosition);
                }
                return true;
            }
        });



        fetchUserDetailsFromDB();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        int userId = sharedPreferences.getInt("USER_ID", -1);

        Log.d("TeachersInfoSubSection", "Fetching user details for phone number: " + phoneNumber + " and UserId: " + userId);

        // Fill about from DB

        if (phoneNumber != null) {
            // Call UserDetailsSelect to retrieve the user's details
            DatabaseHelper.UserDetailsSelect(
                    TeachersInfoSubSection.this,
                    "4", // You might need a specific QryStatus for this
                    phoneNumber,
                    new DatabaseHelper.UserResultListener() {
                        @Override
                        public void onQueryResult(List<UserDetailsClass> userList) {
                            if (userList != null && !userList.isEmpty()) {
                                UserDetailsClass currentUser = userList.get(0); // Assuming only one user will be returned
                                Log.d("TeachersInfoSubSection", "Retrieved user from DB: Name - " + currentUser.getName() + ", Mobile - " + currentUser.getMobileNo() + ", UserId - " + currentUser.getUserId());
                                String aboutYourself = currentUser.getAboutUs();
                                Log.d("TeachersInfoSubSection", "Retrieved About from DB: " + aboutYourself);
                                aboutText.setText(aboutYourself != null ? aboutYourself : "No info available");
                            } else {
                                Log.d("TeachersInfoSubSection", "User data not found in DB");
                                aboutText.setText("No info available");
                            }
                        }
                    }
            );
        } else {
            Log.e("TeachersInfoSubSection", "Logged-in user's mobile number not found.");
            aboutText.setText("Error loading info.");
        }

        Log.d("TeachersInfoSubSection","phoneNumber: " + phoneNumber);

        if (phoneNumber == null) phoneNumber = sharedPreferences.getString("phoneNumber", "N/A");

        // Set values immediately
//        tvFullName.setText(firstName + " " + lastName);
        tvContact.setText(phoneNumber);

        {
            Log.e(TAG, "⚠️ Self Referral Code Not Found! Fetching from DB...");
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
        subjectExpertise.setOnClickListener(v -> startActivity(new Intent(TeachersInfoSubSection.this, TeachersAddSubject.class)));
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
                        tvFullName.setText(user.getName() + " " + user.getLastName());
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
            if (list != null && list.isEmpty() && !title.equals("Promotional Activities") && !title.equals("Dashboard") && !title.equals("Account Info")) {
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
        sectionTitles.add("Dashboard");
        sectionTitles.add("Account Info");

        // Initialize with "Loading..." for dropdown sections
        for (int i = 0; i < 6; i++) {
            List<String> placeholder = new ArrayList<>();
            placeholder.add("Loading...");
            sectionItems.put(sectionTitles.get(i), placeholder);
        }

        // Non-dropdowns
        sectionItems.put("Promotional Activities", new ArrayList<>());
        sectionItems.put("Dashboard", new ArrayList<>());
        sectionItems.put("Account Info", new ArrayList<>());

        adapter = new CustomExpandableListAdapter(this, sectionTitles, sectionItems);
        expandableListView.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        Log.d("TeachersInfoSubSection","UserId fetched through sharedpreferences: " + userId);
        getAllUserInfoDirect(userId);
    }


    private void getAllUserInfoDirect(int userId) {
        Log.d("TeachersInfoSubSection", "getAllUserInfoDirect() called");

        new Thread(() -> {
            List<UserInfoItem> userInfoList = DatabaseHelper.getAllUserInfo(userId);

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

                        Log.d("TeachersInfoSubSection", "Heading: " + heading + ", Description: " + description);

                        if (headingMap.containsKey(heading)) {
                            List<String> list = headingMap.get(heading);
                            if (list != null && list.contains("Loading...")) list.clear();
                            list.add(description);

                        }
                    }

                    // ✅ Add "Add" button/subsection entry to all mapped sections
                    for (Map.Entry<String, List<String>> entry : headingMap.entrySet()) {
                        entry.getValue().add("Add");
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("TeachersInfoSubSection", "No data returned from database.");
                }
            });
        }).start();
    }





//    private void loadUserSubjects(List<String> subjectList) {
//        loadUserInfoByType(1, subjectList); // 1 = Subject
//    }
//
//    private void loadUserEducation(List<String> educationList) {
//        loadUserInfoByType(2, educationList); // 2 = Education
//    }
//
//    private void loadUserWorkExperience(List<String> workList) {
//        loadUserInfoByType(3, workList); // 3 = Work
//    }
//
//    private void loadUserCertificates(List<String> certificateList) {
//        loadUserInfoByType(4, certificateList); // 4 = Certificate
//    }
//
//    private void loadUserGradesTaught(List<String> gradesList) {
//        loadUserInfoByType(5, gradesList); // 5 = Grades
//    }
//
//    private void loadUserAwards(List<String> awardsList) {
//        loadUserInfoByType(6, awardsList); // 6 = Awards
//    }


    private int getCurrentUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("USER_ID", -1);
        return userId;
    }


//    private void loadUserInfoByType(List<String> targetList) {
//        int userId = 10;
//
//        new Thread(() -> {
//            DatabaseHelper.getUserWiseInfo(userId, targetList);
//            runOnUiThread(() -> adapter.notifyDataSetChanged());
//        }).start();
//    }


    private void loadUserSubjects(List<String> subjectExpertiseOptions) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Log.e("TeachersInfoSubSection", "Cannot load subjects: User ID not found.");
            return;
        }

        DatabaseHelper.UserWiseSubjectSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseSubjectResultListener() {
            @Override
            public void onQueryResult(List<UserWiseSubject> userSubjects) {
                subjectExpertiseOptions.clear(); // Clear previous data

                if (userSubjects == null || userSubjects.isEmpty()) {
                    Log.d("TeachersInfoSubSection", "⚠️ No subjects found for the user.");
                    subjectExpertiseOptions.add("Add your Subject Expertise");
                } else {
                    List<String> subjects = new ArrayList<>();
                    int subjectCount = userSubjects.size();

                    // Show first 5 subjects and then "More..." if there are more than 6 subjects
                    for (int i = 0; i < Math.min(5, subjectCount); i++) {
                        subjects.add(userSubjects.get(i).getSubjectName());
                    }

                    if (subjectCount > 6) {
                        subjects.add("More...");
                    } else if (subjectCount == 6) {
                        subjects.add(userSubjects.get(5).getSubjectName());
                    }

                    // Add subjects to the list individually (each subject appears on a new line)
                    subjectExpertiseOptions.addAll(subjects);
                    subjectExpertiseOptions.add("Add your Subject Expertise");
                }

                // Notify adapter of data change
                adapter = new CustomExpandableListAdapter(TeachersInfoSubSection.this, sectionTitles, sectionItems);
                expandableListView.setAdapter(adapter);
            }
        });
    }




    private void loadUserEducation(List<String> educationOptions) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Log.e(TAG, "❌ User ID not found in SharedPreferences!");
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "📌 Fetching education entries for user: " + userId);

        DatabaseHelper.UserWiseEducationSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseEducationResultListener() {
            @Override
            public void onQueryResult(List<UserWiseEducation> userWiseEducationList) {
                educationOptions.clear(); // ✅ Clear placeholder values before adding actual data

                int totalRecords = userWiseEducationList.size();

                if (totalRecords > 0) {
                    // ✅ Show up to 6 records
                    int limit = Math.min(totalRecords, 6);
                    for (int i = 0; i < limit; i++) {
                        UserWiseEducation education = userWiseEducationList.get(i);
                        educationOptions.add(education.getEducationLevelName() + "(" + education.getInstitutionName() + ")");
                    }

                    // ✅ If more than 6 records, add "More..."
                    if (totalRecords > 6) {
                        educationOptions.add("More...");
                    }

                    // ✅ Always add "View your education" at the end
                    educationOptions.add("Add Education");
                } else {
                    // ✅ Show default message if no records exist
                    educationOptions.add("Add Education");
                    Log.e(TAG, "⚠️ No education records found in DB!");
                }

                // ✅ Notify adapter of data change
                runOnUiThread(() -> {
                    if (adapter instanceof BaseExpandableListAdapter) {
                        ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();

                        // ✅ Re-expand the last opened group **AFTER** notifying data change
                        expandableListView.postDelayed(() -> {
                            if (lastExpandedPosition != -1) {
                                expandableListView.expandGroup(lastExpandedPosition);
                            }
                        }, 300); // Slight delay ensures smooth UI
                    }
                });

            }

            public void onError(String error) {
                Log.e(TAG, "❌ Failed to fetch education records: " + error);
                runOnUiThread(() -> Toast.makeText(TeachersInfoSubSection.this, "Error fetching education details!", Toast.LENGTH_SHORT).show());
            }
        });
    }



    private void loadUserWorkExperience(List<String> workExperienceOptions) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "🟢 Fetching first work experience entry for UserID: " + userId);

        DatabaseHelper.UserWiseWorkExperienceSelect(this, "4", String.valueOf(userId), new DatabaseHelper.WorkExperienceCallback() {
            @Override
            public void onSuccess(List<UserWiseWorkExperience> result) {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "❌ No work experience data retrieved!");
                    return;
                }

                // Clear old data
                workExperienceOptions.clear();

                workExperienceOptions.clear(); // ✅ Clear old data

                // ✅ Loop through the list and add up to 6 records
                int count = 0;
                for (UserWiseWorkExperience experience : result) {
                    if (count >= 6) break;  // ✅ Stop after adding 6 records
                    String experienceText = experience.getProfessionName() + " - " + experience.getWorkExperience();
                    workExperienceOptions.add(experienceText);
                    count++;
                }

                // ✅ If there are more than 6 records, show "More..."
                if (result.size() > 6) {
                    workExperienceOptions.add("More...");
                }

                // ✅ Always add the "Add your work experience" option at the end
                workExperienceOptions.add("Add your work experience");

                runOnUiThread(() -> {
                    // Notify adapter that data has changed
                    if (adapter instanceof BaseExpandableListAdapter) {
                        ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
                        Log.d(TAG, "✅ Work experience data updated in ExpandableListView.");
                    }

                    // Update the adapter instance (Fix potential adapter issue)
                    adapter = new CustomExpandableListAdapter(TeachersInfoSubSection.this, sectionTitles, sectionItems);
                    expandableListView.setAdapter(adapter);
                });
            }

            @Override
            public void onMessage(String message) {
                runOnUiThread(() -> Toast.makeText(TeachersInfoSubSection.this, message, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Error fetching work experience: " + error);
                runOnUiThread(() -> Toast.makeText(TeachersInfoSubSection.this, "Database error: " + error, Toast.LENGTH_LONG).show());
            }
        });
    }


    private void loadUserCertificates(List<String> certificateOptions) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper.UserWiseCertificateSelect(this, userId, new DatabaseHelper.DatabaseCallback() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                certificateOptions.clear(); // ✅ Clear existing data

                if (!result.isEmpty()) {
                    // ✅ Loop through the list and add up to 6 records
                    int count = 0;
                    for (Map<String, String> certificate : result) {
                        if (count >= 6) break; // ✅ Stop after adding 6 records

                        String certificateName = certificate.get("CertificateName");
                        String issueYear = certificate.get("IssueYear");
                        certificateOptions.add("📜 " + certificateName + " (" + issueYear + ")");
                        count++;
                    }

                    // ✅ If there are more than 6 records, show "More..."
                    if (result.size() > 6) {
                        certificateOptions.add("More...");
                    }

                    // ✅ Always add the "View your certifications" option at the end
                    certificateOptions.add("Add certifications");

                    Log.d(TAG, "✅ Loaded " + count + " certificate(s).");

                    runOnUiThread(() -> {
                        if (adapter instanceof BaseExpandableListAdapter) {
                            ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
                            Log.d(TAG, "✅ Certificate data updated in ExpandableListView.");
                        }
                    });
                } else {
                    Log.d(TAG, "⚠️ No certificates found!");
                }
            }

            @Override
            public void onMessage(String message) {
                Log.d("loadUserCertificates", "ℹ️ Message: " + message);
                Toast.makeText(TeachersInfoSubSection.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Log.e("loadUserCertificates", "❌ Error: " + error);
                Toast.makeText(TeachersInfoSubSection.this, "Database error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void loadUserGradesTaught(List<String> gradesTaughtOptions) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Log.e("loadUserGradesTaught", "❌ User ID not found in SharedPreferences!");
            return;
        }

        Log.d("loadUserGradesTaught", "📌 Fetching Grades Taught for UserID: " + userId);

        DatabaseHelper.UserWiseGradesSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseGradesResultListener() {
            @Override
            public void onQueryResult(List<UserWiseGrades> userWiseGradesList) {
                gradesTaughtOptions.clear(); // ✅ Clear previous data

                if (userWiseGradesList == null || userWiseGradesList.isEmpty()) {
                    Log.d("loadUserGradesTaught", "⚠️ No grades data found for UserID: " + userId);
                    Toast.makeText(TeachersInfoSubSection.this, "No Grades Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ Loop through the list and add up to 6 records
                int count = 0;
                for (UserWiseGrades grade : userWiseGradesList) {
                    if (count >= 6) break; // ✅ Stop after adding 6 records

                    gradesTaughtOptions.add("🎓 " + grade.getGradename() + " - " + grade.getSubjectName());
                    count++;
                }

                // ✅ If there are more than 6 records, add a "More..." option
                if (userWiseGradesList.size() > 6) {
                    gradesTaughtOptions.add("More...");
                }

                // ✅ Always add the "View grades taught" option at the end
                gradesTaughtOptions.add("Add grades taught");

                Log.d("loadUserGradesTaught", "✅ Loaded " + count + " grade(s) taught.");

                // ✅ Ensure UI is updated properly
                runOnUiThread(() -> {
                    if (adapter instanceof BaseExpandableListAdapter) {
                        ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
                        Log.d("loadUserGradesTaught", "✅ Grades data updated in ExpandableListView.");
                    }
                });
            }
        });
    }


    private void loadUserAwards(List<String> awardOptions) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Log.e("loadUserAwards", "❌ User ID not found in SharedPreferences!");
            return;
        }

        Log.d("loadUserAwards", "📌 Fetching Awards for UserID: " + userId);

        DatabaseHelper.UserWiseAwardSelect(this, userId, new DatabaseHelper.DatabaseCallback() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                awardOptions.clear(); // ✅ Clear previous data

                if (result == null || result.isEmpty()) {
                    Log.d("loadUserAwards", "⚠️ No awards data found for UserID: " + userId);
                    Toast.makeText(TeachersInfoSubSection.this, "No Awards Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ Loop through the list and add up to 6 records
                int count = 0;
                for (Map<String, String> award : result) {
                    if (count >= 6) break; // ✅ Stop after adding 6 records

                    String title = award.get("AwardTitleName");
                    String year = award.get("IssueYear");

                    if (title != null && year != null) {
                        awardOptions.add("🏆 " + title + " (" + year + ")");
                        count++;
                    } else {
                        Log.d("loadUserAwards", "⚠️ Award data missing for one entry.");
                    }
                }

                // ✅ If there are more than 6 records, add a "More..." option
                if (result.size() > 6) {
                    awardOptions.add("More...");
                }

                // ✅ Always add the "View your achievements" option at the end
                awardOptions.add("Add your achievements");

                Log.d("loadUserAwards", "✅ Loaded " + count + " award(s).");

                // ✅ Ensure UI updates properly
                runOnUiThread(() -> {
                    if (adapter instanceof BaseExpandableListAdapter) {
                        ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
                        Log.d("loadUserAwards", "✅ Awards data updated in ExpandableListView.");
                    }
                });
            }

            @Override
            public void onMessage(String message) {
                Log.d("loadUserAwards", "ℹ️ Message: " + message);
                Toast.makeText(TeachersInfoSubSection.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Log.e("loadUserAwards", "❌ Database Error: " + error);
                Toast.makeText(TeachersInfoSubSection.this, "Database error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }



    private void navigateToActivity(String selectedItem) {
        Intent intent = null;
        switch (selectedItem) {
            case "View your Subject Expertise":
                intent = new Intent(this, TeachersAddSubject.class);
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
            case "Promotional Activities":
                intent = new Intent(this, AddPromotionalMedia.class);
                break;
            case "Dashboard":
                intent = new Intent(this, TeachersDashboardNew.class);
                break;
            case "Account Info":
                intent = new Intent(this, TeachersBasicInfo.class);
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
        Log.d("TeachersInfoSubSection", "phoneNumberFetched: " + phoneNumber);
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
