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

public class AwardsDropDown extends AppCompatActivity {
    private static final String TAG = "AwardsDropDown";
    private AutoCompleteTextView awardsDropdown;
    private Map<String, String> awardsMap = new HashMap<>(); // AwardTitleName -> AwardTitleID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awards_drop_down);

        awardsDropdown = findViewById(R.id.awardsDropdown);
        Log.d(TAG, "onCreate: Activity Started");

        loadAwards();
    }

    private void loadAwards() {
        String query = "SELECT AwardTitleID, AwardTitleName FROM AwardTitle WHERE active = 'true' ORDER BY AwardTitleName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (result == null || result.isEmpty()) {
                    Log.e(TAG, "No Awards records found!");
                    Toast.makeText(AwardsDropDown.this, "No Awards Found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> awards = new ArrayList<>();
                awardsMap.clear();

                for (Map<String, String> row : result) {
                    String id = row.get("AwardTitleID");
                    String name = row.get("AwardTitleName");

                    Log.d(TAG, "Award Retrieved - ID: " + id + ", Name: " + name);
                    awards.add(name);
                    awardsMap.put(name, id);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AwardsDropDown.this, android.R.layout.simple_dropdown_item_1line, awards);
                awardsDropdown.setAdapter(adapter);
                Log.d(TAG, "Adapter set with values: " + awards);
            }
        });

        awardsDropdown.setOnClickListener(v -> {
            Log.d(TAG, "awardsDropdown clicked - Showing dropdown");
            awardsDropdown.showDropDown();
        });

        awardsDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedAward = (String) parent.getItemAtPosition(position);
            String awardID = awardsMap.get(selectedAward);
            Log.d(TAG, "Award Selected: " + selectedAward + " (ID: " + awardID + ")");
        });
    }
}
