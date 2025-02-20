package com.example.loginpage;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.loginpage.fragments.FilterDialogFragment;

public class SearchStudentsDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_students_dashboard);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button filterButton = findViewById(R.id.buttonFilter);
        filterButton.setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            FilterDialogFragment filterDialog = new FilterDialogFragment();
            filterDialog.show(fm, "filter_dialog");
        });

        // Apply background colors
        CardView cardTeacher1 = findViewById(R.id.cardTeacher1);
        cardTeacher1.setCardBackgroundColor(Color.parseColor("#D3E2F1"));

        CardView cardTeacher2 = findViewById(R.id.cardTeacher2);
        cardTeacher2.setCardBackgroundColor(Color.parseColor("#D3E2F1"));

        CardView cardTeacher3 = findViewById(R.id.cardTeacher3);
        CardView cardTeacher4 = findViewById(R.id.cardTeacher4);
        CardView cardTeacher5 = findViewById(R.id.cardTeacher5);

        cardTeacher3.setCardBackgroundColor(Color.parseColor("#D3E2F1"));
        cardTeacher4.setCardBackgroundColor(Color.parseColor("#D3E2F1"));
        cardTeacher5.setCardBackgroundColor(Color.parseColor("#D3E2F1"));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
