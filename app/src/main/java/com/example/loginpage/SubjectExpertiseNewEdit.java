package com.example.loginpage;
import com.example.loginpage.adapters.SubjectAdapter;


import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class SubjectExpertiseNewEdit extends AppCompatActivity {
    private RecyclerView majorSubjectsRecyclerView, minorSubjectsRecyclerView;
    private SubjectAdapter majorAdapter, minorAdapter;
    private SharedPreferences sharedPreferences;
    private Set<String> selectedMajorSubjects, selectedMinorSubjects;
    private Button btnSaveSubjects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject_expertise_new_edit);

        List<String> majorSubjects = Arrays.asList("Mathematics", "Physics", "Chemistry", "Biology");
        List<String> minorSubjects = Arrays.asList("Music", "Art", "Drama", "Dance");

        Log.d("SubjectsDebug", "Major Subjects: " + majorSubjects.toString());
        Log.d("SubjectsDebug", "Minor Subjects: " + minorSubjects.toString());

        majorSubjectsRecyclerView = findViewById(R.id.majorSubjectsRecycler);
        minorSubjectsRecyclerView = findViewById(R.id.minorSubjectsRecycler);

        btnSaveSubjects = findViewById(R.id.button21);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        selectedMajorSubjects = new HashSet<>(sharedPreferences.getStringSet("MAJOR_SUBJECTS", new HashSet<>()));
        selectedMinorSubjects = new HashSet<>(sharedPreferences.getStringSet("MINOR_SUBJECTS", new HashSet<>()));





        // Setup adapters
        majorAdapter = new SubjectAdapter(this, majorSubjects, selectedMajorSubjects, "MAJOR_SUBJECTS");
        minorAdapter = new SubjectAdapter(this, minorSubjects, selectedMinorSubjects, "MINOR_SUBJECTS");

        // Set RecyclerViews
        majorSubjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        minorSubjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        majorSubjectsRecyclerView.setAdapter(majorAdapter);
        minorSubjectsRecyclerView.setAdapter(minorAdapter);

        btnSaveSubjects.setOnClickListener(v -> {
            Log.d("SubjectsDebug", "Saved Major Subjects: " + selectedMajorSubjects.toString());
            Log.d("SubjectsDebug", "Saved Minor Subjects: " + selectedMinorSubjects.toString());

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("MAJOR_SUBJECTS", new HashSet<>(majorAdapter.getSelectedSubjects()));
            editor.putStringSet("MINOR_SUBJECTS", new HashSet<>(minorAdapter.getSelectedSubjects()));
            editor.apply();

            majorAdapter.notifyDataSetChanged();
            minorAdapter.notifyDataSetChanged();

            Intent intent = new Intent(SubjectExpertiseNewEdit.this, SubjectExpertiseNew.class);
            startActivity(intent);
            finish();
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}