package com.example.loginpage;

import okhttp3.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.OpenableColumns;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUploader {
    private static final String API_URL = "http://129.154.238.214/Pathshaala/api/UploadFile/";
    private static final OkHttpClient client = new OkHttpClient();


    // New method (with callback) for TeachersBasicInfo
    // Original method (used by AddAwards, AddPromotionalMedia, CertificationsAdd)
    public static boolean uploadImage(File imageFile, Context context, String prefix) {
        final boolean[] result = {false}; // Store the result of upload

        uploadImage(imageFile, context, prefix, success -> {
            result[0] = success;
        });

        return result[0]; // Return upload result (though it executes asynchronously)
    }

    public interface UploadCallback {
        void onUploadComplete(boolean success);
    }

    // New method (with callback) for TeachersBasicInfo
    public static void uploadImage(File imageFile, Context context, String prefix, UploadCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                Log.d("FileUploader", "📌 Starting image upload...");

                if (!imageFile.exists()) {
                    Log.e("FileUploader", "❌ File does not exist: " + imageFile.getAbsolutePath());
                    return false;
                }

                SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                int userId = sharedPreferences.getInt("USER_ID", -1);
                if (userId == -1) {
                    Log.e("FileUploader", "❌ User ID not found in SharedPreferences.");
                    return false;
                }

                File renamedFile = renameFile(imageFile, userId, prefix);
                if (renamedFile == null) {
                    Log.e("FileUploader", "❌ Failed to rename file.");
                    return false;
                }

                try {
                    Log.d("FileUploader", "Uploading file: " + renamedFile.getAbsolutePath()); // ✅ Added log before upload
                    RequestBody fileBody = RequestBody.create(renamedFile, MediaType.parse("image/*"));
                    MultipartBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", renamedFile.getName(), fileBody)
                            .build();

                    Request request = new Request.Builder()
                            .url(API_URL)
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    int responseCode = response.code();
                    String responseBody = response.body() != null ? response.body().string() : "No response";

                    if (responseCode == 200) {
                        Log.d("FileUploader", "✅ File upload successful!");
                        return true;  // ✅ Mark as success
                    } else {
                        Log.e("FileUploader", "❌ Upload failed. Response Code: " + responseCode);
                        Log.e("FileUploader", "❌ Server Response: " + responseBody);
                        return false;
                    }

                } catch (IOException e) {
                    Log.e("FileUploader", "❌ Upload failed: " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (callback != null) {
                    callback.onUploadComplete(success);
                }
            }
        }.execute();
    }



    public static File renameFile(File file, int userId, String prefix) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = formatter.format(new Date());
        String extension = file.getName().substring(file.getName().lastIndexOf("."));

        File directory = file.getParentFile();
        String newFileName = prefix + "_" + userId + "_" + timestamp + extension;
        File renamedFile = new File(directory, newFileName);

        if (file.renameTo(renamedFile)) {
            Log.d("FileUploader", "✅ File renamed successfully: " + renamedFile.getName());
            return renamedFile;
        } else {
            Log.e("FileUploader", "❌ File renaming failed.");
            return null;
        }
    }


    public static File renameFileForTeachers(Context context, Uri sourceUri, int userId, String prefix) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = formatter.format(new Date());

        String extension = getFileExtension(context, sourceUri);
        if (extension == null) extension = ".jpg"; // Default extension if not found

        String newFileName = prefix + "_" + userId + "_" + timestamp + extension;
        File storageDir = context.getFilesDir(); // Internal storage directory
        File newFile = new File(storageDir, newFileName);

        try (InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
             OutputStream outputStream = new FileOutputStream(newFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            Log.d("FileUploader", "✅ File copied to internal storage: " + newFile.getAbsolutePath());
            return newFile;
        } catch (IOException e) {
            Log.e("FileUploader", "❌ Failed to copy file: " + e.getMessage());
            return null;
        }
    }

    // Helper method to get file extension from URI
    private static String getFileExtension(Context context, Uri uri) {
        String extension = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1 && cursor.moveToFirst()) {
                String fileName = cursor.getString(nameIndex);
                if (fileName.contains(".")) {
                    extension = fileName.substring(fileName.lastIndexOf("."));
                }
            }
            cursor.close();
        }
        return extension;
    }



    private static File copyFileToInternalStorage(File sourceFile, String newFileName, Context context) {
        File storageDir = context.getFilesDir(); // Internal storage
        File newFile = new File(storageDir, newFileName);

        try {
            FileInputStream fis = new FileInputStream(sourceFile);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fis.close();
            fos.close();

            Log.d("FileUploader", "✅ File copied to internal storage: " + newFile.getAbsolutePath());
            return newFile;
        } catch (IOException e) {
            Log.e("FileUploader", "❌ Failed to copy file: " + e.getMessage());
            return null;
        }
    }

}
