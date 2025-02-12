package com.example.loginpage;

import static com.example.loginpage.R.id.button9;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


public class Welcome extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        Button btnSkip = findViewById(button9);


        btnSkip.setOnClickListener(v -> {
            // Create an Intent to navigate to NextLoginPage activity
            Intent intent = new Intent(Welcome.this, NextLoginPage.class);
            startActivity(intent); // Start the NextLoginPage activity
            finish(); // Finish the current activity (Welcome) to prevent going back
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}