package com.example.loginpage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddPromotionalMedia extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;
    private ImageView mediaImageView, backButton;  // Image to trigger video selection
    private VideoView videoView;  // VideoView to play the selected video

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_promotional_media);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views
        mediaImageView = findViewById(R.id.image_select_vid);
        videoView = findViewById(R.id.videoView);
        Button saveButton = findViewById(R.id.button15);
        backButton = findViewById(R.id.imageView156);

        backButton.setOnClickListener(v -> finish());


        // Set click listener to open video picker
        mediaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoPicker();
            }
        });

        saveButton.setOnClickListener(v -> {
            Toast.makeText(AddPromotionalMedia.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddPromotionalMedia.this, TeachersInfoSubSection.class);
            startActivity(intent);
            finish(); // Close current activity
        });
    }

    // Method to open video picker
    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");  // Only allow video selection
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    // Handle selected video
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedVideoUri = data.getData();
            if (selectedVideoUri != null) {
                // Hide ImageView and show VideoView
                mediaImageView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);

                // Set video URI and add controls
                videoView.setVideoURI(selectedVideoUri);
                videoView.setMediaController(new MediaController(this));
                videoView.requestFocus();
                videoView.start();

                Button saveButton = findViewById(R.id.button15);

                saveButton.setOnClickListener(v -> {
                    Toast.makeText(AddPromotionalMedia.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddPromotionalMedia.this, TeachersInfo.class);
                    startActivity(intent);
                    finish(); // Close current activity

                });

                TextView textView54 = findViewById(R.id.textView54);
                saveButton.setVisibility(View.VISIBLE);

                // Change text
                textView54.setText("");
            }
        }
    }
}