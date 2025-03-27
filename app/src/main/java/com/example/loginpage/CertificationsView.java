package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.CertificationsAdapter;
import com.example.loginpage.models.CertificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CertificationsView extends AppCompatActivity {

    private RecyclerView certificationsRecyclerView;
    private CertificationsAdapter adapter;
    private List<CertificationModel> certificationList;
    private SharedPreferences sharedPreferences;
    private ImageView addCertification, backButton;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_certifications_view);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        backButton = findViewById(R.id.imageView150);
        certificationsRecyclerView = findViewById(R.id.certificationRecyclerView);
        addCertification = findViewById(R.id.imageView91);
        buttonContinue = findViewById(R.id.button27);

        // Initialize RecyclerView
        certificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        certificationList = new ArrayList<>();
        adapter = new CertificationsAdapter(this, certificationList, this::deleteCertification);
        certificationsRecyclerView.setAdapter(adapter);

        certificationsRecyclerView.post(() -> Log.d("CertificationsView", "RecyclerView Adapter Set"));

        Log.d("CertificationsView", "Fetching data from DB...");
        fetchCertificatesFromDB();  // Make sure this is called only once

        addCertification.setOnClickListener(v -> {
            Intent intent = new Intent(CertificationsView.this, CertificationsAdd.class);
            startActivity(intent);
        });

        buttonContinue.setOnClickListener(v -> {
            startActivity(new Intent(CertificationsView.this, TeachersInfo.class));
            finish();
        });

        backButton.setOnClickListener(v -> finish());

    }


    private void fetchCertificatesFromDB() {
        int userId = sharedPreferences.getInt("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper.UserWiseCertificateSelect(this, userId, new DatabaseHelper.DatabaseCallback() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                if (!result.isEmpty()) {
                    certificationList.clear();
                    for (Map<String, String> certificateData : result) {
                        CertificationModel cert = new CertificationModel(
                                certificateData.get("CertificateName"),
                                certificateData.get("IssuingOrganization"),
                                certificateData.get("IssueYear"),
                                certificateData.get("CredentialURL"),
                                certificateData.get("CertificateFileName")
                        );

                        certificationList.add(cert);
                        Log.d("CertificationsView", "Adding Certificate: " + cert.getName()
                                + ", Organization: " + cert.getOrganisation());
                    }

                    Log.d("CertificationsView", "Final List Size Before Adapter Update: " + certificationList.size());

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        adapter.updateData(new ArrayList<>(certificationList));  // Pass a new list reference
                        adapter.notifyDataSetChanged();  // Force UI refresh
                    }, 500); // Wait for 500ms before updating UI

                } else {
                    Toast.makeText(CertificationsView.this, "No certificates found!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onMessage(String message) {
                Log.d("fetchCertificate", "ℹ️ Message: " + message);
                Toast.makeText(CertificationsView.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CertificationsView.this, "Database error: " + error, Toast.LENGTH_LONG).show();
                Log.e("fetchCertificate", "Error: " + error);
            }
        });
    }







    private void saveCertificationData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString("CERTIFICATIONS_LIST", gson.toJson(certificationList));
        editor.apply();
    }

    private void deleteCertification(int position) {
        if (position >= 0 && position < certificationList.size()) {
            certificationList.remove(position);
            saveCertificationData(); // Save changes
            adapter.updateData(certificationList); // Refresh RecyclerView
            Toast.makeText(this, "Certification deleted!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        fetchCertificatesFromDB(); // Fetch new data when activity resumes
    }
}
