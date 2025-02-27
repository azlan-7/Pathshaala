package com.example.loginpage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.MySqliteDatabase.Connection_Class;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SelectCityActivity extends AppCompatActivity {

    private AutoCompleteTextView cityDropdown;
    private List<String> cityList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_city);

        cityDropdown = findViewById(R.id.cityDropdown);

        // Initialize Adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cityList);
        cityDropdown.setAdapter(adapter);

        // Fetch City Data
        new FetchCityTask().execute();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Force Show Dropdown on Click
        cityDropdown.setOnClickListener(v -> cityDropdown.showDropDown());
    }

    private class FetchCityTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> cities = new ArrayList<>();
            try {
                Connection_Class connectionClass = new Connection_Class();
                Connection connection = connectionClass.CONN();
                if (connection != null) {
                    String query = "SELECT city_nm FROM city";
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {
                        cities.add(rs.getString("city_nm"));
                    }
                    rs.close();
                    stmt.close();
                    connection.close();
                }
            } catch (Exception e) {
                Log.e("SelectCityActivity", "Error fetching city data: " + e.getMessage());
            }
            return cities;
        }

        @Override
        protected void onPostExecute(List<String> cities) {
            if (cities != null && !cities.isEmpty()) {
                cityList.clear();
                cityList.addAll(cities);
                adapter.notifyDataSetChanged(); // Update UI
            }
        }
    }
}
