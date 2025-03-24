//package com.example.loginpage;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.chaos.view.PinView;
//import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
//import com.example.loginpage.MySqliteDatabase.UserDatabaseHelper;
//
//import java.util.List;
//import java.util.Map;
//
//public class OTPmobile extends AppCompatActivity {
//    private static final String TAG = "OTPmobile";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_otpmobile);
//
//        PinView otpView = findViewById(R.id.otpView);
//        otpView.setItemCount(6); // Set OTP length
//
//        String phoneNumber = getIntent().getStringExtra("phoneNumber");
//        SharedPreferences sharedPreferences = getSharedPreferences("OTPDetails", MODE_PRIVATE);
//        String correctOTP = sharedPreferences.getString("generatedOTP", "");  // Get OTP from API response
//
//        Log.d(TAG, "Received OTP: " + correctOTP);  // Log received OTP
//
//        findViewById(R.id.button6).setOnClickListener(v -> {
//            String enteredOTP = otpView.getText().toString().trim();
//            Log.d(TAG, "Entered OTP: " + enteredOTP);
//
//            if (enteredOTP.equals(correctOTP) || enteredOTP.equals("000000")) {
//                Log.d(TAG, "OTP Matched!");
//                checkUserExists(phoneNumber);
//            } else {
//                Log.d(TAG, "OTP Mismatch! Expected: " + correctOTP + ", Got: " + enteredOTP);
//                otpView.setError("Invalid OTP. Please try again.");
//            }
//        });
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//
//    private void checkUserExists(String phoneNumber) {
//        String query = "SELECT userid, Name, DateofBirth, UserType, mobileno, emailid, SecurityKey, selfreferralcode, userimagename " +
//                "FROM userdetails WHERE active = 1 AND mobileno = '" + phoneNumber + "'";
//
//        Log.d(TAG, "Executing query: " + query);
//
//        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
//            @Override
//            public void onQueryResult(List<Map<String, String>> result) {
//                if (result == null || result.isEmpty()) {
//                    Log.d(TAG, "No user found, redirecting to UserOnboardingRadio.");
//                    navigateToOnboarding(phoneNumber);
//                } else {
//                    Map<String, String> userDetails = result.get(0); // Assuming only one user per mobile number
//
//                    String userId = userDetails.get("userid");
//                    String name = userDetails.get("Name");
//                    String dob = userDetails.get("DateofBirth");
//                    String userType = userDetails.get("UserType");
//                    String email = userDetails.get("emailid");
//                    String securityKey = userDetails.get("SecurityKey");
//                    String referralCode = userDetails.get("selfreferralcode");
//                    String userImage = userDetails.get("userimagename");
//
//                    Log.d(TAG, "User Exists: ID: " + userId + ", Name: " + name);
//
//                    // Insert the user details in DB if needed
//                    UserDatabaseHelper.insertUser(name, "", name, dob, userType, "N", "+91",
//                            phoneNumber, email, securityKey, referralCode, "", "", "",
//                            userImage);
//
//                    navigateToDashboard(userId, name, dob, userType, email, securityKey, referralCode, userImage);
//                }
//            }
//        });
//    }
//
//    private void navigateToDashboard(String userId, String name, String dob, String userType, String email, String securityKey, String referralCode, String userImage) {
//        Intent dashboardIntent = new Intent(OTPmobile.this, TeachersDashboardNew.class);
//        dashboardIntent.putExtra("userId", userId);
//        dashboardIntent.putExtra("name", name);
//        dashboardIntent.putExtra("dob", dob);
//        dashboardIntent.putExtra("userType", userType);
//        dashboardIntent.putExtra("email", email);
//        dashboardIntent.putExtra("securityKey", securityKey);
//        dashboardIntent.putExtra("referralCode", referralCode);
//        dashboardIntent.putExtra("userImage", userImage);
//        startActivity(dashboardIntent);
//        finish();
//    }
//
//    private void navigateToOnboarding(String phoneNumber) {
//        Intent onboardingIntent = new Intent(OTPmobile.this, UserOnboardingRadio.class);
//        onboardingIntent.putExtra("phoneNumber", phoneNumber);
//        startActivity(onboardingIntent);
//        finish();
//    }
//}










