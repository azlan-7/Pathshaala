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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.GradesTaughtAdapter;
import com.example.loginpage.models.GradesTaughtModel;
import com.example.loginpage.models.UserWiseGrades;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GradesStudentView extends AppCompatActivity {

    private RecyclerView gradesRecyclerView;
    private GradesTaughtAdapter adapter;
    private List<GradesTaughtModel> gradesList;
    private SharedPreferences sharedPreferences;
    private ImageView addGrades, backButton;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grades_student_view);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        gradesRecyclerView = findViewById(R.id.gradesTaughtRecyclerView);
        addGrades = findViewById(R.id.imageView96);
        buttonContinue = findViewById(R.id.button30);
        backButton = findViewById(R.id.imageView153);

        gradesList = new ArrayList<>();
        adapter = new GradesTaughtAdapter(this, gradesList, this::deleteGrade);
        gradesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        gradesRecyclerView.setAdapter(adapter);


        // Fetch grades from database
        fetchGradesFromDatabase();

        addGrades.setOnClickListener(v -> startActivity(new Intent(this, GradesStudentAdd.class)));

        buttonContinue.setOnClickListener(v -> {
            startActivity(new Intent(this, TeachersInfo.class));
            finish();
        });

        backButton.setOnClickListener(v -> finish());

    }

    private void fetchGradesFromDatabase() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Log.e("GradesStudentView", "❌ User ID not found in SharedPreferences!");
            return;
        }

        Log.d("GradesStudentView", "📌 Fetching Grades Taught for UserID: " + userId);

        DatabaseHelper.UserWiseGradesSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseGradesResultListener() {
            @Override
            public void onQueryResult(List<UserWiseGrades> userWiseGradesList) {
                if (userWiseGradesList == null || userWiseGradesList.isEmpty()) {
                    Log.d("GradesStudentView", "⚠️ No grades data found for UserID: " + userId);
                    Toast.makeText(GradesStudentView.this, "No Grades Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ Clear the existing list before adding new data
                gradesList.clear();

                for (UserWiseGrades grade : userWiseGradesList) {
                    gradesList.add(new GradesTaughtModel(grade.getSubjectName(), "", grade.getGradename(),grade.getUserId()));
                    Log.d("GradesStudentView", "✅ Loaded Grade: " + grade.getGradename() + " | Subject: " + grade.getSubjectName());
                }

                // ✅ Ensure adapter is updated and notified
                runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();  // 🚀 Refresh RecyclerView with new data
                });
            }
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
