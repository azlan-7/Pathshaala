package com.example.loginpage.MySqliteDatabase;

//import android.app.Notification;
//import com.example.loginpage.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Timestamp;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.loginpage.models.UserDetailsClass;
import com.example.loginpage.models.UserInfoItem;
import com.example.loginpage.models.UserWiseEducation;
import com.example.loginpage.models.UserWiseGrades;
import com.example.loginpage.models.UserWiseSubject;
import com.example.loginpage.models.UserWiseWorkExperience;
import com.example.loginpage.models.VirtualClassClass;

import com.example.loginpage.models.Notification;
import com.example.loginpage.models.UserInfoItem;

import static net.sourceforge.jtds.jdbc.DefaultProperties.DATABASE_NAME;
import static net.sourceforge.jtds.jdbc.DefaultProperties.getServerType;

public class DatabaseHelper {
    private static final String IP = "199.79.62.22"; // Your SQL Server IP
    private static final String DB_NAME = "pathshaala";
    private static final String USERNAME = "pathshaala";
    private static final String PASSWORD = "Darshi@2025#";
    private static final String PORT = "1433"; // Default SQL Server Port

    public interface QueryResultListener {
        void onQueryResult(List<Map<String, String>> result);
    }


    public interface UserResultListener {
        void onQueryResult(List<UserDetailsClass> userList);
    }

    public interface UserWiseSubjectResultListener {
        void onQueryResult(List<UserWiseSubject> userWiseSubjects);
    }

    public interface UserWiseEducationResultListener {
        void onQueryResult(List<UserWiseEducation> educationList);
    }

    public interface UserWiseWorkExperienceResultListener {
        void onQueryResult(List<UserWiseWorkExperience> userWiseWorkExperienceList);
        void onError(String error);
    }

    public interface UserWiseGradesResultListener {
        void onQueryResult(List<UserWiseGrades> userWiseGradesList);
    }


    public interface DatabaseCallback {
        void onSuccess(List<Map<String, String>> result);
        void onMessage(String message);
        void onError(String error);
    }

    public interface EducationLevelsCallback {
        void onSuccess(List<Map<String, String>> educationLevels);
        void onError(String error);
    }

    public interface CoursesCallback {
        void onSuccess(List<Map<String, String>> courses);
        void onError(String error);
    }
    public interface WorkExperienceCallback {
        void onSuccess(List<UserWiseWorkExperience> workExperienceList); // ✅ Fix return type
        void onMessage(String message);
        void onError(String error);

    }

    public interface ProcedureCallback {
        void onSuccess(String message);
        void onError(String error);
    }


    public static Connection getConnection() {
        Connection connection = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String connectionURL = "jdbc:jtds:sqlserver://" + IP + ":" + PORT + ";databaseName=" + DB_NAME + ";user=" + USERNAME + ";password=" + PASSWORD + ";";

            connection = DriverManager.getConnection(connectionURL);
            Log.d("DatabaseHelper", "Connected to SQL Server successfully! 11 "+connection.toString());


        } catch (SQLException se) {
            Log.e("DatabaseHelper", "SQL Exception: " + se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("DatabaseHelper", "JDBC Driver Not Found: " + e.getMessage());
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Unknown Error: " + e.getMessage());
        }
        return connection;
    }



