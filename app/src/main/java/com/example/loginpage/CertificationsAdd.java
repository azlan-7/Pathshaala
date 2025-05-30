package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.CertificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CertificationsAdd extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView backButton;
    private EditText etCertificationName, etOrganisation, etYear, etCredentialUrl;
    private Button btnSave, btnUpload;
    private SharedPreferences sharedPreferences;
    private Uri certificateImageUri = null; // Stores uploaded image URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_certifications_add);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Initialize UI Elements
        backButton = findViewById(R.id.imageView149);
        etCertificationName = findViewById(R.id.editTextText26);
        etOrganisation = findViewById(R.id.editTextText27);
        etYear = findViewById(R.id.editTextText28);
        etCredentialUrl = findViewById(R.id.editTextUrl);
        btnSave = findViewById(R.id.button28);
        btnUpload = findViewById(R.id.buttonUpload);

        retrieveCertificate();

        // Save Button Click - Save Certification
        btnSave.setOnClickListener(v -> saveCertification());

        // Upload Button Click - Pick Image
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        backButton.setOnClickListener(v -> startActivity(new Intent(CertificationsAdd.this, TeachersInfoSubSection.class)));

    }

    private static final int FILE_PICKER_REQUEST_CODE = 100;

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");  // Accept all file types
        startActivityForResult(Intent.createChooser(intent, "Select a file"), FILE_PICKER_REQUEST_CODE);
    }


    private File getFileFromUri(Uri uri) {
        File file = null;
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                String fileName = (index != -1) ? cursor.getString(index) : "temp_file";
                cursor.close();

                File cacheDir = getCacheDir();
                file = new File(cacheDir, fileName);

                InputStream inputStream = getContentResolver().openInputStream(uri);
                OutputStream outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                outputStream.close();
            }
        } catch (Exception e) {
            Log.e("CertificationsAdd", "❌ Error getting file from URI: " + e.getMessage());
        }
        return file;
    }




    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    private void saveCertification() {
        String name = etCertificationName.getText().toString().trim();
        String organisation = etOrganisation.getText().toString().trim();
        String yearStr = etYear.getText().toString().trim();
        String credentialUrl = etCredentialUrl.getText().toString().trim();

        Log.d("saveCertification", "📌 certificateImageUri: " + certificateImageUri);

        int userId = sharedPreferences.getInt("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the actual file from URI
        File originalFile = getFileFromUri(certificateImageUri);
        if (originalFile == null || !originalFile.exists()) {
            Toast.makeText(this, "File error. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Rename the file using FileUploader's method
        File renamedFile = FileUploader.renameFile(originalFile, userId, "C");
        String certificateFileName = (renamedFile != null) ? renamedFile.getName() : "No_File";

        Log.d("saveCertification", "✅ Final Stored Filename: " + certificateFileName);

        if (name.isEmpty() || organisation.isEmpty() || yearStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        int issueYear;
        try {
            issueYear = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid year format!", Toast.LENGTH_SHORT).show();
            return;
        }

        String selfReferralCode = sharedPreferences.getString("SELF_REFERRAL_CODE", "");

        DatabaseHelper.UserWiseCertificateInsert(
                this,
                "1",  // ✅ Insert operation
                userId,
                name,
                organisation,
                credentialUrl,
                issueYear,
                certificateFileName,
                selfReferralCode,
                new DatabaseHelper.DatabaseCallback() {
                    @Override
                    public void onMessage(String message) {
                        Toast.makeText(CertificationsAdd.this, message, Toast.LENGTH_SHORT).show();
                        if (message.toLowerCase().contains("success")) {
                            startActivity(new Intent(CertificationsAdd.this, CertificationsView.class));
                            finish();
                        }
                    }

                    @Override
                    public void onSuccess(List<Map<String, String>> result) {
                        Log.d("DatabaseCallback", "Insert successful but no list expected.");
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(CertificationsAdd.this, "Database error: " + error, Toast.LENGTH_LONG).show();
                        Log.e("DatabaseCallback", "Error: " + error);
                    }
                }
        );


    }



    private void retrieveCertificate() {
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper.UserWiseCertificateSelect(this, userId, new DatabaseHelper.DatabaseCallback() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                if (!result.isEmpty()) {
                    Map<String, String> certificateData = result.get(0); // Get first certificate

                    etCertificationName.setText(certificateData.get("CertificateName"));
                    etOrganisation.setText(certificateData.get("IssuingOrganization"));
                    etCredentialUrl.setText(certificateData.get("CredentialURL"));
                    etYear.setText(certificateData.get("IssueYear"));

                    Log.d("retrieveCertificate", "Certificate Loaded: " + certificateData.toString());
                } else {
                    Toast.makeText(CertificationsAdd.this, "No certificates found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMessage(String message) {
                Log.d("retrieveCertificate", "ℹ️ Message: " + message);
                Toast.makeText(CertificationsAdd.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CertificationsAdd.this, "Database error: " + error, Toast.LENGTH_LONG).show();
                Log.e("retrieveCertificate", "Error: " + error);
            }
        });
    }





}



















//package com.example.loginpage;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.ImageView;
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.example.loginpage.adapters.CertificationsAdapter;
//import com.example.loginpage.models.CertificationModel;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//
//public class CertificationsAdd extends AppCompatActivity {
//
//
//    private CertificationsAdapter adapter;
//    private List<CertificationModel> certificationList;
//    private SharedPreferences sharedPreferences;
//    private ImageView addCertification;
//    private Button buttonContinue;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_certifications_view);
//
//        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        addCertification = findViewById(R.id.imageView91);
//        buttonContinue = findViewById(R.id.button27);
//
//        certificationList = loadCertificationData();
//        adapter = new CertificationsAdapter(this, certificationList);
//
//        certificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        certificationRecyclerView.setAdapter(adapter);
//
//        addCertification.setOnClickListener(v -> startActivity(new Intent(this, CertificationsView.class)));
//
//        buttonContinue.setOnClickListener(v -> startActivity(new Intent(this, NextActivity.class)));
//    }
//
//    private List<CertificationModel> loadCertificationData() {
//        String json = sharedPreferences.getString("CERTIFICATION_LIST", null);
//        if (json == null) return new ArrayList<>();
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<CertificationModel>>() {}.getType();
//        return gson.fromJson(json, type);
//    }
//}
