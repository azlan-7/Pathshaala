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
import com.example.loginpage.models.UserWiseEducation;

import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentsInfo extends AppCompatActivity {

    private TextView tvFullName, tvContact, tvEmail;
    private UserDetailsClass user;
    private TextView tvAboutYourself;
    private TextView uniqueIdTextView;
    private ImageView profileImage;

    private ExpandableListView expandableListView;

    private List<String> sectionTitles;
    private HashMap<String, List<String>> sectionItems;
    private ExpandableListAdapter adapter;
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
        ImageView qrCode = findViewById(R.id.imageView113);
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

        qrCode.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsInfo.this, QRCode.class);
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
            Glide.with(this).load(imageUrl).placeholder(R.drawable.generic_avatar).error(R.drawable.generic_avatar).into(profileImage);
        }

        tvAboutYourself.setText(aboutYourself);
        tvContact.setText(contactNumber);

        String firstName = sharedPreferences.getString("FIRST_NAME", "N/A");
        String lastName = sharedPreferences.getString("LAST_NAME", "");
        String contact = sharedPreferences.getString("CONTACT", "N/A");
        String email = sharedPreferences.getString("EMAIL", "N/A");
        String selfreferralcode = sharedPreferences.getString("selfreferralcode", "N/A");

//        tvFullName.setText(firstName + " " + lastName);
//        tvContact.setText(contact);
//        tvEmail.setText(email);
        tvFullName.setText("Student " + "Test");
        tvContact.setText("2323232333");
        tvEmail.setText("student@Test.com");
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
        parentDetailsOptions.add("Edit Details");
        parentDetailsOptions.add("Add Details");

        List<String> skillsOptions = new ArrayList<>();
        skillsOptions.add("View your Skills");
        skillsOptions.add("Add Skills/Extracurriculars");

        // Correct Mapping
        sectionItems.put(sectionTitles.get(1), academicDetailsOptions); // Academic Details -> Correct dropdown
        sectionItems.put(sectionTitles.get(2), parentDetailsOptions); // Parent Guardian Details -> Correct dropdown
        sectionItems.put(sectionTitles.get(3), skillsOptions); // Skills and Extracurriculars -> Correct dropdown

        // Sections with no dropdown (empty lists)
        for (int i = 4; i < sectionTitles.size(); i++) {
            sectionItems.put(sectionTitles.get(i), new ArrayList<>());
        }

        adapter = new StudentsExpandableListAdapter(this, sectionTitles, sectionItems);
        expandableListView.setAdapter(adapter);

        loadUserEducation(academicDetailsOptions);
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
                    tvFullName.setText(user.getName());
                    tvContact.setText(user.getMobileNo());
                    tvEmail.setText(user.getEmailId());

                    String imageName = user.getUserImageName();
                    if (imageName != null && !imageName.isEmpty()) {
                        String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + imageName;
                        Log.d("StudentsInfo", "✅ Profile image URL: " + imageUrl);
                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.generic_avatar)
                                .error(R.drawable.generic_avatar)
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
