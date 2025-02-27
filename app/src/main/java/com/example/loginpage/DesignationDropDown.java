package com.example.loginpage;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesignationDropDown extends AppCompatActivity {
    private static final String TAG = "DesignationDropDown";
    private AutoCompleteTextView designationDropdown;
    private Map<String, String> designationMap = new HashMap<>(); // DesignationName -> DesignationID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designation_drop_down);

        designationDropdown = findViewById(R.id.designationDropdown);
        Log.d(TAG, "onCreate: Activity Started");

        loadDesignations();
    }

    private void loadDesignations() {
        String query = "SELECT DesignationID, DesignationName FROM Designation WHERE active = 'true' ORDER BY DesignationName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "No Designation records found!");
                    Toast.makeText(DesignationDropDown.this, "No Designations Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> designations = new ArrayList<>();
                designationMap.clear();

                for (Map<String, String> row : result) {
                    String id = row.get("DesignationID");
                    String name = row.get("DesignationName");

                    Log.d(TAG, "Designation Retrieved - ID: " + id + ", Name: " + name);
                    designations.add(name);
                    designationMap.put(name, id);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(DesignationDropDown.this, android.R.layout.simple_dropdown_item_1line, designations);
                designationDropdown.setAdapter(adapter);
                Log.d(TAG, "Adapter set with values: " + designations);
            }
        });

        designationDropdown.setOnClickListener(v -> {
            Log.d(TAG, "designationDropdown clicked - Showing dropdown");
            designationDropdown.showDropDown();
        });

        designationDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDesignation = (String) parent.getItemAtPosition(position);
            String designationID = designationMap.get(selectedDesignation);
            Log.d(TAG, "Designation Selected: " + selectedDesignation + " (ID: " + designationID + ")");
        });
    }
}
