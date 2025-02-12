package com.example.loginpage;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.util.HashSet;
import java.util.Set;




public class WorkExperience extends AppCompatActivity {

    private EditText etProfession, etInstitution, etDesignation;
    private AutoCompleteTextView experienceDropdown;
    private Button btnSave;
    private ImageView btnAddPrevious;
    private RadioGroup radioGroupWork;
    private RadioButton radioCurrent, radioPrevious;
    private Set<String> previousProfessions = new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_work_experience);

        etProfession = findViewById(R.id.editTextText23);
        etInstitution = findViewById(R.id.editTextText24);
        etDesignation = findViewById(R.id.editTextText25);
        experienceDropdown = findViewById(R.id.experienceDropdown);
        btnSave = findViewById(R.id.button24); // Add this button in XML
        btnAddPrevious = findViewById(R.id.imageView79);
        radioPrevious = findViewById(R.id.radioPrevious);
        radioGroupWork = findViewById(R.id.radioGroupWork);
        radioCurrent = findViewById(R.id.radioCurrent);


        String[] experienceOptions = {"1 Year", "2 Years", "3 Years", "5+ Years", "10+ Years"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, experienceOptions);
        experienceDropdown.setAdapter(adapter);
        experienceDropdown.setOnClickListener(v -> experienceDropdown.showDropDown());

        // Radio Button Selection Handling
        radioGroupWork.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioCurrent) {
                etProfession.setHint("Current Profession");
                btnAddPrevious.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioPrevious) {
                etProfession.setHint("Previous Profession");
                btnAddPrevious.setVisibility(View.VISIBLE);
            }
        });

        // Add Previous Profession Button
        btnAddPrevious.setOnClickListener(v -> {
            String previousProfession = etProfession.getText().toString().trim();
            if (!previousProfession.isEmpty()) {
                previousProfessions.add(previousProfession);
                etProfession.setText(""); // Clear input
                Toast.makeText(WorkExperience.this, "Previous Profession Added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(WorkExperience.this, "Enter a profession!", Toast.LENGTH_SHORT).show();
            }
        });

        loadSavedData();

        // Save Button Click Listener
        btnSave.setOnClickListener(v -> {
            saveWorkExperience();
            Toast.makeText(WorkExperience.this, "Work Experience Saved!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WorkExperience.this, TeachersInfo.class); // Redirect to another page
            startActivity(intent);
            finish();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void saveWorkExperience() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("WORK_PROFESSION", etProfession.getText().toString().trim());
        editor.putString("WORK_INSTITUTION", etInstitution.getText().toString().trim());
        editor.putString("WORK_DESIGNATION", etDesignation.getText().toString().trim());
        editor.putString("WORK_EXPERIENCE", experienceDropdown.getText().toString().trim());
        editor.apply();
    }
    private void loadSavedData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        etProfession.setText(sharedPreferences.getString("WORK_PROFESSION", ""));
        etInstitution.setText(sharedPreferences.getString("WORK_INSTITUTION", ""));
        etDesignation.setText(sharedPreferences.getString("WORK_DESIGNATION", ""));
        experienceDropdown.setText(sharedPreferences.getString("WORK_EXPERIENCE", ""));
    }

}