package com.example.loginpage;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.adapters.SubjectAdapterOne;
import com.example.loginpage.models.UserWiseSubject;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubjectExpertiseNewOne extends AppCompatActivity {

    private static final String TAG = "SubjectExpertiseNewOne";
    private FlexboxLayout subjectContainer;

    private FlexboxLayout retrievedSubjectContainer;
    private RecyclerView subjectsRecyclerView;
    private SharedPreferences sharedPreferences;
    private Button saveButton;
    private Set<String> selectedSubjects;
    private SubjectAdapterOne adapter; // Adapter reference
    private Map<String, String> subjectMap = new HashMap<>();

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject_expertise_new_one);

        subjectContainer = findViewById(R.id.subjectContainer);
        subjectsRecyclerView = findViewById(R.id.subjectsRecycler);
        saveButton = findViewById(R.id.button21);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        selectedSubjects = new HashSet<>(sharedPreferences.getStringSet("SELECTED_SUBJECTS", new HashSet<>()));


        userId = sharedPreferences.getInt("USER_ID", -1);
        Log.d(TAG, "Retrieved User ID from SharedPreferences: " + userId);

//        if (userId == -1) {
//            Log.e(TAG, "USER_ID not found! Fetching from the database...");
//            DatabaseHelper.UserDetailsSelect(this, "3", "USER_PHONE_NUMBER", userList -> {
//                if (!userList.isEmpty()) {
//                    int storedUserId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("USER_ID", -1);
//                    Log.d(TAG, "✅ User details retrieved, User ID stored: " + storedUserId);
//                } else {
//                    Log.e(TAG, "⚠️ No user details found.");
//                }
//            });
//
//        }

        retrievedSubjectContainer = findViewById(R.id.retrievedSubjectContainer);

        // Call method to load subjects from the database into the new container
        loadRetrievedSubjects();

        // Call UserWiseSubjectSelect to load user's subjects
        loadUserSubjects();

        // Debug: Print all stored SharedPreferences values
        Log.d(TAG, "Retrieved SharedPreferences: " + sharedPreferences.getAll());
        

        // Load selected subjects into the FlexboxLayout
        loadSelectedSubjects();

        // Save subjects on button click
        saveButton.setOnClickListener(v -> saveSubjects());

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



    private void loadRetrievedSubjects() {
        userId = sharedPreferences.getInt("USER_ID", -1);
        if (userId == -1) {
            Log.e(TAG, "Cannot load retrieved subjects: User ID not found.");
            return;
        }

        Log.d(TAG, "Fetching retrieved subjects for User ID: " + userId);

        DatabaseHelper.UserWiseSubjectSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseSubjectResultListener() {
            @Override
            public void onQueryResult(List<UserWiseSubject> userSubjects) {
                if (userSubjects == null || userSubjects.isEmpty()) {
                    Log.d(TAG, "⚠️ No retrieved subjects found for the user.");
                    return;
                }

                retrievedSubjectContainer.removeAllViews(); // Clear previous chips

                for (UserWiseSubject subject : userSubjects) {
                    addRetrievedChip(subject.getSubjectName(), subject.getSubjectId());
                }
            }
        });
    }

    private void addRetrievedChip(String subjectName, String subjectId) {
        // ✅ Prevent adding a chip with a null or empty subject name
        if (subjectName == null || subjectName.trim().isEmpty()) {
            Log.e(TAG, "❌ Skipping chip creation for null or empty subject name!");
            return;
        }

        Chip chip = new Chip(this);
        chip.setText(subjectName);
        chip.setTextSize(16);
        chip.setPadding(20, 8, 20, 8);
        chip.setTypeface(ResourcesCompat.getFont(this, R.font.work_sans_bold));
        chip.setCloseIconVisible(true); // ✅ Show close button
        chip.setCloseIconTintResource(R.color.blue); // ✅ Set blue color for close button

        chip.setChipBackgroundColorResource(R.color.light_blue);
        chip.setTextColor(ContextCompat.getColor(this, R.color.black));

        chip.setOnCloseIconClickListener(v -> {
            Log.d(TAG, "❌ Removing retrieved subject: " + subjectName);
            retrievedSubjectContainer.removeView(chip); // ✅ Remove chip from UI
            deleteSubjectFromDB(subjectId);
        });

        retrievedSubjectContainer.addView(chip);
    }


    private void loadUserSubjects() {
        if (userId == -1) {
            Log.e(TAG, "Cannot load subjects: User ID not found.");
            return;
        }

        Log.d(TAG, "Fetching subjects for User ID: " + userId);

        DatabaseHelper.UserWiseSubjectSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseSubjectResultListener() {
            @Override
            public void onQueryResult(List<UserWiseSubject> userSubjects) {
                if (userSubjects == null || userSubjects.isEmpty()) {
                    Log.d(TAG, "⚠️ No subjects found for the user.");
                    return;
                }

                // ✅ Clear previous data
                retrievedSubjectContainer.removeAllViews();
                subjectMap.clear();

                List<String> subjects = new ArrayList<>();
                for (UserWiseSubject subject : userSubjects) {
                    Log.d(TAG, "Subject Retrieved: " + subject.getSubjectName());

                    subjects.add(subject.getSubjectName());
                    subjectMap.put(subject.getSubjectName(), subject.getSubjectId()); // Store Subject ID
                    addRetrievedChip(subject.getSubjectName(), subject.getSubjectId()); // ✅ Add chip dynamically
                }

                setupRecyclerView(subjects);
            }
        });
    }


    private void deleteSubjectFromDB(String subjectId) {
        Log.d(TAG, "⚠️ Deleting Subject From DB: " + subjectId);

        DatabaseHelper.UserWiseSubjectDelete(this, subjectId, String.valueOf(userId), new DatabaseHelper.DatabaseCallback() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                Log.d(TAG, "✅ Subject Deleted Successfully from DB!");
                runOnUiThread(() -> loadRetrievedSubjects()); // ✅ Reload UI after deletion
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, "📩 DB Response: " + message);
                Toast.makeText(SubjectExpertiseNewOne.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Error Deleting Subject: " + error);
            }
        });
    }



    public void loadSelectedSubjects() {
        subjectContainer.removeAllViews(); // Only remove views, not the data

        selectedSubjects = new HashSet<>(sharedPreferences.getStringSet("SELECTED_SUBJECTS", new HashSet<>())); // Re-load stored subjects

        for (String subject : selectedSubjects) {
            addChip(subject);
        }

        // Ensure checkboxes in RecyclerView match stored subjects
        if (adapter != null) {
            adapter.updateSelection(selectedSubjects);
        }
    }

    private void addChip(String subject) {
        Chip chip = new Chip(this);
        chip.setText(subject);
        chip.setTextSize(18);
        chip.setPadding(30, 10, 30, 10);
        chip.setTypeface(ResourcesCompat.getFont(this, R.font.work_sans_bold));
        chip.setCloseIconVisible(true);

        // Set colors
        chip.setChipBackgroundColorResource(R.color.light_blue);
        chip.setTextColor(ContextCompat.getColor(this, R.color.blue));
        chip.setCloseIconTintResource(R.color.blue);

        // Correctly remove the single chip and update UI
        chip.setOnCloseIconClickListener(v -> {
            if (selectedSubjects.contains(subject)) {
                selectedSubjects.remove(subject);  // Remove the specific subject from memory

                // Update SharedPreferences
                sharedPreferences.edit().putStringSet("SELECTED_SUBJECTS", new HashSet<>(selectedSubjects)).apply();

                // Remove chip from the layout
                subjectContainer.removeView(chip);

                // **Update the RecyclerView checkboxes**
                if (adapter != null) {
                    adapter.updateSelection(selectedSubjects);
                }
            }
        });

        subjectContainer.addView(chip);
    }

    private void setupRecyclerView(List<String> subjects) {
        adapter = new SubjectAdapterOne(this, subjects.toArray(new String[0]), selectedSubjects);
        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        subjectsRecyclerView.setAdapter(adapter);
    }


    private void saveSubjects() {
//        int userId = 1; // Hardcoded UserId
//        Log.d(TAG, "Retrieved User ID: " + userId);

        for (String subject : selectedSubjects) {
            Integer subjectId = subjectMap.containsKey(subject) ? Integer.parseInt(subjectMap.get(subject)) : null;

            if (subjectId != null) {
                DatabaseHelper.userSubjectInsert(this,
                        1, // Insert operation
                        subjectId,
                        userId,
                        null, // No selfReferralCode
                        new DatabaseHelper.DatabaseCallback() {
                            @Override
                            public void onSuccess(List<Map<String, String>> result) {
                                Log.d(TAG, "✅ Subject successfully inserted: " + subject);
                            }

                            @Override
                            public void onMessage(String message) {
                                Log.d(TAG, "Database message: " + message);
                                Toast.makeText(getApplicationContext(),  message, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "❌ Error inserting subject: " + subject + ", Error: " + error);
                            }
                        }
                );
            }
        }

        Log.d(TAG, "🚀 Navigating to SubjectExpertise with selectedSubjects: " + selectedSubjects);
        navigateToSubjectExpertise();
    }

    private void navigateToSubjectExpertise() {
        selectedSubjects.clear();
        for (int i = 0; i < subjectContainer.getChildCount(); i++) {
            Chip chip = (Chip) subjectContainer.getChildAt(i);
            selectedSubjects.add(chip.getText().toString());
        }

        Log.d(TAG, "🚀 Navigating to SubjectExpertise with selectedSubjects: " + selectedSubjects);

        Intent intent = new Intent(SubjectExpertiseNewOne.this, SubjectExpertise.class);
        intent.putStringArrayListExtra("selectedSubjects", new ArrayList<>(selectedSubjects));
        startActivity(intent);
        finish();
    }

}

