package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.StudentsExpandableListAdapter;
import com.example.loginpage.models.Education;
import com.example.loginpage.models.UserDetailsClass;
import com.example.loginpage.models.UserInfoItem;
import com.example.loginpage.models.UserSearchResult;
import com.example.loginpage.models.UserWiseEducation;
import com.example.loginpage.models.UserWiseGrades;

import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfilePageStudent extends AppCompatActivity {

    private TextView tvFullName, tvContact, tvEmail;
    private UserDetailsClass user;
    private TextView tvAboutYourself;
    private TextView uniqueIdTextView;
    private ImageView profileImage;

    private ExpandableListView expandableListView;

    private List<String> sectionTitles;
    private HashMap<String, List<String>> sectionItems;
    StudentsExpandableListAdapter adapter;

    private int lastExpandedPosition = -1;

    private static final String TAG = "ProfilePageStudent";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page_student);


        String phoneNumber = getIntent().getStringExtra("USER_PHONE");
        if (phoneNumber != null) {
            Log.d("ProfilePageStudent", "Phone received: " + phoneNumber);
            // Use phoneNumber to fetch user info and render it
            fetchUserDetailsFromDB(phoneNumber);
        } else {
            Toast.makeText(this, "Invalid user info", Toast.LENGTH_SHORT).show();
        }

        expandableListView = findViewById(R.id.expandableView);

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


        uniqueIdTextView = findViewById(R.id.uniqueIdTextView2);


        profileImage = findViewById(R.id.imageView55);


        // Get Unique ID
        String uniqueID = getIntent().getStringExtra("UNIQUE_ID");
        if (uniqueID == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            uniqueID = sharedPreferences.getString("UNIQUE_ID", "Unique ID not available");
        }

        uniqueIdTextView.setText("[" + uniqueID + "]");




        // Initialize TextViews
        tvContact = findViewById(R.id.tvContact);
        tvEmail = findViewById(R.id.tvEmail);
        tvFullName = findViewById(R.id.tvFullName);

        // 🔹 Fetch all data from Intent
        String contactNumber = getIntent().getStringExtra("USER_PHONE");
        String aboutYourself = getIntent().getStringExtra("USER_ABOUT");
        String imageName = getIntent().getStringExtra("USER_IMAGE");
        String firstName = getIntent().getStringExtra("USER_FIRST_NAME");
        String lastName = getIntent().getStringExtra("USER_LAST_NAME");
        String email = getIntent().getStringExtra("USER_EMAIL");
        String selfReferralCode = getIntent().getStringExtra("USER_SELF_REFERRAL");

        if (imageName != null && !imageName.isEmpty()) { // Added null check
            String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + imageName;
            Log.d("ProfilePageStudent", "✅ Loaded Image from SharedPreferences: " + imageUrl);
            Glide.with(this).load(imageUrl).placeholder(R.drawable.generic_avatar).error(R.drawable.generic_avatar).into(profileImage);
        } else {
            // Handle case where imageName is null or empty.
            Log.e("ProfilePageStudent", "Image name is null or empty");
        }

        tvContact.setText(contactNumber);

//        tvFullName.setText(firstName + " " + lastName);
//        tvContact.setText(contact);
//        tvEmail.setText(email);
        tvContact.setText(contactNumber != null ? contactNumber : "Not Available");
        tvEmail.setText(email != null ? email : "Not Available");
        tvFullName.setText(firstName + " " + (lastName != null ? lastName : ""));
        uniqueIdTextView.setText(selfReferralCode);

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

//        sectionTitles.add("Account Info");
        sectionTitles.add("Academic Details");
        sectionTitles.add("Grade");
        sectionTitles.add("Parent Guardian Details");
        sectionTitles.add("Skills and Extracurriculars");
        sectionTitles.add("Progress and Performance");
        sectionTitles.add("Attendance and Participation");
        sectionTitles.add("Communication Preferences");
        sectionTitles.add("Feedback and Ratings");
//        sectionTitles.add("Dashboard");

        // Adding subsections (Dropdown Items)
        List<String> academicDetailsOptions = new ArrayList<>();
        academicDetailsOptions.add("Loading...");

        List<String> parentDetailsOptions = new ArrayList<>();
        parentDetailsOptions.add("Loading...");

        List<String> gradeOptions = new ArrayList<>();
        gradeOptions.add("Loading...");

        List<String> skillsOptions = new ArrayList<>();
        skillsOptions.add("Loading...");
