package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.StudentsExpandableListAdapter;
import com.example.loginpage.models.Education;
import com.example.loginpage.models.UserDetailsClass;
import com.example.loginpage.models.UserInfoItem;
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


    private final ActivityResultLauncher<Intent> aboutActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            String updatedAbout = result.getData().getStringExtra("ABOUT_YOURSELF");
                            if (updatedAbout != null) {
                                tvAboutYourself.setText(updatedAbout);  // Update UI instantly
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_info);


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

        ImageView editAbout = findViewById(R.id.imageView44);
        ImageView payment = findViewById(R.id.imageView138);

        tvAboutYourself = findViewById(R.id.textViewAboutYourself);
        uniqueIdTextView = findViewById(R.id.uniqueIdTextView2);


        profileImage = findViewById(R.id.imageView55);


        // Get Unique ID
        String uniqueID = getIntent().getStringExtra("UNIQUE_ID");
        if (uniqueID == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            uniqueID = sharedPreferences.getString("UNIQUE_ID", "Unique ID not available");
        }

        uniqueIdTextView.setText("[" + uniqueID + "]");

        // Open About Student Page
        editAbout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePageStudent.this, AboutStudent.class);
            aboutActivityLauncher.launch(intent);
            startActivity(intent);
        });


        payment.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePageStudent.this, PaymentGatewayDemo.class);
            aboutActivityLauncher.launch(intent);
            startActivity(intent);
        });



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

        if (!imageName.isEmpty()) {
            String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + imageName;
            Log.d("ProfilePageStudent", "✅ Loaded Image from SharedPreferences: " + imageUrl);
            Glide.with(this).load(imageUrl).placeholder(R.drawable.generic_avatar).error(R.drawable.generic_avatar).into(profileImage);
        }

        tvAboutYourself.setText(aboutYourself);
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
        skillsOptions.add("View your Skills");
//        skillsOptions.add("Add Skills/Extracurriculars");

        // Correct Mapping
        sectionItems.put(sectionTitles.get(1), academicDetailsOptions); // Academic Details -> Correct dropdown
        sectionItems.put(sectionTitles.get(2), gradeOptions); // Academic Details -> Correct dropdown
        sectionItems.put(sectionTitles.get(3), parentDetailsOptions); // Parent Guardian Details -> Correct dropdown
        sectionItems.put(sectionTitles.get(4), skillsOptions); // Skills and Extracurriculars -> Correct dropdown

        // Sections with no dropdown (empty lists)
        for (int i = 4; i < sectionTitles.size(); i++) {
            sectionItems.put(sectionTitles.get(i), new ArrayList<>());
        }

        adapter = new StudentsExpandableListAdapter(this, sectionTitles, sectionItems);
        expandableListView.setAdapter(adapter);

//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        int userId = sharedPreferences.getInt("USER_ID", -1);
        int userId = getIntent().getIntExtra("USER_ID", -1);


        Log.d("ProfilePageStudent","UserId fetched through sharedpreferences: " + userId);
        loadParentGuardianInfo(parentDetailsOptions);
        loadUserEducation(academicDetailsOptions);
        loadUserGradesTaught(gradeOptions);

//        getAllUserInfoDirect(userId);
    }



    private void getAllUserInfoDirect(int userId) {
        Log.d("ProfilePageStudent", "getAllUserInfoDirect() called");

        new Thread(() -> {
            List<UserInfoItem> userInfoList = DatabaseHelper.getAllUserInfo(userId);

            runOnUiThread(() -> {
                if (userInfoList != null) {
                    Map<String, List<String>> headingMap = new HashMap<>();
                    headingMap.put("Education", sectionItems.get("Academic Details"));
                    headingMap.put("Grades", sectionItems.get("Grade"));
                    headingMap.put("Parent", sectionItems.get("Parent Guardian Details"));
                    headingMap.put("Skills", sectionItems.get("Skills and Extracurriculars"));

                    for (UserInfoItem item : userInfoList) {
                        String heading = item.getHeading();
                        String description = item.getDescription();

                        Log.d("ProfilePageStudent", "Heading: " + heading + ", Description: " + description);

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
                    Log.e("ProfilePageStudent", "No data returned from database.");
                }
            });
        }).start();
    }


    private void loadUserGradesTaught(List<String> gradesTaughtOptions) {
//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        int userId = sharedPreferences.getInt("USER_ID", -1);

        int userId = getIntent().getIntExtra("USER_ID", -1);


        Log.d("loadUserGradesTaught", "📌 Fetching Grades Taught for UserID: " + userId);

        DatabaseHelper.UserWiseGradesSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseGradesResultListener() {
            @Override
            public void onQueryResult(List<UserWiseGrades> userWiseGradesList) {
                gradesTaughtOptions.clear(); // ✅ Clear previous data

                if (userWiseGradesList == null || userWiseGradesList.isEmpty()) {
                    Log.d("loadUserGradesTaught", "⚠️ No grades data found for UserID: " + userId);
                    Toast.makeText(ProfilePageStudent.this, "No Grades Found!", Toast.LENGTH_SHORT).show();
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
//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        int userId = sharedPreferences.getInt("USER_ID", -1);

        int userId = getIntent().getIntExtra("USER_ID", -1);


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

//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        String storedImageName = sharedPreferences.getString("USER_PROFILE_IMAGE", "");

        String storedImageName = getIntent().getStringExtra("USER_IMAGE");

        if (!storedImageName.isEmpty()) {
            String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + storedImageName;
            Log.d("ProfilePageStudent", "✅ Loaded Image from SharedPreferences: " + imageUrl);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.generic_avatar)
                    .error(R.drawable.generic_avatar)
                    .into(profileImage);
        }

        DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, userList -> {
            if (!userList.isEmpty()) {
                UserDetailsClass user = userList.get(0);
                Log.d("ProfilePageStudent", "✅ Loaded Correct User: " + user.getName());

                runOnUiThread(() -> {
                    tvFullName.setText(user.getName());
                    tvContact.setText(user.getMobileNo());
                    tvEmail.setText(user.getEmailId());

                    String imageName = user.getUserImageName();
                    if (imageName != null && !imageName.isEmpty()) {
                        String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + imageName;
                        Log.d("ProfilePageStudent", "✅ Loaded Image from Intent: " + imageUrl);
                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.generic_avatar)
                                .error(R.drawable.generic_avatar)
                                .into(profileImage);
                    } else {
                        Log.e("ProfilePageStudent", "❌ No image name found in intent.");
                    }

                });
            } else {
                Log.e("ProfilePageStudent", "❌ No user found for phone: " + phoneNumber);
            }
        });
    }


}
