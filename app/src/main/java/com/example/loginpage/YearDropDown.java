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

public class YearDropDown extends AppCompatActivity {
    private static final String TAG = "YearDropDown";
    private AutoCompleteTextView yearDropdown;
    private Map<String, String> yearMap = new HashMap<>(); // Year -> YearID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_drop_down);

        yearDropdown = findViewById(R.id.yearDropdown);
        Log.d(TAG, "onCreate: Activity Started");

        loadYears();
    }

    private void loadYears() {
        String query = "SELECT YearID, Year FROM Year WHERE active = 'true' ORDER BY Year";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "No Year records found!");
                    Toast.makeText(YearDropDown.this, "No Years Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> years = new ArrayList<>();
                yearMap.clear();

                for (Map<String, String> row : result) {
                    String id = row.get("YearID");
                    String name = row.get("Year");

                    Log.d(TAG, "Year Retrieved - ID: " + id + ", Name: " + name);
                    years.add(name);
                    yearMap.put(name, id);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(YearDropDown.this, android.R.layout.simple_dropdown_item_1line, years);
                yearDropdown.setAdapter(adapter);
                Log.d(TAG, "Adapter set with values: " + years);
            }
        });

        yearDropdown.setOnClickListener(v -> {
            Log.d(TAG, "yearDropdown clicked - Showing dropdown");
            yearDropdown.showDropDown();
        });

        yearDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedYear = (String) parent.getItemAtPosition(position);
            String yearID = yearMap.get(selectedYear);
            Log.d(TAG, "Year Selected: " + selectedYear + " (ID: " + yearID + ")");
        });
    }
}
