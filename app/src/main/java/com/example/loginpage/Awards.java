package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Awards extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AwardAdapter awardAdapter;
    private List<Award> awardList;
    private Button addAwardsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_awards);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.awardsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dummy Data for Testing
        awardList = new ArrayList<>();
        awardList.add(new Award("Best Teacher Award", "Delhi Public School", "2017", "PCM"));
        awardList.add(new Award("Teacher of the Year", "Delhi Public School", "2019", "PCM"));

        awardAdapter = new AwardAdapter(awardList, position -> {
            awardList.remove(position);
            awardAdapter.notifyItemRemoved(position);
        });

        addAwardsButton = findViewById(R.id.addAwardButton);

        // Set Click Listener to open AddAwards Activity
        addAwardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Awards.this, AddAwards.class);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(awardAdapter);
    }
}
