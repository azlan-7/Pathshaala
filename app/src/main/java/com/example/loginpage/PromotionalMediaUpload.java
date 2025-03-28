package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PromotionalMediaUpload extends AppCompatActivity {

    private Button addUpdateVideoBtn;
    private ImageView uploadedImage;
    private ImageView youtubeLink;
    private ImageView instagramLink;
    private ImageView linkedinLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_promotional_media_upload);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        youtubeLink = findViewById(R.id.youtubeLink);
        instagramLink = findViewById(R.id.instagramLink);
        linkedinLink = findViewById(R.id.linkedinLink);
        addUpdateVideoBtn = findViewById(R.id.addUpdateVideo);
        uploadedImage = findViewById(R.id.image_thumbnail);  // Add ImageView in XML

        // Retrieve stored image URI from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String imageUri = prefs.getString("uploadedImageUri", null);

        if (imageUri != null) {
            // If an image is available, show it and hide the button
            uploadedImage.setImageURI(Uri.parse(imageUri));
            uploadedImage.setVisibility(View.VISIBLE);
            addUpdateVideoBtn.setVisibility(View.GONE);
        } else {
            // If no image, show the button
            uploadedImage.setVisibility(View.GONE);
            addUpdateVideoBtn.setVisibility(View.VISIBLE);
        }

        // Redirect to AddPromotionalMedia when button is clicked
        addUpdateVideoBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Redirecting to add or update video.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AddPromotionalMedia.class);
            startActivity(intent);
        });

        // Retrieve social media links from SharedPreferences
        String youtubeUrl = prefs.getString("youtubeURL", "");
        String instagramUrl = prefs.getString("instagramURL", "");
        String linkedinUrl = prefs.getString("linkedinURL", "");

        // Set onClick listeners for social media icons
        youtubeLink.setOnClickListener(v -> openLink(youtubeUrl, "YouTube"));
        instagramLink.setOnClickListener(v -> openLink(instagramUrl, "Instagram"));
        linkedinLink.setOnClickListener(v -> openLink(linkedinUrl, "LinkedIn"));
    }

    // Method to open URL
    private void openLink(String url, String platform) {
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else {
            Toast.makeText(this, platform + " link not available", Toast.LENGTH_SHORT).show();
        }
    }
}
