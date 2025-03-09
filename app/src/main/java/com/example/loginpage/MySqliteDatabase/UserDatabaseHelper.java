package com.example.loginpage.MySqliteDatabase;

import android.os.AsyncTask;
import android.util.Log;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class UserDatabaseHelper {

    private static final String TAG = "UserDatabaseHelper";

    public static void insertUser(
            Integer QRyStatus, Integer UserId, String username, String password, String Name,
            String LastName, String DateOfBirth, String userType, String signUpType,
            String countryCode, String mobileNo, String emailId, String securityKey,
            String selfReferralCode, String custReferralCode, Character LoginStatus,
            String latitude, String longitude, String userImageName, int Active
    ) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                Connection connection = DatabaseHelper.getConnection();
                if (connection == null) {
                    Log.e(TAG, "Connection is NULL. Check DB Credentials.");
                    return false;
                }

                try {
                    Log.d(TAG, "Executing stored procedure...");

                    CallableStatement stmt = connection.prepareCall(
                            "{call sp_UserDetailsInsertUpdate(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"
                    );

                    stmt.setInt(1, QRyStatus);
                    stmt.setInt(2, UserId);
                    stmt.setString(3, username);
                    stmt.setString(4, password);
                    stmt.setString(5, Name);
                    stmt.setString(6, LastName);
                    stmt.setString(7, DateOfBirth);
                    stmt.setString(8, userType);
                    stmt.setString(9, signUpType);
                    stmt.setString(10, countryCode);
                    stmt.setString(11, mobileNo);
                    stmt.setString(12, emailId);
                    stmt.setString(13, securityKey);
                    stmt.setString(14, selfReferralCode);
                    stmt.setString(15, custReferralCode);
                    stmt.setString(16, String.valueOf(LoginStatus));
                    stmt.setString(17, latitude);
                    stmt.setString(18, longitude);
                    stmt.setString(19, userImageName);
                    stmt.setInt(20, Active); // **This is the last parameter**

                    stmt.execute();
                    stmt.close();
                    connection.close();

                    Log.d(TAG, "User inserted successfully.");
                    return true;
                } catch (SQLException e) {
                    Log.e(TAG, "Error executing stored procedure: " + e.getMessage());
                    return false;
                }
            }
        }.execute();
    }


}







//package com.example.loginpage.MySqliteDatabase;
//
//import android.os.AsyncTask;
//import android.util.Log;
//import java.sql.Connection;
//import java.sql.CallableStatement;
//import java.sql.SQLException;
//
//public class UserDatabaseHelper {
//
//    private static final String TAG = "UserDatabaseHelper";
//
//    public static void insertUser(
//            Integer QRyStatus,Integer UserId, String username, String password ,String Name,String LastName, String DateOfBirth,
//            String userType, String signUpType, String countryCode,
//            String mobileNo, String emailId, String securityKey,
//            String selfReferralCode, String custReferralCode,Character LoginStatus,
//            String latitude, String longitude, String userImageName
//    )
// {
//        new AsyncTask<Void, Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//                Connection connection = DatabaseHelper.getConnection();
//                if (connection == null) {
//                    Log.e(TAG, "Connection is NULL. Check DB Credentials.");
//                    return false;
//                }
//
//                try {
//                    Log.d(TAG, "Executing stored procedure...");
//
//                    // Prepare the SQL Callable Statement for executing the stored procedure
//                    CallableStatement stmt = connection.prepareCall("{call sp_UserDetailsInsertUpdate(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
//
//                    // Set parameters
//                    stmt.setInt(1, 1);  // @qrystatus = 1 (Insert)
//                    stmt.setInt(2, 0);  // @UserId (Not required for insert)
//                    stmt.setString(3, username);
//                    stmt.setString(4, password);
//                    stmt.setString(5, Name);
//                    stmt.setString(6, LastName);
//                    stmt.setString(7, DateOfBirth);
//                    stmt.setString(8, userType);
//                    stmt.setString(9, signUpType);
//                    stmt.setString(10, countryCode);
//                    stmt.setString(11, mobileNo);
//                    stmt.setString(12, emailId);
//                    stmt.setString(13, securityKey);
//                    stmt.setString(14, selfReferralCode);
//                    stmt.setString(15, custReferralCode);
//                    stmt.setString(16, "1"); // LoginStatus
//                    stmt.setString(17, latitude);
//                    stmt.setString(18, longitude);
//                    stmt.setString(19, userImageName);
//                    stmt.setInt(20, 0); // login_attempts
//
//                    // Execute the stored procedure
//                    stmt.execute();
//                    stmt.close();
//                    connection.close();
//
//                    Log.d(TAG, "User inserted successfully.");
//                    return true;
//                } catch (SQLException e) {
//                    Log.e(TAG, "Error executing stored procedure: " + e.getMessage());
//                    return false;
//                }
//            }
//        }.execute();
//    }
//}


//
//package com.example.loginpage.MySqliteDatabase;
//
//import android.os.AsyncTask;
//import android.util.Log;
//import java.sql.Connection;
//import java.sql.CallableStatement;
//import java.sql.SQLException;
//
//public class UserDatabaseHelper {
//
//    private static final String TAG = "UserDatabaseHelper";
//
//    public static void insertUser(
//            String username, String password, String name, String dateOfBirth,
//            String userType, String signUpType, String countryCode,
//            String mobileNo, String emailId, String securityKey,
//            String selfReferralCode, String custReferralCode,
//            String latitude, String longitude, String userImageName
//    ) {
//        new AsyncTask<Void, Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//                Connection connection = DatabaseHelper.getConnection();
//                if (connection == null) {
//                    Log.e(TAG, "Connection is NULL. Check DB Credentials.");
//                    return false;
//                }
//
//                try {
//                    Log.d(TAG, "Executing stored procedure...");
//
//                    // Prepare the SQL Callable Statement for executing the stored procedure
//                    CallableStatement stmt = connection.prepareCall("{call sp_UserDetailsInsertUpdate(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
//
//                    // Set parameters
//                    stmt.setInt(1, 1);  // @qrystatus = 1 (Insert)
//                    stmt.setInt(2, 0);  // @UserId (Not required for insert)
//                    stmt.setString(3, username);
//                    stmt.setString(4, password);
//                    stmt.setString(5, name);
//                    stmt.setString(6, dateOfBirth);
//                    stmt.setString(7, userType);
//                    stmt.setString(8, signUpType);
//                    stmt.setString(9, countryCode);
//                    stmt.setString(10, mobileNo);
//                    stmt.setString(11, emailId);
//                    stmt.setString(12, securityKey);
//                    stmt.setString(13, selfReferralCode);
//                    stmt.setString(14, custReferralCode);
//                    stmt.setString(15, null); // LoginCreationDate (uses GETDATE() in SP)
//                    stmt.setString(16, "1"); // LoginStatus
//                    stmt.setString(17, latitude);
//                    stmt.setString(18, longitude);
//                    stmt.setString(19, userImageName);
//                    stmt.setInt(20, 0); // login_attempts
//
//                    // Execute the stored procedure
//                    stmt.execute();
//                    stmt.close();
//                    connection.close();
//
//                    Log.d(TAG, "User inserted successfully.");
//                    return true;
//                } catch (SQLException e) {
//                    Log.e(TAG, "Error executing stored procedure: " + e.getMessage());
//                    return false;
//                }
//            }
//        }.execute();
//    }
//}

