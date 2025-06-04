package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.UserDetailsClass;

import java.util.List;

public class AboutTeacher extends AppCompatActivity {

    private EditText etAbout;
    private TextView charCount;
    private static final int MAX_CHAR = 150; // Max character limit
    private String userId;
    private String userContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about_teacher);

        etAbout = findViewById(R.id.editTextText22);
        charCount = findViewById(R.id.textView55);
        Button btnSave = findViewById(R.id.button20);

        // Retrieve data passed from TeachersInfoSubSection
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            userContact = intent.getStringExtra("USER_CONTACT");
            Log.d("AboutTeacher", "Retrieved USER_ID: " + userId + " | USER_CONTACT: " + userContact);
        } else {
            Log.d("AboutTeacher", "No data received from Intent.");
            userId = null;
            userContact = null;
        }


        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedText = sharedPreferences.getString("ABOUT_YOURSELF", "");
        etAbout.setText(savedText);
        charCount.setText(savedText.length() + "/" + MAX_CHAR);


        // Character counter logic
        etAbout.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                charCount.setText(length + "/" + MAX_CHAR);
                if (length > MAX_CHAR) {
                    charCount.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    charCount.setTextColor(getResources().getColor(android.R.color.black));
                }
            }


            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });



        btnSave.setOnClickListener(v -> {
            String aboutText = etAbout.getText().toString().trim();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ABOUT_YOURSELF", aboutText);
            editor.apply();

            UserDetailsClass currentUser = new UserDetailsClass();
            // **Use the retrieved userId and userContact**
            currentUser.setUserId(userId);
            currentUser.setMobileNo(userContact); // Assuming you need this for your update logic

            // **Important:** You'll still need to ensure other necessary fields for the update
            // are also populated in 'currentUser'. For example, if your update logic
            // relies on other user details, you need to retrieve or have them available.

            currentUser.setAboutUs(aboutText);
            currentUser.setStateId(0); // Initialize or retrieve the actual value
            currentUser.setCityId(0);   // Initialize or retrieve the actual value
            // ... set other fields if needed for the update ...

            DatabaseHelper.UserDetailsInsert(
                    AboutTeacher.this,
                    "1",
                    currentUser,
                    new DatabaseHelper.UserResultListener() {
                        @Override
                        public void onQueryResult(List<UserDetailsClass> userList) {
                            // Handle the result
                            if (userList != null && !userList.isEmpty()) {
                                Log.d("AboutTeacher", "User details updated successfully.");
                            } else {
                                Log.d("AboutTeacher", "About us text saved successfully.");
                            }

                            Intent intent = new Intent(AboutTeacher.this, TeachersInfoSubSection.class);
                            startActivity(intent);
                            finish();
                        }
                    }
            );
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}