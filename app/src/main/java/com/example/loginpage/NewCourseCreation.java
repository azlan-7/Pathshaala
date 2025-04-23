package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NewCourseCreation extends AppCompatActivity {

    EditText courseName, courseSubject, courseClass, courseDate, coursePrice;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_course_creation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        courseName = findViewById(R.id.editTextCourseName);
        courseSubject = findViewById(R.id.editTextSubject);
        courseClass = findViewById(R.id.editTextClass);
        courseDate = findViewById(R.id.editTextDate);
        coursePrice = findViewById(R.id.editTextPrice);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            if (validateFields()) {
                Toast.makeText(this, "New Course Created 🎉", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewCourseCreation.this, TeachersDashboardNew.class));
                finish();
            }
        });
    }

    private boolean validateFields() {
        if (courseName.getText().toString().trim().isEmpty()) {
            courseName.setError("Course Name is required");
            return false;
        }
        if (courseSubject.getText().toString().trim().isEmpty()) {
            courseSubject.setError("Subject is required");
            return false;
        }
        if (courseClass.getText().toString().trim().isEmpty()) {
            courseClass.setError("Class is required");
            return false;
        }
        if (courseDate.getText().toString().trim().isEmpty()) {
            courseDate.setError("Date is required");
            return false;
        }
        if (coursePrice.getText().toString().trim().isEmpty()) {
            coursePrice.setError("Price is required");
            return false;
        }
        return true;
    }
}
