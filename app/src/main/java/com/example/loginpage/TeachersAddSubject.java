package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TeachersAddSubject extends AppCompatActivity {

    private FlexboxLayout subjectContainer;
    private TextView tvSelectedCount;
    private List<String> selectedSubjects = new ArrayList<>();
    private int selectedCount = 0;

    private String[] subjects = {
            "Science", "Math", "Environmental Science", "Art",
            "Biology", "Economics", "Chemistry", "Computer Sc."
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_add_subject);

        subjectContainer = findViewById(R.id.subjectContainer);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);

        createSubjectChips();

        // "Add it here" button logic
        findViewById(R.id.button14).setOnClickListener(v -> {
            // Save button
            ArrayList<String> selectedSubjects = new ArrayList<>();

            for (int i = 0; i < subjectContainer.getChildCount(); i++) {
                Chip chip = (Chip) subjectContainer.getChildAt(i);
                if (chip.isChecked()) {
                    selectedSubjects.add(chip.getText().toString());
                }
            }
            Intent intent = new Intent(TeachersAddSubject.this, SubjectExpertise.class);
            intent.putStringArrayListExtra("selectedSubjects", selectedSubjects);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void createSubjectChips() {
        for (String subject : subjects) {
            Chip chip = new Chip(this);
            chip.setTextSize(16);  // Increase font size
            chip.setPadding(30, 10, 30, 10);  // Increase inner padding
            chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);  // Center align text
            chip.setChipStrokeWidth(0f);  // Removes the border
            chip.setText(subject);
            chip.setTextAppearance(R.style.ChipTextStyle);
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setChipBackgroundColorResource(R.color.light_blue);
            chip.setTextColor(ContextCompat.getColor(this, R.color.blue));
            chip.setTextSize(16);
            chip.setPadding(15, 10, 15, 10);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    chip.setChipBackgroundColorResource(R.color.blue);
                    chip.setTextColor(ContextCompat.getColor(this, R.color.white));// Selected background
                    selectedSubjects.add(subject);
                } else {
                    chip.setChipBackgroundColorResource(R.color.light_blue);
                    chip.setTextColor(ContextCompat.getColor(this, R.color.blue));
                    selectedSubjects.remove(subject);
                }
                updateSelectionCount();
            });

            subjectContainer.addView(chip);
        }
    }

    private void updateSelectionCount() {
        if (selectedSubjects.isEmpty()) {
            tvSelectedCount.setText("None Selected");
        } else {
            tvSelectedCount.setText(selectedSubjects.size() + " Selected");
        }
    }
}