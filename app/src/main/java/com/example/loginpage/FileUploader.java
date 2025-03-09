package com.example.loginpage;

import okhttp3.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUploader {
    private static final String API_URL = "http://129.154.238.214/Pathshaala/api/UploadFile/";
    private static final OkHttpClient client = new OkHttpClient();

    public static boolean uploadImage(File imageFile, Context context) {
        Log.d("FileUploader", "Starting image upload...");

        if (!imageFile.exists()) {
            Log.e("FileUploader", "File does not exist: " + imageFile.getAbsolutePath());
            return false;
        }

        // Retrieve user ID from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Log.e("FileUploader", "User ID not found in SharedPreferences.");
            return false;
        }

        Log.d("FileUploader", "Retrieved User ID: " + userId);

        // Rename the file to include user ID
        File renamedFile = renameFile(imageFile, userId);
        if (renamedFile == null) {
            Log.e("FileUploader", "Failed to rename file.");
            return false;
        }

        try {
            // Prepare image request body
            RequestBody fileBody = RequestBody.create(renamedFile, MediaType.parse("image/*"));
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", renamedFile.getName(), fileBody)
                    .build();

            // Create request
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .build();

            // Execute request
            Response response = client.newCall(request).execute();

            // Check response
            if (!response.isSuccessful()) {
                Log.e("FileUploader", "Upload failed. Status Code: " + response.code() + ", Error: " + response.message());
                return false;
            }

            // Log success
            Log.d("FileUploader", "Image uploaded successfully. Response: " + response.body().string());
            return true;
        } catch (IOException e) {
            Log.e("FileUploader", "Upload failed: " + e.getMessage(), e);
            return false;
        }
    }

    // Method to rename file to include userId
    private static File renameFile(File file, int userId) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = formatter.format(new Date());
        String extension = file.getName().substring(file.getName().lastIndexOf("."));

        File directory = file.getParentFile();
        String newFileName = userId + "_" + timestamp + extension;
        File renamedFile = new File(directory, newFileName);

        if (file.renameTo(renamedFile)) {
            Log.d("FileUploader", "File renamed to: " + renamedFile.getName());
            return renamedFile;
        } else {
            Log.e("FileUploader", "File renaming failed.");
            return null;
        }
    }
}
