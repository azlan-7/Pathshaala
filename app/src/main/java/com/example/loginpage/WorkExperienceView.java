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
import java.util.Map;

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

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Initialize UI elements
        addWorkExperience = findViewById(R.id.imageView81);
        buttonContinue = findViewById(R.id.button25);
        workExperienceRecyclerView = findViewById(R.id.workExperienceRecyclerView);

        workExperienceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        workExperienceList = new ArrayList<>();
        workExperienceAdapter = new WorkExperienceAdapter(this, workExperienceList);
        workExperienceRecyclerView.setAdapter(workExperienceAdapter);

        fetchWorkExperience();  // ✅ Fetch data after initializing the adapter


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

    private void fetchWorkExperience() {
        int userId = sharedPreferences.getInt("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("WorkExperienceView", "🟢 Fetching work experience for UserID: " + userId);

        DatabaseHelper.UserWiseWorkExperienceSelect(this, "4", String.valueOf(userId), new DatabaseHelper.WorkExperienceCallback() {
            @Override
            public void onSuccess(List<UserWiseWorkExperience> result) {
                if (result == null || result.isEmpty()) {
                    Log.e("WorkExperienceView", "❌ No work experience data retrieved!");
                    return;
                }

                List<WorkExperienceModel> newList = new ArrayList<>();

                for (UserWiseWorkExperience row : result) {
                    WorkExperienceModel experience = new WorkExperienceModel(
                            row.getProfessionName(),
                            row.getInstitutionName(),
                            row.getDesignationName(),
                            row.getWorkExperience(),
                            row.getCurPreExperience(),
                            row.getProfessionId(),
                            row.getUserId()
                    );

                    newList.add(experience);
                }

                Log.d("WorkExperienceView", "Total items to display: " + newList.size());

                runOnUiThread(() -> {
                    workExperienceAdapter.updateData(newList);
                    workExperienceRecyclerView.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onMessage(String message) {
                runOnUiThread(() -> Toast.makeText(WorkExperienceView.this, message, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String error) {
                Log.e("WorkExperienceView", "❌ Error fetching work experience: " + error);
                runOnUiThread(() -> Toast.makeText(WorkExperienceView.this, "Database error: " + error, Toast.LENGTH_LONG).show());
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
        fetchWorkExperience();
    }


    private void deleteExperience(int position) {
        if (position >= 0 && position < workExperienceList.size()) {
            workExperienceList.remove(position); // Remove item from list
            workExperienceAdapter.updateData(workExperienceList); // Update RecyclerView
            saveWorkExperienceData(); // Save updated list in SharedPreferences
        }
    }
}


