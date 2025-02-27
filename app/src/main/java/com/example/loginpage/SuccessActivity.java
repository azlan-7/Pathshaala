package com.example.loginpage;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_success);

        Button continueButton = findViewById(R.id.button7);

        SharedPreferences sharedPreferences = getSharedPreferences("OTPDetails", MODE_PRIVATE);
        String savedPhoneNumber = sharedPreferences.getString("phoneNumber", "");

        // Set OnClickListener to navigate to UserOnboarding
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessActivity.this, UserOnboardingRadio.class);
                intent.putExtra("phoneNumber", savedPhoneNumber);
                startActivity(intent);
                finish();  // This prevents users from going back to SuccessActivity
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}