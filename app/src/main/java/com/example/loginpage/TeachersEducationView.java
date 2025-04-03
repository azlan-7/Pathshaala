package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.EducationAdapter;
import com.example.loginpage.models.Education;
import com.example.loginpage.models.UserWiseEducation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TeachersEducationView extends AppCompatActivity {
    private RecyclerView educationRecyclerView;
    private ImageView backButton;
    private EducationAdapter educationAdapter;
    private List<Education> educationList;
    private static final String TAG = "TeachersEducationView";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_education_view);

        Button continueButton = findViewById(R.id.button23); // Continue Button
        ImageView editButton = findViewById(R.id.imageView74); // Pencil (Edit) Button
        backButton = findViewById(R.id.imageView145);

        backButton.setOnClickListener(v -> finish());


        // Set click listeners for buttons
        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeachersEducationView.this, TeachersInfo.class);
            startActivity(intent);
            finish();
        });

        // Navigate to TeachersEducation when Edit button (pencil) is clicked
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeachersEducationView.this, TeachersEducation.class);
            startActivity(intent);
        });

        educationRecyclerView = findViewById(R.id.educationRecyclerView);
        educationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize education list and adapter
        educationList = new ArrayList<>();
        educationAdapter = new EducationAdapter(this, educationList);
        educationRecyclerView.setAdapter(educationAdapter);

        // Fetch education details from the database
        fetchUserEducationDetails();

        // Note: You do not need loadEducationData() here anymore if you're fetching from the DB
        // loadEducationData(); // This was removed since we're fetching data from the database now

        RecyclerView recyclerView = findViewById(R.id.educationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(educationAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserEducationDetails();  // Refresh RecyclerView when returning
    }


    private void fetchUserEducationDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Log.e(TAG, "❌ User ID not found in SharedPreferences!");
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "📌 Fetching education data for user: " + userId);

        DatabaseHelper.UserWiseEducationSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseEducationResultListener() {
            @Override
            public void onQueryResult(List<UserWiseEducation> userWiseEducationList) {
                if (userWiseEducationList.isEmpty()) {
                    Log.e(TAG, "⚠️ No education records found in DB!");
                    runOnUiThread(() -> Toast.makeText(TeachersEducationView.this, "No education records found!", Toast.LENGTH_SHORT).show());
                    return;
                }

                educationList.clear();
                for (UserWiseEducation edu : userWiseEducationList) {
                    educationList.add(new Education(
                            edu.getInstitutionName(),
                            edu.getEducationLevelName(),
                            String.valueOf(edu.getPassingYear())  // Convert int to String
                    ));
                }

                // Update RecyclerView on UI thread
                runOnUiThread(() -> {
                    educationAdapter.notifyDataSetChanged();
                    Log.d(TAG, "✅ Education data updated in RecyclerView.");
                });
            }

            // ✅ Remove @Override if onError() is not in the interface
            public void onError(String error) {
                Log.e(TAG, "❌ Failed to fetch education records: " + error);
                runOnUiThread(() -> Toast.makeText(TeachersEducationView.this, "Error fetching education details!", Toast.LENGTH_SHORT).show());
            }
        });

    }

}