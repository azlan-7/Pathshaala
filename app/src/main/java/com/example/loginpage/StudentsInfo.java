package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.loginpage.models.UserWiseEducation;
import com.example.loginpage.models.UserWiseGrades;

import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StudentsInfo extends AppCompatActivity {

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

    private static final String TAG = "StudentsInfo";


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

        expandableListView = findViewById(R.id.expandableView);

        setupExpandableList();

        // Add the Group Expand Listener here (as you already have)
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

        // Add the OnGroupClickListener here
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                String groupTitle = sectionTitles.get(groupPosition);

                switch (groupTitle) {
                    case "Account Info":
                        // Create an Intent to start the AccountInfoActivity
                        Intent accountInfoIntent = new Intent(StudentsInfo.this, StudentsBasicInfo.class);
                        startActivity(accountInfoIntent);
                        return true; // Consume the click
                    case "Dashboard":
                        // Create an Intent to start the DashboardActivity
                        Intent dashboardIntent = new Intent(StudentsInfo.this, StudentsDashboard.class);
                        startActivity(dashboardIntent);
                        return true; // Consume the click
                    case "Communication Preferences":
                        // Create an Intent to start the CommunicationPreferencesActivity
                        Intent communicationIntent = new Intent(StudentsInfo.this, CommunicationPreferences.class);
                        startActivity(communicationIntent);
                        return true; // Consume the click
                    case "Feedback and Ratings":
                        // Create an Intent to start the FeedbackAndRatingsActivity
                    default:
                        // For other groups (with dropdowns), let the default expand/collapse happen
                        return false;
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

        fetchUserDetailsFromDB();

        // Get Unique ID
        String uniqueID = getIntent().getStringExtra("UNIQUE_ID");
        if (uniqueID == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            uniqueID = sharedPreferences.getString("UNIQUE_ID", "Unique ID not available");
        }

        uniqueIdTextView.setText("[" + uniqueID + "]");

        // Open About Student Page
        editAbout.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsInfo.this, AboutStudent.class);
            aboutActivityLauncher.launch(intent);
            startActivity(intent);
        });


        payment.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsInfo.this, PaymentGatewayDemo.class);
            aboutActivityLauncher.launch(intent);
            startActivity(intent);
        });


        // Initialize TextViews
        tvContact = findViewById(R.id.tvContact);
        tvEmail = findViewById(R.id.tvEmail);
        tvFullName = findViewById(R.id.tvFullName);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String contactNumber = sharedPreferences.getString("phoneNumber", "Not Available");
        String aboutYourself = sharedPreferences.getString("ABOUT_STUDENT", "Write about yourself..."); // <-- Changed from ABOUT_YOURSELF
        String storedImageName = sharedPreferences.getString("USER_PROFILE_IMAGE", "");
        if (!storedImageName.isEmpty()) {
            String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + storedImageName;
            Log.d("StudentsInfo", "✅ Loaded Image from SharedPreferences: " + imageUrl);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.generic_avatar)
                    .error(R.drawable.generic_avatar)
                    .apply(RequestOptions.circleCropTransform()) // Apply the circle transformation here
                    .into(profileImage);
        }

        tvContact.setText(contactNumber);

        String firstName = sharedPreferences.getString("FIRST_NAME", "N/A");
        String lastName = sharedPreferences.getString("LAST_NAME", "");
        String contact = sharedPreferences.getString("CONTACT", "N/A");
        String email = sharedPreferences.getString("EMAIL", "N/A");
        String selfreferralcode = sharedPreferences.getString("selfreferralcode", "N/A");

