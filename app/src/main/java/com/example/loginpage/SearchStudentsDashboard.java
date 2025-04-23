package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.fragments.FilterDialogFragment;
import com.example.loginpage.models.UserSearchResult;

import java.util.List;

public class SearchStudentsDashboard extends AppCompatActivity implements FilterDialogFragment.OnFiltersSelectedListener {

    private LinearLayout cardContainer;
    private String gradeFilter;
    private String subjectFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_students_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cardContainer = findViewById(R.id.cardContainer);

        // Retrieve Grade and Subject from Intent
        Intent intent = getIntent();
        gradeFilter = intent.getStringExtra("GRADE");
        subjectFilter = intent.getStringExtra("SUBJECT");

        fetchAndDisplayTeachers();

        Button filterButton = findViewById(R.id.buttonFilter);
        filterButton.setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            FilterDialogFragment filterDialog = new FilterDialogFragment();
            filterDialog.setOnFiltersSelectedListener(this); // Set the listener
            filterDialog.show(fm, "filter_dialog");
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onFiltersSelected(String grade, String subject) {
        gradeFilter = grade;
        subjectFilter = subject;
        fetchAndDisplayTeachers();
    }

    private void fetchAndDisplayTeachers() {
        DatabaseHelper.searchUsersForTS(this, "T", 0, 0, 0, new DatabaseHelper.ProcedureResultCallback<List<UserSearchResult>>() {
            @Override
            public void onSuccess(List<UserSearchResult> userList) {
                if (userList != null && !userList.isEmpty()) {
                    if (cardContainer != null) {
                        cardContainer.removeAllViews();
                    } else {
                        Log.e("SearchStudentsDashboard", "ERROR: cardContainer is NULL (inside onSuccess)!");
                        return;
                    }

                    for (UserSearchResult teacher : userList) {
                        // Apply filters
                        if ((gradeFilter == null || gradeFilter.isEmpty() || teacher.getGradeName().equalsIgnoreCase(gradeFilter)) &&
                                (subjectFilter == null || subjectFilter.isEmpty() || teacher.getSubjectName().equalsIgnoreCase(subjectFilter))) {

                            View cardView = LayoutInflater.from(SearchStudentsDashboard.this).inflate(R.layout.teacher_card, cardContainer, false);

                            TextView name = cardView.findViewById(R.id.tvTeacherName);
                            TextView grade = cardView.findViewById(R.id.tvGrade);
                            TextView subjects = cardView.findViewById(R.id.tvSubjects);
                            TextView referralCode = cardView.findViewById(R.id.tvLocation);
                            ImageView profileIcon = cardView.findViewById(R.id.profileIcon);
                            Button messageButton = cardView.findViewById(R.id.tvNotificationButton);
                            Button timeTableButton = cardView.findViewById(R.id.tvTimeTableButton);

                            messageButton.setOnClickListener(v -> {
                                Intent intent = new Intent(SearchStudentsDashboard.this, NotificationStudentsMessage.class);
                                intent.putExtra("USER_PHONE", teacher.getMobileNo());
                                intent.putExtra("USER_ID", teacher.getUserId());
                                intent.putExtra("USER_FIRST_NAME", teacher.getUsername());
                                Log.d("SearchTeachersDashboard","Intent passed for UserID: " + teacher.getUserId());
                                startActivity(intent);
                            });


                            timeTableButton.setOnClickListener(v -> {
                                Intent intent = new Intent(SearchStudentsDashboard.this, ShowTimeTableNewView.class);
                                intent.putExtra("USER_PHONE", teacher.getMobileNo());
                                intent.putExtra("USER_ID", teacher.getUserId());
                                intent.putExtra("USER_FIRST_NAME", teacher.getUsername());
                                Log.d("SearchTeachersDashboard","Intent passed for UserID: " + teacher.getUserId());
                                startActivity(intent);
                            });


                            profileIcon.setOnClickListener(v -> {
                                Log.d("SearchStudentsDashboard", "Teacher UserID: " + teacher.getUserId());
                                Log.d("SearchStudentsDashboard", "Teacher Name: " + teacher.getUsername() + ", Teacher UserID before Intent: " + teacher.getUserId());
                                Intent intent = new Intent(SearchStudentsDashboard.this, ProfilePageTeacher.class);

                                if (teacher.getMobileNo() != null) {
                                    intent.putExtra("USER_PHONE", teacher.getMobileNo());
                                }
                                if (teacher.getUserId() != 0) {
                                    intent.putExtra("USER_ID", String.valueOf(teacher.getUserId())); // Convert to String
                                }
                                if (teacher.getUsername() != null) {
                                    intent.putExtra("USER_FIRST_NAME", teacher.getUsername());
                                }
                                if (teacher.getSelfReferralCode() != null) {
                                    intent.putExtra("USER_SELF_REFERRAL", teacher.getSelfReferralCode());
                                }
                                if (teacher.getEmail() != null) {
                                    intent.putExtra("USER_EMAIL", teacher.getEmail());
                                }
                                startActivity(intent);
                            });


                            name.setText(teacher.getUsername());
                            grade.setText("Grade: " + teacher.getGradeName());
                            subjects.setText("Subjects: " + teacher.getSubjectName());
                            referralCode.setText("Referral Code: " + teacher.getSelfReferralCode());

                            cardContainer.addView(cardView);
                        }
                    }
                } else {
                    Log.e("SearchStudentsDashboard", "No teachers found.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("SearchStudentsDashboard", "Error fetching teachers: " + errorMessage);
            }
        });
    }
}