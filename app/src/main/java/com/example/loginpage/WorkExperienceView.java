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

import com.example.loginpage.adapters.WorkExperienceAdapter;
import com.example.loginpage.models.WorkExperienceModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WorkExperienceView extends AppCompatActivity {

    private RecyclerView workExperienceRecyclerView;
    private WorkExperienceAdapter adapter;
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
        workExperienceRecyclerView = findViewById(R.id.workExperienceRecyclerView);
        addWorkExperience = findViewById(R.id.imageView81);
        buttonContinue = findViewById(R.id.button25);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        if (sharedPreferences == null) {
            Log.e("WorkExperienceView", "SharedPreferences is NULL! Exiting.");
            return;
        }

        workExperienceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load work experience data
        workExperienceList = loadWorkExperienceData();

        adapter = new WorkExperienceAdapter(this, workExperienceList, this::deleteExperience);
        workExperienceRecyclerView.setAdapter(adapter);

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

    private void saveWorkExperienceData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String updatedJson = gson.toJson(workExperienceList); // Convert updated list to JSON
        editor.putString("WORK_EXPERIENCE_LIST", updatedJson);
        editor.apply();
        Log.d("WorkExperienceView", "Updated Work Experience List Saved: " + updatedJson);
    }

    /**
     * Loads work experience data from SharedPreferences.
     */
    private List<WorkExperienceModel> loadWorkExperienceData() {
        String json = sharedPreferences.getString("WORK_EXPERIENCE_LIST", null);
        if (json == null) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<WorkExperienceModel>>() {}.getType();
        List<WorkExperienceModel> experienceList = gson.fromJson(json, type);
        return (experienceList != null) ? experienceList : new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when returning to this screen
        workExperienceList = loadWorkExperienceData();
        adapter.updateData(workExperienceList);
    }

    private void deleteExperience(int position) {
        if (position >= 0 && position < workExperienceList.size()) {
            workExperienceList.remove(position); // Remove item from list
            adapter.updateData(workExperienceList); // Update RecyclerView
            saveWorkExperienceData(); // Save updated list in SharedPreferences
        }
    }
}

























//package com.example.loginpage;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.loginpage.adapters.WorkExperienceAdapter;
//import com.example.loginpage.models.WorkExperienceModel;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//
//public class WorkExperienceView extends AppCompatActivity {
//
//    private RecyclerView previousExperienceRecyclerView;
//    private TextView currentProfessionText;
//    private WorkExperienceAdapter previousExperienceAdapter;
//    private List<WorkExperienceModel> previousExperienceList;
//    private WorkExperienceModel currentProfession;
//    private SharedPreferences sharedPreferences;
//    private ImageView addWorkExperience;
//    private Button buttonContinue;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_work_experience_view);
//
//        previousExperienceRecyclerView = findViewById(R.id.previousExperienceRecyclerView);
//        currentProfessionText = findViewById(R.id.tvCurrentProfession);
//        addWorkExperience = findViewById(R.id.imageView81);
//        buttonContinue = findViewById(R.id.button25);
//        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//
//        if (sharedPreferences == null) {
//            Log.e("WorkExperienceView", "SharedPreferences is NULL! Exiting.");
//            return;
//        }
//
//        previousExperienceList = new ArrayList<>();
//
//        previousExperienceAdapter = new WorkExperienceAdapter(this, previousExperienceList, this::deleteExperience);
//        previousExperienceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        previousExperienceRecyclerView.setAdapter(previousExperienceAdapter);
//
//
//        loadWorkExperienceData(); // Load saved data
//
//
//        // Add work experience button
//        addWorkExperience.setOnClickListener(v -> {
//            if (currentProfession != null) {
//                Toast.makeText(this, "You already have a Current Profession!", Toast.LENGTH_SHORT).show();
//            } else {
//                Intent intent = new Intent(WorkExperienceView.this, WorkExperience.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        // Continue button
//        buttonContinue.setOnClickListener(v -> {
//            Toast.makeText(WorkExperienceView.this, "Proceeding to next section!", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(WorkExperienceView.this, TeachersInfo.class));
//            finish();
//        });
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//
//    private void loadWorkExperienceData() {
//
//        previousExperienceList.clear(); // Clear before adding new data
//        currentProfession = null; // Reset current profession
//
//        String json = sharedPreferences.getString("WORK_EXPERIENCE_LIST", null);
//        if (json != null && !json.isEmpty()) {
//            Gson gson = new Gson();
//            Type type = new TypeToken<List<WorkExperienceModel>>() {}.getType();
//            List<WorkExperienceModel> allExperience = gson.fromJson(json, type);
//
//            for (WorkExperienceModel experience : allExperience) {
//                if ("Current".equals(experience.getExperienceType())) {
//                    currentProfession = experience; // Save the current profession
//                } else {
//                    previousExperienceList.add(experience); // Add to previous experience list
//                }
//            }
//        }
//
//        // Update the UI for current profession
//        if (currentProfession != null) {
//            currentProfessionText.setText(currentProfession.getProfession());
//        } else {
//            currentProfessionText.setText("No Current Profession");
//        }
//
//        // Notify adapter
//        if (previousExperienceAdapter != null) {
//            previousExperienceAdapter.updateData(previousExperienceList);
//        }
//
//    }
//
//    private void deleteExperience(int position) {
//        previousExperienceList.remove(position);
//        previousExperienceAdapter.updateData(previousExperienceList);
//        saveWorkExperienceData();
//    }
//
//    private void saveWorkExperienceData() {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        editor.putString("WORK_EXPERIENCE_LIST", gson.toJson(previousExperienceList));
//        editor.apply();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        loadWorkExperienceData(); // Reload data
//    }
//}
