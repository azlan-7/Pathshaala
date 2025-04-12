package com.example.loginpage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.ParentInfoAdapter;
import com.example.loginpage.models.ParentInfoModel;

import java.util.ArrayList;
import java.util.List;

public class StudentsParentInfoView extends AppCompatActivity {

    private LinearLayout GuardianInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_students_parent_info_view);

        RecyclerView recyclerView = findViewById(R.id.guardianRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        List<DatabaseHelper.ParentGuardianInfo> infoList = DatabaseHelper.getParentGuardianInfo(userId);

        if (infoList != null && !infoList.isEmpty()) {
            DatabaseHelper.ParentGuardianInfo info = infoList.get(0);
            ParentInfoModel model = new ParentInfoModel(
                    info.FatherName, info.FatherContactNo,
                    info.MotherName, info.MotherContactNo,
                    info.GuardianName, info.GuardianRelation, info.GuardianContactNo
            );

            List<ParentInfoModel> modelList = new ArrayList<>();
            modelList.add(model);

            ParentInfoAdapter adapter = new ParentInfoAdapter(modelList);
            recyclerView.setAdapter(adapter);
        }



        // Retrieve data
        retrieveParentGuardianInfo();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void retrieveParentGuardianInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1); // Retrieve from SharedPreferences
        String fatherContactNo = getIntent().getStringExtra("FATHER_CONTACT_NO"); //get the father contact number from the intent.

        if (userId == -1) {
            Toast.makeText(this, "User ID not provided.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<DatabaseHelper.ParentGuardianInfo> infoList = DatabaseHelper.getParentGuardianInfo(userId);

        if (infoList != null && !infoList.isEmpty()) {
            // Display data
            displayParentGuardianInfo(infoList.get(0)); // Assuming you want to display the first result
        } else {
            Toast.makeText(this, "No data found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayParentGuardianInfo(DatabaseHelper.ParentGuardianInfo info) {
        // Create TextViews to display the data
        TextView fatherNameTextView = new TextView(this);
        fatherNameTextView.setText("Father Name: " + info.FatherName);

        TextView fatherContactTextView = new TextView(this);
        fatherContactTextView.setText("Father Contact: " + info.FatherContactNo);

        TextView motherNameTextView = new TextView(this);
        motherNameTextView.setText("Mother Name: " + info.MotherName);

        TextView motherContactTextView = new TextView(this);
        motherContactTextView.setText("Mother Contact: " + info.MotherContactNo);

        TextView guardianNameTextView = new TextView(this);
        guardianNameTextView.setText("Guardian Name: " + info.GuardianName);

        TextView guardianRelationTextView = new TextView(this);
        guardianRelationTextView.setText("Guardian Relation: " + info.GuardianRelation);

        TextView guardianContactTextView = new TextView(this);
        guardianContactTextView.setText("Guardian Contact: " + info.GuardianContactNo);

        // Add TextViews to the LinearLayout
        GuardianInfo.addView(fatherNameTextView);
        GuardianInfo.addView(fatherContactTextView);
        GuardianInfo.addView(motherNameTextView);
        GuardianInfo.addView(motherContactTextView);
        GuardianInfo.addView(guardianNameTextView);
        GuardianInfo.addView(guardianRelationTextView);
        GuardianInfo.addView(guardianContactTextView);
    }
}