    /**
     * Existing method for loading data into AutoCompleteTextView (unchanged)
     */
    public static void loadDataFromDatabase(Context context, String query, AutoCompleteTextView autoCompleteTextView, TextView textView) {
        new AsyncTask<Void, Void, Map<String, String>>() {
            @Override
            protected Map<String, String> doInBackground(Void... voids) {
                Map<String, String> dataMap = new HashMap<>();
                try {
                    Log.d("DatabaseHelper", "Attempting to establish a connection...");
                    Connection connection = getConnection();
                    Log.d("DatabaseHelper", "dytyy 44gg ");
                    if (connection != null) {
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {
                            String id = rs.getString(1); // First column: ID
                            String name = rs.getString(2); // Second column: Name
                            dataMap.put(name, id);
                        }
                        rs.close();
                        stmt.close();
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("DatabaseHelper", "Error fetching data: " + e.getMessage());
                }
                return dataMap;
            }

            @Override
            protected void onPostExecute(Map<String, String> data) {
                if (!data.isEmpty()) {
                    List<String> names = new ArrayList<>(data.keySet());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, names);
                    autoCompleteTextView.setAdapter(adapter);
                    autoCompleteTextView.setOnClickListener(v -> autoCompleteTextView.showDropDown());

                    autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                        String selectedName = (String) parent.getItemAtPosition(position);
                        textView.setText(data.get(selectedName)); // Show corresponding ID
                    });

                    Log.d("DatabaseHelper", "Data loaded successfully!");
                } else {
                    Log.e("DatabaseHelper", "No data fetched!");
                }
            }
        }.execute();
    }

    /**
     * New method for fetching data dynamically with a listener.
     */
    public static void loadDataFromDatabase(Context context, String query, QueryResultListener listener) {
        new AsyncTask<Void, Void, List<Map<String, String>>>() {
            @Override
            protected List<Map<String, String>> doInBackground(Void... voids) {
                List<Map<String, String>> resultList = new ArrayList<>();
                try {
                    Log.d("DatabaseHelper", "Attempting to fetch data...");
                    Connection connection = getConnection();
                    if (connection != null) {
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {
                            Map<String, String> row = new HashMap<>();
                            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                                row.put(rs.getMetaData().getColumnName(i), rs.getString(i));
                            }
                            resultList.add(row);
                        }
                        rs.close();
                        stmt.close();
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("DatabaseHelper", "Error fetching data: " + e.getMessage());
                }
                return resultList;
            }

            @Override
            protected void onPostExecute(List<Map<String, String>> result) {
                listener.onQueryResult(result);
            }
        }.execute();
    }

    public static void UserDetailsInsert(Context context, String QryStatus, UserDetailsClass user, UserResultListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String messageOutput = "Operation failed"; // Default message
                try {
                    Log.d("DatabaseHelper", "Calling function ResultSet rs 00 QryStatus = " + QryStatus);
                    Connection connection = getConnection();
                    Log.d("DatabaseHelper", "Calling function ResultSet rs 1 : " + connection);

                    if (connection != null) {
                        String query = "{call sp_UserDetailsInsertUpdate(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
                        CallableStatement stmt = connection.prepareCall(query);

                        Log.d("DatabaseHelper", "Executing stored procedure for user lookup with Value 3144  : " + user.getMobileNo());

                        // Set stored procedure input parameters
                        stmt.setString(1, QryStatus);
                        stmt.setString(2, "0");
                        stmt.setString(3, user.getUsername());
                        stmt.setString(4, user.getPassword());
                        stmt.setString(5, user.getName());
                        Log.d("DatabaseHelper", "Name of the User 12: " + user.getName());
                        stmt.setString(6, user.getLastName());
                        stmt.setString(7, user.getDateOfBirth());
                        Log.d("DatabaseHelper", "Date of birth of the user: " + user.getDateOfBirth());
                        stmt.setString(8, user.getUserType());
                        stmt.setString(9, "");
                        stmt.setString(10, user.getCountryCode());
                        stmt.setString(11, user.getMobileNo());
                        stmt.setString(12, user.getEmailId());
                        stmt.setString(13, user.getSecurityKey());
                        Log.d("DatabaseHelper", "Email Id of the user 14: " + user.getEmailId());
                        stmt.setString(14, "");
                        stmt.setString(15, "");
                        stmt.setString(16, "");
                        stmt.setString(17, user.getLatitude());
                        stmt.setString(18, user.getLongitude());
                        stmt.setString(19, user.getUserImageName());

                        Log.d("DatabaseHelper", "Executing stored procedure for user lookup with Value of mobile no.  : " + user.getMobileNo());
                        stmt.registerOutParameter(20, Types.VARCHAR); // @selfreferralcodeOutput
                        stmt.registerOutParameter(21, Types.VARCHAR); // @messageOutput

                        // Execute stored procedure
                        stmt.execute();
                        Log.d("DatabaseHelper", "Executing the code block after registerOutParameter 2234 " );

                        // Retrieve OUT parameter value
                        String selfReferralCodeOutput = stmt.getString(20);
                        messageOutput = stmt.getString(21); // Get the message from the stored procedure

                        Log.d("DatabaseHelper", "Self Referral Code Output: " + selfReferralCodeOutput);
                        Log.d("DatabaseHelper", "Message output: 144 " + messageOutput);

                        stmt.close();
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "SQL Error fetching user by insert: " + e.getMessage());
                }
                return messageOutput;
            }

            @Override
            protected void onPostExecute(String messageOutput) {
                Log.d("DatabaseHelper", "onPostExecute called with messageOutput: " + messageOutput);

                // Show Toast with the messageOutput
                Toast.makeText(context, messageOutput, Toast.LENGTH_SHORT).show();

                // Notify listener
                listener.onQueryResult(new ArrayList<>()); // Assuming no user list is returned here
            }
        }.execute();
    }

    public static void UserDetailsSelect(Context context, String QryStatus, String phoneNumber, UserResultListener listener) {
        new AsyncTask<Void, Void, List<UserDetailsClass>>() {
            @Override
            protected List<UserDetailsClass> doInBackground(Void... voids) {
                List<UserDetailsClass> userList = new ArrayList<>();
                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserDetailsSelect.4455.."+phoneNumber);

                    Connection con = getConnection();

                    if (con != null) {
                        String query = "{call sp_UserDetailsInsertUpdate(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
                        PreparedStatement stmt = con.prepareStatement(query);
                        Log.d("DatabaseHelper", "📌 Executing query for phone: Query  " + query);

                        stmt.setString(1, QryStatus);
                        stmt.setInt(2, 0);
                        stmt.setString(3, "");
                        stmt.setString(4, "");
                        stmt.setString(5, "");
                        stmt.setString(6, "");
                        stmt.setString(7, null); //dob
                        stmt.setString(8, "");
                        stmt.setString(9, "");
                        stmt.setString(10, "");
                        stmt.setString(11, phoneNumber);  // ✅ Phone number
                        stmt.setString(12, "");
                        stmt.setString(13, "");
                        Log.d("DatabaseHelper", "📌 Executing query for phone: " + phoneNumber);
                        stmt.setString(14, "");
                        stmt.setString(15, "");
                        stmt.setString(16, "");
                        stmt.setString(17, "");
                        stmt.setString(18, "");
                        stmt.setString(19, "");
                        stmt.setString(20, "");
                        stmt.setString(21, "");


                        ResultSet rs = stmt.executeQuery();

                        Log.d("DatabaseHelper", "📌 Executing query for phone:  " + phoneNumber);
                        Log.d("DatabaseHelper", "📌 Query Result Size: " + userList.size());

                        if (!rs.isBeforeFirst()) { // ✅ No data found
                            Log.d("DatabaseHelper", "⚠️ No user data found in DB!");
                        }

                        while (rs.next()) {
                            Log.d("DatabaseHelper", "Retrieved row from database: " + rs.toString());
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int columnCount = rsmd.getColumnCount();
                            for (int i = 1; i <= columnCount; i++) {
                                Log.d("DatabaseHelper", "Column: " + rsmd.getColumnName(i) + " -> " + rs.getString(i));
                            }

                            String userIdString = rs.getString("UserId");
                            Log.d("DatabaseHelper", "✅ UserId Retrieved: " + userIdString);

                            UserDetailsClass user = new UserDetailsClass();
                            user.setUserId(rs.getString("UserId"));
                            user.setName(rs.getString("Name"));
                            user.setEmailId(rs.getString("emailId"));
                            user.setMobileNo(rs.getString("mobileno"));
                            user.setSelfReferralCode(rs.getString("selfreferralcode"));
                            user.setUserType(rs.getString("usertype"));
                            user.setUserImageName(rs.getString("UserImageName"));
                            userList.add(user);

                            Log.d("DatabaseHelper", "✅ User Retrieved: " + user.getUserType() + " (ID: " + user.getUserId() + ")");
                        }

                        rs.close();
                        stmt.close();
                        con.close();
                    } else {
                        Log.e("DatabaseHelper", "❌ DB connection failed!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return userList;
            }

            @Override
            protected void onPostExecute(List<UserDetailsClass> userList) {
                Log.d("DatabaseHelper", "📌 onPostExecute called! Users found: " + userList.size());

                listener.onQueryResult(userList);
            }
        }.execute();
    }

    public static void updateUserProfileImage(int userId, String fileName) {
        String updateQuery = "UPDATE UserDetails SET UserImageName = ? WHERE UserID = ? AND LoginStatus = 1";

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try (Connection conn = getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

                    pstmt.setString(1, fileName);
                    pstmt.setInt(2, userId);
                    int affectedRows = pstmt.executeUpdate();

                    if (affectedRows > 0) {
                        Log.d("DatabaseHelper", "Profile image updated successfully.");
                    } else {
                        Log.e("DatabaseHelper", "User not found or not active.");
                    }

                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "SQL Exception: " + e.getMessage());
                }
                return null;
            }
        }.execute();
    }


    public static void fetchUserProfileImage(int userId, ProfileImageListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String imageName = null;
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement("SELECT UserImageName FROM UserDetails WHERE UserID = ? AND LoginStatus = 1")) {

                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        imageName = rs.getString("UserImageName");
                    }
                    rs.close();
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return imageName;
            }

            @Override
            protected void onPostExecute(String imageName) {
                if (imageName != null && !imageName.isEmpty()) {
                    listener.onProfileImageFetched(imageName);
                } else {
                    listener.onProfileImageFetched(null);
                }
            }
        }.execute();
    }

    // Listener Interface for Callback
    public interface ProfileImageListener {
        void onProfileImageFetched(String imageName);
    }



    public static void UserWiseSubjectSelect(Context context, String QryStatus, String UserID, UserWiseSubjectResultListener listener) {
        new AsyncTask<Void, Void, List<UserWiseSubject>>() {
            @Override
            protected List<UserWiseSubject> doInBackground(Void... voids) {
                List<UserWiseSubject> userWiseSubjectList = new ArrayList<>();
                List<UserWiseSubject> filteredSubjects = new ArrayList<>(); // ✅ This will store only the correct user's subjects

                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWiseSubjectSelect for UserID: " + UserID);
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserSubjectInsertUpdateSelect(?,?,?,?,?,?)}";
                        PreparedStatement stmt = connection.prepareStatement(query);

                        stmt.setString(1, QryStatus);
                        stmt.setInt(2, 0);
                        stmt.setInt(3, 0);
                        stmt.setString(4, UserID);
                        stmt.setString(5, "");
                        stmt.setString(6, "");

                        Log.d("DatabaseHelper", "🔍 Executing stored procedure for UserID: " + UserID);
                        ResultSet rs = stmt.executeQuery();

                        while (rs.next()) {
                            String retrievedUserId = rs.getString("UserId");
                            String subjectName = rs.getString("subjectname");

                            if (retrievedUserId != null && subjectName != null) {
                                UserWiseSubject userSubject = new UserWiseSubject();
                                userSubject.setUserId(retrievedUserId);
                                userSubject.setSubjectName(subjectName);
                                userSubject.setUserwiseSubjectId(rs.getString("UserwiseSubjectId"));
                                userSubject.setSubjectId(rs.getString("SubjectId"));
                                userSubject.setSelfReferralCode(rs.getString("selfreferralcode"));
                                userSubject.setMobileNo(rs.getString("mobileno"));
                                userSubject.setUsername(rs.getString("username"));

                                userWiseSubjectList.add(userSubject);
                            }
                        }

                        // ✅ Manually filter only subjects that match the given UserID
                        for (UserWiseSubject subject : userWiseSubjectList) {
                            if (subject.getUserId().equals(UserID)) {
                                filteredSubjects.add(subject);
                            }
                        }

                        Log.d("DatabaseHelper", "✅ Final filtered subjects for UserID: " + UserID + " -> " + filteredSubjects.size());

                        rs.close();
                        stmt.close();
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }

                return filteredSubjects; // ✅ Return only the filtered subjects
            }

            @Override
            protected void onPostExecute(List<UserWiseSubject> userWiseSubjectList) {
                listener.onQueryResult(userWiseSubjectList);
            }
        }.execute();
    }



    public static void userSubjectInsert(Context context, int qryStatus,Integer userId, Integer subjectId, String selfReferralCode, DatabaseCallback callback) {
        String query = "{CALL sp_UserSubjectInsertUpdateSelect(?, ?, ?, ?, ?, ?)}"; // Use {CALL ...} for stored procedures

        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(query)) { // Use CallableStatement



            // Set input parameters
            stmt.setInt(1, qryStatus);
            stmt.setInt(2, 0);  // Hardcoded UserwiseSubjectId
            stmt.setInt(3, userId);
            stmt.setInt(4, subjectId);
            stmt.setString(5, selfReferralCode);

            // Ensure selfReferralCode is NEVER NULL
            if (selfReferralCode == null || selfReferralCode.trim().isEmpty()) {
                selfReferralCode = "";  // Assign an empty string instead of null
            }
            stmt.setString(5, selfReferralCode); // Always set a valid string

            stmt.registerOutParameter(6, Types.VARCHAR);

            // Execute
            boolean isResultSet = stmt.execute();

            if (qryStatus == 3 || qryStatus == 4) {
                List<Map<String, String>> result = new ArrayList<>();
                try (ResultSet rs = stmt.getResultSet()) {
                    while (rs.next()) {
                        Map<String, String> row = new HashMap<>();
                        row.put("UserwiseSubjectId", rs.getString("UserwiseSubjectId"));
                        row.put("SubjectId", rs.getString("SubjectId"));
                        row.put("UserId", rs.getString("UserId"));
                        row.put("selfreferralcode", rs.getString("selfreferralcode"));
                        row.put("Active", rs.getString("Active"));
                        row.put("entrydate", rs.getString("entrydate"));
                        row.put("IsActive", rs.getString("IsActive"));
                        result.add(row);
                    }
                }
                callback.onSuccess(result);
            } else {
                // Retrieve message from the stored procedure
                String message = stmt.getString(6);
                callback.onMessage(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            callback.onError("Database error: " + e.getMessage());
        }
    }


    public static void UserWiseSubjectDelete(Context context, String userWiseSubjectId, String userId, DatabaseCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String message = "";

                // ✅ Validate Subject ID before executing the query
                if (userWiseSubjectId == null || userWiseSubjectId.equals("0")) {
                    Log.e("DatabaseHelper", "❌ Invalid Subject ID for deletion!");
                    return "Invalid Subject ID for deletion!";
                }

                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWiseSubjectDelete...");
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserSubjectInsertUpdateSelect(?,?,?,?,?,?)}";
                        CallableStatement stmt = connection.prepareCall(query);

                        stmt.setInt(1, 5); // 5 = Delete operation
                        stmt.setInt(2, Integer.parseInt(userWiseSubjectId)); // UserwiseSubjectId
                        stmt.setInt(3, 0); // SubjectId (not needed for delete)
                        stmt.setInt(4, Integer.parseInt(userId)); // UserId
                        stmt.setString(5, ""); // selfreferralcode (not needed)
                        stmt.registerOutParameter(6, Types.VARCHAR); // ✅ FIX: Changed NVARCHAR to VARCHAR

                        Log.d("DatabaseHelper", "🔍 Deleting Subject: UserWiseSubjectId=" + userWiseSubjectId + ", UserId=" + userId);
                        stmt.execute();

                        message = stmt.getString(6);
                        Log.d("DatabaseHelper", "✅ Delete Response: " + message);

                        stmt.close();
                        connection.close();
                    } else {
                        Log.e("DatabaseHelper", "❌ DB Connection Failed!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return message;
            }

            @Override
            protected void onPostExecute(String message) {
                if (callback != null) {
                    callback.onMessage(message);
                }
            }
        }.execute();
    }




    public static void UserWiseEducationInsert(Context context, String QryStatus, Integer userId, Integer educationLevelId,
                                               String institutionName, Integer passingYear, String selfReferralCode,
                                               DatabaseCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String messageOutput = "Operation failed";
                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWiseEducationInsert...");
                    Connection connection = getConnection();

                    if (connection != null) {
                        Log.d("DatabaseHelper", "📌 Insert Query -> UserID: " + userId +
                                ", EducationLevelID: " + educationLevelId +
                                ", Institution: " + institutionName +
                                ", Passing Year: " + passingYear);

                        String query = "{call sp_UserEducationInsertUpdateSelect(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                        CallableStatement stmt = connection.prepareCall(query);

                        stmt.setInt(1, Integer.parseInt(QryStatus)); // 1 = Insert
                        stmt.setNull(2, Types.INTEGER); // NULL for UserwiseEducationId
                        stmt.setInt(3, userId);
                        stmt.setInt(4, educationLevelId);
                        stmt.setString(5, institutionName);
                        stmt.setInt(6, passingYear);
                        stmt.setNull(7, Types.INTEGER); // NULL for CityID
                        stmt.setString(8, selfReferralCode);
                        stmt.registerOutParameter(9, Types.VARCHAR); // Output message

                        Log.d("DatabaseHelper", "🔍 Executing insert query for UserID: " + userId);
                        stmt.executeUpdate();  // ✅ FIXED: Use executeUpdate()

                        messageOutput = stmt.getString(9); // ✅ Retrieve the output message
                        Log.d("DatabaseHelper", "✅ Insert Response: " + messageOutput);

                        stmt.close();
                        connection.close();
                    } else {
                        Log.e("DatabaseHelper", "❌ DB Connection Failed!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return messageOutput;
            }

            @Override
            protected void onPostExecute(String messageOutput) {
                if (callback != null) {
                    callback.onMessage(messageOutput);
                }
            }
        }.execute();
    }



    public static void UserWiseEducationSelect(Context context, String qryStatus, String userId, UserWiseEducationResultListener listener) {
        new AsyncTask<Void, Void, List<UserWiseEducation>>() {
            @Override
            protected List<UserWiseEducation> doInBackground(Void... voids) {
                List<UserWiseEducation> userWiseEducationList = new ArrayList<>();
                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWiseEducationSelect...");
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserEducationInsertUpdateSelect(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                        PreparedStatement stmt = connection.prepareStatement(query);

                        stmt.setString(1, qryStatus); // Query Status (4 = Select by UserID)
                        stmt.setInt(2, 0); // UserwiseEducationId (Not needed for select)
                        stmt.setString(3, userId); // UserId
                        stmt.setNull(4, Types.INTEGER);
                        stmt.setNull(5, Types.VARCHAR);
                        stmt.setNull(6, Types.INTEGER);
                        stmt.setNull(7, Types.INTEGER);
                        stmt.setNull(8, Types.VARCHAR);
                        stmt.setNull(9, Types.VARCHAR);

                        Log.d("DatabaseHelper", "🔍 Executing query for UserID: " + userId);
                        boolean hasResults = stmt.execute();
                        Log.d("DatabaseHelper", "🔍 Query executed. Has results: " + hasResults);

                        if (hasResults) {
                            ResultSet rs = stmt.getResultSet();
                            int count = 0;
                            while (rs.next()) {
                                UserWiseEducation edu = new UserWiseEducation();
                                edu.setUserwiseEducationId(rs.getString("UserwiseEducationId"));
                                edu.setUserId(rs.getString("UserId"));
                                edu.setInstitutionName(rs.getString("InstitutionName"));
                                edu.setEducationLevelName(rs.getString("EducationLevelname"));
                                edu.setPassingYear(rs.getString("PasssingYear"));

                                Log.d("DatabaseHelper", "✅ Retrieved: " + edu.getInstitutionName() + " | Year: " + edu.getPassingYear());
                                userWiseEducationList.add(edu);
                                count++;
                            }
                            rs.close();

                            Log.d("DatabaseHelper", "✅ Total Records Fetched: " + count);
                        } else {
                            Log.e("DatabaseHelper", "❌ No data found for UserID: " + userId);
                        }

                        stmt.close();
                        connection.close();
                    } else {
                        Log.e("DatabaseHelper", "❌ DB Connection Failed!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return userWiseEducationList;
            }

            @Override
            protected void onPostExecute(List<UserWiseEducation> userWiseEducationList) {
                listener.onQueryResult(userWiseEducationList);
            }
        }.execute();
    }


    public static void getEducationLevels(Context context, DatabaseHelper.EducationLevelsCallback callback) {
        new AsyncTask<Void, Void, List<Map<String, String>>>() {
            @Override
            protected List<Map<String, String>> doInBackground(Void... voids) {
                List<Map<String, String>> educationLevels = new ArrayList<>();
                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for Education Levels...");
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "SELECT EducationLevelID, EducationLevelName FROM EducationLevel WHERE active = 'true' ORDER BY EducationLevelName";
                        PreparedStatement stmt = connection.prepareStatement(query);
                        ResultSet rs = stmt.executeQuery();

                        while (rs.next()) {
                            Map<String, String> education = new HashMap<>();
                            education.put("EducationLevelId", rs.getString("EducationLevelID"));
                            education.put("EducationLevelName", rs.getString("EducationLevelName"));
                            educationLevels.add(education);
                        }

                        Log.d("DatabaseHelper", "✅ Total Education Levels Fetched: " + educationLevels.size());
                        rs.close();
                        stmt.close();
                        connection.close();
                    } else {
                        Log.e("DatabaseHelper", "❌ Failed to connect to DB for education levels!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return educationLevels;
            }

            @Override
            protected void onPostExecute(List<Map<String, String>> educationLevels) {
                if (callback != null) {
                    callback.onSuccess(educationLevels);
                }
            }
        }.execute();
    }


    public static void getCourses(Context context, String educationLevelID, DatabaseHelper.CoursesCallback callback) {
        new AsyncTask<Void, Void, List<Map<String, String>>>() {
            @Override
            protected List<Map<String, String>> doInBackground(Void... voids) {
                List<Map<String, String>> courses = new ArrayList<>();
                try {
                    Log.d("DatabaseHelper", "🛠️ Fetching courses for EducationLevelID: " + educationLevelID);
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "SELECT CourseID, CourseName FROM Courses WHERE EducationLevelID = ? AND active = 'true' ORDER BY CourseName";
                        PreparedStatement stmt = connection.prepareStatement(query);
                        stmt.setString(1, educationLevelID);
                        ResultSet rs = stmt.executeQuery();

                        while (rs.next()) {
                            Map<String, String> course = new HashMap<>();
                            course.put("CourseID", rs.getString("CourseID"));
                            course.put("CourseName", rs.getString("CourseName"));
                            courses.add(course);
                        }

                        Log.d("DatabaseHelper", "✅ Total Courses Fetched: " + courses.size());
                        rs.close();
                        stmt.close();
                        connection.close();
                    } else {
                        Log.e("DatabaseHelper", "❌ Failed to connect to DB for courses!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return courses;
            }

            @Override
            protected void onPostExecute(List<Map<String, String>> courses) {
                if (callback != null) {
                    callback.onSuccess(courses);
                }
            }
        }.execute();
    }




    public static void UserWiseWorkExperienceInsert(Context context, String qryStatus, Integer userId, Integer professionId,
                                                    Integer curPreExperience, Integer designationId, String institutionName,
                                                    Integer cityId, Integer workExperienceId, String selfReferralCode,
                                                    WorkExperienceCallback callback) {  // ✅ Use the new callback
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String messageOutput = "";
                Connection connection = null;
                CallableStatement stmt = null;
                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWiseWorkExperienceInsert...");
                    connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserWorkExperienceInsertUpdateSelect(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                        stmt = connection.prepareCall(query);

                        stmt.setInt(1, Integer.parseInt(qryStatus)); // QryStatus (1=Insert)
                        stmt.setNull(2, Types.INTEGER); // UserwiseWorkExperienceId (NULL for insert)
                        stmt.setInt(3, userId); // UserId
                        stmt.setInt(4, professionId != null ? professionId : Types.NULL); // ✅ Handle potential null
                        stmt.setInt(5, curPreExperience); // Current/Previous Experience
                        stmt.setInt(6, designationId != null ? designationId : Types.NULL); // ✅ Handle potential null
                        stmt.setString(7, institutionName); // Institution Name
                        stmt.setInt(8, cityId != null ? cityId : Types.NULL); // ✅ Handle potential null
                        stmt.setInt(9, workExperienceId != null ? workExperienceId : Types.NULL); // ✅ Handle potential null
                        stmt.setString(10, selfReferralCode != null ? selfReferralCode : ""); // ✅ Handle potential null

                        stmt.registerOutParameter(11, Types.VARCHAR); // ✅ Output Message

                        Log.d("DatabaseHelper", "🔍 Executing insert query...");
                        stmt.execute();

                        messageOutput = stmt.getString(11);
                        Log.d("DatabaseHelper", "✅ Insert Response from DB: " + messageOutput);
                    } else {
                        Log.e("DatabaseHelper", "❌ DB Connection Failed!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                } finally {
                    try {
                        if (stmt != null) stmt.close();
                        if (connection != null) connection.close();
                    } catch (SQLException e) {
                        Log.e("DatabaseHelper", "❌ Error closing DB resources: " + e.getMessage());
                    }
                }
                return messageOutput;
            }

            @Override
            protected void onPostExecute(String messageOutput) {
                if (callback != null) {
                    callback.onMessage(messageOutput);  // ✅ Use WorkExperienceCallback
                }
            }
        }.execute();
    }



    public static void UserWiseWorkExperienceSelect(Context context, String QryStatus, String UserID, WorkExperienceCallback callback) {
        new AsyncTask<Void, Void, List<UserWiseWorkExperience>>() {
            @Override
            protected List<UserWiseWorkExperience> doInBackground(Void... voids) {
                List<UserWiseWorkExperience> workExperienceList = new ArrayList<>();
                List<UserWiseWorkExperience> filteredExperience = new ArrayList<>(); // ✅ To store only correct user data

                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWiseWorkExperienceSelect. UserID: " + UserID);
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserWorkExperienceInsertUpdateSelect(?,?,?,?,?,?,?,?,?,?,?)}";
                        PreparedStatement stmt = connection.prepareStatement(query);

                        stmt.setString(1, QryStatus);
                        stmt.setInt(2, 0);
                        stmt.setString(3, UserID);
                        stmt.setInt(4, 0);
                        stmt.setInt(5, 0);
                        stmt.setInt(6, 0);
                        stmt.setString(7, "");
                        stmt.setInt(8, 0);
                        stmt.setInt(9, 0);
                        stmt.setString(10, "");
                        stmt.setString(11, "");

                        Log.d("DatabaseHelper", "🔍 Executing stored procedure for UserID: " + UserID);
                        ResultSet rs = stmt.executeQuery();

                        while (rs.next()) {
                            String retrievedUserId = rs.getString("UserId");
                            String experience = rs.getString("WorkExperience");

                            if (retrievedUserId != null && experience != null) {
                                UserWiseWorkExperience workExp = new UserWiseWorkExperience(
                                        retrievedUserId,
                                        rs.getString("InstitutionName"),
                                        rs.getString("DesignationName"),
                                        experience,
                                        rs.getString("CurPreExperience"),
                                        rs.getString("ProfessionId"),
                                        rs.getString("ProfessionName")
                                );

                                workExperienceList.add(workExp);
                            }
                        }

                        // ✅ Manually filter only experiences that match the given UserID
                        for (UserWiseWorkExperience exp : workExperienceList) {
                            if (exp.getUserId().equals(UserID)) {
                                filteredExperience.add(exp);
                            }
                        }

                        Log.d("DatabaseHelper", "✅ Final filtered work experiences for UserID: " + UserID + " -> " + filteredExperience.size());

                        rs.close();
                        stmt.close();
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }

                return filteredExperience; // ✅ Return only the filtered experiences
            }

            @Override
            protected void onPostExecute(List<UserWiseWorkExperience> workExperienceList) {
                callback.onSuccess(workExperienceList);
            }
        }.execute();
    }





    public static void UserWiseGradesInsert(Context context, String QryStatus, Integer userId, Integer currentProfession,
                                            Integer gradeId, Integer subjectId, String selfReferralCode,
                                            DatabaseCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String messageOutput = "";
                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWiseGradesInsert...");
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserGradesInsertUpdateSelect(?, ?, ?, ?, ?, ?, ?, ?)}"; // ✅ Now includes 8th parameter
                        CallableStatement stmt = connection.prepareCall(query);

                        stmt.setInt(1, Integer.parseInt(QryStatus)); // QryStatus (1=Insert)
                        stmt.setNull(2, Types.INTEGER); // UserwiseGradesId (NULL for insert)
                        stmt.setInt(3, userId); // UserId
                        stmt.setInt(4, currentProfession); // Current Profession
                        stmt.setInt(5, gradeId); // GradeID
                        stmt.setInt(6, subjectId); // SubjectID
                        stmt.setString(7, selfReferralCode); // SelfReferralCode

                        // ✅ Register OUTPUT parameter for @Message
                        stmt.registerOutParameter(8, Types.VARCHAR); // Fix: Correctly register the OUTPUT parameter

                        Log.d("DatabaseHelper", "🔍 Executing query for UserID: " + userId);
                        stmt.execute(); // ✅ Execute the stored procedure

                        // ✅ Retrieve OUT parameter value
                        messageOutput = stmt.getString(8); // Get the message from stored procedure

                        Log.d("DatabaseHelper", "✅ Insert Response: " + messageOutput);

                        stmt.close();
                        connection.close();
                    } else {
                        Log.e("DatabaseHelper", "❌ DB Connection Failed!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return messageOutput;
            }

            @Override
            protected void onPostExecute(String messageOutput) {
                if (callback != null) {
                    callback.onMessage(messageOutput);
                }
            }
        }.execute();
    }


    public static void UserWiseGradesSelect(Context context, String QryStatus, String UserID, UserWiseGradesResultListener listener) {
        new AsyncTask<Void, Void, List<UserWiseGrades>>() {
            @Override
            protected List<UserWiseGrades> doInBackground(Void... voids) {
                List<UserWiseGrades> userWiseGradesList = new ArrayList<>();
                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWiseGradesSelect...");
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserGradesInsertUpdateSelect(?,?,?,?,?,?,?,?)}"; // ✅ Adjusted parameter count
                        CallableStatement stmt = connection.prepareCall(query);

                        stmt.setString(1, QryStatus); // Query Type (4 = Select by UserId)
                        stmt.setNull(2, Types.INTEGER); // UserwiseGradesId
                        stmt.setString(3, UserID); // UserId
                        stmt.setNull(4, Types.INTEGER); // CurrentProfession (Dropped)
                        stmt.setNull(5, Types.INTEGER); // GradeID
                        stmt.setNull(6, Types.INTEGER); // SubjectID
                        stmt.setNull(7, Types.VARCHAR); // SelfReferralCode
                        stmt.registerOutParameter(8, Types.VARCHAR); // ✅ Output parameter

                        Log.d("DatabaseHelper", "🔍 Executing query for UserID: " + UserID);
                        ResultSet rs = stmt.executeQuery();

                        if (!rs.isBeforeFirst()) { // ✅ No data found
                            Log.d("DatabaseHelper", "⚠️ No grades data found in DB!");
                        }

                        while (rs.next()) {
                            UserWiseGrades grade = new UserWiseGrades(
                                    rs.getString("UserwiseGradesId"),
                                    rs.getString("UserId"),
                                    rs.getString("selfreferralcode"),
                                    rs.getString("mobileno"),
                                    rs.getString("SubjectName"), // ✅ Removed CurrentProfession
                                    rs.getString("Subjectid"),
                                    rs.getString("Gradename"),
                                    rs.getString("username"),
                                    rs.getString("IsActive")
                            );

                            Log.d("DatabaseHelper", "✅ Retrieved Subject: " + grade.getSubjectName() + " | Grade: " + grade.getGradename() + " | UserID: " + grade.getUserId());
                            userWiseGradesList.add(grade);
                        }

                        // ✅ Retrieve the output message from @Message
                        String messageOutput = stmt.getString(8);
                        Log.d("DatabaseHelper", "✅ DB Response: " + messageOutput);

                        rs.close();
                        stmt.close();
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return userWiseGradesList;
            }

            @Override
            protected void onPostExecute(List<UserWiseGrades> userWiseGradesList) {
                listener.onQueryResult(userWiseGradesList);
            }
        }.execute();
    }


    public static void UserWiseCertificateInsert(
            Context context, String QryStatus, int userId, String certificateName,
            String issuingOrganization, String credentialURL, int issueYear,
            String certificateFileName, String selfReferralCode, DatabaseCallback callback) {

        // ✅ Ensure `certificateFileName` is never null
        final String finalCertificateFileName =
                (certificateFileName == null || certificateFileName.trim().isEmpty()) ? "No_File" : certificateFileName;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String messageOutput = "Operation failed";
                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWiseCertificateInsert...");
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserCertificateInsertUpdateSelect(?,?,?,?,?,?,?,?,?,?)}";
                        CallableStatement stmt = connection.prepareCall(query);

                        stmt.setString(1, QryStatus);
                        stmt.setInt(2, 0);
                        stmt.setString(3, certificateName);
                        stmt.setString(4, issuingOrganization);
                        stmt.setString(5, credentialURL);
                        stmt.setInt(6, issueYear);

                        // ✅ Ensure we send a valid `certificateFileName`
                        Log.d("DatabaseHelper", "📂 finalCertificateFileName (Before Insert): " + finalCertificateFileName);
                        stmt.setString(7, finalCertificateFileName);

                        stmt.setInt(8, userId);
                        stmt.setString(9, selfReferralCode);
                        stmt.registerOutParameter(10, Types.VARCHAR);

                        Log.d("DatabaseHelper", "🔍 Query Params: ");
                        Log.d("DatabaseHelper", "  - QryStatus: " + QryStatus);
                        Log.d("DatabaseHelper", "  - UserID: " + userId);
                        Log.d("DatabaseHelper", "  - Certificate Name: " + certificateName);
                        Log.d("DatabaseHelper", "  - Issuing Org: " + issuingOrganization);
                        Log.d("DatabaseHelper", "  - Credential URL: " + credentialURL);
                        Log.d("DatabaseHelper", "  - Issue Year: " + issueYear);
                        Log.d("DatabaseHelper", "  - Certificate File Name: " + finalCertificateFileName);
                        Log.d("DatabaseHelper", "  - Self Referral Code: " + selfReferralCode);

                        stmt.execute();

                        messageOutput = stmt.getString(10);
                        Log.d("DatabaseHelper", "✅ Insert Response: " + messageOutput);

                        stmt.close();
                        connection.close();
                    } else {
                        Log.e("DatabaseHelper", "❌ DB Connection Failed!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return messageOutput;
            }

            @Override
            protected void onPostExecute(String messageOutput) {
                if (callback != null) {
                    callback.onMessage(messageOutput);
                }
            }
        }.execute();
    }

    public static void UserWiseCertificateSelect(Context context, int userId, DatabaseCallback callback) {
        new AsyncTask<Void, Void, List<Map<String, String>>>() {
            @Override
            protected List<Map<String, String>> doInBackground(Void... voids) {
                List<Map<String, String>> resultList = new ArrayList<>();
                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWiseCertificateSelect...");
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserCertificateInsertUpdateSelect(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                        CallableStatement stmt = connection.prepareCall(query);

                        stmt.setInt(1, 4); // Query Type: Select by UserID
                        stmt.setNull(2, Types.INTEGER); // UserwiseCertificateId (Not required for select)
                        stmt.setNull(3, Types.VARCHAR); // CertificateName
                        stmt.setNull(4, Types.VARCHAR); // IssuingOrganization
                        stmt.setNull(5, Types.VARCHAR); // CredentialURL
                        stmt.setNull(6, Types.INTEGER); // IssueYear
                        stmt.setNull(7, Types.VARCHAR); // CertificateFileName
                        stmt.setInt(8, userId); // UserId
                        stmt.setNull(9, Types.VARCHAR); // SelfReferralCode
                        stmt.registerOutParameter(10, Types.VARCHAR); // Output Message

                        Log.d("DatabaseHelper", "🔍 Executing stored procedure for UserID: " + userId);
                        ResultSet rs = stmt.executeQuery();

                        while (rs.next()) {
                            Map<String, String> row = new HashMap<>();
                            row.put("UserwiseCertificateId", rs.getString("UserwiseCertificateId"));
                            row.put("CertificateName", rs.getString("CertificateName"));
                            row.put("IssuingOrganization", rs.getString("IssueingOrganization"));
                            row.put("CredentialURL", rs.getString("CredentialURL"));
                            row.put("IssueYear", rs.getString("IssueYear"));
                            row.put("UserId", rs.getString("UserId"));
                            row.put("SelfReferralCode", rs.getString("selfreferralcode"));
                            row.put("IsActive", rs.getString("isActive"));

                            resultList.add(row);
                        }

                        String messageOutput = stmt.getString(10);
                        Log.d("DatabaseHelper", "✅ Select Response: " + messageOutput);

                        rs.close();
                        stmt.close();
                        connection.close();
                    } else {
                        Log.e("DatabaseHelper", "❌ DB Connection Failed!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return resultList;
            }

            @Override
            protected void onPostExecute(List<Map<String, String>> result) {
                if (callback != null) {
                    if (!result.isEmpty()) {
                        callback.onSuccess(result);
                    } else {
                        callback.onMessage("No certificates found for this user.");
                    }
                }
            }
        }.execute();
    }




    public static void UserWiseAwardInsert(
            Context context, String QryStatus, int userId, int awardTitleID,
            String awardingOrganization, int issueYear, String remarks,
            String awardFileName, String selfReferralCode, DatabaseCallback callback) {

        // ✅ Ensure `awardFileName` is explicitly a valid string
        final String finalAwardFileName =
                (awardFileName == null || awardFileName.trim().isEmpty()) ? "No_File" : awardFileName.trim();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String messageOutput = "Operation failed";
                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWiseAwardInsert...");
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserAwardInsertUpdateSelect(?,?,?,?,?,?,?,?,?,?)}";
                        CallableStatement stmt = connection.prepareCall(query);

                        stmt.setString(1, QryStatus);
                        stmt.setNull(2, Types.INTEGER); // UserwiseAwardId (NULL for insert)
                        stmt.setInt(3, awardTitleID); // AwardTitleID
                        stmt.setString(4, awardingOrganization); // Awarding Organization
                        stmt.setInt(5, issueYear); // Issue Year
                        stmt.setString(6, remarks); // Remarks

                        // ✅ Ensure we send a valid `awardFileName`
                        Log.d("DatabaseHelper", "📂 finalAwardFileName (Before Insert): '" + finalAwardFileName + "'");
                        stmt.setString(7, finalAwardFileName.isEmpty() ? "No_File" : finalAwardFileName);

                        stmt.setInt(8, userId); // User ID
                        stmt.setString(9, selfReferralCode); // Self Referral Code
                        stmt.registerOutParameter(10, Types.VARCHAR); // Output Message

                        Log.d("DatabaseHelper", "🔍 Query Params: ");
                        Log.d("DatabaseHelper", "  - QryStatus: " + QryStatus);
                        Log.d("DatabaseHelper", "  - UserID: " + userId);
                        Log.d("DatabaseHelper", "  - Award Title ID: " + awardTitleID);
                        Log.d("DatabaseHelper", "  - Awarding Org: " + awardingOrganization);
                        Log.d("DatabaseHelper", "  - Issue Year: " + issueYear);
                        Log.d("DatabaseHelper", "  - Remarks: " + remarks);
                        Log.d("DatabaseHelper", "  - Award File Name: '" + finalAwardFileName + "'");
                        Log.d("DatabaseHelper", "  - Self Referral Code: " + selfReferralCode);

                        stmt.execute();

                        messageOutput = stmt.getString(10);
                        Log.d("DatabaseHelper", "✅ Insert Response: " + messageOutput);

                        stmt.close();
                        connection.close();
                    } else {
                        Log.e("DatabaseHelper", "❌ DB Connection Failed!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return messageOutput;
            }

            @Override
            protected void onPostExecute(String messageOutput) {
                if (callback != null) {
                    callback.onMessage(messageOutput);
                }
            }
        }.execute();
    }


    public static void UserWiseAwardSelect(
            Context context, int userId, DatabaseCallback callback) {

        new AsyncTask<Void, Void, List<Map<String, String>>>() {
            @Override
            protected List<Map<String, String>> doInBackground(Void... voids) {
                List<Map<String, String>> awardsList = new ArrayList<>();
                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWiseAwardSelect...");
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserAwardInsertUpdateSelect(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                        CallableStatement stmt = connection.prepareCall(query);

                        stmt.setInt(1, 4); // Query Type: Select by UserID
                        stmt.setNull(2, Types.INTEGER); // UserwiseAwardId
                        stmt.setNull(3, Types.INTEGER); // AwardTitleID
                        stmt.setNull(4, Types.VARCHAR); // AwardingOrganization
                        stmt.setNull(5, Types.INTEGER); // IssueYear
                        stmt.setNull(6, Types.VARCHAR); // Remarks
                        stmt.setNull(7, Types.VARCHAR); // AwardFileName
                        stmt.setInt(8, userId); // UserId
                        stmt.setNull(9, Types.VARCHAR); // SelfReferralCode
                        stmt.registerOutParameter(10, Types.VARCHAR); // Output Message

                        Log.d("DatabaseHelper", "🔍 Executing stored procedure for UserID: " + userId);
                        ResultSet rs = stmt.executeQuery();

                        while (rs.next()) {
                            Map<String, String> row = new HashMap<>();
                            row.put("UserwiseAwardId", rs.getString("UserwiseAwardId"));
                            row.put("AwardTitleName", rs.getString("AwardTitleName"));
                            row.put("AwardingOrganization", rs.getString("AwardingOrganization"));
                            row.put("Remarks", rs.getString("Remarks"));
                            row.put("IssueYear", rs.getString("IssueYear"));
                            row.put("AwardFileName", rs.getString("AwardFileName"));

                            awardsList.add(row);

                            // ✅ LOG EACH AWARD'S DETAILS
                            Log.d("DatabaseHelper", "🏆 Award Retrieved: ");
                            Log.d("DatabaseHelper", "  - Award ID: " + row.get("UserwiseAwardId"));
                            Log.d("DatabaseHelper", "  - Title: " + row.get("AwardTitleName"));
                            Log.d("DatabaseHelper", "  - Organization: " + row.get("AwardingOrganization"));
                            Log.d("DatabaseHelper", "  - Year: " + row.get("IssueYear"));
                            Log.d("DatabaseHelper", "  - Remarks: " + row.get("Remarks"));
                            Log.d("DatabaseHelper", "  - File Name: " + row.get("AwardFileName"));
                        }

                        String messageOutput = stmt.getString(10);
                        Log.d("DatabaseHelper", "✅ Select Response: " + messageOutput);

                        rs.close();
                        stmt.close();
                        connection.close();
                    } else {
                        Log.e("DatabaseHelper", "❌ DB Connection Failed!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return awardsList;
            }

            @Override
            protected void onPostExecute(List<Map<String, String>> result) {
                if (callback != null) {
                    if (!result.isEmpty()) {
                        callback.onSuccess(result);
                    } else {
                        callback.onMessage("No awards found for this user.");
                    }
                }
            }
        }.execute();
    }


    public static void FetchAllAwards(Context context, DatabaseCallback callback) {
        new AsyncTask<Void, Void, List<Map<String, String>>>() {
            @Override
            protected List<Map<String, String>> doInBackground(Void... voids) {
                List<Map<String, String>> awardsList = new ArrayList<>();
                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for FetchAllAwards...");
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserAwardInsertUpdateSelect(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                        CallableStatement stmt = connection.prepareCall(query);

                        stmt.setInt(1, 5); // Query Type: Select All Awards
                        stmt.setNull(2, Types.INTEGER);
                        stmt.setNull(3, Types.INTEGER);
                        stmt.setNull(4, Types.VARCHAR);
                        stmt.setNull(5, Types.INTEGER);
                        stmt.setNull(6, Types.VARCHAR);
                        stmt.setNull(7, Types.VARCHAR);
                        stmt.setNull(8, Types.INTEGER);
                        stmt.setNull(9, Types.VARCHAR);
                        stmt.registerOutParameter(10, Types.VARCHAR);

                        ResultSet rs = stmt.executeQuery();

                        while (rs.next()) {
                            Map<String, String> row = new HashMap<>();
                            row.put("UserwiseAwardId", rs.getString("UserwiseAwardId"));
                            row.put("AwardTitleName", rs.getString("AwardTitleName"));
                            row.put("AwardingOrganization", rs.getString("AwardingOrganization"));
                            row.put("Remarks", rs.getString("Remarks"));
                            row.put("IssueYear", rs.getString("IssueYear"));
                            row.put("AwardFileName", rs.getString("AwardFileName"));

                            awardsList.add(row);
                        }

                        rs.close();
                        stmt.close();
                        connection.close();
                    } else {
                        Log.e("DatabaseHelper", "❌ DB Connection Failed!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }
                return awardsList;
            }

            @Override
            protected void onPostExecute(List<Map<String, String>> result) {
                if (callback != null) {
                    if (!result.isEmpty()) {
                        callback.onSuccess(result);
                    } else {
                        callback.onMessage("No awards found.");
                    }
                }
            }
        }.execute();
    }


    public static void UserWisePromotionalMediaInsert(
            Context context, String QryStatus, int userId, String promotionalCaption,
            String mediaType, String remarks, String promotionalMediaFileName,
            String selfReferralCode, DatabaseCallback callback) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String messageOutput = "Operation failed";  // Default error message
                Connection connection = null;
                CallableStatement stmt = null;
                if (connection == null) {
                    Log.e("DatabaseHelper", "❌ DB Connection is NULL! Check database credentials.");
                }


                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWisePromotionalMediaInsert...");
                    connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserPromotionalMediaInsertUpdateSelect(?,?,?,?,?,?,?,?,?)}";
                        stmt = connection.prepareCall(query);

                        stmt.setString(1, QryStatus);
                        stmt.setNull(2, Types.INTEGER);  // Always NULL for insert
                        stmt.setString(3, promotionalCaption);
                        stmt.setString(4, mediaType);
                        stmt.setString(5, remarks);
                        stmt.setString(6, promotionalMediaFileName);
                        stmt.setInt(7, userId);
                        stmt.setString(8, selfReferralCode);
                        stmt.registerOutParameter(9, Types.VARCHAR);  // Output parameter

                        // ✅ Log all parameters before execution
                        Log.d("DatabaseHelper", "📌 Executing Stored Procedure with Params:");
                        Log.d("DatabaseHelper", "  - QryStatus: " + QryStatus);
                        Log.d("DatabaseHelper", "  - UserID: " + userId);
                        Log.d("DatabaseHelper", "  - Promotional Caption: " + promotionalCaption);
                        Log.d("DatabaseHelper", "  - Media Type: " + mediaType);
                        Log.d("DatabaseHelper", "  - Remarks: " + remarks);
                        Log.d("DatabaseHelper", "  - File Name: " + promotionalMediaFileName);
                        Log.d("DatabaseHelper", "  - Self Referral Code: " + selfReferralCode);

                        // ✅ Execute Stored Procedure
                        Log.d("DatabaseHelper", "🛠️ Preparing to execute stored procedure...");
                        boolean executed = stmt.execute();
                        Log.d("DatabaseHelper", "✅ Stored procedure executed: " + executed);

                        // ✅ Fetch the output message safely
                        messageOutput = stmt.getString(9);
                        Log.d("DatabaseHelper", "📩 DB Output Message: " + messageOutput);

                    } else {
                        Log.e("DatabaseHelper", "❌ DB Connection Failed!");
                        messageOutput = "DB Connection Failed!";
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                    messageOutput = "SQL Error: " + e.getMessage();
                } finally {
                    try {
                        if (stmt != null) stmt.close();
                        if (connection != null) connection.close();
                    } catch (SQLException closeEx) {
                        Log.e("DatabaseHelper", "❌ Error closing DB resources: " + closeEx.getMessage());
                    }
                }
                return messageOutput;
            }

            @Override
            protected void onPostExecute(String messageOutput) {
                if (callback != null) {
                    callback.onMessage(messageOutput);
                }
            }
        }.execute();
    }



    public static void UserWisePromotionalMediaSelect(
            Context context, int userId, DatabaseCallback callback) {

        new AsyncTask<Void, Void, List<Map<String, String>>>() {
            @Override
            protected List<Map<String, String>> doInBackground(Void... voids) {
                List<Map<String, String>> mediaList = new ArrayList<>();
                ResultSet resultSet = null; // Declare outside try block

                try {
                    Log.d("DatabaseHelper", "🛠️ Connecting to DB for UserWisePromotionalMediaSelect...");
                    Connection connection = getConnection();

                    if (connection != null) {
                        String query = "{call sp_UserPromotionalMediaInsertUpdateSelect(?,?,?,?,?,?,?,?,?)}";
                        CallableStatement stmt = connection.prepareCall(query);

                        stmt.setInt(1, 4); // Query Type: Select by User ID
                        stmt.setNull(2, Types.INTEGER);
                        stmt.setNull(3, Types.VARCHAR);
                        stmt.setNull(4, Types.CHAR);
                        stmt.setNull(5, Types.VARCHAR);
                        stmt.setNull(6, Types.VARCHAR);
                        stmt.setInt(7, userId);
                        stmt.setNull(8, Types.VARCHAR);
                        stmt.registerOutParameter(9, Types.VARCHAR);

                        Log.d("DatabaseHelper", "🔍 Executing stored procedure for UserID: " + userId);
                        boolean hasResults = stmt.execute();  // ✅ Check if there are results
                        Log.d("DatabaseHelper", "Stored procedure executed, has result set: " + hasResults);

                        if (hasResults) {
                            resultSet = stmt.getResultSet();  // ✅ Fetch result set

                            while (resultSet != null && resultSet.next()) {
                                Map<String, String> mediaData = new HashMap<>();
                                mediaData.put("PromotionalCaption", resultSet.getString("PromotionalCaption"));
                                mediaData.put("PromotionalMediaFileName", resultSet.getString("PromotionalMediaFileName"));
                                mediaData.put("TypeOfMedia", resultSet.getString("TypeOfMedia"));
                                mediaData.put("Remarks", resultSet.getString("Remarks"));
                                mediaData.put("UserId", resultSet.getString("UserId"));
                                mediaData.put("SelfReferralCode", resultSet.getString("SelfReferralCode"));

                                mediaList.add(mediaData);

                                Log.d("DatabaseHelper", "Retrieved Media Data:");
                                for (Map.Entry<String, String> entry : mediaData.entrySet()) {
                                    Log.d("DatabaseHelper", entry.getKey() + ": " + entry.getValue());
                                }
                            }
                        } else {
                            Log.w("DatabaseHelper", "⚠️ No result set returned from stored procedure.");
                        }

                        // ✅ Fetch output message safely
                        String messageOutput;
                        try {
                            messageOutput = stmt.getString(9);
                        } catch (SQLException e) {
                            messageOutput = "No message returned.";
                        }
                        Log.d("DatabaseHelper", "✅ Select Response: " + messageOutput);

                        // ✅ Close resources safely
                        if (resultSet != null) {
                            resultSet.close();
                        }
                        stmt.close();
                        connection.close();
                    } else {
                        Log.e("DatabaseHelper", "❌ DB Connection Failed!");
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
                }

                return mediaList;
            }

            @Override
            protected void onPostExecute(List<Map<String, String>> result) {
                if (callback != null) {
                    if (!result.isEmpty()) {
                        callback.onSuccess(result);
                    } else {
                        callback.onMessage("No promotional media found for this user.");
                    }
                }
            }
        }.execute();
    }

    public static void VirtualClassInsert(Context context, int QryStatus, VirtualClassClass virtualClass, QueryResultListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String messageOutput = "Operation failed";
                try {
                    Connection connection = getConnection();
                    if (connection != null) {
                        // Adjust the query to match the stored procedure's parameters
                        String query = "{call sp_VirtualClassAPICode(?,?,?,?,?,?,?,?,?,?,?)}";
                        CallableStatement stmt = connection.prepareCall(query);

                        // Debugging Logs
                        Log.d("DatabaseHelper", "LiveId: " + virtualClass.getLiveId());
                        Log.d("DatabaseHelper", "QryStatus: " + QryStatus + ", UserId: " + virtualClass.getUserId());

                        // Setting input parameters
                        stmt.setInt(1, QryStatus);
                        stmt.setInt(2, 0); // VirtualClassAPICodeID
                        stmt.setInt(3, virtualClass.getUserId());
                        stmt.setString(4, virtualClass.getSelfReferralCode());
                        stmt.setInt(5, virtualClass.getClassId());
                        stmt.setInt(6, virtualClass.getTimeTableId());
                        stmt.setString(7, virtualClass.getLiveId() != null ? virtualClass.getLiveId() : "");
                        stmt.setString(8, "L"); // LoginStatus 'L' for Live
                        stmt.setNull(9, Types.TIMESTAMP);
                        stmt.setNull(10, Types.TIMESTAMP);

                        // Register OUTPUT parameter for messageOutput
                        stmt.registerOutParameter(11, Types.VARCHAR);  // Ensure it matches stored procedure type

                        // Execute statement
                        stmt.execute();

                        // Retrieve the output message
                        messageOutput = stmt.getString(11);

                        stmt.close();
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "SQL Error inserting virtual class: " + e.getMessage());
                }
                return messageOutput;
            }

            @Override
            protected void onPostExecute(String messageOutput) {
                Toast.makeText(context, messageOutput, Toast.LENGTH_SHORT).show();
                listener.onQueryResult(new ArrayList<>());
            }
        }.execute();
    }



    public static void VirtualClassSelect(Context context, int QryStatus, int VirtualClassAPICodeID, int userId, String LiveId, QueryResultListener listener) {
        new AsyncTask<Void, Void, List<VirtualClassClass>>() {
            @Override
            protected List<VirtualClassClass> doInBackground(Void... voids) {
                List<VirtualClassClass> classList = new ArrayList<>();
                try {
                    Connection con = getConnection();
                    if (con != null) {
                        String query = "{call sp_VirtualClassAPICode(?,?,?,?,?,?,?,?,?,?)}";
                        PreparedStatement stmt = con.prepareStatement(query);

                        stmt.setInt(1, QryStatus);
                        stmt.setInt(2, VirtualClassAPICodeID);
                        stmt.setInt(3, userId);
                        stmt.setString(4, ""); // selfreferralcode
                        stmt.setInt(5, 0); // classID
                        stmt.setInt(6, 0); // TimeTableID
                        stmt.setString(7, LiveId);
                        stmt.setString(8, ""); // LoginStatus
                        stmt.setTimestamp(9, null); // classStartTime
                        stmt.setTimestamp(10, null); // classEndTime

                        ResultSet rs = stmt.executeQuery();
                        while (rs.next()) {
                            VirtualClassClass virtualClass = new VirtualClassClass();
                            virtualClass.setVirtualClassAPICodeID(rs.getInt("VirtualClassAPICodeid"));
                            virtualClass.setUserId(rs.getInt("UserId"));
                            virtualClass.setSelfReferralCode(rs.getString("selfreferralcode"));
                            virtualClass.setClassId(rs.getInt("classID"));
                            virtualClass.setTimeTableId(rs.getInt("TimeTableID"));
                            virtualClass.setLiveId(rs.getString("LiveId"));
                            virtualClass.setClassStartTime(rs.getTimestamp("classStartTime"));
                            virtualClass.setClassEndTime(rs.getTimestamp("classEndTime"));
                            virtualClass.setFirstName(rs.getString("Name"));
                            virtualClass.setLastName(rs.getString("LastName"));
                            classList.add(virtualClass);
                        }

                        rs.close();
                        stmt.close();
                        con.close();
                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "SQL Error fetching virtual class: " + e.getMessage());
                }
                return classList;
            }

            @Override
            protected void onPostExecute(List<VirtualClassClass> classList) {
                List<Map<String, String>> mappedList = new ArrayList<>();
                for (VirtualClassClass v : classList) {
                    Map<String, String> map = new HashMap<>();
                    map.put("VirtualClassAPICodeID", String.valueOf(v.getVirtualClassAPICodeID()));
                    map.put("UserId", String.valueOf(v.getUserId()));
                    map.put("SelfReferralCode", v.getSelfReferralCode());
                    map.put("ClassId", String.valueOf(v.getClassId()));
                    map.put("TimeTableId", String.valueOf(v.getTimeTableId()));
                    map.put("LiveId", v.getLiveId());
                    map.put("ClassStartTime", String.valueOf(v.getClassStartTime()));
                    map.put("ClassEndTime", String.valueOf(v.getClassEndTime()));
                    map.put("FirstName", v.getFirstName());
                    map.put("LastName", v.getLastName());
                    mappedList.add(map);
                }
                listener.onQueryResult(mappedList);
            }
        }.execute();
    }


    public static void fetchLatestLiveId(Context context, int userId, DatabaseHelper.QueryResultListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String liveId = null;
                try {
                    Connection connection = getConnection();
                    if (connection != null) {
                        // Stored procedure or direct query to fetch LiveId
                        String query = "{call sp_VirtualClassAPICode(?,?,?,?,?,?,?,?,?,?,?)}";
                        CallableStatement stmt = connection.prepareCall(query);

                        // Setting input parameters (adjust as per stored procedure)
                        stmt.setInt(1, 3); // Assuming 3 is the QryStatus for fetching
                        stmt.setInt(2, 0); // VirtualClassAPICodeID
                        stmt.setInt(3, userId);
                        stmt.setString(4, ""); // SelfReferralCode
                        stmt.setInt(5, 0); // ClassId
                        stmt.setInt(6, 0); // TimeTableId
                        stmt.setString(7, ""); // LiveId
                        stmt.setString(8, ""); // LoginStatus
                        stmt.setNull(9, Types.TIMESTAMP); // ClassStartTime
                        stmt.setNull(10, Types.TIMESTAMP); // ClassEndTime

                        // Registering the missing OUT parameter '@messageOutput'
                        stmt.registerOutParameter(11, Types.VARCHAR); // Assuming it's a VARCHAR type

                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            liveId = rs.getString("LiveId");
                        }

                        // Fetch the output message (if needed)
                        String messageOutput = stmt.getString(11);
                        Log.d("DatabaseHelper", "Stored Procedure Output Message: " + messageOutput);

                        rs.close();
                        stmt.close();
                        connection.close();

                    }
                } catch (SQLException e) {
                    Log.e("DatabaseHelper", "SQL Error fetching LiveId: " + e.getMessage());
                }
                return liveId;
            }

            @Override
            protected void onPostExecute(String liveId) {
                listener.onQueryResult(Collections.singletonList(Collections.singletonMap("LiveId", liveId)));
            }
        }.execute();
    }


