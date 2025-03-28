package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddSocials extends AppCompatActivity {
    public EditText editTextYouTube, editTextLinkedIn, editTextInstagram;
    private Button buttonSaveSocials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_socials);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextYouTube = findViewById(R.id.editTextYouTube);
        editTextLinkedIn = findViewById(R.id.editTextLinkedIn);
        editTextInstagram = findViewById(R.id.editTextInstagram);
        buttonSaveSocials = findViewById(R.id.buttonSaveSocials);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("youtubeURL", editTextYouTube.getText().toString());
        editor.putString("instagramURL", editTextInstagram.getText().toString());
        editor.putString("linkedinURL", editTextLinkedIn.getText().toString());
        editor.apply();


        buttonSaveSocials.setOnClickListener(v -> {
            Toast.makeText(AddSocials.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddSocials.this, PromotionalMediaUpload.class);
            startActivity(intent);
            finish();
        });
    }
}