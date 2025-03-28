package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.loginpage.models.UserDetailsClass;

public class StudentsInfo extends AppCompatActivity {

    private TextView tvFullName, tvContact, tvEmail;
    private UserDetailsClass user;
    private TextView accountInfo, academicDetails, learningPreferences, parentGuardian, skillExtraCurr,progress,attendance,commPref,feedback,dashboard;
    private TextView tvAboutYourself;
    private TextView uniqueIdTextView;
    private ImageView profileImage;

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

        accountInfo = findViewById(R.id.textViewAccountInfo);
        academicDetails = findViewById(R.id.textViewEducation);
        learningPreferences = findViewById(R.id.textViewCourses);
        skillExtraCurr = findViewById(R.id.textViewActivities);
        parentGuardian = findViewById(R.id.textViewLocation);
        dashboard = findViewById(R.id.textView121);
        ImageView editAbout = findViewById(R.id.imageView44);
        ImageView qrCode = findViewById(R.id.imageView113);
        ImageView payment = findViewById(R.id.imageView138);
        progress = findViewById(R.id.textView84);
        attendance = findViewById(R.id.textView85);
        commPref = findViewById(R.id.textView86);
        feedback = findViewById(R.id.textView87);
        tvAboutYourself = findViewById(R.id.textViewAboutYourself);
        uniqueIdTextView = findViewById(R.id.uniqueIdTextView2);
        skillExtraCurr = findViewById(R.id.textViewActivities);
        
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

        accountInfo.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsInfo.this, StudentsBasicInfo.class);
            aboutActivityLauncher.launch(intent);
            startActivity(intent);
        });

        dashboard.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsInfo.this, StudentsDashboard.class);
            aboutActivityLauncher.launch(intent);
            startActivity(intent);
        });

        academicDetails.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsInfo.this, StudentsAcademicDetails.class);
            aboutActivityLauncher.launch(intent);
            startActivity(intent);
        });


        parentGuardian.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsInfo.this, StudentsParentInfo.class);
            aboutActivityLauncher.launch(intent);
            startActivity(intent);
        });

        commPref.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsInfo.this, CommunicationPreferences.class);
            aboutActivityLauncher.launch(intent);
            startActivity(intent);
        });

        learningPreferences.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsInfo.this, LearningPreferences.class);
            aboutActivityLauncher.launch(intent);
            startActivity(intent);
        });

        skillExtraCurr.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsInfo.this, AddExtracurriculars.class);
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
