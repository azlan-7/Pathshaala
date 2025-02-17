package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TeachersInfo extends AppCompatActivity {

    private TextView tvFirstName,tvFullName, tvLastName, tvContact, tvEmail, tvDOB;
    private TextView accountInfo, subjectExpertise, education, workExperience,gradesTaught,certifications,awards,promotionalActivities, location;

    private TextView tvAboutYourself;
    private TextView uniqueIdTextView;

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
        setContentView(R.layout.activity_teachers_info);


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
        tvAboutYourself = findViewById(R.id.textView41);
        uniqueIdTextView = findViewById(R.id.uniqueIdTextView);


        String uniqueID = getIntent().getStringExtra("UNIQUE_ID");


        if (uniqueID != null) {
            uniqueIdTextView.setText("["+uniqueID+"]");
        } else {
            uniqueIdTextView.setText("Unique ID not available");
        }


        editAbout.setOnClickListener(v -> {
            Intent intent = new Intent(TeachersInfo.this, AboutTeacher.class);
            aboutActivityLauncher.launch(intent);
        });


        accountInfo.setOnClickListener(v ->{
            Intent intent = new Intent(TeachersInfo.this, TeachersBasicInfo.class);
            startActivity(intent);
        });


        subjectExpertise.setOnClickListener(v ->{
            Intent intent = new Intent(TeachersInfo.this, SubjectExpertiseNew.class);
            startActivity(intent);
        });


        education.setOnClickListener(v ->{
            Intent intent = new Intent(TeachersInfo.this,TeachersEducation.class);
            startActivity(intent);
        });

        workExperience.setOnClickListener(v ->{
            Intent intent = new Intent(TeachersInfo.this, WorkExperience.class);
            startActivity(intent);
        });

        gradesTaught.setOnClickListener(v ->{
            Intent intent = new Intent(TeachersInfo.this, GradesTaught.class);
            startActivity(intent);
        });

        certifications.setOnClickListener(v ->{
            Intent intent = new Intent(TeachersInfo.this,CertificationsView.class);
            startActivity(intent);
        });
        awards.setOnClickListener(v ->{
            Intent intent = new Intent(TeachersInfo.this,Awards.class);
            startActivity(intent);
        });
        promotionalActivities.setOnClickListener(v ->{
            Intent intent = new Intent(TeachersInfo.this, AddPromotionalMedia.class);
            startActivity(intent);

        });

        location.setOnClickListener(v ->{
            Intent intent = new Intent(TeachersInfo.this,TeachersAddress.class);
            startActivity(intent);
        });

        // Initialize TextViews
//        tvFirstName = findViewById(R.id.tvFirstName);
//        tvLastName = findViewById(R.id.tvLastName);
        tvContact = findViewById(R.id.tvContact);
        tvEmail = findViewById(R.id.tvEmail);
//        tvDOB = findViewById(R.id.tvDOB);
        tvFullName = findViewById((R.id.tvFullName));


        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String contactNumber = sharedPreferences.getString("phoneNumber", "Not Available"); // Default value if not found
        String aboutYourself = sharedPreferences.getString("ABOUT_YOURSELF", "Write about yourself...");


        TextView tvAboutYourself = findViewById(R.id.textView41);
        tvAboutYourself.setText(aboutYourself);


        TextView contactTextView = findViewById(R.id.tvContact); // Replace with actual TextView ID
        contactTextView.setText(contactNumber);

        String firstName = sharedPreferences.getString("FIRST_NAME", "N/A");
        String lastName = sharedPreferences.getString("LAST_NAME", "N/A");
        String contact = sharedPreferences.getString("CONTACT", "N/A");
        String email = sharedPreferences.getString("EMAIL", "N/A");
        String dob = sharedPreferences.getString("DOB", "N/A");

//        tvFirstName.setText("First Name: " + firstName);
//        tvLastName.setText("Last Name: " + lastName);
        tvFullName.setText(firstName + " " + lastName);
        tvContact.setText(contact);
        tvEmail.setText(email);
//        tvDOB.setText("Date of Birth: " + dob);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}