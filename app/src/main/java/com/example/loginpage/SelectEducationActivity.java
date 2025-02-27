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

public class SelectEducationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_education);

//        private void loadMastersFromDatabase(String strqry, AutoCompleteTextView autoCompleteCity) {
//            new AsyncTask<Void, Void, List<String>>() {
//                @Override
//                protected List<String> doInBackground(Void... voids) {
//                    List<String> cityList = new ArrayList<>();
//                    try {
//                        Connection_Class connectionClass = new Connection_Class();
//                        Connection connection = connectionClass.CONN();
//                        if (connection != null) {
//                            String query = "SELECT city_nm FROM city";
//                            Statement stmt = connection.createStatement();
//                            ResultSet rs = stmt.executeQuery(query);
//
//                            while (rs.next()) {
//                                cityList.add(rs.getString("city_nm"));
//                            }
//                            rs.close();
//                            stmt.close();
//                            connection.close();
//                        }
//                    } catch (Exception e) {
//                        Log.e("UserOnboardingRadio", "Error fetching city data: " + e.getMessage());
//                    }
//                    return cityList;
//                }
//
//                @Override
//                protected void onPostExecute(List<String> cities) {
//                    if (!cities.isEmpty()) {
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(UserOnboardingRadio.this, android.R.layout.simple_dropdown_item_1line, cities);
//                        SelectEducationActivity.this.autoCompleteCity.setAdapter(adapter);
//                        SelectEducationActivity.this.autoCompleteCity.setOnClickListener(v -> UserOnboardingRadio.this.autoCompleteCity.showDropDown()); // Show dropdown when clicked
//                    }
//                }
//            }.execute();
//        }





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}