//        skillsOptions.add("Add Skills/Extracurriculars");

        // Correct Mapping
        sectionItems.put(sectionTitles.get(0), academicDetailsOptions); // Academic Details -> Correct dropdown
        sectionItems.put(sectionTitles.get(1), gradeOptions); // Academic Details -> Correct dropdown
        sectionItems.put(sectionTitles.get(2), parentDetailsOptions); // Parent Guardian Details -> Correct dropdown
        sectionItems.put(sectionTitles.get(3), skillsOptions); // Skills and Extracurriculars -> Correct dropdown

        // Sections with no dropdown (empty lists)
        for (int i = 4; i < sectionTitles.size(); i++) {
            sectionItems.put(sectionTitles.get(i), new ArrayList<>());
        }

        adapter = new StudentsExpandableListAdapter(this, sectionTitles, sectionItems) {
            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                View view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
                ImageView itemIcon = view.findViewById(R.id.itemIcon);
                itemIcon.setOnClickListener(null); // Disable the click listener
                return view;
            }
        };
        expandableListView.setAdapter(adapter);

//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        int userId = sharedPreferences.getInt("USER_ID", -1);
        int userId = getIntent().getIntExtra("USER_ID", -1);


        Log.d("ProfilePageStudent","UserId fetched through intent: " + userId);
        loadParentGuardianInfo(parentDetailsOptions);
        loadUserEducation(academicDetailsOptions);
        loadUserGradesTaught(gradeOptions);
        loadSkillsData(skillsOptions); // Add this method.

//        loadStudentInfoSections(gradeOptions, academicDetailsOptions, parentDetailsOptions, userId);

