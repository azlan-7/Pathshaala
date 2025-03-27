package com.example.loginpage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.fragments.FilterDialogFragment;
import com.example.loginpage.models.UserDetailsClass;
import com.example.loginpage.models.UserWiseSubject;
import com.example.loginpage.models.UserWiseWorkExperience;

import java.util.List;
import java.util.Map;

public class SearchStudentsDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_students_dashboard);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[][] teachers = {
                {"Mr. John Doe", "Experience: 10 years", "Math, Science", "Lucknow"},
                {"Ms. Jane Smith", "Experience: 8 years", "English, History", "Delhi"},
                {"Dr. Arjun Kumar", "Experience: 12 years", "Physics, Chemistry", "Mumbai"},
                {"Prof. Jim Halert", "Experience: 15 years", "Biology, Geography", "Bangalore"},
                {"Mr. Dwight Schrute", "Experience: 5 years", "Agriculture, Business", "Scranton"}
        };

        int[] cardIds = {R.id.cardTeacher1, R.id.cardTeacher2, R.id.cardTeacher3, R.id.cardTeacher4, R.id.cardTeacher5};

        String query = "{call sp_UserDetailsInsertUpdate(3, 0, '', '', '', '', NULL, '', '', '', '', '', '', '', NULL, '', '', '', '', '')}";

        DatabaseHelper.UserDetailsSelect(this, "3", "", userList -> {
            if (userList.size() > 0) {
                for (int i = 0; i < userList.size() && i < cardIds.length; i++) {
                    View cardView = findViewById(cardIds[i]);

                    TextView name = cardView.findViewById(R.id.tvTeacherName);
                    TextView experience = cardView.findViewById(R.id.tvExperience);
                    TextView subjects = cardView.findViewById(R.id.tvSubjects);
                    TextView location = cardView.findViewById(R.id.tvLocation);
                    Button payment = cardView.findViewById(R.id.paymentButton);

                    UserDetailsClass teacher = userList.get(i);
                    name.setText(teacher.getName());
                    location.setText("Referral Code: " + teacher.getSelfReferralCode()); // Adjust field if needed

                    ImageView profileIcon = cardView.findViewById(R.id.profileIcon);

                    // Fetch profile image name
                    String profileImageName = teacher.getUserImageName();

                    Log.d("ProfileImage", "✅ Retrieved Profile Image Name: " + profileImageName);

                    if (profileImageName != null && !profileImageName.isEmpty()) {
                        // Construct full image URL
                        String imageUrl = "http://129.154.238.214/Pathshaala/UploadedFiles/UserProfile/" + profileImageName;
                        Log.d("ProfileImage", "✅ Profile image URL: " + imageUrl);

                        // Load image using Glide
                        Glide.with(cardView.getContext())
                                .load(imageUrl)
                                .placeholder(R.drawable.generic_avatar) // Show default profile pic if empty
                                .error(R.drawable.generic_avatar) // Show default if loading fails
                                .apply(RequestOptions.circleCropTransform()) // Make the image round
                                .into(profileIcon);
                    } else {
                        Log.e("ProfileImage", "❌ No profile image found for user.");
                        profileIcon.setImageResource(R.drawable.generic_avatar); // Default avatar
                    }

                    // Fetch Experience for the teacher
                    DatabaseHelper.UserWiseWorkExperienceSelect(this, "3", teacher.getUserId(), new DatabaseHelper.WorkExperienceCallback() {
                        @Override
                        public void onSuccess(List<UserWiseWorkExperience> workExperienceList) {
                            if (workExperienceList != null && !workExperienceList.isEmpty()) {
                                Log.d("DatabaseHelper", "Experience count: " + workExperienceList.size());
                                UserWiseWorkExperience workExperience = workExperienceList.get(0);
                                experience.setText("Experience: " + workExperience.getWorkExperience() + " years");
                            } else {
                                experience.setText("Experience: N/A");
                            }
                        }

                        @Override
                        public void onMessage(String message) {
                            Log.d("Experience Fetch", "Message: " + message);
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("Experience Fetch", "Error: " + error);
                        }
                    });

                    //Fetch Subject Data
                    DatabaseHelper.UserWiseSubjectSelect(this, "3", teacher.getUserId(), new DatabaseHelper.UserWiseSubjectResultListener() {
                        @Override
                        public void onQueryResult(List<UserWiseSubject> subjectsList) {
                            Log.d("Subjects Fetch", "✅ Filtered Subjects Retrieved for UserID: " + teacher.getUserId() + " -> " + subjectsList.size());

                            if (subjectsList != null && !subjectsList.isEmpty()) {
                                StringBuilder subjectText = new StringBuilder("Subjects: ");
                                int count = 0;

                                for (UserWiseSubject subject : subjectsList) {
                                    if (subject.getSubjectName() != null && !subject.getSubjectName().trim().isEmpty()) {
                                        Log.d("Subjects Fetch", "Processing Subject: " + subject.getSubjectName() + " for UserID: " + teacher.getUserId());
                                        subjectText.append(subject.getSubjectName());
                                        count++;

                                        if (count == 2) break;
                                        subjectText.append(", ");
                                    }
                                }

                                if (count > 0) {
                                    subjects.setText(subjectText.toString());
                                } else {
                                    subjects.setText("Subjects: N/A");
                                }
                            } else {
                                subjects.setText("Subjects: N/A");
                            }
                        }
                    });

                    payment.setOnClickListener(v -> MoveToPaymentGateway());
                }
            } else {
                Log.d("SearchStudentsDashboard", "No teachers found.");
            }
        });

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


    private void MoveToPaymentGateway() {
        Intent intent = new Intent(this, PaymentGatewayDemo.class);
        startActivity(intent);
    }
}
