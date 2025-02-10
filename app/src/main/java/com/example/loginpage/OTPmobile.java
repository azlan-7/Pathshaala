package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.chaos.view.PinView;

public class OTPmobile extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpmobile);


        PinView otpView = findViewById(R.id.otpView);
        otpView.setItemCount(6); // Set it explicitly to 6

        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        SharedPreferences sharedPreferences = getSharedPreferences("OTPDetails", MODE_PRIVATE);
        String correctOTP = sharedPreferences.getString("generatedOTP", "");  // Get the OTP saved from the API response
        // DON"T Generate OTP Locally
//        String correctOTP = getIntent().getStringExtra("generatedOTP");

        Log.d("OTPmobile", "Received OTP: " + correctOTP);  // Log received OTP

        findViewById(R.id.button6).setOnClickListener(v -> {
            String enteredOTP = otpView.getText().toString().trim();
            Log.d("OTPmobile", "Entered OTP: " + enteredOTP);  // Log entered OTP

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("phoneNumber", phoneNumber);
            editor.apply();
            Log.d("OTPmobile", "Saved Phone Number: " + phoneNumber);

            if (enteredOTP.equals(correctOTP)) {
                Log.d("OTPmobile", "OTP Matched!");
                Intent successIntent = new Intent(OTPmobile.this, SuccessActivity.class);
                startActivity(successIntent);
                finish();
            } else {
                Log.d("OTPmobile", "OTP Mismatch! Expected: " + correctOTP + ", Got: " + enteredOTP);
                otpView.setError("Invalid OTP. Please try again.");
            }


        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        return null;
    }
}