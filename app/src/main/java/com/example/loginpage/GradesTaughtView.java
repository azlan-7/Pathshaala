package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.adapters.GradesTaughtAdapter;
import com.example.loginpage.models.GradesTaughtModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GradesTaughtView extends AppCompatActivity {

    private RecyclerView gradesRecyclerView;
    private GradesTaughtAdapter adapter;
    private List<GradesTaughtModel> gradesList;
    private SharedPreferences sharedPreferences;
    private ImageView addGrades;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grades_taught_view);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        gradesRecyclerView = findViewById(R.id.gradesTaughtRecyclerView);
        addGrades = findViewById(R.id.imageView96);
        buttonContinue = findViewById(R.id.button30);

        gradesList = loadGrades();
        adapter = new GradesTaughtAdapter(this, gradesList, this::deleteGrade);
        gradesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        gradesRecyclerView.setAdapter(adapter);

        addGrades.setOnClickListener(v -> startActivity(new Intent(this, GradesTaught.class)));

        buttonContinue.setOnClickListener(v -> {
            startActivity(new Intent(this, TeachersInfo.class));
            finish();
        });
    }

    private void deleteGrade(int position) {
        if (position >= 0 && position < gradesList.size()) {
            gradesList.remove(position);  // Remove item from list
            saveGrades();  // Save updated list
            adapter.updateData(new ArrayList<>(gradesList));  // Update adapter with a copy of the list
            Toast.makeText(this, "Grade deleted!", Toast.LENGTH_SHORT).show();
        }
    }
    private List<GradesTaughtModel> loadGrades() {
        String json = sharedPreferences.getString("GRADES_TAUGHT_LIST", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<GradesTaughtModel>>() {}.getType();
        return (json == null) ? new ArrayList<>() : gson.fromJson(json, type);
    }

    private void saveGrades() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String updatedJson = gson.toJson(gradesList);  // Convert updated list to JSON
        editor.putString("GRADES_TAUGHT_LIST", updatedJson);  // Save in SharedPreferences
        editor.apply();
    }
}
