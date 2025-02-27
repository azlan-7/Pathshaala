package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.adapters.EducationAdapter;
import com.example.loginpage.models.Education;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TeachersEducationView extends AppCompatActivity {
    private RecyclerView educationRecyclerView;
    private EducationAdapter educationAdapter;
    private List<Education> educationList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_education_view);

        Button continueButton = findViewById(R.id.button23); // Continue Button
        ImageView editButton = findViewById(R.id.imageView74); // Pencil (Edit) Button


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

        loadEducationData(); // Load data directly

        educationAdapter = new EducationAdapter(this,educationList);
        educationRecyclerView.setAdapter(educationAdapter);


        RecyclerView recyclerView = findViewById(R.id.educationRecyclerView);
        List<Education> educationList = new TeachersEducation().loadEducationData(this);


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
        loadEducationData();  // Refresh RecyclerView when returning
    }

    private void loadEducationData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String json = sharedPreferences.getString("EDUCATION_LIST", "");

        if (!json.isEmpty()) {
            Type type = new TypeToken<List<Education>>() {}.getType();
            educationList = new Gson().fromJson(json, type);
        } else {
            educationList = new ArrayList<>();
        }

        educationAdapter = new EducationAdapter(this,educationList);
        educationRecyclerView.setAdapter(educationAdapter);
    }

}