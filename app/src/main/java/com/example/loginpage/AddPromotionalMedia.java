package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
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

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;

import java.util.List;
import java.util.Map;

public class AddPromotionalMedia extends AppCompatActivity {

    private static final int PICK_MEDIA_REQUEST = 1;
    private ImageView mediaImageView;
    private VideoView videoView;
    private Button uploadButton, saveButton;
    private TextView textView54;
    private Uri selectedMediaUri = null;
    private String mediaType = "I"; // Default type is Image ('I' for Image, 'V' for Video)

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

        // Initialize Views
        mediaImageView = findViewById(R.id.image_select_vid);
        videoView = findViewById(R.id.videoView);
        uploadButton = findViewById(R.id.uploadButton);
        saveButton = findViewById(R.id.button15);
        textView54 = findViewById(R.id.textView54);

        retrievePromotionalMedia();

        // Open media picker when image is clicked
        mediaImageView.setOnClickListener(v -> openMediaPicker());

        // Save button - Insert into DB
        saveButton.setOnClickListener(v -> {
            if (selectedMediaUri == null) {
                Toast.makeText(this, "Please select a media file first!", Toast.LENGTH_SHORT).show();
            } else {
                insertPromotionalMediaIntoDB();
            }
        });
    }

    // Open media picker (Accepts both images & videos)
    private void openMediaPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("*/*"); // Accepts all file types
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        startActivityForResult(intent, PICK_MEDIA_REQUEST);
    }

    // Handle selected media
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_MEDIA_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedMediaUri = data.getData();

            if (selectedMediaUri != null) {
                String mimeType = getContentResolver().getType(selectedMediaUri);

                if (mimeType != null) {
                    if (mimeType.startsWith("image")) {
                        mediaType = "I"; // Image
                        showImage(selectedMediaUri); // ✅ Pass the URI
                    } else if (mimeType.startsWith("video")) {
                        mediaType = "V"; // Video
                        showVideo(selectedMediaUri); // ✅ Pass the URI
                    }
                }

                Log.d("onActivityResult", "📂 Selected File: " + selectedMediaUri.toString());
                Log.d("onActivityResult", "🎥 Media Type: " + mediaType);
            }
        }
    }


    // Show image preview
    // Show image preview
    private void showImage(Uri imageUri) {
        videoView.setVisibility(View.GONE);
        mediaImageView.setVisibility(View.VISIBLE);
        mediaImageView.setImageURI(imageUri);

        uploadButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);
        textView54.setText("Image selected successfully. Click Save to upload.");
    }


    // Show video preview
    private void showVideo(Uri videoUri) {
        mediaImageView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        videoView.setVideoURI(videoUri);
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();

        uploadButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);
        textView54.setText("Video selected successfully. Click Save to upload.");
    }



    // Insert promotional media into DB
    private void insertPromotionalMediaIntoDB() {
        if (selectedMediaUri == null) {
            Toast.makeText(this, "Please select a media file first!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String promotionalMediaFileName = getFileNameFromUri(selectedMediaUri);
        String promotionalCaption = "Promotional Media"; // Default caption
        String remarks = "User uploaded a promotional media";
        String selfReferralCode = ""; // Modify if required

        Log.d("insertPromotionalMedia", "🟢 Inserting Promotional Media into DB");
        Log.d("insertPromotionalMedia", "  - User ID: " + userId);
        Log.d("insertPromotionalMedia", "  - Promotional Caption: " + promotionalCaption);
        Log.d("insertPromotionalMedia", "  - Type of Media: " + mediaType);
        Log.d("insertPromotionalMedia", "  - Remarks: " + remarks);
        Log.d("insertPromotionalMedia", "  - Media File Name: " + promotionalMediaFileName);

        DatabaseHelper.UserWisePromotionalMediaInsert(
                this,
                "1", // Insert operation
                userId,
                promotionalCaption,
                mediaType,
                remarks,
                promotionalMediaFileName,
                selfReferralCode,
                new DatabaseHelper.DatabaseCallback() {
                    @Override
                    public void onMessage(String message) {
                        Log.d("DatabaseHelper", "📩 DB Message: " + message);
                        Toast.makeText(AddPromotionalMedia.this, message, Toast.LENGTH_SHORT).show();
                        if (message.toLowerCase().contains("success")) {
                            startActivity(new Intent(AddPromotionalMedia.this, TeachersInfo.class));
                            finish();
                        }
                    }

                    @Override
                    public void onSuccess(List<Map<String, String>> result) {
                        Log.d("DatabaseCallback", "✅ Promotional Media Insert successful!");
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("DatabaseHelper", "❌ Insert failed: " + error);
                        Toast.makeText(AddPromotionalMedia.this, "Database error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    // Extract file name from URI
    private String getFileNameFromUri(Uri uri) {
        String fileName = "No_File"; // Default file name

        Log.d("getFileNameFromUri", "🔍 Checking file name for URI: " + uri.toString());

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            Log.d("getFileNameFromUri", "📌 Column index for DISPLAY_NAME: " + nameIndex);
            if (nameIndex != -1) {
                cursor.moveToFirst();
                fileName = cursor.getString(nameIndex);
                Log.d("getFileNameFromUri", "✅ Extracted file name: " + fileName);
            }
            cursor.close();
        } else {
            Log.e("getFileNameFromUri", "❌ Cursor is NULL. Could not extract file name.");
        }

        return fileName;
    }


    private void retrievePromotionalMedia() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper.UserWisePromotionalMediaSelect(this, userId, new DatabaseHelper.DatabaseCallback() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                if (!result.isEmpty()) {
                    Map<String, String> mediaData = result.get(0); // Get first media entry

                    Log.d("retrievePromotionalMedia", "✅ Media Loaded: " + mediaData.toString());

                    String mediaType = mediaData.get("TypeOfMedia");
                    String mediaFileName = mediaData.get("PromotionalMediaFileName");

                    Uri mediaUri = Uri.parse(mediaFileName); // Convert file name to URI

                    if ("I".equals(mediaType)) {
                        showImage(mediaUri);
                    } else if ("V".equals(mediaType)) {
                        showVideo(mediaUri);
                    }

                    Toast.makeText(AddPromotionalMedia.this, "Promotional media retrieved successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("retrievePromotionalMedia", "⚠️ No promotional media found for UserID: " + userId);
                    Toast.makeText(AddPromotionalMedia.this, "No promotional media found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMessage(String message) {
                Log.d("retrievePromotionalMedia", "ℹ️ Message: " + message);
                Toast.makeText(AddPromotionalMedia.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddPromotionalMedia.this, "Database error: " + error, Toast.LENGTH_LONG).show();
                Log.e("retrievePromotionalMedia", "Error: " + error);
            }
        });
    }


}
