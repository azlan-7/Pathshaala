package com.example.loginpage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import com.example.loginpage.fragments.FilterDialogFragment;

public class SearchTeachersDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_teachers_dashboard);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[][] students = {
                {"John Doe", "Grade: 10", "Math, Science", "Lucknow","S-012378"},
                {"Jane Doe", "Grade: 8", "English, History", "Delhi","S-231026"},
                {"Arjun Kumar", "Grade: 9", "Physics, Chemistry", "Mumbai","S-554398"},
                {"Michael Scott", "Grade: 7", "Biology, Geography", "Bangalore","S-544213"},
                {"Kevin Malone", "Grade: 7", "History, Geography", "Jaipur","S-762231"}
        };

        int[] cardIds = {R.id.cardStudent1, R.id.cardStudent2, R.id.cardStudent3, R.id.cardStudent4, R.id.cardStudent5};

        for (int i = 0; i < students.length; i++) {
            View cardView = findViewById(cardIds[i]);

            TextView name = cardView.findViewById(R.id.tvStudentName);
            TextView grade = cardView.findViewById(R.id.tvGrade);
            TextView subjects = cardView.findViewById(R.id.tvSubjects);
            TextView location = cardView.findViewById(R.id.tvLocation);
            TextView referral = cardView.findViewById(R.id.tvSelfReferral);
            Button whatsAppButton = cardView.findViewById(R.id.whatsappButton);

            name.setText(students[i][0]);
            grade.setText(students[i][1]);
            subjects.setText("Learning: " + students[i][2]);
            location.setText("Location: " + students[i][3]);
            referral.setText(students[i][4]);

            // Set card background color
            ((CardView) cardView).setCardBackgroundColor(Color.parseColor("#D3E2F1"));

            // Set WhatsApp button click listener
            whatsAppButton.setOnClickListener(v -> MoveToWhatsAppScreen());
        }

        // Filter Button
        Button filterButton = findViewById(R.id.button36);
        filterButton.setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            FilterDialogFragment filterDialog = new FilterDialogFragment();
            filterDialog.show(fm, "filter_dialog");
        });

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void MoveToWhatsAppScreen() {
        Intent intent = new Intent(this, WhatsAppScreen.class);
        startActivity(intent);
    }
}
