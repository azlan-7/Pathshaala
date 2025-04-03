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
import android.widget.ImageView;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
    private SharedPreferences sharedPreferences;
    private ImageView backButton;
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
        backButton = findViewById(R.id.imageView154);

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

        backButton.setOnClickListener(v -> startActivity(new Intent(AddAwards.this, TeachersInfoSubSection.class)));

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

        Log.d(TAG, "🟢 Fetching awards from DB...");

        DatabaseHelper.FetchAllAwards(this, new DatabaseHelper.DatabaseCallback() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                List<String> awardNames = new ArrayList<>();

                for (Map<String, String> award : result) {
                    String awardId = award.get("AwardTitleID");
                    String awardName = award.get("AwardTitleName");

                    awardsMap.put(awardName, awardId); // Store ID -> Name mapping
                    awardNames.add(awardName);

                    Log.d(TAG, "Award Retrieved - ID: " + awardId + ", Name: " + awardName);
                }

                ArrayAdapter<String> awardAdapter = new ArrayAdapter<>(AddAwards.this, android.R.layout.simple_dropdown_item_1line, awardNames);
                awardsDropdown.setAdapter(awardAdapter);
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, "ℹ️ Message: " + message);
                Toast.makeText(AddAwards.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Error retrieving awards: " + error);
                Toast.makeText(AddAwards.this, "Error fetching awards: " + error, Toast.LENGTH_LONG).show();
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
            File originalFile = getFileFromUri(selectedFileUri);
            if (originalFile != null) {
                File renamedFile = FileUploader.renameFile(originalFile, userId, "A"); // Renaming with "A" prefix
                awardFileName = (renamedFile != null) ? renamedFile.getName() : "No_File";
            }
        }

        Log.d("insertAwardIntoDB", "✅ Final Stored Filename: " + awardFileName);

//        int randomAwardId = (int) (Math.random() * 1000) + 10; // Generates a new Award ID

        int awardTitleId = getAwardTitleID(title);
        if (awardTitleId == -1) {
            Toast.makeText(this, "Invalid Award Title! Please select from dropdown.", Toast.LENGTH_SHORT).show();
            return;
        }


        DatabaseHelper.UserWiseAwardInsert(
                this,
                "1", // Insert operation
                userId,
//                randomAwardId,   // remove this when done, using for randomAwardIDs(for when you've used all the awards)
                awardTitleId,
                organisation,
                issueYear,
                description,
                awardFileName,
                "", // No referral code
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



    // Helper method to convert award title to awardTitleID
    private int getAwardTitleID(String awardName) {
        if (awardsMap.containsKey(awardName)) {
            return Integer.parseInt(awardsMap.get(awardName));
        } else {
            Log.e(TAG, "⚠️ Award ID not found for: " + awardName);
            return -1; // Handle missing case properly
        }
    }



    // Method to open the file picker
    private static final int FILE_PICKER_REQUEST_CODE = 100;
    private Uri selectedFileUri = null; // Store selected image URI

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");  // Accept only images
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select an image"), FILE_PICKER_REQUEST_CODE);
    }

    // Handle file selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                Toast.makeText(this, "Image Selected: " + selectedFileUri.getPath(), Toast.LENGTH_SHORT).show();

                File imageFile = getFileFromUri(selectedFileUri);
                if (imageFile != null) {
                    new Thread(() -> {
                        boolean success = FileUploader.uploadImage(imageFile, AddAwards.this,"A");
                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                } else {
                    Log.e("AddAwards", "Failed to get image file from URI");
                }
            }
        }
    }


    private File getFileFromUri(Uri uri) {
        File file = null;
        try {
            String fileName = getFileNameFromUri(uri);
            if (fileName == null || fileName.isEmpty()) {
                fileName = "temp_image"; // Fallback name
            }

            File tempFile = new File(getCacheDir(), fileName);
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 OutputStream outputStream = new FileOutputStream(tempFile)) {

                if (inputStream == null) {
                    Log.e("AddAwards", "Failed to open InputStream");
                    return null;
                }

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                file = tempFile;
            }
        } catch (Exception e) {
            Log.e("AddAwards", "Error getting file from URI: " + e.getMessage());
        }
        return file;
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
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