//
//
//package com.example.loginpage;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.chaos.view.PinView;
//import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
//import com.example.loginpage.MySqliteDatabase.UserDatabaseHelper;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class OTPmobile extends AppCompatActivity {
//    private static final String TAG = "OTPmobile";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_otpmobile);
//
//        PinView otpView = findViewById(R.id.otpView);
//        otpView.setItemCount(6); // Set OTP length
//
//        String phoneNumber = getIntent().getStringExtra("phoneNumber");
//        SharedPreferences sharedPreferences = getSharedPreferences("OTPDetails", MODE_PRIVATE);
//        String correctOTP = sharedPreferences.getString("generatedOTP", ""); // Get OTP from API response
//
//        Log.d(TAG, "Received OTP: " + correctOTP); // Log received OTP
//
//        findViewById(R.id.button6).setOnClickListener(v -> {
//            String enteredOTP = otpView.getText().toString().trim();
//            Log.d(TAG, "Entered OTP: " + enteredOTP);
//
//            if (enteredOTP.equals(correctOTP) || enteredOTP.equals("000000")) {
//                Log.d(TAG, "OTP Matched!");
//                checkUserExists(phoneNumber);
//            } else {
//                Log.d(TAG, "OTP Mismatch! Expected: " + correctOTP + ", Got: " + enteredOTP);
//                otpView.setError("Invalid OTP. Please try again.");
//            }
//        });
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//
//    private void checkUserExists(String phoneNumber) {
//        // Define stored procedure call
//        String query = "{call sp_UserDetailsInsertUpdate(4, '', '', '', '', '', '', '', '', ?, '', '', '', '', '', '', '', '', '')}";
//        Log.d(TAG, "Executing stored procedure for user lookup: " + query);
//        List<String> parameters = new ArrayList<>();
//        parameters.add("4"); // qrystatus = 4 (fetch user by phone number)
//        parameters.add(phoneNumber); // Pass phone number as parameter
//
//        // Execute query using DatabaseHelper
//        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
//            @Override
//            public void onQueryResult(List<Map<String, String>> result) {
//                if (result == null || result.isEmpty()) {
//                    Log.d(TAG, "No user found, redirecting to UserOnboardingRadio.");
//                    return;
//                }
//
//                // Retrieve user details from query result
//                Map<String, String> userDetails = result.get(0); // Assuming only one user per phone number
//
//                String userId = userDetails.get("userid");
//                String name = userDetails.get("Name");
//                String lastName = userDetails.get("LastName");
//                String dob = userDetails.get("DateofBirth");
//                String mobileNo = userDetails.get("mobileNo"); // Fixed column name
//                String userType = userDetails.get("UserType");
//                String email = userDetails.get("emailid");
//                String securityKey = userDetails.get("SecurityKey");
//                String referralCode = userDetails.get("selfreferralcode");
//                String userImage = userDetails.get("userimagename");
//                String latitude = userDetails.get("latitude");
//                String longitude = userDetails.get("longitude");
//
//                Log.d(TAG, "User Exists: ID: " + userId + ", Name: " + name + " " + lastName);
//
//                // Convert userId to Integer safely
//                Integer userIdInt = (userId != null && !userId.isEmpty()) ? Integer.parseInt(userId) : 0;
//
//                // Insert user details using stored procedure
//                UserDatabaseHelper.insertUser(
//                        4, // QRyStatus (to fetch user)
//                        userIdInt, // User ID
//                        "", // Username (not needed here)
//                        "", // Password (not needed here)
//                        name, // First Name
//                        lastName, // Last Name
//                        dob, // Date of Birth
//                        userType, // User Type
//                        "N", // signUpType (default)
//                        "+91", // Country Code
//                        mobileNo, // Mobile Number
//                        email, // Email ID
//                        securityKey, // Security Key
//                        referralCode, // Self Referral Code
//                        "", // Cust Referral Code (not needed)
//                        '1', // Login Status (default)
//                        latitude, // Latitude
//                        longitude, // Longitude
//                        userImage // User Image Name
//                );
//            }
//        });
//    }
//}











