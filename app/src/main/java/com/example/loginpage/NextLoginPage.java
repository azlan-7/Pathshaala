package com.example.loginpage;
import com.example.loginpage.MySqliteDatabase.Connection_Class;
import com.example.loginpage.OTPService;

import java.sql.SQLException;
import java.util.ArrayList;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;

import android.os.AsyncTask;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.Random;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageView;

import java.util.Random;

public class NextLoginPage extends AppCompatActivity {
    private EditText PhoneNumber;
    private Button btnLogin;

    private String phoneNumber;;

    private String otp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_next_login_page);

        fetchCityData();

        if(savedInstanceState == null){ //Prevent re-running on rotation /config change
            executeDatabaseQuery();
        }


        PhoneNumber = findViewById(R.id.editTextText4);
        btnLogin = findViewById(R.id.button);

        btnLogin.setOnClickListener(v -> {
            String phoneNumber = PhoneNumber.getText().toString().trim();

            if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
                PhoneNumber.setError("Please enter a valid mobile number");
            } else {
                Log.d("NextLoginPage", "📲 Sending OTP to: " + phoneNumber);

                // Call OTPService to send OTP
                OTPService.sendOTP(phoneNumber, this);

                // Store the phone number in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phoneNumber", phoneNumber);
                editor.apply();

                // Navigate to OTPmobile activity
                Intent intentPhone = new Intent(NextLoginPage.this, OTPmobile.class);
                intentPhone.putExtra("phoneNumber", phoneNumber);  // Pass phone number
                startActivity(intentPhone);
            }
        });







        View facebookIcon = findViewById(R.id.imageView);
        View outlookIcon = findViewById(R.id.imageView2);
        View googleIcon = findViewById(R.id.imageView6);



        facebookIcon.setOnClickListener(v -> {
            Intent intent = new Intent(NextLoginPage.this, FacebookLogin.class);
            startActivity(intent);
        });


        outlookIcon.setOnClickListener(v -> {
            Intent intent = new Intent(NextLoginPage.this, OutlookLogin.class);
            startActivity(intent);
        });


        googleIcon.setOnClickListener(v -> {
            Intent intent = new Intent(NextLoginPage.this, GoogleLogin.class);
            startActivity(intent);
        });


        TextView signupLink = findViewById(R.id.textView7);


        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NextLoginPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



    private String generateOTP () {
        Random random = new Random();
        int otp = random.nextInt(900000) + 100000;
        return String.valueOf(otp);
    }




    private void executeDatabaseQuery() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                Log.d("NextLoginPage", "SqlTest1");
                String s = "";
                Connection con = null;
                PreparedStatement ps = null;
                ResultSet count = null;

                try {
                    Connection_Class connect = new Connection_Class();
                    con = connect.CONN();
                    if (con == null) {
                        s = "There is no Internet Connection";
                        Log.d("NextLoginPage", "Value of s1 =" + s);
                    } else {
                        String query = "SELECT countvalue FROM visitorcounter";
                        ps = con.prepareStatement(query);
                        count = ps.executeQuery();

                        if (count.next()) {
                            int visitorCount = count.getInt("countvalue");
                            Log.d("NextLoginPage", "Value of count = " + visitorCount);
                            s = "Visitor Count: " + visitorCount;
                        } else {
                            Log.d("NextLoginPage", "No data found in visitorcounter table.");
                        }
                    }
                } catch (Exception e) {
                    s = "Error retrieving data from table: " + e.getMessage();
                    Log.d("NextLoginPage", "SQL Error: " + s);
                } finally {
                    try {
                        if (count != null) count.close();
                        if (ps != null) ps.close();
                        if (con != null) con.close();
                    } catch (Exception e) {
                        Log.e("NextLoginPage", "Error closing database resources: " + e.getMessage());
                    }
                }
                return s;
            }

            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(NextLoginPage.this, result, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void fetchCityData() {
        Connection connection = DatabaseHelper.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT cityid, city_nm FROM city";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int cityId = resultSet.getInt("cityid");
                    String cityName = resultSet.getString("city_nm");
                    Log.d("CityData", "ID: " + cityId + ", Name: " + cityName);
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();

            } catch (SQLException e) {
                Log.e("DatabaseError", "SQL Exception: " + e.getMessage());
            }
        } else {
            Log.e("DatabaseError", "Connection is NULL");
        }
    }





