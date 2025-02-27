package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginpage.models.CertificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CertificationsAdd extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
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
        etCertificationName = findViewById(R.id.editTextText26);
        etOrganisation = findViewById(R.id.editTextText27);
        etYear = findViewById(R.id.editTextText28);
        etCredentialUrl = findViewById(R.id.editTextUrl);
        btnSave = findViewById(R.id.button28);
        btnUpload = findViewById(R.id.buttonUpload);

        // Save Button Click - Save Certification
        btnSave.setOnClickListener(v -> saveCertification());

        // Upload Button Click - Pick Image
        btnUpload.setOnClickListener(v -> openFileChooser());
    }

    private void saveCertification() {
        String name = etCertificationName.getText().toString().trim();
        String organisation = etOrganisation.getText().toString().trim();
        String year = etYear.getText().toString().trim();
        String credentialUrl = etCredentialUrl.getText().toString().trim();
        String imageUri = (certificateImageUri != null) ? certificateImageUri.toString() : ""; // Store URI as String

        if (name.isEmpty() || organisation.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load existing data from SharedPreferences
        Gson gson = new Gson();
        String json = sharedPreferences.getString("CERTIFICATIONS_LIST", null);
        Type type = new TypeToken<List<CertificationModel>>() {}.getType();
        List<CertificationModel> certificationList = (json == null) ? new ArrayList<>() : gson.fromJson(json, type);

        // Add new certification
        certificationList.add(new CertificationModel(name, organisation, year, credentialUrl, imageUri));

        // Save updated list to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CERTIFICATIONS_LIST", gson.toJson(certificationList));
        editor.apply();

        Toast.makeText(this, "Certification Saved!", Toast.LENGTH_SHORT).show();

        // Redirect to CertificationsView
        startActivity(new Intent(this, CertificationsView.class));
        finish();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            certificateImageUri = data.getData();
            Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
        }
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
