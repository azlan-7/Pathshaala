package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

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
import com.example.loginpage.models.UserDetailsClass;
import com.example.loginpage.models.UserWiseEducation;

import java.util.List;
import java.util.Map;

public class SearchTeachersDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_teachers_dashboard);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int[] cardIds = {R.id.cardStudent1, R.id.cardStudent2, R.id.cardStudent3, R.id.cardStudent4, R.id.cardStudent5};

        DatabaseHelper.UserDetailsSelect(this, "3", "", userList -> {
            if (userList.size() > 0) {
                int studentIndex = 0; // Track number of students displayed

                for (UserDetailsClass student : userList) {
                    if (!"S".equals(student.getUserType())) continue; // ✅ Show only students

                    if (studentIndex >= cardIds.length) break; // ✅ Prevent array overflow

                    View cardView = findViewById(cardIds[studentIndex]);

                    TextView name = cardView.findViewById(R.id.tvStudentName);
                    TextView grade = cardView.findViewById(R.id.tvGrade);
                    TextView subjects = cardView.findViewById(R.id.tvSubjects);
                    TextView email = cardView.findViewById(R.id.tvEmail);
                    TextView referral = cardView.findViewById(R.id.tvSelfReferral);
                    ImageView profileIcon = cardView.findViewById(R.id.profileIcon);
                    Button whatsAppButton = cardView.findViewById(R.id.whatsappButton);

                    name.setText(student.getName());
                    email.setText(student.getEmailId());
                    referral.setText(student.getSelfReferralCode());

                    // ✅ Fetch Grade & Institution from UserWiseEducation
                    DatabaseHelper.UserWiseEducationSelect(this, "3", student.getUserId(), new DatabaseHelper.UserWiseEducationResultListener() {
                        @Override
                        public void onQueryResult(List<UserWiseEducation> educationList) {
                            if (educationList != null && !educationList.isEmpty()) {
                                Log.d("Education Fetch", "✅ Total Records Retrieved: " + educationList.size());

                                // Check if the correct user data is being retrieved
                                for (UserWiseEducation education : educationList) {
                                    Log.d("Education Fetch", "🔍 UserID: " + student.getUserId() +
                                            " | Education Level: " + education.getEducationLevelName() +
                                            " | Institution: " + education.getInstitutionName());
                                }

                                // Find the record that matches the student ID
                                UserWiseEducation matchingEducation = null;
                                for (UserWiseEducation edu : educationList) {
                                    if (edu.getUserId().equals(student.getUserId())) {
                                        matchingEducation = edu;
                                        break;
                                    }
                                }

                                if (matchingEducation != null) {
                                    grade.setText("Grade: " + matchingEducation.getEducationLevelName());
                                    subjects.setText("Learning: " + matchingEducation.getInstitutionName());
                                    Log.d("Education Fetch", "✅ Displayed: " + matchingEducation.getEducationLevelName() +
                                            " | " + matchingEducation.getInstitutionName());
                                } else {
                                    grade.setText("Grade: N/A");
                                    subjects.setText("Learning: N/A");
                                    Log.e("Education Fetch", "❌ No matching record found for UserID: " + student.getUserId());
                                }
                            } else {
                                grade.setText("Grade: N/A");
                                subjects.setText("Learning: N/A");
                                Log.e("Education Fetch", "❌ No education data found for UserID: " + student.getUserId());
                            }
                        }
                    });



                    // ✅ WhatsApp Button Click
                    whatsAppButton.setOnClickListener(v -> MoveToWhatsAppScreen());

                    studentIndex++; // ✅ Move to the next student card
                }
            }
        });



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
