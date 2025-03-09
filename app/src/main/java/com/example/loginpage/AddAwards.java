package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.models.AwardModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddAwards extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 100; // Request code for file picker
    private Spinner spinner6;
    private static final String TAG = "AddAwards";
    private Uri selectedFileUri; // Stores the selected file URI
    private SharedPreferences sharedPreferences;

    private EditText etAwardTitle, etOrganisation, etYear, etDescription;
    private AutoCompleteTextView awardsDropdown,yearDropdown;
    private Button btnSubmit;
    private Map<String, String> awardsMap = new HashMap<>(); // AwardTitleName -> AwardTitleID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_awards);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        etAwardTitle = findViewById(R.id.editTextText14);
        awardsDropdown = findViewById(R.id.editTextText14);
        etOrganisation = findViewById(R.id.editTextText18);
        etYear = findViewById(R.id.editTextText19);
        AutoCompleteTextView yearDropdown = findViewById(R.id.editTextText19);
        etDescription = findViewById(R.id.editTextTextMultiLine2);
        btnSubmit = findViewById(R.id.button17);

        retrieveAwards(); // ✅ Call this to fetch existing awards

        List<String> years = new ArrayList<>();
        for (int i = 2025; i >= 1960; i--) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years);
        yearDropdown.setAdapter(adapter);

        yearDropdown.setOnClickListener(v -> yearDropdown.showDropDown());


        loadAwards();

        btnSubmit.setOnClickListener(v -> insertAwardIntoDB());



        Button uploadButton = findViewById(R.id.button16); // Upload button
        // Set up file picker when button is clicked
        uploadButton.setOnClickListener(v -> openFilePicker());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }


    private void saveAwardDetails() {
        String title = etAwardTitle.getText().toString().trim();
        String organisation = etOrganisation.getText().toString().trim();
        String year = etYear.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || organisation.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<AwardModel> awardList = loadAwardData();
        awardList.add(new AwardModel(title, organisation, year, description));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String updatedJson = gson.toJson(awardList);
        editor.putString("AWARD_LIST", updatedJson);
        editor.apply();


        Log.d("AddAwards", "Saved Award List: " + updatedJson);


        awardList.add(new AwardModel(title, organisation, year, description));

        Toast.makeText(this, "Award Added Successfully!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AddAwards.this, Awards.class);
        startActivity(intent);
        finish();
    }

    private void retrieveAwards() {
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("retrieveAwards", "🟢 Fetching awards for UserID: " + userId);

        DatabaseHelper.UserWiseAwardSelect(this, userId, new DatabaseHelper.DatabaseCallback() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                if (!result.isEmpty()) {
                    Map<String, String> awardData = result.get(0); // Get first award

                    Log.d("retrieveAwards", "✅ Award Loaded: " + awardData.toString());

                    etAwardTitle.setText(awardData.get("AwardTitleName"));
                    etOrganisation.setText(awardData.get("AwardingOrganization"));
                    etDescription.setText(awardData.get("Remarks"));
                    etYear.setText(awardData.get("IssueYear"));

                    Toast.makeText(AddAwards.this, "Award retrieved successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("retrieveAwards", "⚠️ No awards found for UserID: " + userId);
                    Toast.makeText(AddAwards.this, "No awards found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMessage(String message) {
                Log.d("retrieveAwards", "ℹ️ Message: " + message);
                Toast.makeText(AddAwards.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddAwards.this, "Database error: " + error, Toast.LENGTH_LONG).show();
                Log.e("retrieveAwards", "Error: " + error);
            }
        });
    }







    public void insertAwardIntoDB() {
        String title = etAwardTitle.getText().toString().trim();
        String organisation = etOrganisation.getText().toString().trim();
        String yearStr = etYear.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || organisation.isEmpty() || yearStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        int issueYear;
        try {
            issueYear = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid year format!", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sharedPreferences.getInt("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String awardFileName = "No_File";
        if (selectedFileUri != null) {
            awardFileName = getFileNameFromUri(selectedFileUri);
            Log.d("insertAwardIntoDB", "📂 Selected file URI: " + selectedFileUri.toString());
            Log.d("insertAwardIntoDB", "📄 Extracted file name: " + awardFileName);
        } else {
            Log.d("insertAwardIntoDB", "⚠️ No file selected, using default file name: " + awardFileName);
        }

        String selfReferralCode = "";

        Log.d("insertAwardIntoDB", "🟢 Inserting Award into DB");
        Log.d("insertAwardIntoDB", "  - User ID: " + userId);
        Log.d("insertAwardIntoDB", "  - Award Title: " + title);
        Log.d("insertAwardIntoDB", "  - Organization: " + organisation);
        Log.d("insertAwardIntoDB", "  - Issue Year: " + issueYear);
        Log.d("insertAwardIntoDB", "  - Remarks: " + description);
        Log.d("insertAwardIntoDB", "  - Final Award File Name: " + awardFileName);


        DatabaseHelper.UserWiseAwardInsert(
                this,
                "1", // Insert operation
                userId,
                getAwardTitleID(title),
                organisation,
                issueYear,
                description,
                awardFileName,
                selfReferralCode,
                new DatabaseHelper.DatabaseCallback() {
                    @Override
                    public void onMessage(String message) {
                        Log.d("DatabaseHelper", "📩 DB Message: " + message);
                        Toast.makeText(AddAwards.this, message, Toast.LENGTH_SHORT).show();
                        if (message.toLowerCase().contains("success")) {
                            startActivity(new Intent(AddAwards.this, Awards.class));
                            finish();
                        }
                    }

                    @Override
                    public void onSuccess(List<Map<String, String>> result) {
                        Log.d("DatabaseCallback", "✅ Award Insert successful!");
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("DatabaseHelper", "❌ Insert failed: " + error);
                        Toast.makeText(AddAwards.this, "Database error: " + error, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    // Helper method to extract the file name from the URI
    private String getFileNameFromUri(Uri uri) {
        String fileName = "No_File"; // Default file name

        Log.d("getFileNameFromUri", "🔍 Checking file name for URI: " + uri.toString());

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            Log.d("getFileNameFromUri", "📌 Column index for DISPLAY_NAME: " + nameIndex);
            if (nameIndex != -1) {
                cursor.moveToFirst();
                fileName = cursor.getString(nameIndex);
                Log.d("getFileNameFromUri", "✅ Extracted file name: " + fileName);
            }
            cursor.close();
        } else {
            Log.e("getFileNameFromUri", "❌ Cursor is NULL. Could not extract file name.");
        }

        return fileName;
    }

    // Helper method to convert award title to awardTitleID
    private int getAwardTitleID(String awardTitle) {
        return awardsMap.containsKey(awardTitle) ? Integer.parseInt(awardsMap.get(awardTitle)) : 1;
    }


    // Method to open the file picker
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Accepts all file types
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a file"), FILE_SELECT_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "No file manager installed!", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle file selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                Toast.makeText(this, "Selected File: " + selectedFileUri.getPath(), Toast.LENGTH_SHORT).show();
                // Implement actual file upload logic here
            }
        }
    }
    private List<AwardModel> loadAwardData() {
        String json = sharedPreferences.getString("AWARD_LIST", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<AwardModel>>() {}.getType();
        List<AwardModel> awardList = (json == null) ? new ArrayList<>() : gson.fromJson(json, type);
        return awardList;
    }

    private void loadAwards() {
        String query = "SELECT AwardTitleID, AwardTitleName FROM AwardTitle WHERE active = 'true' ORDER BY AwardTitleName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "No Awards records found!");
                    Toast.makeText(AddAwards.this, "No Awards Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> awards = new ArrayList<>();
                awardsMap.clear();

                for (Map<String, String> row : result) {
                    String id = row.get("AwardTitleID");
                    String name = row.get("AwardTitleName");

                    Log.d(TAG, "Award Retrieved - ID: " + id + ", Name: " + name);
                    awards.add(name);
                    awardsMap.put(name, id);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddAwards.this, android.R.layout.simple_dropdown_item_1line, awards);
                awardsDropdown.setAdapter(adapter);
                Log.d(TAG, "Adapter set with values: " + awards);
            }
        });

        awardsDropdown.setOnClickListener(v -> {
            Log.d(TAG, "awardsDropdown clicked - Showing dropdown");
            awardsDropdown.showDropDown();
        });

        awardsDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedAward = (String) parent.getItemAtPosition(position);
            String awardID = awardsMap.get(selectedAward);
            Log.d(TAG, "Award Selected: " + selectedAward + " (ID: " + awardID + ")");
        });
    }

}