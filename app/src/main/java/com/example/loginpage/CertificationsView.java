package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
    private ImageView addCertification;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_certifications_view);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        certificationsRecyclerView = findViewById(R.id.certificationRecyclerView);
        addCertification = findViewById(R.id.imageView91);
        buttonContinue = findViewById(R.id.button27);
        TextView textView110 = findViewById(R.id.textView110);

        textView110.setOnClickListener(v -> fetchCertificatesFromDB());

        // Load certifications
        certificationList = loadCertifications();

        // Setup RecyclerView
        certificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CertificationsAdapter(this, certificationList, this::deleteCertification);
        certificationsRecyclerView.setAdapter(adapter);

        // Add Certification Button - Open CertificationsAdd
        addCertification.setOnClickListener(v -> {
            Intent intent = new Intent(CertificationsView.this, CertificationsAdd.class);
            startActivity(intent);
        });

        // Continue Button - Redirect to TeachersInfo
        buttonContinue.setOnClickListener(v -> {
            startActivity(new Intent(CertificationsView.this, TeachersInfo.class));
            finish();
        });
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
                        certificationList.add(new CertificationModel(
                                certificateData.get("CertificateName"),
                                certificateData.get("IssueingOrganization"),  // ✅ Matches stored procedure
                                certificateData.get("IssueYear"),
                                certificateData.get("CredentialURL"),
                                certificateData.get("CertificateFileName")
                        ));
                    }
                    adapter.updateData(certificationList);
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





    private List<CertificationModel> loadCertifications() {
        String json = sharedPreferences.getString("CERTIFICATIONS_LIST", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<CertificationModel>>() {}.getType();

        return (json == null) ? new ArrayList<>() : gson.fromJson(json, type);
    }


    private void deleteCertification(int position) {
        if (position >= 0 && position < certificationList.size()) {
            certificationList.remove(position);
            saveCertificationData(); // Save changes
            adapter.updateData(certificationList); // Refresh RecyclerView
            Toast.makeText(this, "Certification deleted!", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveCertificationData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString("CERTIFICATIONS_LIST", gson.toJson(certificationList));
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        certificationList = loadCertifications();
        adapter.updateData(certificationList);
    }
}



















//package com.example.loginpage;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.loginpage.models.CertificationModel;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//
//public class CertificationsView extends AppCompatActivity {
//
//    private EditText etCertificationTitle, etOrganisation, etYear, etCredentialUrl;
//    private RecyclerView certificationRecyclerView;
//    private Button btnSave;
//    private SharedPreferences sharedPreferences;
//    private List<CertificationModel> certificationList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_certifications_add);
//
//        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        certificationRecyclerView = findViewById(R.id.certificationRecyclerView);
//        etCertificationTitle = findViewById(R.id.editTextText26);
//        etOrganisation = findViewById(R.id.editTextText27);
//        etYear = findViewById(R.id.editTextText28);
//        etCredentialUrl = findViewById(R.id.editTextUrl);
//        btnSave = findViewById(R.id.button28);
//
//        certificationList = loadCertificationData();
//
//        btnSave.setOnClickListener(v -> saveCertification());
//    }
//
//    private void saveCertification() {
//        String title = etCertificationTitle.getText().toString().trim();
//        String organisation = etOrganisation.getText().toString().trim();
//        String year = etYear.getText().toString().trim();
//        String credentialUrl = etCredentialUrl.getText().toString().trim();
//
//        if (title.isEmpty() || organisation.isEmpty() || year.isEmpty() || credentialUrl.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        certificationList.add(new CertificationModel(title, organisation, year, credentialUrl));
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        editor.putString("CERTIFICATION_LIST", gson.toJson(certificationList));
//        editor.apply();
//
//        Toast.makeText(this, "Certification Saved!", Toast.LENGTH_SHORT).show();
//
//        startActivity(new Intent(this, CertificationsAdd.class));
//        finish();
//    }
//
//    private List<CertificationModel> loadCertificationData() {
//        String json = sharedPreferences.getString("CERTIFICATION_LIST", null);
//        if (json == null) return new ArrayList<>();
//
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<CertificationModel>>() {}.getType();
//        List<CertificationModel> list = gson.fromJson(json, type);
//        return list != null ? list : new ArrayList<>();
//    }
//}
