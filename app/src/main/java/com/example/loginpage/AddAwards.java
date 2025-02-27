package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class AddAwards extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 100; // Request code for file picker
    private Spinner spinner6;
    private Uri selectedFileUri; // Stores the selected file URI
    private SharedPreferences sharedPreferences;

    private EditText etAwardTitle, etOrganisation, etYear, etDescription;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_awards);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        etAwardTitle = findViewById(R.id.editTextText14);
        etOrganisation = findViewById(R.id.editTextText18);
        etYear = findViewById(R.id.editTextText19);
        etDescription = findViewById(R.id.editTextTextMultiLine2);
        btnSubmit = findViewById(R.id.button17);

        btnSubmit.setOnClickListener(v -> saveAwardDetails());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button uploadButton = findViewById(R.id.button16); // Upload button
        // Set up file picker when button is clicked
        uploadButton.setOnClickListener(v -> openFilePicker());
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

}