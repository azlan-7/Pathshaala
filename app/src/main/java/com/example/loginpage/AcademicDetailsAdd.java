package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AcademicDetailsAdd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_academic_details_add);




        // **Grade Enrolled In** (1st to 12th)
        AutoCompleteTextView gradeEnrolled = findViewById(R.id.editTextText33);
        String[] grades = {"1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th", "11th", "12th"};
        gradeEnrolled.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, grades));

        // **Year of Passing** (Current year to 2010)
        AutoCompleteTextView yearOfPassing = findViewById(R.id.editTextText34);
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear; year >= 2010; year--) years.add(String.valueOf(year));
        yearOfPassing.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years));

        // **Learning Stream**
        AutoCompleteTextView learningStream = findViewById(R.id.editTextText37);
        String[] streams = {"PCM", "PCMB", "PCB", "Commerce", "Arts", "Other"};
        ArrayAdapter<String> streamAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, streams);
        learningStream.setAdapter(streamAdapter);

        // **Select Board**
        AutoCompleteTextView selectBoard = findViewById(R.id.editTextText38);
        String[] boards = {"CBSE", "ICSE", "UP Board"};
        ArrayAdapter<String> boardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, boards);
        selectBoard.setAdapter(boardAdapter);

        // **Save Button** - Redirect to StudentsInfo.java
        Button saveButton = findViewById(R.id.button31);
        saveButton.setOnClickListener(v -> {
            Intent intent = new Intent(AcademicDetailsAdd.this, StudentsInfo.class);
            startActivity(intent);
            finish();
        });

        gradeEnrolled.setOnClickListener(v -> gradeEnrolled.showDropDown());
        yearOfPassing.setOnClickListener(v -> yearOfPassing.showDropDown());
        learningStream.setOnClickListener(v -> learningStream.showDropDown());
        selectBoard.setOnClickListener(v -> selectBoard.showDropDown());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
