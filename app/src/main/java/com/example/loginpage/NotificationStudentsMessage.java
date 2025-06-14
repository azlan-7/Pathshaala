package com.example.loginpage;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.loginpage.models.UserDetailsClass;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationStudentsMessage extends AppCompatActivity implements DatabaseHelper.UserResultListener {

    private AppCompatButton sendButton;
    private EditText messageNotification;
    private EditText messageTitle;
    private UserDetailsClass user;
    private String receiverPhoneNumber;
    private int receiverUserId;
    private String receiverFirstName;
    private AutoCompleteTextView subjectDropdown;
    private AutoCompleteTextView gradeDropdown;
    Map<String, String> gradeMap = new HashMap<>();
    Map<String, String> subjectMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_students_message);

        sendButton = findViewById(R.id.button39);
        messageNotification = findViewById(R.id.editTextText30);
        messageTitle = findViewById(R.id.editTextText31);
        subjectDropdown = findViewById(R.id.autoCompleteSubject);
        gradeDropdown = findViewById(R.id.autoCompleteGrade);


        Intent intent = getIntent();
        receiverPhoneNumber = intent.getStringExtra("USER_PHONE");
        receiverUserId = intent.getIntExtra("USER_ID", -1);
        receiverFirstName = intent.getStringExtra("USER_FIRST_NAME");

        Log.d("NotificationStudentsMessage","Teachers Id received through Intent: " + receiverUserId);

        if (receiverPhoneNumber == null || receiverUserId == -1) {
            Toast.makeText(this, "Recipient details not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

//
//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        String phoneNumber = sharedPreferences.getString("phoneNumber", "");

//        if (phoneNumber == null) {
//            Toast.makeText(this, "Phone number not found.", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }

        DatabaseHelper.UserDetailsSelect(this, "4", receiverPhoneNumber, this);

        sendButton.setOnClickListener(v -> sendMessage());

        gradeDropdown.setOnClickListener(v -> gradeDropdown.showDropDown());
        subjectDropdown.setOnClickListener(v -> subjectDropdown.showDropDown());

        String selectedSubject = subjectDropdown.getText().toString();
        String selectedGrade = gradeDropdown.getText().toString();

        loadGrades();
        loadSubjects();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onQueryResult(List<UserDetailsClass> userList) {
        if (!userList.isEmpty()) {
            user = userList.get(0);
            Log.d("NotificationStudentsMessage", "User Type: " + user.getUserType());
        } else {
            Toast.makeText(this, "User details not found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void sendMessage() {
        String message = messageNotification.getText().toString().trim();
        String title = messageTitle.getText().toString().trim();
        String userType = "info";

        if (message.isEmpty() || title.isEmpty()) {
            Toast.makeText(this, "Please enter a title and message.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int senderId = sharedPreferences.getInt("USER_ID", -1);

        Log.d("NotificationStudentsMessage", "Fetched UserId for Student " + senderId);

        Log.d("NotificationStudentsMessage","Received UserId for Teacher:  " + receiverUserId);

        if (senderId == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (receiverUserId == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }


        int notificationId = DatabaseHelper.insertNotification(senderId, title, message, userType);

        if (notificationId != -1) {
            DatabaseHelper.insertNotificationRead(notificationId, receiverUserId); // Insert into Notification_Reads for user
            Toast.makeText(this, "Message sent successfully. Notification ID: " + notificationId + " for Teacher's UserId: " + receiverUserId, Toast.LENGTH_SHORT).show();
            messageNotification.setText("");
            messageTitle.setText("");
            Log.d("NotificationStudentsMessage", "Notification inserted with ID: " + notificationId + " for UserId: " + receiverUserId);
            Intent intent = new Intent(this, SearchStudentsDashboard.class);
            intent.putExtra("key", "value"); // Example of passing data
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Failed to send message.", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadGrades() {
        String query = "SELECT GradeID, GradeName FROM Grades WHERE active = 'true' ORDER BY GradeName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
            if (result == null || result.isEmpty()) {
                Log.e(TAG, "No grades found!");
                Toast.makeText(this, "No Grades Found!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> grades = new ArrayList<>();
            gradeMap.clear();

            for (Map<String, String> row : result) {
                grades.add(row.get("GradeName"));
                gradeMap.put(row.get("GradeName"), row.get("GradeID"));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, grades);
            gradeDropdown.setAdapter(adapter); // Use gradeDropdown here
        });
    }

    private void loadSubjects() {
        String query = "SELECT SubjectID, SubjectName FROM Subject WHERE active = 'true' ORDER BY SubjectName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
            if (result == null || result.isEmpty()) {
                Log.e(TAG, "No subjects found in the database.");
                Toast.makeText(this, "No Subjects Found!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> subjects = new ArrayList<>();
            subjectMap.clear();

            for (Map<String, String> row : result) {
                String id = row.get("SubjectID");
                String name = row.get("SubjectName");
                Log.d(TAG, "Subject Retrieved - ID: " + id + ", Name: " + name);
                subjects.add(name);
                subjectMap.put(name, id);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects);
            subjectDropdown.setAdapter(adapter);
        });
    }
}