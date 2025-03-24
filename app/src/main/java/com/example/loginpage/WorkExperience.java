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

import com.example.loginpage.adapters.WorkExperienceAdapter;
import com.example.loginpage.models.UserWiseWorkExperience;
import com.example.loginpage.models.WorkExperienceModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkExperience extends AppCompatActivity {

    private static final String TAG = "WorkExperience";
    private Map<String, String> workExperienceMap = new HashMap<>(); // WorkExperience -> WorkExperienceID
    private Map<String, String> designationMap = new HashMap<>(); // DesignationName -> DesignationID
    private Map<String, String> professionMap = new HashMap<>(); // ProfessionName -> ProfessionID
    private EditText etProfession, etInstitution, etDesignation;
    private AutoCompleteTextView experienceDropdown,designationDropdown,professionDropdown;
    private Button btnSave;
    private RadioGroup radioGroupWork;
    private RadioButton radioCurrent, radioPrevious;
    private SharedPreferences sharedPreferences;

    private WorkExperienceAdapter workExperienceAdapter;
    private List<WorkExperienceModel> workExperienceList;

    private String selectedProfession = "";
    private int professionId = -1; // Default value


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_work_experience);

        workExperienceList = new ArrayList<>();
        workExperienceAdapter = new WorkExperienceAdapter(this, workExperienceList);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

         etProfession = findViewById(R.id.editTextText23);
         etDesignation = findViewById(R.id.editTextText25);
        etInstitution = findViewById(R.id.editTextText24);

        experienceDropdown = findViewById(R.id.experienceDropdown);
        designationDropdown = findViewById(R.id.editTextText25);
        professionDropdown = findViewById(R.id.editTextText23);
        btnSave = findViewById(R.id.button24);
        Log.d(TAG, "📌 btnSave initialized. Setting onClickListener.");
        radioPrevious = findViewById(R.id.radioPrevious);
        radioCurrent = findViewById(R.id.radioCurrent);
        radioGroupWork = findViewById(R.id.radioGroupWork);


        professionDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProfession = (String) parent.getItemAtPosition(position);
            Log.d(TAG, "📌 Profession Selected in dropdown: " + selectedProfession);
        });


        fetchUserWorkExperienceDetails();
        loadWorkExperience();
        loadDesignations();
        loadProfessions();


        radioCurrent.setChecked(true);
        etProfession.setHint("Current Profession");


        radioGroupWork.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioCurrent) {
                etProfession.setHint("Current Profession");
                radioCurrent.setTextColor(ContextCompat.getColor(this, R.color.blue));
                radioPrevious.setTextColor(ContextCompat.getColor(this, R.color.black));
            } else if (checkedId == R.id.radioPrevious) {
                etProfession.setHint("Previous Profession");
                radioPrevious.setTextColor(ContextCompat.getColor(this, R.color.blue));
                radioCurrent.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        });

        String[] experienceOptions = {"1 Year", "2 Years", "3 Years", "5+ Years", "10+ Years"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, experienceOptions);
        experienceDropdown.setAdapter(adapter);
        experienceDropdown.setThreshold(0);
        experienceDropdown.setOnClickListener(v -> {
            Log.d(TAG, "📌 experienceDropdown clicked - Showing dropdown");
            experienceDropdown.showDropDown();
        });

        btnSave.setOnClickListener(v -> {
            Log.d(TAG, "📌 Save Button Clicked! Calling saveWorkExperience().");
            saveWorkExperience();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "📌 onResume() called. Reloading dropdowns.");

        // ✅ Reload dropdowns when activity resumes
        loadWorkExperience();
        loadDesignations();
        loadProfessions();

        // ✅ Show dropdowns again when clicked
        experienceDropdown.setOnClickListener(v -> experienceDropdown.showDropDown());
        designationDropdown.setOnClickListener(v -> designationDropdown.showDropDown());
        professionDropdown.setOnClickListener(v -> professionDropdown.showDropDown());
    }



    private void saveWorkExperience() {
        Log.d(TAG, "📌 Inside saveWorkExperience() method!");

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Log.e(TAG, "❌ User ID not found in SharedPreferences!");
            Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedProfession = professionDropdown.getText().toString().trim();
        String professionId = professionMap.getOrDefault(selectedProfession, "0");

        String profession = etProfession.getText().toString().trim();
        String institution = etInstitution.getText().toString().trim();
        String designation = etDesignation.getText().toString().trim();
        String experience = experienceDropdown.getText().toString().trim();

        Log.d(TAG, "📌 Profession: " + profession + ", Institution: " + institution + ", Designation: " + designation + ", Experience: " + experience);

        if (profession.isEmpty() || institution.isEmpty() || designation.isEmpty() || experience.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "❌ Missing required fields! Profession: " + profession + ", Institution: " + institution);
            Log.e(TAG, "❌ Missing required fields!");
            return;
        }

        List<WorkExperienceModel> workExperienceList = loadWorkExperienceData();
        WorkExperienceModel newExperience = new WorkExperienceModel(
                profession, institution, designation, experience,
                radioCurrent.isChecked() ? "Current" : "Previous",
                professionId, String.valueOf(userId)
        );

        Log.d(TAG, "📌 New Experience Object Created: " + newExperience.toString());

        workExperienceList.add(newExperience);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String updatedJson = gson.toJson(workExperienceList);
        editor.putString("WORK_EXPERIENCE_LIST", updatedJson);
        editor.apply();

        Log.d(TAG, "📌 Saving Work Experience to SharedPreferences...");
        Log.d(TAG, "✅ Work Experience saved to SharedPreferences!");

        Toast.makeText(this, "Work Experience Saved!", Toast.LENGTH_SHORT).show();

        // ✅ Ensure insertUserWorkExperience() gets called
        Log.d(TAG, "📌 Calling insertUserWorkExperience() with Profession ID: " + professionId);
        insertUserWorkExperience(Integer.parseInt(professionId));
        Log.d(TAG, "📌 Insert function called successfully.");

        Log.d(TAG, "📌 Starting new activity: WorkExperienceView.class");
        startActivity(new Intent(this, WorkExperienceView.class));
        finish();
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

        DatabaseHelper.UserWiseWorkExperienceSelect(this, "4", userIdString, new DatabaseHelper.WorkExperienceCallback() {
            @Override
            public void onSuccess(List<UserWiseWorkExperience> workExperienceData) {
                if (workExperienceData.isEmpty()) {
                    Log.e(TAG, "⚠️ No work experience records found in DB for UserID: " + userIdString);
                    return;
                }

                List<WorkExperienceModel> workExperienceModels = new ArrayList<>();

                for (UserWiseWorkExperience workExp : workExperienceData) {
                    workExperienceModels.add(new WorkExperienceModel(
                            workExp.getProfessionName(),
                            workExp.getInstitutionName(),
                            workExp.getDesignationName(),
                            workExp.getWorkExperience(),
                            workExp.getCurPreExperience(),
                            workExp.getProfessionId(),
                            workExp.getUserId()
                    ));
                }

                // ✅ Save to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String workExperienceJson = gson.toJson(workExperienceModels);
                editor.putString("WORK_EXPERIENCE_LIST_" + userIdString, workExperienceJson);
                editor.apply();

                // ✅ Refresh RecyclerView
                runOnUiThread(() -> {
                    workExperienceAdapter.updateData(workExperienceModels);
                    Log.d(TAG, "✅ Work experience data updated in RecyclerView.");
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Failed to fetch work experience records: " + error);
                runOnUiThread(() -> Toast.makeText(WorkExperience.this, "Error fetching work experience details!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onMessage(String message) {}

        });
    }


    private void insertUserWorkExperience(int professionId) {
        Log.d(TAG, "📌 insertUserWorkExperience() called with Profession ID: " + professionId);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);
        if (userId == -1) {
            Log.e(TAG, "❌ User ID not found in SharedPreferences!");
            return;
        }

        int curPreExperience = radioCurrent.isChecked() ? 1 : 0;
        String institutionName = etInstitution.getText().toString().trim();

        String selectedProfession = professionDropdown.getText().toString().trim();
        String selectedDesignation = designationDropdown.getText().toString().trim();
        String selectedExperience = experienceDropdown.getText().toString().trim();

        int designationId = Integer.parseInt(designationMap.getOrDefault(selectedDesignation, "0"));
        int workExperienceId = Integer.parseInt(workExperienceMap.getOrDefault(selectedExperience, "0"));
        int cityId = 0;
        String selfReferralCode = "123";

        Log.d(TAG, "📌 Inserting into DB: UserID=" + userId + ", ProfessionID=" + professionId);

        DatabaseHelper.UserWiseWorkExperienceInsert(this, "1",
                userId, professionId, curPreExperience,
                designationId, institutionName, cityId, workExperienceId, selfReferralCode,
                new DatabaseHelper.WorkExperienceCallback() {
                    @Override
                    public void onMessage(String message) {
                        Log.d(TAG, "✅ Database Response: " + message);
                        runOnUiThread(() -> {
                            Toast.makeText(WorkExperience.this, message, Toast.LENGTH_SHORT).show();
                        });

                        // ✅ Refresh Data After Insert
                        fetchUserWorkExperienceDetails();
                    }

                    @Override
                    public void onSuccess(List<UserWiseWorkExperience> result) {
                        Log.d(TAG, "✅ Data inserted successfully!");
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "❌ Database Error: " + error);
                    }

                });
    }



    private List<WorkExperienceModel> loadWorkExperienceData() {
        String json = sharedPreferences.getString("WORK_EXPERIENCE_LIST", null);
        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<WorkExperienceModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void loadWorkExperience() {
        String query = "SELECT WorkExperienceID, WorkExperience FROM WorkExperience WHERE active = 'true' ORDER BY WorkExperience";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "❌ No Work Experience records found!");
                    Toast.makeText(WorkExperience.this, "No Work Experience Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> workExperiences = new ArrayList<>();
                workExperienceMap.clear();

                for (Map<String, String> row : result) {
                    String id = row.get("WorkExperienceID");
                    String name = row.get("WorkExperience");

                    Log.d(TAG, "✅ Work Experience Retrieved - ID: " + id + ", Name: " + name);
                    workExperiences.add(name);
                    workExperienceMap.put(name, id);
                }

                // ✅ Ensure UI updates properly
                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(WorkExperience.this, android.R.layout.simple_dropdown_item_1line, workExperiences);
                    experienceDropdown.setAdapter(adapter);
                    experienceDropdown.setThreshold(1);
                    experienceDropdown.dismissDropDown();  // ✅ Ensure dropdown doesn't stay open
                    experienceDropdown.showDropDown();
                    Log.d(TAG, "📌 Work experience dropdown updated.");
                });
            }
        });
    }



    private void loadDesignations() {
        String query = "SELECT DesignationID, DesignationName FROM Designation WHERE active = 'true' ORDER BY DesignationName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "❌ No Designation records found!");
                    Toast.makeText(WorkExperience.this, "No Designations Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> designations = new ArrayList<>();
                designationMap.clear();

                for (Map<String, String> row : result) {
                    String id = row.get("DesignationID");
                    String name = row.get("DesignationName");

                    Log.d(TAG, "✅ Designation Retrieved - ID: " + id + ", Name: " + name);
                    designations.add(name);
                    designationMap.put(name, id);
                }

                // ✅ Run UI update on the main thread
                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(WorkExperience.this, android.R.layout.simple_dropdown_item_1line, designations);
                    designationDropdown.setAdapter(adapter);
                    designationDropdown.setThreshold(1);
                    designationDropdown.showDropDown();  // ✅ Show dropdown after setting adapter
                    Log.d(TAG, "📌 Designation dropdown updated.");
                });
            }
        });
    }


    private void loadProfessions() {
        String query = "SELECT ProfessionID, ProfessionName FROM Profession WHERE active = 'true' ORDER BY ProfessionName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "No Profession records found!");
                    Toast.makeText(WorkExperience.this, "No Professions Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> professions = new ArrayList<>();
                professionMap.clear();

                for (Map<String, String> row : result) {
                    String id = row.get("ProfessionID");
                    String name = row.get("ProfessionName");

                    Log.d(TAG, "Profession Retrieved - ID: " + id + ", Name: " + name);
                    professions.add(name);
                    professionMap.put(name, id);
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(WorkExperience.this, android.R.layout.simple_dropdown_item_1line, professions);
                    professionDropdown.setAdapter(adapter);
                    professionDropdown.setThreshold(1);
                    professionDropdown.showDropDown();  // ✅ Show dropdown after setting adapter
                    Log.d(TAG, "📌 Profession dropdown updated.");
                });
            }
        });

        professionDropdown.setOnClickListener(v -> {
            Log.d(TAG, "📌 professionDropdown clicked - Showing dropdown");
            professionDropdown.showDropDown();
        });

        professionDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProfession = (String) parent.getItemAtPosition(position);
            String professionID = professionMap.get(selectedProfession);
            Log.d(TAG, "Profession Selected: " + selectedProfession + " (ID: " + professionID + ")");
        });
    }
}


