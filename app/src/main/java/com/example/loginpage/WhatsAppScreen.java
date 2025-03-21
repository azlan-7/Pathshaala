package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class WhatsAppScreen extends AppCompatActivity {

    private TextView referralCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_whats_app_screen);

        Button sendButton = findViewById(R.id.button38);
        referralCode = findViewById(R.id.textView116);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String selfReferralCode = sharedPreferences.getString("selfreferralcode", "N/A"); // Default: "N/A" if not found

        // ✅ Set Referral Code to TextView
        referralCode.setText(selfReferralCode);

        sendButton.setOnClickListener(v -> {
            Toast.makeText(this,"Message sent!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WhatsAppScreen.this,SearchTeachersDashboard.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}