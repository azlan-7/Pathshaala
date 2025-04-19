package com.example.loginpage.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.loginpage.MySqliteDatabase.Connection_Class;
import com.example.loginpage.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FilterDialogFragment extends DialogFragment {

    private AutoCompleteTextView autoCompleteGrade, autoCompleteSubject, autoCompleteLocation;
    private Button btnSaveFilters;

    // Listener Interface
    public interface OnFiltersSelectedListener {
        void onFiltersSelected(String grade, String subject);
    }

    private OnFiltersSelectedListener listener;

    public void setOnFiltersSelectedListener(OnFiltersSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Inflate layout
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter, null);
        dialog.setContentView(view);

        // Initialize Views
        autoCompleteGrade = view.findViewById(R.id.autoCompleteGrade);
        autoCompleteSubject = view.findViewById(R.id.autoCompleteSubject);
        autoCompleteLocation = view.findViewById(R.id.autoCompleteLocation);
        btnSaveFilters = view.findViewById(R.id.btnSaveFilters);

        // Populate dropdowns
        setupDropdownMenus();

        // Fetch City Data
        fetchCityData();

        // Save Button Click Listener
        btnSaveFilters.setOnClickListener(v -> {
            String grade = autoCompleteGrade.getText().toString().trim();
            String subject = autoCompleteSubject.getText().toString().trim();

            if (listener != null) {
                listener.onFiltersSelected(grade, subject);
            }
            dismiss();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        return dialog;
    }

    private void setupDropdownMenus() {
        String[] gradeLevels = {
                "Primary","Secondary","Middle School","1-5","6-8","9th", "10th", "11th", "12th"
        };

        String[] subjects = {"Accountancy","Math", "Science", "English", "History", "Geography","Economics","Computer Science","Sociology","Business Studies","Biology"};

        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, gradeLevels);
        autoCompleteGrade.setAdapter(gradeAdapter);
        autoCompleteGrade.setOnClickListener(v -> autoCompleteGrade.showDropDown());

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, subjects);
        autoCompleteSubject.setAdapter(subjectAdapter);
        autoCompleteSubject.setOnClickListener(v -> autoCompleteSubject.showDropDown());
    }

    private void fetchCityData() {
        new Thread(() -> {
            List<String> cityList = new ArrayList<>();
            try {
                Connection_Class connectionClass = new Connection_Class();
                Connection connection = connectionClass.CONN();
                if (connection != null) {
                    String query = "SELECT city_nm FROM city";
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        cityList.add(rs.getString("city_nm"));
                    }
                    rs.close();
                    stmt.close();
                    connection.close();
                }
            } catch (Exception e) {
                Log.e("FilterDialogFragment", "Error fetching city data: " + e.getMessage());
            }

            // Update UI on the main thread
            requireActivity().runOnUiThread(() -> {
                if (autoCompleteLocation != null) {
                    autoCompleteLocation.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, cityList));
                    autoCompleteLocation.setOnClickListener(v -> autoCompleteLocation.showDropDown());
                }
            });
        }).start();
    }
}
