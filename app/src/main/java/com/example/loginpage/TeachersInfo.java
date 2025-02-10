package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TeachersInfo extends AppCompatActivity {

    private TextView tvFirstName,tvFullName, tvLastName, tvContact, tvEmail, tvDOB;
    private TextView accountInfo, subjectExpertise, education, professionalDetails, location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_info);

        accountInfo = findViewById(R.id.textView43);
        subjectExpertise = findViewById(R.id.textView44);
        education = findViewById(R.id.textView45);
        professionalDetails = findViewById(R.id.textView46);
        location = findViewById(R.id.textView48);


        accountInfo.setOnClickListener(v ->{
            Intent intent = new Intent(TeachersInfo.this, TeachersBasicInfo.class);
            startActivity(intent);
        });


        subjectExpertise.setOnClickListener(v ->{
            Intent intent = new Intent(TeachersInfo.this, TeachersAddSubject.class);
            startActivity(intent);
        });


        education.setOnClickListener(v ->{
            Intent intent = new Intent(TeachersInfo.this,TeachersEducation.class);
            startActivity(intent);
        });

        professionalDetails.setOnClickListener(v ->{
            Intent intent = new Intent(TeachersInfo.this, ProfessionalDetails.class);
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