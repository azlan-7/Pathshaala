package com.example.loginpage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddAwards extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 100; // Request code for file picker
    private Spinner spinner6;
    private Uri selectedFileUri; // Stores the selected file URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_awards);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button uploadButton = findViewById(R.id.button16); // Upload button
        // Set up file picker when button is clicked
        uploadButton.setOnClickListener(v -> openFilePicker());
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
}
