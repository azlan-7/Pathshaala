package com.example.loginpage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.adapters.AwardsAdapter;
import com.example.loginpage.models.AwardModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Awards extends AppCompatActivity {
    private RecyclerView awardsRecyclerView;
    private AwardsAdapter adapter;
    private List<AwardModel> awardsList;
    private SharedPreferences sharedPreferences;
    private ImageView addAwardButton;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_awards);

        awardsRecyclerView = findViewById(R.id.awardsRecyclerView);
        addAwardButton = findViewById(R.id.imageView82);
        continueButton = findViewById(R.id.button26);

        awardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);


        // Load Awards
        loadAwardsData();

        awardsList = loadAwardsData();
        adapter = new AwardsAdapter(this, awardsList);
        awardsRecyclerView.setAdapter(adapter);

        addAwardButton.setOnClickListener(v -> {
            Intent intent = new Intent(Awards.this, AddAwards.class);
            startActivity(intent);
        });


        continueButton.setOnClickListener(v -> {
            Toast.makeText(Awards.this, "Proceeding to next section!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Awards.this, TeachersInfo.class)); // Change to the actual next activity
            finish();

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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