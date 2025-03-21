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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class AddPromotionalMedia extends AppCompatActivity {

    private static final int PICK_MEDIA_REQUEST = 1;
    private ImageView mediaImageView,retrievedImageView;
    private VideoView videoView;
    private Button uploadButton, saveButton;
    private TextView textView54;
    private Uri selectedMediaUri = null;
    private String mediaType = "I"; // Default type is Image ('I' for Image, 'V' for Video)
    private String promotionalMediaFileName;
    private int userIdFinal;


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
        retrievedImageView = findViewById(R.id.retrieved_image_view);
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
        if (isDestroyed() || isFinishing()) {
            Log.e("showImage", "❌ Activity is destroyed. Skipping image loading.");
            return;
        }

        if (retrievedImageView == null) {
            Log.e("showImage", "❌ retrievedImageView is null, skipping image display.");
            return;
        }
        videoView.setVisibility(View.GONE);
        retrievedImageView.setVisibility(View.VISIBLE);

        Log.d("showImage", "📷 Loading image from: " + imageUri.toString());

//        retrievedImageView.setImageURI(imageUri);

        Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.photo_camera_24dp_1674ae_fill0_wght400_grad0_opsz24)  // Use a placeholder if the image takes time to load
                .error(R.drawable.ic_7)  // Show an error image if it fails to load
                .override(200, 200)  // ✅ Limit image size
                .into(retrievedImageView);

        mediaImageView.setVisibility(View.VISIBLE); // Keep button visible
        uploadButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);
        textView54.setText("Image selected successfully. Click Save to upload.");
    }



    // Show video preview
    private void showVideo(Uri videoUri) {
        mediaImageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.VISIBLE);

        videoView.setVideoURI(videoUri);
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();

        uploadButton.setVisibility(View.VISIBLE);
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

        // ✅ Ensure variables are effectively final
        final int userIdFinal = userId;
        final String[] promotionalMediaFileName = {"No_File"};

        if (selectedMediaUri != null) {
            File originalFile = getFileFromUri(selectedMediaUri);
            if (originalFile != null) {
                Log.d("insertPromotionalMediaIntoDB", "📂 File exists: " + originalFile.getAbsolutePath());
                File renamedFile = FileUploader.renameFile(originalFile, userIdFinal, "P");
                if (renamedFile != null) {
                    Log.d("insertPromotionalMediaIntoDB", "📂 Renamed File exists: " + renamedFile.getAbsolutePath());
                    promotionalMediaFileName[0] = renamedFile.getName();  // ✅ Store inside array to modify inside lambda

                    // ✅ Upload file to the server
                    new Thread(() -> {
                        boolean success = FileUploader.uploadImage(renamedFile, AddPromotionalMedia.this, "P");
                        Log.d("uploadFileToServer", "✅ File Upload Success: " + success);

                        runOnUiThread(() -> {
                            if (success) {
                                Log.d("uploadFileToServer", "✅ File uploaded, inserting into DB...");
                                Toast.makeText(AddPromotionalMedia.this, "Upload successful!", Toast.LENGTH_SHORT).show();  // ✅ Fix toast message
                                insertMediaIntoDatabase();
                            } else {
                                Log.e("uploadFileToServer", "❌ Image upload failed.");
//                                Toast.makeText(AddPromotionalMedia.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }).start();



                } else {
                    Log.e("insertPromotionalMediaIntoDB", "❌ Renamed file is null. Skipping upload.");
                }
            }
        }

        Log.d("insertPromotionalMediaIntoDB", "✅ Final Stored Filename: " + promotionalMediaFileName[0]);
    }


    private void insertMediaIntoDatabase() {
        if (promotionalMediaFileName == null || promotionalMediaFileName.isEmpty()) {
            Log.e("insertMediaIntoDatabase", "❌ No valid file name to insert into DB.");
            return;
        }

        String promotionalCaption = "Promotional Media " + System.currentTimeMillis();
        String remarks = "User uploaded a promotional media";

        Log.d("DatabaseHelper", "📂 Inserting into DB -> FileName: " + promotionalMediaFileName);


        DatabaseHelper.UserWisePromotionalMediaInsert(
                this,
                "1",  // 1 means INSERT operation
                userIdFinal,
                promotionalCaption,
                "I",  // TypeOfMedia (I for Image, V for Video)
                remarks,
                promotionalMediaFileName,
                "",
                new DatabaseHelper.DatabaseCallback() {
                    @Override
                    public void onMessage(String message) {
                        Log.d("DatabaseHelper", "✅ DB Insert Response: " + message);
                        Toast.makeText(AddPromotionalMedia.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(List<Map<String, String>> result) {
                        Log.d("DatabaseHelper", "✅ Insert Successful! Retrieved: " + result.toString());
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("DatabaseHelper", "❌ Database Insert Error: " + error);
                    }
                }
        );
    }


    private File getFileFromUri(Uri uri) {
        File file = null;
        try {
            String fileName = getFileNameFromUri(uri);
            if (fileName == null || fileName.isEmpty()) {
                fileName = "temp_media"; // Fallback name
            }

            File tempFile = new File(getCacheDir(), fileName);
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 OutputStream outputStream = new FileOutputStream(tempFile)) {

                if (inputStream == null) {
                    Log.e("AddPromotionalMedia", "Failed to open InputStream");
                    return null;
                }

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                file = tempFile;
            }
        } catch (Exception e) {
            Log.e("AddPromotionalMedia", "Error getting file from URI: " + e.getMessage());
        }
        return file;
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
                Log.d("retrievePromotionalMedia", "✅ Retrieved Data: " + result.toString());

                if (!result.isEmpty()) {
                    for (Map<String, String> mediaData : result) {
                        String mediaFileName = mediaData.get("PromotionalMediaFileName");

                        if (mediaFileName == null || mediaFileName.isEmpty()) {
                            Log.e("retrievePromotionalMedia", "❌ No valid media file found in DB.");
                            continue;
                        }

                        String fileURL = "http://129.154.238.214/Pathshaala/UploadedFiles/Media/" + mediaFileName;
                        Log.d("retrievePromotionalMedia", "✅ Constructed URL: " + fileURL);
                        showImage(Uri.parse(fileURL));
                    }
                } else {
                    Log.e("retrievePromotionalMedia", "❌ No promotional media found.");
                }
            }

            @Override
            public void onMessage(String message) {
                Log.d("retrievePromotionalMedia", "ℹ️ DB Message: " + message);
            }

            @Override
            public void onError(String error) {
                Log.e("retrievePromotionalMedia", "❌ Database error: " + error);
            }
        });
    }


}
