package com.example.loginpage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.Nullable;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.AwardsAdapter;
import com.example.loginpage.models.AwardModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Awards extends AppCompatActivity {
    private RecyclerView awardsRecyclerView;
    private AwardsAdapter adapter;
    private List<AwardModel> awardsList;
    private SharedPreferences sharedPreferences;
    private ImageView addAwardButton, backButton;
    private Button continueButton;
    private List<AwardModel> awardList = new ArrayList<>();
    private TextView tvNoAwards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_awards);

        awardsRecyclerView = findViewById(R.id.awardsRecyclerView);
        addAwardButton = findViewById(R.id.imageView82);
        continueButton = findViewById(R.id.button26);
        backButton = findViewById(R.id.imageView155);

        awardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AwardsAdapter(this, awardList);
        awardsRecyclerView.setAdapter(adapter);
        tvNoAwards = findViewById(R.id.tvNoAwards);



        TextView awardTextView = findViewById(R.id.textView111);
        awardTextView.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("USER_ID", -1);

            if (userId == -1) {
                Toast.makeText(Awards.this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper.UserWiseAwardSelect(this, userId, new DatabaseHelper.DatabaseCallback() {
                @Override
                public void onSuccess(List<Map<String, String>> result) {
                    if (!result.isEmpty()) {
                        String awardFileName = result.get(0).get("AwardFileName"); // ✅ Get award file name

                        if (awardFileName != null && !awardFileName.equals("No_File") && !awardFileName.isEmpty()) {
                            Intent intent = new Intent(Awards.this, AwardsViewer.class);
                            intent.putExtra("AWARD_FILE_NAME", awardFileName);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Awards.this, "No award image found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Awards.this, "No awards found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onMessage(String message) {
                    Toast.makeText(Awards.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(Awards.this, "Database error: " + error, Toast.LENGTH_LONG).show();
                    Log.e("Awards", "Error: " + error);
                }
            });
        });





        awardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        fetchUserAwards();
        // Load Awards
        loadAwardsData();

        awardsList = loadAwardsData();
        adapter = new AwardsAdapter(this, awardsList);
        awardsRecyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());

        addAwardButton.setOnClickListener(v -> {
            Intent intent = new Intent(Awards.this, AddAwards.class);
            startActivity(intent);
        });


        continueButton.setOnClickListener(v -> {
            Toast.makeText(Awards.this, "Proceeding to next section!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Awards.this, TeachersInfoSubSection.class)); // Change to the actual next activity
            finish();

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void fetchUserAwards() {
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper.UserWiseAwardSelect(this, userId, new DatabaseHelper.DatabaseCallback() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                awardList.clear(); // Clear previous data

                if (!result.isEmpty()) {
                    for (Map<String, String> row : result) {
                        String title = row.get("AwardTitleName");
                        String organisation = row.get("AwardingOrganization");
                        String year = row.get("IssueYear");
                        String description = row.get("Remarks");
                        String awardFileName = row.get("AwardFileName");

                        Log.d("Awards", "Award: " + title + " | File: " + awardFileName);

                        awardList.add(new AwardModel(title, organisation, year, description));

                    }
                    adapter.updateData(awardList);
                    tvNoAwards.setVisibility(View.GONE); // Hide empty state message
                } else {
                    tvNoAwards.setVisibility(View.VISIBLE); // Show if no awards found
                }
            }

            @Override
            public void onMessage(String message) {
                Log.d("fetchUserAwards", "Message: " + message);
                Toast.makeText(Awards.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(Awards.this, "Database error: " + error, Toast.LENGTH_LONG).show();
                Log.e("fetchUserAwards", "Error: " + error);
            }
        });
    }



    private List<AwardModel> loadAwardsData() {
        String json = sharedPreferences.getString("AWARD_LIST", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<AwardModel>>() {}.getType();
        List<AwardModel> awardList = (json == null) ? new ArrayList<>() : gson.fromJson(json, type);
        return awardList;
    }
    @Override
    protected void onResume() {
        super.onResume();
        awardsList = loadAwardsData();


        if (awardsList.isEmpty()) {
            Log.e("Awards", "No awards found in SharedPreferences.");
        } else {
            Log.d("Awards", "Loaded " + awardsList.size() + " awards.");
        }


        adapter.updateData(awardsList);
    }
}