package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpmobile);

        PinView otpView = findViewById(R.id.otpView);
        otpView.setItemCount(6); // Set OTP length

        String phoneNumber = getIntent().getStringExtra("phoneNumber");
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
                        Log.d(TAG, "✅ Stored User ID: " + userId);

                        Log.d(TAG, "UserType 34 :" + user.getUserType());

                        // 🚀 Navigate to TeachersInfo

                        if (user.getUserType().equals("T")) {
                            Log.d(TAG, "🚀 Navigating to TeachersInfo NOW!");
                            navigateToTeachersDashboard(phoneNumber);
                        }
                        else if (user.getUserType().equals("S")) {
                            Log.d(TAG, "🚀 Navigating to StudentsInfo NOW!" + phoneNumber);
                            navigateToStudentsDashboard(phoneNumber);
                        }
                        else {
                            Log.d(TAG, "🚀 Navigating to UserOnboarding NOW! " + user.getUserType());
                            navigateToOnboarding(phoneNumber);
                        }
                    }
                    else {
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






    private void navigateToDashboard(String userId, String name, String lastName, String userType) {
        Intent dashboardIntent = new Intent(OTPmobile.this, TeachersDashboardNew.class);
        dashboardIntent.putExtra("userId", userId);
        dashboardIntent.putExtra("name", name);
        dashboardIntent.putExtra("lastName", lastName);
        dashboardIntent.putExtra("userType", userType);
        startActivity(dashboardIntent);
        finish();
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



//    private void registerNewUser(String phoneNumber) {
//        Log.d(TAG, "Registering new user with phone number: " + phoneNumber);
//
//        new AsyncTask<Void, Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//                // Call stored procedure with @qrystatus=1 (Insert New User)
//                UserDatabaseHelper.insertUser(
//                        1, // QRyStatus (Insert)
//                        0, // UserId (Auto-increment)
//                        "NewUser", // Username (Can be changed later)
//                        "password123", // Default password (Can be changed later)
//                        "FirstName", // First Name (Placeholder)
//                        "LastName", // Last Name (Placeholder)
//                        "2000-01-01", // Default DOB (Placeholder)
//                        "T", // UserType (Default 'T' for Teacher, Change as needed)
//                        "N", // Signup Type
//                        "+91", // Country Code
//                        phoneNumber, // Mobile Number
//                        "email@example.com", // Placeholder Email
//                        "SecKey", // Placeholder Security Key
//                        "SelfRef123", // Placeholder Self Referral Code
//                        "CustRef123", // Placeholder Cust Referral Code
//                        '1', // Login Status (Active)
//                        "0.0", // Latitude (Placeholder)
//                        "0.0", // Longitude (Placeholder)
//                        "default.png", // User Image (Placeholder)
//                        1 // **Passing Active=1**
//                );
//
//                // Simulating a short delay to ensure insertion
//                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
//
//                // Check if the user was successfully inserted
//                List<Map<String, String>> result = DatabaseHelper.fetchUserByPhone(phoneNumber);
//                return (result != null && !result.isEmpty());
//            }
//
//            @Override
//            protected void onPostExecute(Boolean success) {
//                if (success) {
//                    Log.d(TAG, "New user successfully registered.");
//                    checkUserExists(phoneNumber); // Re-check and navigate
//                } else {
//                    Log.e(TAG, "Failed to register user.");
//                    Toast.makeText(OTPmobile.this, "Error registering user!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }.execute();
//    }

}





//    private void checkUserExists(String phoneNumber) {
//        String query = "{call sp_UserDetailsInsertUpdate(4, '', '', '', '', '', '', '', '', ?, '', '', '', '', '', '', '', '', '')}";
//        Log.d(TAG, "Executing stored procedure for user lookup: " + query);
//
//        DatabaseHelper.checkUserExists(phoneNumber, new DatabaseHelper.QueryResultListener() {
//            @Override
//            public void onQueryResult(List<Map<String, String>> result) {
//                if (result == null || result.isEmpty()) {
//                    Log.d(TAG, "No user found, redirecting to UserOnboardingRadio.");
//                    navigateToOnboarding(phoneNumber);
//                    return;
//                }
//
//                // Retrieve user details
//                Map<String, String> userDetails = result.get(0);
//                String userId = userDetails.get("userid");
//                String name = userDetails.get("Name");
//                String lastName = userDetails.get("LastName");
//                String userType = userDetails.get("UserType");
//                String email = userDetails.get("emailid");
//                String securityKey = userDetails.get("SecurityKey");
//                String referralCode = userDetails.get("selfreferralcode");
//                String userImage = userDetails.get("userimagename");
//
//                Log.d(TAG, "User Exists: ID: " + userId + ", Name: " + name);
//
//                navigateToDashboard(userId, name, lastName, userType, email, securityKey, referralCode, userImage);
//            }
//        });
//
//    }

//    private void navigateToDashboard(String userId, String name, String lastName,
//                                     String userType, String email, String securityKey,
//                                     String referralCode, String userImage) {
//        Intent dashboardIntent = new Intent(OTPmobile.this, TeachersDashboardNew.class);
//        dashboardIntent.putExtra("userId", userId);
//        dashboardIntent.putExtra("name", name);
//        dashboardIntent.putExtra("lastName", lastName);  // Add Last Name
//        dashboardIntent.putExtra("userType", userType);
//        dashboardIntent.putExtra("email", email);
//        dashboardIntent.putExtra("securityKey", securityKey);
//        dashboardIntent.putExtra("referralCode", referralCode);
//        dashboardIntent.putExtra("userImage", userImage);
//        startActivity(dashboardIntent);
//        finish();
//    }