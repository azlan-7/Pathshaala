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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.fragments.FilterDialogFragment;
import com.example.loginpage.models.UserSearchResult;

import java.util.List;

public class SearchTeachersDashboard extends AppCompatActivity implements FilterDialogFragment.OnFiltersSelectedListener {

    private LinearLayout cardContainer;
    private String gradeFilter;
    private String subjectFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_teachers_dashboard);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cardContainer = findViewById(R.id.cardContainer);
        // Retrieve Grade and Subject from Intent
        Intent intent = getIntent();
        gradeFilter = intent.getStringExtra("GRADE");
        subjectFilter = intent.getStringExtra("SUBJECT");

        fetchAndDisplaySearchResults();

        // Filter Button
        Button filterButton = findViewById(R.id.button36);
        filterButton.setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            FilterDialogFragment filterDialog = new FilterDialogFragment();
            filterDialog.setOnFiltersSelectedListener(this); // Set the listener
            filterDialog.show(fm, "filter_dialog");
        });

        // Handle window insets
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
        fetchAndDisplaySearchResults();
    }

    private void fetchAndDisplaySearchResults() {
        // Modify the searchUsersForTS call if your database supports filtering by Grade and Subject
        DatabaseHelper.searchUsersForTS(this, "S", 0, 0, 0, new DatabaseHelper.ProcedureResultCallback<List<UserSearchResult>>() {  // Corrected Parameter Type
            @Override
            public void onSuccess(List<UserSearchResult> userList) { // Corrected Override
                if (userList != null && !userList.isEmpty()) {
                    cardContainer.removeAllViews();

                    for (UserSearchResult student : userList) {
                        // Apply filters
                        if ((gradeFilter == null || gradeFilter.isEmpty() || student.getGradeName().equalsIgnoreCase(gradeFilter)) &&
                                (subjectFilter == null || subjectFilter.isEmpty() || student.getSubjectName().equalsIgnoreCase(subjectFilter))) {

                            View cardView = LayoutInflater.from(SearchTeachersDashboard.this).inflate(R.layout.student_card, cardContainer, false);

                            // ... (Populate cardView as before) ...
                            TextView name = cardView.findViewById(R.id.tvStudentName);
                            TextView email = cardView.findViewById(R.id.tvEmail);
                            TextView referral = cardView.findViewById(R.id.tvSelfReferral);
                            TextView grade = cardView.findViewById(R.id.tvGrade);
                            TextView subjects = cardView.findViewById(R.id.tvSubjects);
                            ImageView profileIcon = cardView.findViewById(R.id.profileIcon);
                            Button whatsAppButton = cardView.findViewById(R.id.whatsappButton);
                            Button notificationButton = cardView.findViewById(R.id.tvNotificationButton);

                            name.setText(student.getUsername());
                            email.setText(student.getEmail());
                            referral.setText(student.getSelfReferralCode());
                            grade.setText("Grade: " + student.getGradeName());
                            subjects.setText("Learning: " + student.getSubjectName());

                            Log.d("SearchTeachersDashboard", "Card - Name: " + student.getUsername() + ", Email: " + student.getEmail());

                            // --- Image Loading ---
                            String profileImageName = student.getUserImageName(); // Get the image name
                            Log.d("SearchTeachersDashboard", "✅ Retrieved Profile Image Name: " + profileImageName);

                            if (profileImageName != null && !profileImageName.isEmpty()) {
                                // Construct full image URL
                                String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + profileImageName;
                                Log.d("SearchTeachersDashboard", "✅ Profile image URL: " + imageUrl);

                                // Load image using Glide
                                Glide.with(SearchTeachersDashboard.this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.generic_avatar) // Show default profile pic if empty
                                        .error(R.drawable.generic_avatar) // Show default if loading fails
                                        .apply(RequestOptions.circleCropTransform()) // Make the image round
                                        .into(profileIcon);
                            } else {
                                Log.e("SearchTeachersDashboard", "❌ No profile image found for user.");
                                Glide.with(SearchTeachersDashboard.this)
                                        .load(R.drawable.generic_avatar)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(profileIcon);
                            }
                            // --- End Image Loading ---

                            profileIcon.setOnClickListener(v -> {
                                Intent intent = new Intent(SearchTeachersDashboard.this, ProfilePageStudent.class);
                                intent.putExtra("USER_PHONE", student.getMobileNo());
                                intent.putExtra("USER_ID", student.getUserId());
                                intent.putExtra("USER_FIRST_NAME", student.getUsername());
                                intent.putExtra("USER_SELF_REFERRAL", student.getSelfReferralCode());
                                intent.putExtra("USER_EMAIL", student.getEmail());
                                startActivity(intent);
                            });

                            whatsAppButton.setOnClickListener(v -> MoveToWhatsAppScreen());

                            notificationButton.setOnClickListener(v -> {
                                Intent intent = new Intent(SearchTeachersDashboard.this, NotificationTeachers.class);
                                intent.putExtra("USER_PHONE", student.getMobileNo());
                                intent.putExtra("USER_ID", student.getUserId());
                                intent.putExtra("USER_FIRST_NAME", student.getUsername());
                                Log.d("SearchTeachersDashboard", "Intent passed for UserID: " + student.getUserId());
                                startActivity(intent);
                            });

                            cardContainer.addView(cardView);
                        }
                    }
                } else {
                    Log.e("SearchTeachersDashboard", "No search results found.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("SearchTeachersDashboard", "Error fetching search results: " + errorMessage);
            }
        });
    }

    private void MoveToWhatsAppScreen() {
        Intent intent = new Intent(this, WhatsAppScreen.class);
        startActivity(intent);
    }
}
