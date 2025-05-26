package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView; // Keep ImageView for the background/decorative images
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.UserWiseSubject;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger; // Import for AtomicInteger

public class TeachersAddSubject extends AppCompatActivity {

    private static final String TAG = "TeachersAddSubject";

    private FlexboxLayout subjectContainer; // Now handles all subjects
    private TextView tvSelectedCount;
    private TextView headingTextView; // Corresponds to textView33 in XML
    private ImageView backButton; // Corresponds to imageView36 if used for back, or remove if not needed for navigation
    private Button saveButton; // Corresponds to button14 in XML

    private SharedPreferences sharedPreferences;
    private int userId;

    // Data structures to manage subjects
    private List<String> allAvailableSubjects; // All subjects from DB
    private Map<String, String> subjectNameToIdMap; // Map subject name to ID
    private Set<String> alreadySavedSubjects; // Subjects already associated with the user in DB
    private Set<String> tempSelectedSubjects; // Subjects currently checked in the UI (for saving)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_add_subject);

        // Initialize UI elements based on the provided XML
        subjectContainer = findViewById(R.id.subjectContainer);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        saveButton = findViewById(R.id.button14); // "Save" button
        headingTextView = findViewById(R.id.textView33); // "Add Subject" heading

        // If imageView36 is intended as a back button, initialize it.
        // If it's purely decorative, you might not need to initialize it here.
        // Based on the XML, it seems decorative. If you need a back button, you'll need to add one to the XML.
        // For now, I'll comment out the back button logic, assuming imageView36 is decorative.
        // backButton = findViewById(R.id.imageView36); // Assuming this might be used for back, but XML suggests it's decorative.

        // Initialize data structures
        allAvailableSubjects = new ArrayList<>();
        subjectNameToIdMap = new HashMap<>();
        alreadySavedSubjects = new HashSet<>();
        tempSelectedSubjects = new HashSet<>();

        // Get user ID from SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("USER_ID", -1);
        String userType = sharedPreferences.getString("usertype", "N/A");
        Log.d(TAG, "Retrieved User ID: " + userId + ", User Type: " + userType);

