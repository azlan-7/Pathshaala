package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.loginpage.adapters.WorkExperienceAdapter;
import com.example.loginpage.models.UserWiseWorkExperience;
import com.example.loginpage.models.WorkExperienceModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WorkExperienceView extends AppCompatActivity {

    private RecyclerView workExperienceRecyclerView;
    private static final String TAG = "WorkExperienceView";
    private WorkExperienceAdapter workExperienceAdapter;

    private List<WorkExperienceModel> workExperienceList;
    private SharedPreferences sharedPreferences;
    private ImageView addWorkExperience;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_work_experience_view);

        // Initialize UI elements
        addWorkExperience = findViewById(R.id.imageView81);
        buttonContinue = findViewById(R.id.button25);
        workExperienceRecyclerView = findViewById(R.id.workExperienceRecyclerView);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        workExperienceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load work experience data
        // Initialize empty list
        workExperienceList = new ArrayList<>();
        workExperienceAdapter = new WorkExperienceAdapter(this, workExperienceList, this::deleteExperience);
        workExperienceRecyclerView.setAdapter(workExperienceAdapter);

        // ✅ Fetch data from DB
        fetchUserWorkExperienceDetails();


        // ✅ Log the retrieved data
        for (WorkExperienceModel exp : workExperienceList) {
            Log.d("WorkExperienceView", "✅ Loaded: " +
                    "Institution=" + exp.getInstitutionName() + " | " +
                    "Designation=" + exp.getDesignationName() + " | " +
                    "Experience=" + exp.getWorkExperience());
        }


        // Add work experience button
        addWorkExperience.setOnClickListener(v -> {
            Intent intent = new Intent(WorkExperienceView.this, WorkExperience.class);
            startActivity(intent);
        });

        // Continue button logic
        buttonContinue.setOnClickListener(v -> {
            Toast.makeText(WorkExperienceView.this, "Proceeding to next section!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(WorkExperienceView.this, TeachersInfo.class));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchUserWorkExperienceDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Log.e(TAG, "❌ User ID not found in SharedPreferences!");
            return;
        }

        String userIdString = String.valueOf(userId);
        Log.d(TAG, "📌 Fetching work experience data for user: " + userIdString);

        DatabaseHelper.UserWiseWorkExperienceSelect(this, "4", userIdString, new DatabaseHelper.UserWiseWorkExperienceResultListener() {
            @Override
            public void onQueryResult(List<UserWiseWorkExperience> userWiseWorkExperienceList) {
                if (userWiseWorkExperienceList.isEmpty()) {
                    Log.e(TAG, "⚠️ No work experience records found in DB for UserID: " + userIdString);
                    return;
                }

                workExperienceList.clear();
                for (UserWiseWorkExperience workExp : userWiseWorkExperienceList) {
                    Log.d(TAG, "✅ Mapping data: Institution=" + workExp.getInstitutionName() +
                            ", Designation=" + workExp.getDesignationName() +
                            ", Experience=" + workExp.getWorkExperience() +
                            ", ExperienceType=" + workExp.getCurPreExperience());

                    workExperienceList.add(new WorkExperienceModel(
                            workExp.getInstitutionName(),
                            workExp.getDesignationName() != null ? workExp.getDesignationName() : "Unknown",
                            workExp.getWorkExperience() != null ? workExp.getWorkExperience() : "Unknown",
                            workExp.getCurPreExperience(),
                            workExp.getUserId()
                    ));
                }

                runOnUiThread(() -> {
                    workExperienceAdapter.notifyDataSetChanged();
                    Log.d(TAG, "✅ Work experience data updated in RecyclerView.");
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Failed to fetch work experience records: " + error);
                runOnUiThread(() -> Toast.makeText(WorkExperienceView.this, "Error fetching work experience details!", Toast.LENGTH_SHORT).show());
            }
        });
    }




    private void saveWorkExperienceData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Log.e("WorkExperienceView", "❌ No USER_ID found in SharedPreferences!");
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String updatedJson = gson.toJson(workExperienceList);
        editor.putString("WORK_EXPERIENCE_LIST_" + userId, updatedJson); // ✅ User-specific key
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ✅ Fetch Work Experience from DB instead of SharedPreferences
        fetchUserWorkExperienceDetails();
    }


    private void deleteExperience(int position) {
        if (position >= 0 && position < workExperienceList.size()) {
            workExperienceList.remove(position); // Remove item from list
            workExperienceAdapter.updateData(workExperienceList); // Update RecyclerView
            saveWorkExperienceData(); // Save updated list in SharedPreferences
        }
    }
}