//    private void executeDatabaseQuery() {
//        new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... voids) {
//                Log.d("NextLoginPage", "SqlTest1");
//                String s = "";
//                try {
//                    Connection_Class connect = new Connection_Class();
//                    Connection con = connect.CONN();
//                    if (con == null) {
//                        s = "There is no Internet Connection";
//                        Log.d("NextLoginPage", "Value of s1 =" + s);
//                    } else {
//                        String query = "SELECT countvalue FROM visitorcounter";
//                        PreparedStatement ps = con.prepareStatement(query);
//                        ResultSet count = ps.executeQuery();
//
//                        if (count.next()) {  // Move to first row
//                            int visitorCount = count.getInt("countvalue");
//                            Log.d("NextLoginPage", "Value of count = " + visitorCount);
//                            s = "Visitor Count: " + visitorCount;
//                        } else {
//                            Log.d("NextLoginPage", "No data found in visitorcounter table.");
//                        }
//                    }
//                } catch (Exception e) {
//                    s = "Error retrieving data from table: " + e.getMessage();
//                    Log.d("NextLoginPage", "SQL Error: " + s);
//                }
//                return s;
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                // Runs on UI Thread, update UI here if needed
//                Toast.makeText(NextLoginPage.this, result, Toast.LENGTH_SHORT).show();
//            }
//        }.execute();
//    }





//    protected String doInBackground() {
//        Log.d("NextLoginPage", "SqlTest1");
//
//        String s = "";
//        try {
//            Connection_Class connect = new Connection_Class();
//            Connection con = connect.CONN();
//            if (con == null) {
//                s = "There is no Internet Connection";
//                Log.d("NextLoginPage", "Value of s1 =" + s);
//            } else {
//                String query = "SELECT countvalue FROM visitorcounter";
//                PreparedStatement ps = con.prepareStatement(query);
//                ResultSet count = ps.executeQuery();
//
//                if (count.next()) {  // Move to the first row
//                    int visitorCount = count.getInt("countvalue");  // Retrieve the integer value
//                    Log.d("NextLoginPage", "Value of count = " + visitorCount);
//                } else {
//                    Log.d("NextLoginPage", "No data found in visitorcounter table.");
//                }
//
//                Toast.makeText(NextLoginPage.this, "SQLTEST ", Toast.LENGTH_SHORT).show();
//            }
//        } catch (final Exception e) {
//            s = "Error retrieving data from table: " + e.getMessage();
//            Log.d("NextLoginPage", "SQL Error: " + s);
//        }
//
//        return s;
//    }



//    protected String doInBackground(String... strings) {
//        Log.d("NextLoginPage", "SqlTest1");
//
//        String s = "";
//        try {
//            Connection_Class connect = new Connection_Class();
//            Connection con = connect.CONN();
//            if (con == null) {
//                s = "There is no Internet Connection";
//                Log.d("NextLoginPage", "Value of" + "  s1 ="+s);
//
//            } else {
//                String query = "select countvalue from visitorcounter";
//                PreparedStatement ps = con.prepareStatement(query);
//                ResultSet count = ps.executeQuery();
//                Log.d("NextLoginPage", "Value of" + count + "  s2 ="+s);
//
//                Toast.makeText(NextLoginPage.this, "SQLTEST ", Toast.LENGTH_SHORT).show();
//
//                Integer i = 1;
//            }
//
//        } catch (final Exception e) {
//
//            s = "Error retrieving data from table";
//            Log.d("NextLoginPage", "Value of Count"  + "  s3 ="+s);
//
//        }
//        Log.d("NextLoginPage", "Value of Count"  + "  s4 ="+s);
//
//        return s;
//    }

    private void onClick(View v) {
        // Retrieve the phone number entered in the EditText
        String phoneNumber = PhoneNumber.getText().toString().trim();

        // Check if the phone number is valid (basic validation)
        if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
            PhoneNumber.setError("Please enter a valid phone number");
        } else {
            // Proceed to next step, such as sending OTP or moving to another screen
            // Example: Save the phone number and pass it to another activity or store it

            String otp = generateOTP();
            Log.d("NextLoginPage", "Generated OTP: " + otp);  // Log OTP before sending

            // Call OTPService to send OTP
            OTPService.sendOTP(phoneNumber, this);


            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("phoneNumber", PhoneNumber.getText().toString().trim());

//            editor.putString("phoneNumber", String.valueOf(PhoneNumber));  // Save phone number
            editor.apply();


        }
    }
}