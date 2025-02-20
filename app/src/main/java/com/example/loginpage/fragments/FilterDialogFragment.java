package com.example.loginpage.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.loginpage.R; // Change this to your package name

public class FilterDialogFragment extends DialogFragment {

    private AutoCompleteTextView autoCompleteGrade, autoCompleteSubject;
    private EditText editTextLocation;
    private Button btnSaveFilters;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Request for a custom dialog layout
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Inflate layout
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter, null);
        dialog.setContentView(view);

        // Initialize Views
        autoCompleteGrade = view.findViewById(R.id.autoCompleteGrade);
        autoCompleteSubject = view.findViewById(R.id.autoCompleteSubject);
        editTextLocation = view.findViewById(R.id.autoCompleteLocation);
        btnSaveFilters = view.findViewById(R.id.btnSaveFilters);

        // Populate dropdown menus
        setupDropdownMenus();

        // Handle Save Button Click
        btnSaveFilters.setOnClickListener(v -> {
            String grade = autoCompleteGrade.getText().toString().trim();
            String subject = autoCompleteSubject.getText().toString().trim();
            String location = editTextLocation.getText().toString().trim();

            if (grade.isEmpty() || subject.isEmpty()) {
                Toast.makeText(getActivity(), "Please select Grade and Subject!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(),
                        "Filters Applied:\nGrade: " + grade +
                                "\nSubject: " + subject +
                                "\nLocation: " + (location.isEmpty() ? "Not Selected" : location),
                        Toast.LENGTH_LONG).show();
                dismiss(); // Close dialog after saving
            }
        });

        // Center the Dialog
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        return dialog;
    }

    private void setupDropdownMenus() {
        // Grade Levels (From GradesTaught.java)
        String[] gradeLevels = {
                "Primary", "Secondary", "High School Secondary (10th)",
                "High School Senior (12th)", "Diploma", "Undergraduate",
                "Postgraduate", "Doctorate"
        };

        // Subjects (Modify according to available subjects)
        String[] subjects = {"Math", "Science", "History", "English", "Physics", "Chemistry"};

        // Set Adapters for Dropdowns
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, gradeLevels);
        autoCompleteGrade.setAdapter(gradeAdapter);
        autoCompleteGrade.setOnClickListener(v -> autoCompleteGrade.showDropDown());

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, subjects);
        autoCompleteSubject.setAdapter(subjectAdapter);
        autoCompleteSubject.setOnClickListener(v -> autoCompleteSubject.showDropDown());
    }
}
