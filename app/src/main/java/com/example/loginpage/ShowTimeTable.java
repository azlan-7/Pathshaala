package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.adapters.TimeTableExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowTimeTable extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> sectionTitles;
    private HashMap<String, List<String>> sectionItems;
    private Button saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_time_table);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        expandableListView = findViewById(R.id.expandableListView);
        expandableListView.setBackground(ContextCompat.getDrawable(this, R.drawable.expandable_list_background));
        saveButton = findViewById(R.id.saveTimeTableBtn);
        saveButton.setOnClickListener(v->{
            Intent intent = new Intent(ShowTimeTable.this,TeachersDashboardNew.class);
            startActivity(intent);
        });

        setupExpandableList();
        expandableListView.setAdapter(adapter);

        // Collapse previously expanded group
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup) {
                    expandableListView.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });

        // Handle edit clicks
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String selectedItem = sectionItems.get(sectionTitles.get(groupPosition)).get(childPosition);
            if ("Edit Timetable".equals(selectedItem)) {
                Toast.makeText(this, "Edit clicked for " + sectionTitles.get(groupPosition), Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }
    private void setupExpandableList() {
        sectionTitles = new ArrayList<>();
        sectionItems = new HashMap<>();

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday","Sunday"};
        for (String day : days) {
            sectionTitles.add(day);

            List<String> dayItems = new ArrayList<>();
            dayItems.add("Class 1: 09:00 AM - 10:00 AM");
            dayItems.add("Class 2: 11:00 AM - 12:00 PM");
            dayItems.add("Class 3: 01:00 PM - 02:00 PM");
            dayItems.add("Edit Timetable"); // Add Edit option

            sectionItems.put(day, dayItems);
        }

        adapter = new TimeTableExpandableListAdapter(this, sectionTitles, sectionItems);
        expandableListView.setAdapter(adapter);


        // Optional: handle edit click
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String selectedItem = sectionItems.get(sectionTitles.get(groupPosition)).get(childPosition);
            if ("Edit Timetable".equals(selectedItem)) {
                // Launch edit activity or show dialog
                // Example:
                Toast.makeText(this, "Edit clicked for " + sectionTitles.get(groupPosition), Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }


}