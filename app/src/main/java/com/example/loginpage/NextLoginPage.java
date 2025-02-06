package com.example.loginpage;
import com.example.loginpage.OTPService;

import android.content.SharedPreferences;
import android.os.Bundle;

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




        PhoneNumber = findViewById(R.id.editTextText4);
        btnLogin = findViewById(R.id.button);

        btnLogin.setOnClickListener(v -> {
            // Retrieve mobile number entered by the user
            String mobileNumber = PhoneNumber.getText().toString().trim();

            if (mobileNumber.isEmpty() || mobileNumber.length() < 10) {
                PhoneNumber.setError("Please enter a valid mobile number");
            } else {


//                String otp = generateOTP();
//                Log.d("NextLoginPage", "Generated OTP: " + otp);
                // Send OTP directly to the entered mobile number
                OTPService.sendOTP(mobileNumber, NextLoginPage.this);

                Toast.makeText(NextLoginPage.this, "OTP has been sent to: " + mobileNumber, Toast.LENGTH_SHORT).show();

                Intent intentPhone = new Intent(NextLoginPage.this, OTPmobile.class);
                intentPhone.putExtra("phoneNumber", mobileNumber);  // Pass the phone number to OTP page
                intentPhone.putExtra("generatedOTP", otp);  // Pass the generated OTP
//              intentPhone.putExtra("phoneNumber", PhoneNumber.getText().toString().trim());  // Pass the phone number
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
            OTPService.sendOTP(phoneNumber, NextLoginPage.this);





            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("phoneNumber", String.valueOf(PhoneNumber));  // Save phone number
            editor.apply();





        }
    }
}