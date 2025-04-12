package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;

public class StudentsParentInfo extends AppCompatActivity {

    EditText fatherNameEditText;
    EditText fatherContactEditText;
    EditText motherNameEditText;
    EditText motherContactEditText;
    EditText guardianNameEditText;
    EditText guardianRelationEditText;
    EditText guardianContactEditText;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_parent_info);

        fatherNameEditText = findViewById(R.id.editTextText40);
        fatherContactEditText = findViewById(R.id.editTextText41);
        motherNameEditText = findViewById(R.id.editTextText42);
        motherContactEditText = findViewById(R.id.editTextText43);
        guardianNameEditText = findViewById(R.id.editTextText44);
        guardianRelationEditText = findViewById(R.id.editTextText46);
        guardianContactEditText = findViewById(R.id.editTextText45);
        btnSave = findViewById(R.id.button32);

        btnSave.setOnClickListener(v -> saveParentGuardianInfo());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void saveParentGuardianInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int studentUserId = sharedPreferences.getInt("USER_ID", -1);
        String selfReferralCode = sharedPreferences.getString("selfReferralCode", "");

        if (studentUserId == -1) {
            Toast.makeText(this, "Student User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = DatabaseHelper.insertOrUpdateParentGuardianInfo(
                0,
                studentUserId,
                selfReferralCode,
                fatherNameEditText.getText().toString(),
                fatherContactEditText.getText().toString(),
                motherNameEditText.getText().toString(),
                motherContactEditText.getText().toString(),
                guardianNameEditText.getText().toString(),
                guardianRelationEditText.getText().toString(),
                guardianContactEditText.getText().toString()
        );

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        if (message.startsWith("Success")) {
            Intent intent = new Intent(StudentsParentInfo.this, StudentsInfo.class);
            startActivity(intent);
        }
    }
}