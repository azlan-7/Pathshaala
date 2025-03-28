package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class AddPromotionalMedia extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;
    private ImageView mediaImageView;  // Button to pick a video
    private VideoView videoView;  // VideoView to play the selected video
    public ImageView thumbnailImageView; // ImageView to display video thumbnail
    private Button saveButton, addSocialsButton;
    private TextView textView54;
    private Uri selectedVideoUri; // Store selected video URI

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

        // Initialize views
        mediaImageView = findViewById(R.id.image_select_vid);
        videoView = findViewById(R.id.videoView);
        thumbnailImageView = findViewById(R.id.image_thumbnail);
        saveButton = findViewById(R.id.button15);
        textView54 = findViewById(R.id.textView54);
        addSocialsButton = findViewById(R.id.socialsBtn);

        // Load saved video URI (if exists)
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedVideoUri = prefs.getString("selectedVideoUri", null);

        if (savedVideoUri != null) {
            selectedVideoUri = Uri.parse(savedVideoUri);
            displayThumbnail(selectedVideoUri);
        }

        // Open Socials Page
        addSocialsButton.setOnClickListener(v -> {
            Toast.makeText(AddPromotionalMedia.this, "Redirecting to the Socials Page", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddPromotionalMedia.this, AddSocials.class));
            finish();
        });

        // Open video picker
        mediaImageView.setOnClickListener(v -> openVideoPicker());

        saveButton.setOnClickListener(v -> {
            Toast.makeText(AddPromotionalMedia.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddPromotionalMedia.this, PromotionalMediaUpload.class));
            finish();
        });
    }

    // Open video picker
    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    // Handle selected video
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            if (selectedVideoUri != null) {
                // Save URI in SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("selectedVideoUri", selectedVideoUri.toString());
                editor.apply();

                // Display thumbnail
                displayThumbnail(selectedVideoUri);
            }
        }
    }

    // Display thumbnail instead of playing the video
    private void displayThumbnail(Uri videoUri) {
        // Hide VideoView and upload button
        mediaImageView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        textView54.setVisibility(View.GONE);

        // Show the thumbnail ImageView
        thumbnailImageView.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);

        try {
            Bitmap thumbnail = getVideoThumbnail(videoUri);
            if (thumbnail != null) {
                thumbnailImageView.setImageBitmap(thumbnail);
            } else {
                Toast.makeText(this, "Failed to generate thumbnail", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading thumbnail", Toast.LENGTH_SHORT).show();
        }
    }

    // Extract video thumbnail
    private Bitmap getVideoThumbnail(Uri videoUri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, videoUri);
            return retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC); // Frame at 1 second
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            retriever.release();
        }
    }
}
