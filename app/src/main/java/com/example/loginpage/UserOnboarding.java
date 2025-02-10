package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserOnboarding extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_onboarding);


        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        Button teacherButton = findViewById(R.id.button10);

//        teacherButton.setOnClickListener(v -> {
//                    Intent intent = new Intent(UserOnboarding.this, TeachersBasicInfo.class);
//                    intent.putExtra("phoneNumber", phoneNumber); // Pass phone number
//                    startActivity(intent);
//                });

//        // Pass it to TeachersBasicInfo activity
//        Intent intent = new Intent(UserOnboarding.this, TeachersBasicInfo.class);
//        intent.putExtra("phoneNumber", phoneNumber);
//        startActivity(intent);
//        finish();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}