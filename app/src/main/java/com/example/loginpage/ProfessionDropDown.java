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

public class ProfessionDropDown extends AppCompatActivity {
    private static final String TAG = "ProfessionDropDown";
    private AutoCompleteTextView professionDropdown;
    private Map<String, String> professionMap = new HashMap<>(); // ProfessionName -> ProfessionID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profession_drop_down);

        professionDropdown = findViewById(R.id.professionDropdown);
        Log.d(TAG, "onCreate: Activity Started");

        loadProfessions();
    }

    private void loadProfessions() {
        String query = "SELECT ProfessionID, ProfessionName FROM Profession WHERE active = 'true' ORDER BY ProfessionName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "No Profession records found!");
                    Toast.makeText(ProfessionDropDown.this, "No Professions Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> professions = new ArrayList<>();
                professionMap.clear();

                for (Map<String, String> row : result) {
                    String id = row.get("ProfessionID");
                    String name = row.get("ProfessionName");

                    Log.d(TAG, "Profession Retrieved - ID: " + id + ", Name: " + name);
                    professions.add(name);
                    professionMap.put(name, id);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ProfessionDropDown.this, android.R.layout.simple_dropdown_item_1line, professions);
                professionDropdown.setAdapter(adapter);
                Log.d(TAG, "Adapter set with values: " + professions);
            }
        });

        professionDropdown.setOnClickListener(v -> {
            Log.d(TAG, "professionDropdown clicked - Showing dropdown");
            professionDropdown.showDropDown();
        });

        professionDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProfession = (String) parent.getItemAtPosition(position);
            String professionID = professionMap.get(selectedProfession);
            Log.d(TAG, "Profession Selected: " + selectedProfession + " (ID: " + professionID + ")");
        });
    }
}
