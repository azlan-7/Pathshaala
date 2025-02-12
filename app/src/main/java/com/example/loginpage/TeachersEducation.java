package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class TeachersEducation extends AppCompatActivity {

    private List<Education> educationList;
    private EducationAdapter educationAdapter;
    private SharedPreferences sharedPreferences;
    private RecyclerView educationRecyclerView;
    private AutoCompleteTextView textViewDegree, textViewYear;
    private EditText etInstitution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_education);


        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        educationRecyclerView = findViewById(R.id.educationRecyclerView);
        textViewDegree = findViewById(R.id.textView38);
        textViewYear = findViewById(R.id.yearDropdown);
        etInstitution = findViewById(R.id.editTextText15);
        ImageView addEducation = findViewById(R.id.imageView73);


        educationList = loadEducationData(this);
        educationAdapter = new EducationAdapter(this,educationList);
        educationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        educationRecyclerView.setAdapter(educationAdapter);

        addEducation.setOnClickListener(v -> {
            String institution = etInstitution.getText().toString().trim();
            String degree = textViewDegree.getText().toString().trim();
            String year = textViewYear.getText().toString().trim();

            if (institution.isEmpty() || degree.isEmpty() || year.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            educationList.add(new Education(institution, degree, year));
            educationAdapter.notifyDataSetChanged();
            saveEducationData(); // Save the list in SharedPreferences
            Toast.makeText(TeachersEducation.this, "Education saved successfully!", Toast.LENGTH_SHORT).show();
            etInstitution.setText("");
            textViewDegree.setText("");
            textViewYear.setText("");
        });





        Button saveButton = findViewById(R.id.button16);

        // Set Click Listener
        saveButton.setOnClickListener(v -> {
            // Create Intent to move to ProfessionalDetails.java
            Toast.makeText(TeachersEducation.this, "Education saved successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TeachersEducation.this, TeachersEducationView.class);
            startActivity(intent); // Start the new activity
            finish(); // Optional: Close TeachersEducation so user can't go back
        });

        AutoCompleteTextView etDegree = findViewById(R.id.textView38);
        AutoCompleteTextView yearDropdown = findViewById(R.id.yearDropdown);

        List<String> years = new ArrayList<>();
        for (int i = 2025; i >= 1960; i--) {
            years.add(String.valueOf(i));
        }




        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years);
        yearDropdown.setAdapter(adapter);

        yearDropdown.setOnClickListener(v -> yearDropdown.showDropDown());

        String[] distinctions = {"Doctorate", "Post Graduate", "College Graduate", "High School", "Middle School"};

        ArrayAdapter<String> adapterDegree = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, distinctions);
        etDegree.setAdapter(adapterDegree);

        etDegree.setOnClickListener(v -> etDegree.showDropDown());




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void saveEducationData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(educationList);
        editor.putString("EDUCATION_LIST", json);
        editor.apply();
    }
    public List<Education> loadEducationData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("EDUCATION_LIST", "");

        if (!json.isEmpty()) {
            Type type = new TypeToken<List<Education>>() {}.getType();
            return new Gson().fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }
}