//    public static int insertNotification(int senderId, String title, String message, String type) { //added title parameter
//        String sql = "INSERT INTO Notifications (sender_id, title, message, type, created_at) " +
//                "VALUES (?, ?, ?, ?, GETDATE())";
//        try (Connection conn = getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//
//            stmt.setInt(1, senderId);
//            stmt.setString(2, title); //add title to prepared statement.
//            stmt.setString(3, message);
//            stmt.setString(4, type);
//            stmt.executeUpdate();
//
//            ResultSet rs = stmt.getGeneratedKeys();
//            if (rs.next()) {
//                return rs.getInt(1);
//            }
//
//        } catch (SQLException e) {
//            Log.e("DatabaseHelper", "insertNotification Error: " + e.getMessage() + ", SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode());
//            return -1;
//        }
//        return -1;
//    }

    public static int insertNotification(int senderId, String title, String message, String type) {
        int notificationId = -1;
        try {
            Connection con = getConnection();
            if (con != null) {
                String query = "{call InsertNotification(?, ?, ?, ?, ?)}"; // Call the stored procedure
                CallableStatement stmt = con.prepareCall(query); // Use CallableStatement

                stmt.setInt(1, senderId);
                stmt.setString(2, title);
                stmt.setString(3, message);
                stmt.setString(4, type);
                stmt.registerOutParameter(5, Types.INTEGER); // Register output parameter

                stmt.executeUpdate();

                notificationId = stmt.getInt(5); // Retrieve output parameter

                stmt.close();
                con.close();
            } else {
                Log.e("DatabaseHelper", "❌ DB connection failed!");
            }
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "❌ SQL Error: " + e.getMessage());
        }
        return notificationId;
    }

    public static void insertNotificationRead(int notificationId, int userId) {
        String sql = "INSERT INTO Notification_Reads (notification_id, user_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, notificationId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            Log.e("DatabaseHelper", "insertNotificationRead Error: " + e.getMessage());
        }
    }

    public static void markNotificationRead(int notificationId, int userId) {
        String sql = "UPDATE Notification_Reads SET read_at = DATEADD(minute, 750, GETDATE()) " +
                "WHERE notification_id = ? AND user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, notificationId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            Log.e("DatabaseHelper", "markNotificationRead Error: " + e.getMessage());
        }
    }

    public static List<Notification> getNotificationsForUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT n.id, n.title, n.message, n.type, n.created_at, r.read_at " +
                "FROM Notifications n " +
                "JOIN Notification_Reads r ON n.id = r.notification_id " +
                "WHERE r.user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(new Notification(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("message"),
                        rs.getString("type"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("read_at")
                ));
            }
            Log.d("DatabaseHelper", "Notifications fetched for user " + userId + ": " + notifications.size());
            Log.d("DatabaseHelper", "SQL Query: " + sql);
            Log.d("DatabaseHelper", "User ID: " + userId);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "getNotificationsForUser Error: " + e.getMessage());
        }
        return notifications;
    }



    public static List<UserInfoItem> getAllUserInfo(int userId) {
        Log.d("DatabaseHelper", "Fetching data for userId: " + userId);
        List<UserInfoItem> userInfoList = new ArrayList<>();

        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareCall("{call sp_UserWiseInfo(?)}");
            stmt.setInt(1, 10);

            rs = stmt.executeQuery();
            if(rs!=null)
            {
                Log.d("DatabaseHelper","uyuyttyu 1");
            }
            while (rs.next()) {
                Log.d("DatabaseHelper","inside while loop 1");
                UserInfoItem item = new UserInfoItem();
                item.setSlno(rs.getString("slno"));
                item.setHeading(rs.getString("heading"));
                item.setDescription(rs.getString("description"));
                userInfoList.add(item);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "SQL Error: " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                Log.e("DatabaseHelper", "Closing Error: " + e.getMessage(), e);
            }
        }

        Log.d("DatabaseHelper", "Total items fetched: " + userInfoList.size());
        return userInfoList;
    }


    public static List<UserInfoItem> getTeacherInfoByUserId(int userId) {
        List<UserInfoItem> userInfoList = new ArrayList<>();

        try (Connection conn = getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call sp_UserWiseInfo(?)}");
            stmt.setInt(1, userId); // ✅ Correct: pass dynamic userId

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserInfoItem item = new UserInfoItem();
                item.setSlno(rs.getString("slno"));
                item.setHeading(rs.getString("heading"));
                item.setDescription(rs.getString("description"));
                userInfoList.add(item);
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error fetching teacher info by userId: " + e.getMessage());
        }

        return userInfoList;
    }






    public static String insertOrUpdateParentGuardianInfo(
            int parentGuardianInfoID,
            int userId,
            String selfReferralCode,
            String fatherName,
            String fatherContactNo,
            String motherName,
            String motherContactNo,
            String guardianName,
            String guardianRelation,
            String guardianContactNo
    ) {
        String message = "";
        try (Connection con = getConnection();
             CallableStatement stmt = con.prepareCall("{call InsertOrUpdateParentGuardianInfo(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {


            stmt.setInt(1, parentGuardianInfoID);
            Log.d("DatabaseHelper","ParentID: 1  " + parentGuardianInfoID);
            stmt.setInt(2, userId);
            stmt.setString(3, selfReferralCode);
            stmt.setString(4, fatherName);
            stmt.setString(5, fatherContactNo);
            stmt.setString(6, motherName);
            stmt.setString(7, motherContactNo);
            stmt.setString(8, guardianName);
            stmt.setString(9, guardianRelation);
            stmt.setString(10, guardianContactNo); // Input parameter

            // Output parameter
            stmt.registerOutParameter(11, Types.VARCHAR); // Message output

            stmt.executeUpdate();

            // Retrieve the output message
            message = stmt.getString(11);
            Log.d("DatabaseHelper", "InsertOrUpdateParentGuardianInfo Message: " + message);

        } catch (SQLException e) {
            Log.e("DatabaseHelper", "InsertOrUpdateParentGuardianInfo Error: " + e.getMessage());
            message = "Error: " + e.getMessage();
        }
        return message;
    }


//
//    public static List<ParentGuardianInfo> getParentGuardianInfo(int userId, String fatherContactNo) {
//        List<ParentGuardianInfo> parentGuardianInfoList = new ArrayList<>();
//        try (Connection con = getConnection();
//             CallableStatement stmt = con.prepareCall("{call sp_GetParentGuardianInfo(?, ?)}")) {
//
//            stmt.setInt(1, userId);
//            stmt.setString(2, fatherContactNo);
//
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                ParentGuardianInfo info = new ParentGuardianInfo();
//                info.ParentGuardianInfoID = rs.getInt("ParentGuardianInfoID");
//                info.UserID = rs.getInt("UserID");
//                info.FatherName = rs.getString("FatherName");
//                info.FatherContactNo = rs.getString("FatherContactNo");
//                info.MotherName = rs.getString("MotherName");
//                info.MotherContactNo = rs.getString("MotherContactNo");
//                info.GuardianName = rs.getString("GuardianName");
//                info.GuardianRelation = rs.getString("GuardianRelation");
//                info.GuardianContactNo = rs.getString("GuardianContactNo");
//                info.Active = rs.getBoolean("Active");
//                info.entrydate = rs.getString("entrydate");
//                info.CreatedDate = rs.getString("CreatedDate");
//                info.CreatedBy = rs.getString("CreatedBy");
//                info.IsActive = rs.getBoolean("IsActive");
//                parentGuardianInfoList.add(info);
//            }
//        } catch (SQLException e) {
//            Log.e("DatabaseHelper", "getParentGuardianInfoBy Error: " + e.getMessage());
//        }
//        return parentGuardianInfoList;
//    }

    public static List<ParentGuardianInfo> getParentGuardianInfo(int userId) {
        List<ParentGuardianInfo> parentGuardianInfoList = new ArrayList<>();
        try (Connection con = getConnection();
             CallableStatement stmt = con.prepareCall("{call sp_GetParentGuardianInfo(?)}")) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ParentGuardianInfo info = new ParentGuardianInfo();
                info.ParentGuardianInfoID = rs.getInt("ParentGuardianInfoID");
                info.UserID = rs.getInt("UserID");
                info.FatherName = rs.getString("FatherName");
                info.FatherContactNo = rs.getString("FatherContactNo");
                info.MotherName = rs.getString("MotherName");
                info.MotherContactNo = rs.getString("MotherContactNo");
                info.GuardianName = rs.getString("GuardianName");
                info.GuardianRelation = rs.getString("GuardianRelation");
                info.GuardianContactNo = rs.getString("GuardianContactNo");
                info.Active = rs.getBoolean("Active");
                info.entrydate = rs.getString("entrydate");
                info.CreatedDate = rs.getString("CreatedDate");
                info.CreatedBy = rs.getString("CreatedBy");
                info.IsActive = rs.getBoolean("IsActive");
                parentGuardianInfoList.add(info);
            }
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "getParentGuardianInfo Error: " + e.getMessage());
        }
        return parentGuardianInfoList;
    }



    public static class ParentGuardianInfo {
        public int ParentGuardianInfoID;
        public int UserID;
        public String FatherName;
        public String FatherContactNo;
        public String MotherName;
        public String MotherContactNo;
        public String GuardianName;
        public String GuardianRelation;
        public String GuardianContactNo;
        public boolean Active;
        public String entrydate;
        public String CreatedDate;
        public String CreatedBy;
        public boolean IsActive;

        // --- Getter Methods ---
        public String getFatherName() {
            return FatherName;
        }

        public String getFatherContactNo() {
            return FatherContactNo;
        }

        public String getMotherName() {
            return MotherName;
        }

        public String getMotherContactNo() {
            return MotherContactNo;
        }

        public String getGuardianName() {
            return GuardianName;
        }

        public String getGuardianContactNo() {
            return GuardianContactNo;
        }
    }

    public static void insertOrUpdateTimeTable(
            Context context,
            int timeTableId,
            int userId,
            int subjectId,
            int gradeId,
            int dayOfWeek,
            String startTime,
            String endTime,
            String roomNo,
            String remark,
            int createdBy,
            DatabaseHelper.ProcedureCallback callback) {

        new Thread(() -> {
            try (Connection conn = getConnection()) {
                CallableStatement stmt = conn.prepareCall("{call sp_TimeTableInsertUpdate(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

                stmt.setInt(1, timeTableId); // 0 for insert
                stmt.setInt(2, userId);
                stmt.setInt(3, subjectId);
                stmt.setInt(4, gradeId);
                stmt.setInt(5, dayOfWeek);
                stmt.setString(6, startTime);
                stmt.setString(7, endTime);
                stmt.setString(8, roomNo);
                stmt.setString(9, remark);
                stmt.setInt(10, createdBy);
                stmt.registerOutParameter(11, java.sql.Types.VARCHAR);

                stmt.execute();

                String message = stmt.getString(11);
                Log.d("TimeTableInsertUpdate", "Stored procedure executed. Message: " + message);

                // Log the message returned from the stored procedure
                Log.d("TimeTableInsertUpdate", "DB Message: " + message);

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (callback != null) callback.onSuccess(message);
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (callback != null) callback.onError(e.getMessage());
                });
            }
        }).start();
    }


    public static class TimeTableEntry {
        public String subjectName;
        public String startTime;
        public String endTime;
        public String remark;
        public String room;

        public TimeTableEntry() {
            this.subjectName = "";
            this.startTime = "";
            this.endTime = "";
            this.remark = "";
            this.room = "";
        }


        public TimeTableEntry(String subjectName, String startTime, String endTime, String remark, String room) {
            this.subjectName = subjectName != null ? subjectName : "";
            this.startTime = startTime != null ? startTime : "";
            this.endTime = endTime != null ? endTime : "";
            this.remark = remark != null ? remark : "";
            this.room = room != null ? room : "";
        }
        @Override
        public String toString() {
            return startTime + " - " + endTime; // So when converted to String, it shows the time slot
        }
        public int timeTableId;
        public int userId;
        public int subjectId;
        public int gradeId;
        public int dayOfWeek;
//        public String subjectName;
        public String gradeName;
        public String weekDay;
//        public String startTime;
//        public String endTime;
        public String roomNo;
//        public String remark;
        public String entryDate;

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }
    }

    public static void getTimeTableByUserId(int userId, ProcedureResultCallback<List<TimeTableEntry>> callback) {
        new Thread(() -> {
            List<TimeTableEntry> timeTableEntries = new ArrayList<>();
            try (Connection conn = getConnection()) {
                CallableStatement stmt = conn.prepareCall("{call sp_GetTimeTableByUserID(?)}");
                stmt.setInt(1, userId);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    TimeTableEntry entry = new TimeTableEntry();
                    entry.timeTableId = rs.getInt("TimeTableId");
                    entry.userId = rs.getInt("UserId");
                    entry.subjectId = rs.getInt("SubjectId");
                    entry.subjectName = rs.getString("SubjectName");
                    entry.gradeId = rs.getInt("GradeID");
                    entry.gradeName = rs.getString("GradeName");
                    entry.dayOfWeek = rs.getInt("DayOfWeek");
                    entry.weekDay = rs.getString("WeekDay");
                    String rawStart = rs.getString("StartTime");
                    String rawEnd = rs.getString("EndTime");

                    SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss.SSSSSS"); // SQL Server format
                    SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a"); // e.g., 09:00 AM

                    try {
                        Date startDate = inputFormat.parse(rawStart);
                        Date endDate = inputFormat.parse(rawEnd);

                        entry.startTime = outputFormat.format(startDate);
                        entry.endTime = outputFormat.format(endDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // fallback to raw if formatting fails
                        entry.startTime = rawStart;
                        entry.endTime = rawEnd;
                    }

                    entry.roomNo = rs.getString("RoomNo");
                    entry.remark = rs.getString("Remark");
                    entry.entryDate = rs.getString("entrydate");

                    timeTableEntries.add(entry);
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onSuccess(timeTableEntries);
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onError(e.getMessage());
                });
            }
        }).start();
    }


    public interface ProcedureResultCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }


}