//        tvFullName.setText(firstName + " " + lastName);
//        tvContact.setText(contact);
//        tvEmail.setText(email);
//        tvFullName.setText("Student " + "Test");
//        tvContact.setText("2323232333");
//        tvEmail.setText("student@Test.com");
        uniqueIdTextView.setText(selfreferralcode);

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

        sectionTitles.add("Account Info");
        sectionTitles.add("Academic Details");
        sectionTitles.add("Grade");
        sectionTitles.add("Parent Guardian Details");
        sectionTitles.add("Skills and Extracurriculars");
        sectionTitles.add("Progress and Performance");
        sectionTitles.add("Attendance and Participation");
        sectionTitles.add("Communication Preferences");
        sectionTitles.add("Feedback and Ratings");
        sectionTitles.add("Dashboard");

        // Adding subsections (Dropdown Items)
        List<String> academicDetailsOptions = new ArrayList<>();
        academicDetailsOptions.add("Loading...");

        List<String> parentDetailsOptions = new ArrayList<>();
        parentDetailsOptions.add("Loading...");

        List<String> gradeOptions = new ArrayList<>();
        gradeOptions.add("Loading...");

        List<String> skillsOptions = new ArrayList<>();
        skillsOptions.add("View your Skills");
        skillsOptions.add("Add Skills/Extracurriculars");

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

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        Log.d("StudentsInfo","UserId fetched through sharedpreferences: " + userId);
        getAllUserInfoDirect(userId);
    }



    private void getAllUserInfoDirect(int userId) {
        Log.d("StudentsInfo", "getAllUserInfoDirect() called");

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

                        Log.d("StudentsInfo", "Heading: " + heading + ", Description: " + description);

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
                    Log.e("StudentsInfo", "No data returned from database.");
                }
            });
        }).start();
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
                    Toast.makeText(StudentsInfo.this, "No Grades Found!", Toast.LENGTH_SHORT).show();
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
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);
//        String fatherContactNo = sharedPreferences.getString("FATHER_CONTACT_NO", null); // Assuming it's stored already

        if (userId == -1) {
            Toast.makeText(this, "User ID not found in SharedPreferences", Toast.LENGTH_SHORT).show();
            return;
        }

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
                runOnUiThread(() -> Toast.makeText(StudentsInfo.this, "Error fetching education details!", Toast.LENGTH_SHORT).show());
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




    private void fetchUserDetailsFromDB() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");
        String storedImageName = sharedPreferences.getString("USER_PROFILE_IMAGE", "");

        if (!storedImageName.isEmpty()) {
            String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + storedImageName;
            Log.d("StudentsInfo", "✅ Loaded Image from SharedPreferences: " + imageUrl);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.generic_avatar)
                    .error(R.drawable.generic_avatar)
                    .apply(RequestOptions.circleCropTransform()) // Apply the circle transformation here
                    .into(profileImage);
        } else {
            Log.e("StudentsInfo", "❌ No profile image found in SharedPreferences, checking DB...");
        }

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Log.e("StudentsInfo", "❌ ERROR: Phone number missing from SharedPreferences!");
            return;
        }

        DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, userList -> {
            if (!userList.isEmpty()) {
                UserDetailsClass user = userList.get(0);
                Log.d("StudentsInfo", "✅ Loaded Correct User: " + user.getName());

                runOnUiThread(() -> {
                    tvFullName.setText(user.getName() + " " + user.getLastName());
                    tvContact.setText(user.getMobileNo());
                    tvEmail.setText(user.getEmailId());

                    // --- THIS IS THE KEY PART FOR ABOUT YOURSELF ---
                    tvAboutYourself.setText(user.getAboutUs() != null && !user.getAboutUs().isEmpty()
                            ? user.getAboutUs()
                            : "Tell us about yourself..."); // Default text if aboutUs is null or empty

                    // Update Unique ID
                    uniqueIdTextView.setText(String.format("[%s]",
                            user.getSelfReferralCode() != null ? user.getSelfReferralCode() : "N/A"));

                    String imageName = user.getUserImageName();
                    if (imageName != null && !imageName.isEmpty()) {
                        String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + imageName;
                        Log.d("StudentsInfo", "✅ Profile image URL: " + imageUrl);
                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.generic_avatar)
                                .error(R.drawable.generic_avatar)
                                .apply(RequestOptions.circleCropTransform()) // Apply circle transformation
                                .into(profileImage);

                        // ✅ Save to SharedPreferences for future use
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USER_PROFILE_IMAGE", imageName);
                        editor.apply();
                    } else {
                        Log.e("StudentsInfo", "❌ No profile image found in DB or empty value.");
                    }
                });
            } else {
                Log.e("StudentsInfo", "❌ No user found in DB for phone: " + phoneNumber);
            }
        });
    }

}

