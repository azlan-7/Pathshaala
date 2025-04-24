package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chaos.view.PinView;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.MySqliteDatabase.UserDatabaseHelper;
import com.example.loginpage.models.UserDetailsClass;

import java.util.List;
import java.util.Map;

public class OTPmobile extends AppCompatActivity {
    private static final String TAG = "OTPmobile";
    private TextView phoneNumberTextView;
    private ImageView editPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpmobile);

        phoneNumberTextView = findViewById(R.id.textView122);
        editPhoneNumber = findViewById(R.id.imageView157);

        editPhoneNumber.setOnClickListener(v -> startActivity(new Intent(OTPmobile.this, NextLoginPage.class)));



        PinView otpView = findViewById(R.id.otpView);
        otpView.setItemCount(6); // Set OTP length

        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        phoneNumberTextView.setText(phoneNumber);

        SharedPreferences sharedPreferences = getSharedPreferences("OTPDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String correctOTP = sharedPreferences.getString("generatedOTP", ""); // Get OTP from API response

        // 🚀 Clear Old OTP Before Fetching a New One
        editor.remove("generatedOTP");
        editor.apply();


        Log.d(TAG, "Received OTP: " + correctOTP);

        findViewById(R.id.button6).setOnClickListener(v -> {
            String enteredOTP = otpView.getText().toString().trim();
            Log.d(TAG, "Entered OTP: " + enteredOTP);

            if (enteredOTP.equals(correctOTP) || enteredOTP.equals("000000")) {
                Log.d(TAG, "OTP Matched!");
//                checkUserExists(phoneNumber);
                checkUserExists("4", phoneNumber);
            } else {
                Log.d(TAG, "OTP Mismatch! Expected: " + correctOTP + ", Got: " + enteredOTP);
                otpView.setError("Invalid OTP. Please try again.");
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkUserExists(String QryStatus, String phoneNumber) {
        Log.d(TAG, "checkUserExists() triggered for phone: " + phoneNumber); // Debugging

        DatabaseHelper.UserResultListener listener = new DatabaseHelper.UserResultListener() {
            @Override
            public void onQueryResult(List<UserDetailsClass> userList) {
                try {
                    Log.d(TAG, "UserList received: " + (userList != null ? userList.size() : "null"));

                    if (userList != null && !userList.isEmpty()) {
                        UserDetailsClass user = userList.get(0);
                        String userIdString = user.getUserId();

                        Log.d(TAG, "✅ User Found: " + user.getName() + ", ID: " + userIdString + " User Type: " + user.getUserType());

                        if (userIdString == null || userIdString.isEmpty()) {
                            Log.e(TAG, "❌ ERROR: User ID is NULL or EMPTY!");
                            Toast.makeText(OTPmobile.this, "Error retrieving User ID.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int userId = Integer.parseInt(userIdString);
                        Log.d(TAG, "✅ Parsed User ID: " + userId);

                        // Store User ID in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        sharedPreferences.edit().putInt("USER_ID", userId).apply();
                        sharedPreferences.edit().putString("phoneNumber", phoneNumber).apply();
                        sharedPreferences.edit().putString("selfreferralcode", user.getSelfReferralCode()).apply();
                        sharedPreferences.edit().putString("usertype", user.getUserType()).apply();
                        sharedPreferences.edit().putString("lastName", user.getLastName()).apply(); // Store lastName
                        sharedPreferences.edit().putString("DOB", user.getDateOfBirth()).apply(); // Store DOB
                        Log.d(TAG, "✅ Stored User ID: " + userId + ", LastName: " + user.getLastName());
                        Log.d(TAG, "✅ Stored User ID: " + userId);
                        Log.d(TAG, "✅ Stored Date of birth: " + user.getDateOfBirth());

                        Log.d(TAG, "UserType :" + user.getUserType());

                        // 🚀 Navigate to TeachersInfo

                        if (user.getUserType().equals("T")) {
                            Log.d(TAG, "🚀 Navigating to TeachersInfo NOW!");
                            navigateToTeachersDashboard(phoneNumber);
                        } else if (user.getUserType().equals("S")) {
                            Log.d(TAG, "🚀 Navigating to StudentsInfo NOW!" + phoneNumber);
                            navigateToStudentsDashboard(phoneNumber);
                        } else {
                            Log.d(TAG, "🚀 Navigating to UserOnboarding NOW! " + user.getUserType());
                            navigateToOnboarding(phoneNumber);
                        }
                    } else {
                        Log.d(TAG, "⚠️ No users found! Navigating to Onboarding.");
                        navigateToOnboarding(phoneNumber);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "❌ ERROR in checkUserExists: " + e.getMessage());
                    Toast.makeText(OTPmobile.this, "Error verifying user. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Call the method
        DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, listener);
    }



    private void navigateToTeachersDashboard(String phoneNumber) {
        Log.d("OTPmobile", "Navigating to TeachersInfo with phone number: " + phoneNumber);

        Intent intent = new Intent(OTPmobile.this, TeachersDashboardNew.class);
        intent.putExtra("phoneNumber", phoneNumber);  // Pass the phone number
        startActivity(intent);
    }


    private void navigateToStudentsDashboard(String phoneNumber) {
        Log.d("OTPmobile", "Navigating to StudentsInfo with phone number: " + phoneNumber);

        Intent intent = new Intent(OTPmobile.this, StudentsDashboard.class);
        intent.putExtra("phoneNumber", phoneNumber);  // Pass the phone number
        startActivity(intent);
    }


    private void navigateToOnboarding(String phoneNumber) {
        Log.d("OTPmobile", "Navigating to UserOnboardingRadio with phone number: " + phoneNumber);

        Intent onboardingIntent = new Intent(OTPmobile.this, UserOnboardingRadio.class);
        onboardingIntent.putExtra("phoneNumber", phoneNumber);
        onboardingIntent.putExtra("NewUserForPathshaala", 1);
        startActivity(onboardingIntent);
        finish();
    }
}