        if (userId == -1) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish(); // Close activity if no user ID
            return;
        }

        // Set heading based on user type (from SENO's logic, adapted for textView33)
        if (userType.equals("S")) {
            headingTextView.setText("Learning Preferences");
        } else {
            headingTextView.setText("Add Subject"); // Default for teachers, matches XML
        }

        // Load all available subjects and then the user's saved subjects
        loadAllSubjectsAndPopulateChips();

        // Set up save button click listener
        saveButton.setOnClickListener(v -> saveSubjects());

        // If you had a back button in the XML, you would set its listener here.
        // Example: if (backButton != null) backButton.setOnClickListener(v -> finish());

        // Apply window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Fetches all active subjects from the database and populates the subjectNameToIdMap
     * and allAvailableSubjects list. After fetching, it calls loadAndDisplaySavedSubjects
     * to get the user's current selections, and then createSelectableSubjectChips to draw the UI.
     */
    private void loadAllSubjectsAndPopulateChips() {
        String query = "SELECT SubjectID, SubjectName FROM Subject WHERE active = 'true' ORDER BY SubjectName";
        Log.d(TAG, "Executing query to load all subjects: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
            if (result == null || result.isEmpty()) {
                Log.e(TAG, "No subjects found in the database.");
                runOnUiThread(() -> Toast.makeText(this, "No Subjects Found!", Toast.LENGTH_SHORT).show());
                return;
            }

            allAvailableSubjects.clear();
            subjectNameToIdMap.clear();

            for (Map<String, String> row : result) {
                String id = row.get("SubjectID");
                String name = row.get("SubjectName");
                if (id != null && name != null) {
                    Log.d(TAG, "Subject Retrieved - ID: " + id + ", Name: " + name);
                    allAvailableSubjects.add(name);
                    subjectNameToIdMap.put(name, id);
                }
            }
            // After loading all subjects, load the user's saved subjects
            loadAndDisplaySavedSubjects();
        });
    }

    /**
     * Fetches subjects already saved for the current user from the database
     * and populates the 'alreadySavedSubjects' set. After fetching, it triggers
     * 'createSelectableSubjectChips' to refresh the UI with correct colors.
     */
    private void loadAndDisplaySavedSubjects() {
        if (userId == -1) {
            Log.e(TAG, "Cannot load saved subjects: User ID not found.");
            return;
        }

        Log.d(TAG, "Fetching saved subjects for User ID: " + userId);

        DatabaseHelper.UserWiseSubjectSelect(this, "4", String.valueOf(userId), new DatabaseHelper.UserWiseSubjectResultListener() {
            @Override
            public void onQueryResult(List<UserWiseSubject> userSubjects) {
                alreadySavedSubjects.clear(); // Clear previous saved subjects
                tempSelectedSubjects.clear(); // Clear temporary selections before re-populating

                if (userSubjects != null && !userSubjects.isEmpty()) {
                    for (UserWiseSubject subject : userSubjects) {
                        if (subject.getSubjectName() != null && !subject.getSubjectName().trim().isEmpty()) {
                            Log.d(TAG, "Saved Subject Retrieved: " + subject.getSubjectName());
                            alreadySavedSubjects.add(subject.getSubjectName()); // Add to set of saved subjects
                        } else {
                            Log.e(TAG, "Skipping saved subject with null or empty name!");
                        }
                    }
                } else {
                    Log.d(TAG, "⚠️ No saved subjects found for the user.");
                }
                // Now, create the selectable chips, which will use 'alreadySavedSubjects' to set initial colors
                createSelectableSubjectChips();
            }
        });
    }

    /**
     * Creates and adds selectable Chip views to the subjectContainer.
     * The initial checked state and color of each chip are determined
     * by whether the subject is already saved for the user.
     */
    private void createSelectableSubjectChips() {
        subjectContainer.removeAllViews(); // Clear existing chips
        tempSelectedSubjects.clear();

        final int leftMargin = (int) getResources().getDimension(R.dimen.subject_left_margin); // Define in dimens.xml
        final int chipSpacing = (int) getResources().getDimension(R.dimen.chip_spacing); // Define in dimens.xml
        final int rowSpacing = (int) getResources().getDimension(R.dimen.row_spacing); // Define in dimens.xml

        for (int i = 0; i < allAvailableSubjects.size(); i++) {
            String subject = allAvailableSubjects.get(i);
            Chip chip = new Chip(this);
            chip.setText(subject);
            chip.setTextSize(16);
            chip.setPadding(15, 10, 15, 10);
            chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            chip.setChipStrokeWidth(0f);
            chip.setTextAppearance(R.style.ChipTextStyle);
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setTypeface(ResourcesCompat.getFont(this, R.font.work_sans_bold));

            if (alreadySavedSubjects.contains(subject)) {
                chip.setChecked(true);
                chip.setChipBackgroundColorResource(R.color.blueGradientEnd);
                chip.setTextColor(ContextCompat.getColor(this, R.color.white));
                tempSelectedSubjects.add(subject);
            } else {
                chip.setChecked(false);
                chip.setChipBackgroundColorResource(R.color.light_blue);
                chip.setTextColor(ContextCompat.getColor(this, R.color.blueGradientEnd));
            }

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    chip.setChipBackgroundColorResource(R.color.blueGradientEnd);
                    chip.setTextColor(ContextCompat.getColor(this, R.color.white));
                    tempSelectedSubjects.add(subject);
                } else {
                    chip.setChipBackgroundColorResource(R.color.light_blue);
                    chip.setTextColor(ContextCompat.getColor(this, R.color.blueGradientEnd));
                    tempSelectedSubjects.remove(subject);
                }
                updateSelectionCount();
            });

            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            // Add spacing between chips
            if (i % 2 == 0 && i > 0) { // For the second chip in each potential row
                layoutParams.setMarginStart(chipSpacing);
            } else if (i % 2 != 0) { // For the first chip in each potential row (after the first)
                if (i > 0) {
                    layoutParams.setMarginStart(leftMargin);
                } else {
                    layoutParams.setMarginStart(leftMargin); // Apply to the very first chip
                }
            } else if (i == 0) {
                layoutParams.setMarginStart(leftMargin); // Apply to the very first chip
            }

            // Add vertical spacing between rows (after the first item in a row)
            if (i >= 2) {
                layoutParams.topMargin = rowSpacing;
            }

            chip.setLayoutParams(layoutParams);
            subjectContainer.addView(chip);
        }
        updateSelectionCount();
    }

    /**
     * Deletes a subject from the UserWiseSubject table in the database.
     * This method is now primarily called internally by saveSubjects.
     *
     * @param subjectId The ID of the subject to delete.
     */
    private void deleteSubjectFromDB(String subjectId) {
        Log.d(TAG, "⚠️ Deleting Subject From DB: SubjectID=" + subjectId + ", UserID=" + userId);

        DatabaseHelper.UserWiseSubjectDelete(this, subjectId, String.valueOf(userId), new DatabaseHelper.DatabaseCallback() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                Log.d(TAG, "✅ Subject Deleted Successfully from DB!");
                // No need to reload UI immediately here; saveSubjects will handle the final reload.
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, "📩 DB Response (Delete): " + message);
                // Toast.makeText(TeachersAddSubject.this, message, Toast.LENGTH_SHORT).show(); // Optional: show message
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Error Deleting Subject: " + error);
                runOnUiThread(() -> Toast.makeText(TeachersAddSubject.this, "Error removing subject: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * Updates the text view showing the count of currently selected subjects.
     */
    private void updateSelectionCount() {
        if (tempSelectedSubjects.isEmpty()) {
            tvSelectedCount.setText("None Selected");
        } else {
            tvSelectedCount.setText(tempSelectedSubjects.size() + " Selected");
        }
    }

    /**
     * Saves the newly selected subjects to the database and deletes subjects
     * that were previously saved but are now deselected.
     */
    private void saveSubjects() {
        if (userId == -1) {
            Toast.makeText(this, "User ID not found. Cannot save subjects.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine subjects to add and subjects to delete
        Set<String> subjectsToAdd = new HashSet<>(tempSelectedSubjects);
        subjectsToAdd.removeAll(alreadySavedSubjects); // Subjects in tempSelected but not in alreadySaved

        Set<String> subjectsToDelete = new HashSet<>(alreadySavedSubjects);
        subjectsToDelete.removeAll(tempSelectedSubjects); // Subjects in alreadySaved but not in tempSelected

        Log.d(TAG, "Subjects to Add: " + subjectsToAdd);
        Log.d(TAG, "Subjects to Delete: " + subjectsToDelete);

        final int[] operationsCompleted = {0};
        // Use AtomicInteger for totalOperations to allow modification within inner classes
        final AtomicInteger totalOperations = new AtomicInteger(subjectsToAdd.size() + subjectsToDelete.size());

        if (totalOperations.get() == 0) { // Access value using .get()
            Toast.makeText(this, "No changes to save.", Toast.LENGTH_SHORT).show();
            navigateToSubjectExpertise(); // Navigate even if no changes
            return;
        }

        // Callback to handle completion of each DB operation
        DatabaseHelper.DatabaseCallback operationCallback = new DatabaseHelper.DatabaseCallback() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                operationsCompleted[0]++;
                checkAllOperationsCompleted();
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, "DB Message: " + message);
                // No need to show toast for every message, final toast will cover it.
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "DB Error: " + error);
                operationsCompleted[0]++; // Still increment to track completion
                checkAllOperationsCompleted();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error during save: " + error, Toast.LENGTH_SHORT).show());
            }

            private void checkAllOperationsCompleted() {
                if (operationsCompleted[0] == totalOperations.get()) { // Access value using .get()
                    runOnUiThread(() -> {
                        Toast.makeText(TeachersAddSubject.this, "Subjects updated!", Toast.LENGTH_SHORT).show();
                        loadAllSubjectsAndPopulateChips(); // Reload all data and UI
                        navigateToSubjectExpertise(); // Navigate after all operations and UI refresh
                    });
                }
            }
        };

        // Perform deletions
        for (String subjectName : subjectsToDelete) {
            String subjectId = subjectNameToIdMap.get(subjectName);
            if (subjectId != null) {
                DatabaseHelper.UserWiseSubjectDelete(this, subjectId, String.valueOf(userId), operationCallback);
            } else {
                Log.e(TAG, "Subject ID not found for deletion: " + subjectName);
                totalOperations.decrementAndGet(); // Decrement total operations if ID is missing
            }
        }

        // Perform insertions
        for (String subjectName : subjectsToAdd) {
            String subjectId = subjectNameToIdMap.get(subjectName);
            if (subjectId != null) {
                DatabaseHelper.userSubjectInsert(this,
                        1, // Operation type for insert
                        Integer.parseInt(subjectId),
                        userId,
                        null, // No selfReferralCode
                        operationCallback
                );
            } else {
                Log.e(TAG, "Subject ID not found for insertion: " + subjectName);
                totalOperations.decrementAndGet(); // Decrement total operations if ID is missing
            }
        }
    }

    /**
     * Navigates to the SubjectExpertise activity and finishes the current activity.
     */
    private void navigateToSubjectExpertise() {
        Log.d(TAG, "🚀 Navigating to SubjectExpertise.");
        // Assuming SubjectExpertise can now fetch subjects directly from DB for the user.
        Intent intent = new Intent(TeachersAddSubject.this, SubjectExpertise.class);
        startActivity(intent);
        finish();
    }
}
