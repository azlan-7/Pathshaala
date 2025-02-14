package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.models.WorkExperienceModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WorkExperience extends AppCompatActivity {

    private EditText etProfession, etInstitution, etDesignation;
    private AutoCompleteTextView experienceDropdown;
    private Button btnSave;
    private RadioGroup radioGroupWork;
    private RadioButton radioCurrent, radioPrevious;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_work_experience);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        etProfession = findViewById(R.id.editTextText23);
        etInstitution = findViewById(R.id.editTextText24);
        etDesignation = findViewById(R.id.editTextText25);
        experienceDropdown = findViewById(R.id.experienceDropdown);
        btnSave = findViewById(R.id.button24);
        radioPrevious = findViewById(R.id.radioPrevious);
        radioCurrent = findViewById(R.id.radioCurrent);
        radioGroupWork = findViewById(R.id.radioGroupWork);

        radioCurrent.setChecked(true);
        etProfession.setHint("Current Profession");


        radioGroupWork.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioCurrent) {
                etProfession.setHint("Current Profession");
                radioCurrent.setTextColor(ContextCompat.getColor(this, R.color.blue));
                radioPrevious.setTextColor(ContextCompat.getColor(this, R.color.black));
            } else if (checkedId == R.id.radioPrevious) {
                etProfession.setHint("Previous Profession"); // ✅ Hint now updates properly!
                radioPrevious.setTextColor(ContextCompat.getColor(this, R.color.blue));
                radioCurrent.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        });

        String[] experienceOptions = {"1 Year", "2 Years", "3 Years", "5+ Years", "10+ Years"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, experienceOptions);
        experienceDropdown.setAdapter(adapter);
        experienceDropdown.setThreshold(0);
        experienceDropdown.setOnClickListener(v -> experienceDropdown.showDropDown());

        btnSave.setOnClickListener(v -> saveWorkExperience());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void saveWorkExperience() {


        String profession = etProfession.getText().toString().trim();
        String institution = etInstitution.getText().toString().trim();
        String designation = etDesignation.getText().toString().trim();
        String experience = experienceDropdown.getText().toString().trim();

        if (profession.isEmpty() || institution.isEmpty() || designation.isEmpty() || experience.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<WorkExperienceModel> workExperienceList = loadWorkExperienceData();
        WorkExperienceModel newExperience = new WorkExperienceModel(profession, institution, designation, experience, radioCurrent.isChecked() ? "Current" : "Previous");
        workExperienceList.add(newExperience);



        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String updatedJson = gson.toJson(workExperienceList);

        editor.putString("WORK_EXPERIENCE_LIST", gson.toJson(workExperienceList));
        editor.apply();


        Toast.makeText(this, "Work Experience Saved!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, WorkExperienceView.class));
        finish();
    }

    private List<WorkExperienceModel> loadWorkExperienceData() {
        String json = sharedPreferences.getString("WORK_EXPERIENCE_LIST", null);
        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<WorkExperienceModel>>() {}.getType();
        return gson.fromJson(json, type);
    }
}



























//package com.example.loginpage;
//
//import android.os.Bundle;
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//import android.view.View;
//
//import com.example.loginpage.models.WorkExperienceModel;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//
//public class WorkExperience extends AppCompatActivity {
//
//    private EditText etProfession, etInstitution, etDesignation;
//    private AutoCompleteTextView experienceDropdown;
//    private Button btnSave;
//    private RadioGroup radioGroupWork;
//    private RadioButton radioCurrent, radioPrevious;
//    private SharedPreferences sharedPreferences;
//    private List<WorkExperienceModel> workExperienceList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_work_experience);
//
//        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//
//        // Initialize UI elements
//        etProfession = findViewById(R.id.editTextText23);
//        etInstitution = findViewById(R.id.editTextText24);
//        etDesignation = findViewById(R.id.editTextText25);
//        experienceDropdown = findViewById(R.id.experienceDropdown);
//        btnSave = findViewById(R.id.button24);
//        radioPrevious = findViewById(R.id.radioPrevious);
//        radioCurrent = findViewById(R.id.radioCurrent);
//        radioGroupWork = findViewById(R.id.radioGroupWork);
//
//        radioCurrent.setChecked(true);
//        etProfession.setHint("Current Profession");
//
//        String[] experienceOptions = {"1 Year", "2 Years", "3 Years", "5+ Years", "10+ Years"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, experienceOptions);
//        experienceDropdown.setAdapter(adapter);
//        experienceDropdown.setThreshold(0);
//        experienceDropdown.setOnClickListener(v -> experienceDropdown.showDropDown());
//
//        // Load saved data
//        workExperienceList = loadWorkExperienceData();
//
//        // Disable "Current" radio button if a current profession exists
//        if (hasCurrentProfession(workExperienceList)) {
//            radioCurrent.setEnabled(true);
//        }
//        else {
//            radioCurrent.setChecked(true);
//        }
//
//        // Handle Radio Button Selection
//        radioGroupWork.setOnCheckedChangeListener((group, checkedId) -> {
//            if (checkedId == R.id.radioCurrent) {
//                etProfession.setHint("Current Profession");
//                radioCurrent.setTextColor(ContextCompat.getColor(this, R.color.blue));
//            } else if (checkedId == R.id.radioPrevious) {
//                etProfession.setHint("Previous Profession");
//                radioPrevious.setTextColor(ContextCompat.getColor(this, R.color.blue));
//            }
//        });
//
//        // Save Button Click Listener
//        btnSave.setOnClickListener(v -> saveWorkExperience());
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//
//    /**
//     * Saves work experience based on user input and type selection.
//     */
//    private void saveWorkExperience() {
//        String profession = etProfession.getText().toString().trim();
//        String institution = etInstitution.getText().toString().trim();
//        String designation = etDesignation.getText().toString().trim();
//        String experience = experienceDropdown.getText().toString().trim();
//        String experienceType = radioCurrent.isChecked() ? "Current" : "Previous";  //
//
//        if (profession.isEmpty() || institution.isEmpty() || designation.isEmpty() || experience.isEmpty()) {
//            Toast.makeText(WorkExperience.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//
//        // Load existing data
//        workExperienceList = loadWorkExperienceData();
//        if (workExperienceList == null) {
//            workExperienceList = new ArrayList<>();
//        }
//
//        // Ensure only one "Current" profession exists
//        if (experienceType.equals("Current")) {
//            workExperienceList.removeIf(exp ->
//                    exp.getExperienceType() != null && exp.getExperienceType().equals("Current"));
//        }
//
//        // Create new experience entry
//        WorkExperienceModel newExperience = new WorkExperienceModel(profession, institution, designation, experience, experienceType);
//        workExperienceList.add(newExperience);
//
//        // Save updated list
//        String updatedJson = gson.toJson(workExperienceList);
//        editor.putString("WORK_EXPERIENCE_LIST", updatedJson);
//        editor.apply();
//
//        Toast.makeText(this, "Work Experience Saved!", Toast.LENGTH_SHORT).show();
//
//        // Redirect to WorkExperienceView
//        startActivity(new Intent(this, WorkExperienceView.class));
//        finish();
//    }
//
//    /**
//     * Checks if a "Current" profession exists in the saved work experience list.
//     * @param experienceList List of work experiences
//     * @return true if a "Current" profession exists, otherwise false
//     */
//    private boolean hasCurrentProfession(List<WorkExperienceModel> experienceList) {
//        if (experienceList == null) return false;
//        for (WorkExperienceModel experience : experienceList) {
//            if ("Current".equals(experience.getExperienceType())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Loads work experience data from SharedPreferences.
//     * @return List of WorkExperienceModel objects
//     */
//    private List<WorkExperienceModel> loadWorkExperienceData() {
//        String json = sharedPreferences.getString("WORK_EXPERIENCE_LIST", null);
//        if (json == null) return new ArrayList<>();
//
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<WorkExperienceModel>>() {}.getType();
//        List<WorkExperienceModel> experienceList = gson.fromJson(json, type);
//        return experienceList != null ? experienceList : new ArrayList<>();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        clearFields(); // Clears fields every time the screen opens
//    }
//
//    /**
//     * Clears input fields for a new entry.
//     */
//    private void clearFields() {
//        etProfession.setText("");
//        etInstitution.setText("");
//        etDesignation.setText("");
//        experienceDropdown.setText("");
//        radioCurrent.setChecked(true); // Reset to current profession by default
//    }
//}
//
//
//
//
//




















//package com.example.loginpage;
//
//import android.os.Bundle;
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//import android.view.View;
//
//import com.example.loginpage.models.WorkExperienceModel;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class WorkExperience extends AppCompatActivity {
//
//    private EditText etProfession, etInstitution, etDesignation;
//    private AutoCompleteTextView experienceDropdown;
//    private Button btnSave;
//    private RadioGroup radioGroupWork;
//    private RadioButton radioCurrent, radioPrevious;
//    private SharedPreferences sharedPreferences;
//    private List<WorkExperienceModel> workExperienceList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_work_experience);
//
//        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//
//        // Initialize UI elements
//        etProfession = findViewById(R.id.editTextText23);
//        etInstitution = findViewById(R.id.editTextText24);
//        etDesignation = findViewById(R.id.editTextText25);
//        experienceDropdown = findViewById(R.id.experienceDropdown);
//        btnSave = findViewById(R.id.button24);
//        radioPrevious = findViewById(R.id.radioPrevious);
//        radioCurrent = findViewById(R.id.radioCurrent);
//        radioGroupWork = findViewById(R.id.radioGroupWork);
//
//        radioCurrent.setChecked(true);
//        etProfession.setHint("Current Profession");
//
//        String[] experienceOptions = {"1 Year", "2 Years", "3 Years", "5+ Years", "10+ Years"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, experienceOptions);
//        experienceDropdown.setAdapter(adapter);
//        experienceDropdown.setThreshold(0);
//        experienceDropdown.setOnClickListener(v -> experienceDropdown.showDropDown());
//
//        // Load saved data
//        workExperienceList = loadWorkExperienceData();
//
//        // Check if a "Current" profession exists, disable selection if found
//        if (hasCurrentProfession(workExperienceList)) {
//            radioCurrent.setEnabled(false);
//        }
//
//        // Handle Radio Button Selection
//        radioGroupWork.setOnCheckedChangeListener((group, checkedId) -> {
//            if (checkedId == R.id.radioCurrent) {
//                etProfession.setHint("Current Profession");
//                radioCurrent.setTextColor(ContextCompat.getColor(this, R.color.blue));
//            } else if (checkedId == R.id.radioPrevious) {
//                etProfession.setHint("Previous Profession");
//                radioPrevious.setTextColor(ContextCompat.getColor(this, R.color.blue));
//            }
//        });
//
//        // Save Button Click Listener
//        btnSave.setOnClickListener(v -> saveWorkExperience());
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//
//    /**
//     * Saves work experience based on user input and type selection.
//     */
//    private void saveWorkExperience() {
//        String profession = etProfession.getText().toString().trim();
//        String institution = etInstitution.getText().toString().trim();
//        String designation = etDesignation.getText().toString().trim();
//        String experience = experienceDropdown.getText().toString().trim();
//        String experienceType = radioCurrent.isChecked() ? "Current" : "Previous";
//
//        if (profession.isEmpty() || institution.isEmpty() || designation.isEmpty() || experience.isEmpty()) {
//            Toast.makeText(WorkExperience.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//
//        // Load existing data
//        workExperienceList = loadWorkExperienceData();
//        if (workExperienceList == null) {
//            workExperienceList = new ArrayList<>();
//        }
//
//        if (radioCurrent.isChecked()) {
//            // Ensure only one "Current" profession exists
//            workExperienceList.removeIf(exp ->
//                    exp.getExperienceType() != null && exp.getExperienceType().equals(experienceType));
//        }
//
//        // Create new experience entry
//        WorkExperienceModel newExperience = new WorkExperienceModel(profession, institution, designation, experience,
//                radioCurrent.isChecked() ? "Current" : "Previous");
//
//        workExperienceList.add(newExperience);
//
//        // Save updated list
//        String updatedJson = gson.toJson(workExperienceList);
//        editor.putString("WORK_EXPERIENCE_LIST", updatedJson);
//        editor.apply();
//
//        Toast.makeText(this, "Work Experience Saved!", Toast.LENGTH_SHORT).show();
//
//        // Redirect to WorkExperienceView
//        startActivity(new Intent(this, WorkExperienceView.class));
//        finish();
//    }
//
//    /**
//     * Checks if a "Current" profession exists in the saved work experience list.
//     * @param experienceList List of work experiences
//     * @return true if a "Current" profession exists, otherwise false
//     */
//    private boolean hasCurrentProfession(List<WorkExperienceModel> experienceList) {
//        if (experienceList == null) return false;
//        for (WorkExperienceModel experience : experienceList) {
//            if ("Current".equals(experience.getExperienceType())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Loads work experience data from SharedPreferences.
//     * @return List of WorkExperienceModel objects
//     */
//    private List<WorkExperienceModel> loadWorkExperienceData() {
//        String json = sharedPreferences.getString("WORK_EXPERIENCE_LIST", null);
//        if (json == null) return new ArrayList<>();
//
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<WorkExperienceModel>>() {}.getType();
//        List<WorkExperienceModel> experienceList = gson.fromJson(json, type);
//        return experienceList != null ? experienceList : new ArrayList<>();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        clearFields(); // Clears fields every time the screen opens
//    }
//
//    /**
//     * Clears input fields for a new entry.
//     */
//    private void clearFields() {
//        etProfession.setText("");
//        etInstitution.setText("");
//        etDesignation.setText("");
//        experienceDropdown.setText("");
//        radioCurrent.setChecked(true); // Reset to current profession by default
//    }
//}
//
//
//
//
//
//




















//package com.example.loginpage;
//
//import android.os.Bundle;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.widget.ImageView;
//import android.widget.Toast;
//import android.view.View;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//
//import com.example.loginpage.models.WorkExperienceModel;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//
//
//
//public class WorkExperience extends AppCompatActivity {
//
//    private EditText etProfession, etInstitution, etDesignation;
//    private AutoCompleteTextView experienceDropdown;
//    private Button btnSave;
//    private RadioGroup radioGroupWork;
//    private RadioButton radioCurrent, radioPrevious;
//    private Set<String> previousProfessions = new HashSet<>();
//    private SharedPreferences sharedPreferences;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_work_experience);
//
//        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//
//
//        etProfession = findViewById(R.id.editTextText23);
//        etInstitution = findViewById(R.id.editTextText24);
//        etDesignation = findViewById(R.id.editTextText25);
//        experienceDropdown = findViewById(R.id.experienceDropdown);
//        btnSave = findViewById(R.id.button24); // Add this button in XML
//        radioPrevious = findViewById(R.id.radioPrevious);
//        radioGroupWork = findViewById(R.id.radioGroupWork);
//
//
//        radioCurrent = findViewById(R.id.radioCurrent);
//        radioCurrent.setChecked(true);
//        etProfession.setHint("Current Profession");
//
//
//
//        String[] experienceOptions = {"1 Year", "2 Years", "3 Years", "5+ Years", "10+ Years"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, experienceOptions);
//        experienceDropdown.setAdapter(adapter);
//        experienceDropdown.setThreshold(0); // Opens dropdown without typing
//        experienceDropdown.setOnClickListener(v -> experienceDropdown.showDropDown());
//
//        // Radio Button Selection Handling
//        radioGroupWork.setOnCheckedChangeListener((group, checkedId) -> {
//            if (checkedId == R.id.radioCurrent) {
//                etProfession.setHint("Current Profession");
//                radioCurrent.setTextColor(ContextCompat.getColor(this,R.color.blue));
//            } else if (checkedId == R.id.radioPrevious) {
//                etProfession.setHint("Previous Profession");
//                radioPrevious.setTextColor(ContextCompat.getColor(this,R.color.blue));
//            }
//        });
//
//        loadSavedData();
//
//        // Save Button Click Listener
//        btnSave.setOnClickListener(v -> {
//            String profession = etProfession.getText().toString().trim();
//            String institution = etInstitution.getText().toString().trim();
//            String designation = etDesignation.getText().toString().trim();
//            String experience = experienceDropdown.getText().toString().trim();
//
//
//            if (profession.isEmpty() || institution.isEmpty() || designation.isEmpty() || experience.isEmpty()) {
//                Toast.makeText(WorkExperience.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//
//
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            if (radioCurrent.isChecked()) {
//                editor.putString("WORK_PROFESSION", profession);
//                editor.putString("WORK_INSTITUTION", institution);
//                editor.putString("WORK_DESIGNATION", designation);
//                editor.putString("WORK_EXPERIENCE", experience);
//            } else if (radioPrevious.isChecked()) {
//                // Save multiple previous professions
//                Set<String> previousProfessions = sharedPreferences.getStringSet("PREVIOUS_PROFESSIONS", new HashSet<>());
//                previousProfessions.add(profession);
//                editor.putStringSet("PREVIOUS_PROFESSIONS", previousProfessions);
//            }
//
//            editor.apply();
//
//            saveWorkExperience();
//            Toast.makeText(WorkExperience.this, "Work Experience Saved!", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(WorkExperience.this, WorkExperienceView.class); // Redirect to another page
//            startActivity(intent);
//            finish();
//        });
//
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//
//    private void saveWorkExperience() {
//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        // Retrieve existing data
//        Gson gson = new Gson();
//
//        String json = sharedPreferences.getString("WORK_EXPERIENCE_LIST", null);
//        Type type = new TypeToken<List<WorkExperienceModel>>() {}.getType();
//        List<WorkExperienceModel> experienceList = (json == null) ? new ArrayList<>() : gson.fromJson(json, type);
//
//
//        WorkExperienceModel newExperience = new WorkExperienceModel(
//                etProfession.getText().toString().trim(),
//                etInstitution.getText().toString().trim(),
//                etDesignation.getText().toString().trim(),
//                experienceDropdown.getText().toString().trim()
//        );
//
//        experienceList.add(newExperience); // Append new data to list
//
//        // Save the updated list back to SharedPreferences
//        String updatedJson = gson.toJson(experienceList);
//        editor.putString("WORK_EXPERIENCE_LIST", updatedJson);
//        editor.apply();
//        Log.d("WorkExperience", "Saved work experience: " + updatedJson);
//    }
//    private void loadSavedData() {
//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        etProfession.setText(sharedPreferences.getString("WORK_PROFESSION", ""));
//        etInstitution.setText(sharedPreferences.getString("WORK_INSTITUTION", ""));
//        etDesignation.setText(sharedPreferences.getString("WORK_DESIGNATION", ""));
//        experienceDropdown.setText(sharedPreferences.getString("WORK_EXPERIENCE", ""));
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        clearFields(); // Clears fields every time the screen opens
//    }
//
//    private void clearFields() {
//        etProfession.setText("");
//        etInstitution.setText("");
//        etDesignation.setText("");
//        experienceDropdown.setText("");
//        radioCurrent.setChecked(true); // Reset to current profession by default
//    }
//
//}