//        getAllUserInfoDirect(userId);
    }


    private void loadStudentInfoSections(
            List<String> gradeOptions,
            List<String> academicDetailsOptions,
            List<String> parentDetailsOptions,
            int userId
    ) {
        new Thread(() -> {
            List<UserSearchResult> results = DatabaseHelper.getUserSearchResults("S", 0, 0, 0);

            runOnUiThread(() -> {
                if (results != null && !results.isEmpty()) {
                    for (UserSearchResult result : results) {
                        if (result.getUserId() == userId) {
                            // Grade
                            String grade = result.getGradeName();
                            if (grade != null && !grade.isEmpty() && !gradeOptions.contains(grade)) {
                                gradeOptions.clear();
                                gradeOptions.add(grade);
                            }

                            // Academic Detail: Subject
                            String subject = result.getSubjectName();
                            if (subject != null && !subject.isEmpty() && !academicDetailsOptions.contains(subject)) {
                                academicDetailsOptions.clear();
                                academicDetailsOptions.add(subject);
                            }

                            // Parent/Guardian Detail: Institution
                            String institution = result.getInstitutionName();
                            if (institution != null && !institution.isEmpty() && !parentDetailsOptions.contains(institution)) {
                                parentDetailsOptions.clear();
                                parentDetailsOptions.add(institution);
                            }

                            break; // Exit once matched
                        }
                    }
                } else {
                    Log.e("ProfilePageStudent", "No results from getUserSearchResults()");
                }

                // Add fallback "Add" entries for UI
//                gradeOptions.add("Add");
//                academicDetailsOptions.add("Add");
//                parentDetailsOptions.add("Add");

                adapter.notifyDataSetChanged();
            });
        }).start();
    }




    private void getAllUserInfoDirect(int userId) {
        Log.d("StudentsInfo", "getAllUserInfoDirect() called");

        new Thread(() -> {
            List<UserInfoItem> userInfoList = DatabaseHelper.getAllUserInfo(userId);

            runOnUiThread(() -> {
                if (userInfoList != null) {
                    Map<String, List<String>> headingMap = new HashMap<>();
                    headingMap.put("Education", sectionItems.get("Academic Details"));
                    headingMap.put("Grade", sectionItems.get("Grade"));
                    headingMap.put("Parent", sectionItems.get("Parent Guardian Details"));
                    headingMap.put("Skills", sectionItems.get("Skills and Extracurriculars"));

                    for (UserInfoItem item : userInfoList) {
                        String heading = item.getHeading();
                        String description = item.getDescription();

                        Log.d("StudentsInfo", "Heading: " + heading + ", Description: " + description);

                        if (headingMap.containsKey(heading)) {
                            List<String> list = headingMap.get(heading);
                            if (list != null && list.contains("Loading...")) list.clear();
                            list.add(description);
                        }
                    }

                    for (Map.Entry<String, List<String>> entry : headingMap.entrySet()) {
                        if (entry.getValue() != null) {
//                            entry.getValue().add("Add");
                        } else {
                            Log.e("ProfilePageStudent", "⚠️ List is null for heading: " + entry.getKey());
                        }
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("StudentsInfo", "No data returned from database.");
                }
            });
        }).start();
    }


    private void loadSkillsData(List<String> skillsOptions){
//        skillsOptions.clear();
//        skillsOptions.add("View your Skills");
//        skillsOptions.add("Add Skills/Extracurriculars");
        adapter.notifyDataSetChanged();
    }
    private void loadUserGradesTaught(List<String> gradesTaughtOptions) {
        int userId = getIntent().getIntExtra("USER_ID", -1);
        Log.d("loadUserGradesTaught", "📌 Fetching Grades Taught for UserID: " + userId);

        DatabaseHelper.UserWiseGradesSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseGradesResultListener() {
            @Override
            public void onQueryResult(List<UserWiseGrades> userWiseGradesList) {
                Log.d("loadUserGradesTaught", "onQueryResult called. List size: " + (userWiseGradesList != null ? userWiseGradesList.size() : "null"));

                gradesTaughtOptions.clear();

                if (userWiseGradesList == null || userWiseGradesList.isEmpty()) {
                    Log.d("loadUserGradesTaught", "⚠️ No grades data found for UserID: " + userId);
                    gradesTaughtOptions.add("No Grades Found");
                } else {
                    int count = 0;
                    for (UserWiseGrades grade : userWiseGradesList) {
                        if (count >= 6) break;
                        gradesTaughtOptions.add("🎓 " + grade.getGradename() + " - " + grade.getSubjectName());
                        count++;
                    }
                    if (userWiseGradesList.size() > 6) {
                        gradesTaughtOptions.add("More...");
                    }
//                    gradesTaughtOptions.add("Add grades taught");
                }
                Log.d("loadUserGradesTaught", "Grades options list: " + gradesTaughtOptions);

                runOnUiThread(() -> {
                    if (adapter instanceof BaseExpandableListAdapter) {
                        Log.d("loadUserGradesTaught", "Notifying adapter of data change.");
                        ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
                        Log.d("loadUserGradesTaught", "Adapter notified.");
                    }
                });
            }
        });
    }

    private void loadParentGuardianInfo(List<String> parentDetailsOptions) {
//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        int userId = sharedPreferences.getInt("USER_ID", -1);
//        String fatherContactNo = sharedPreferences.getString("FATHER_CONTACT_NO", null); // Assuming it's stored already

        int userId = getIntent().getIntExtra("USER_ID", -1);


        List<DatabaseHelper.ParentGuardianInfo> infoList = DatabaseHelper.getParentGuardianInfo(userId);

        parentDetailsOptions.clear(); // Clear the placeholder
        if (infoList != null && !infoList.isEmpty()) {
            for (DatabaseHelper.ParentGuardianInfo info : infoList) {
                parentDetailsOptions.add("Father: " + info.getFatherName());
                parentDetailsOptions.add("Mother: " + info.getMotherName());
                parentDetailsOptions.add("Guardian: " + info.getGuardianName());
                // Add more if needed
            }
        } else {
            parentDetailsOptions.add("No Parent/Guardian Info Found");
        }

        adapter.notifyDataSetChanged();

    }


    private void loadUserEducation(List<String> educationOptions) {
        int userId = getIntent().getIntExtra("USER_ID", -1);
        Log.d(TAG, "📌 Fetching education entries for user: " + userId);

        DatabaseHelper.UserWiseEducationSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseEducationResultListener() {
            @Override
            public void onQueryResult(List<UserWiseEducation> userWiseEducationList) {
                educationOptions.clear();
                Log.d(TAG, "Education list cleared"); // Add Log

                int totalRecords = userWiseEducationList.size();
                Log.d(TAG, "Total records: " + totalRecords); // Add Log

                if (totalRecords > 0) {
                    int limit = Math.min(totalRecords, 6);
                    for (int i = 0; i < limit; i++) {
                        UserWiseEducation education = userWiseEducationList.get(i);
                        if (education != null) { // Add null check
                            educationOptions.add(education.getEducationLevelName() + "(" + education.getInstitutionName() + ")");
                            Log.d(TAG, "Added education: " + education.getEducationLevelName()); // Add Log
                        } else {
                            Log.e(TAG, "Education object is null");
                        }
                    }

                    if (totalRecords > 6) {
                        educationOptions.add("More...");
                        Log.d(TAG, "Added More..."); // Add Log
                    }
                } else {
                    educationOptions.add("No Education records found");
                    Log.e(TAG, "⚠️ No education records found in DB!");
                }

                runOnUiThread(() -> {
                    if (adapter instanceof BaseExpandableListAdapter) {
                        ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();

                        expandableListView.postDelayed(() -> {
                            if (lastExpandedPosition != -1) {
                                expandableListView.expandGroup(lastExpandedPosition);
                            }
                        }, 300);
                    }
                });
            }

            public void onError(String error) {
                Log.e(TAG, "❌ Failed to fetch education records: " + error);
                runOnUiThread(() -> Toast.makeText(ProfilePageStudent.this, "Error fetching education details!", Toast.LENGTH_SHORT).show());
            }
        });
    }




    private void navigateToActivity(String selectedItem) {
        Intent intent = null;
        switch (selectedItem) {
            case "View your Academic Details":
                intent = new Intent(this, StudentsAcademicDetailsView.class);
                break;
            case "Add Academic Details":
                intent = new Intent(this, StudentsAcademicDetails.class);
                break;
            case "View your Skills":
                intent = new Intent(this, AddExtracurriculars.class);
                break;
            case "Add Skills/Extracurriculars":
                intent = new Intent(this, AddExtracurriculars.class);
                break;
            case "Edit Details":
                intent = new Intent(this, StudentsParentInfo.class);
                break;
            case "Add Details":
                intent = new Intent(this, StudentsParentInfo.class);
                break;

            default:
                Toast.makeText(this, "Invalid selection!", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
    }




    private void fetchUserDetailsFromDB(String phoneNumber) {
        Log.d("ProfilePageStudent", "phoneNumberFetched: " + phoneNumber);

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Log.e("ProfilePageStudent", "❌ ERROR: Phone number missing from Intent!");
            return;
        }

        String storedImageName = getIntent().getStringExtra("USER_IMAGE");

        if (storedImageName != null && !storedImageName.isEmpty()) {
            String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + storedImageName;
            Log.d("ProfilePageStudent", "✅ Loaded Image from SharedPreferences: " + imageUrl);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.generic_avatar)
                    .error(R.drawable.generic_avatar)
                    .apply(RequestOptions.circleCropTransform()) // Apply circle transformation
                    .into(profileImage);
        }

        DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, userList -> {
            if (!userList.isEmpty()) {
                UserDetailsClass user = userList.get(0);
                if (user != null) { // Add null check for user
                    Log.d("ProfilePageStudent", "✅ Loaded Correct User: " + user.getName());

                    runOnUiThread(() -> {
                        if (tvFullName != null) tvFullName.setText(user.getName());
                        if (tvContact != null) tvContact.setText(user.getMobileNo());
                        if (tvEmail != null) tvEmail.setText(user.getEmailId());

                        String imageName = user.getUserImageName();
                        if (imageName != null && !imageName.isEmpty()) {
                            String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + imageName;
                            Log.d("ProfilePageStudent", "✅ Loaded Image from Intent: " + imageUrl);
                            Glide.with(this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.generic_avatar)
                                    .error(R.drawable.generic_avatar)
                                    .apply(RequestOptions.circleCropTransform()) // Makes the image round
                                    .into(profileImage);
                        } else {
                            Log.e("ProfilePageStudent", "❌ No image name found in intent.");
                        }

                    });
                } else {
                    Log.e("ProfilePageStudent", "❌ User object is null.");
                }
            } else {
                Log.e("ProfilePageStudent", "❌ No user found for phone: " + phoneNumber);
            }
        });
    }


}
