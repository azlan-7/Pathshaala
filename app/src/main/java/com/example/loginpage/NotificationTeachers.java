package com.example.loginpage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import java.util.List;

public class NotificationTeachers extends AppCompatActivity implements DatabaseHelper.UserResultListener {

    private AppCompatButton sendButton;
    private EditText messageNotification;
    private EditText messageTitle;
    private UserDetailsClass user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_teachers);

        sendButton = findViewById(R.id.button39);
        messageNotification = findViewById(R.id.editTextText30);
        messageTitle = findViewById(R.id.editTextText31);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber", "");

        if (phoneNumber == null) {
            Toast.makeText(this, "Phone number not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseHelper.UserDetailsSelect(this, "4", phoneNumber, this);

        sendButton.setOnClickListener(v -> sendMessage());

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
            Log.d("NotificationTeachers", "User Type: " + user.getUserType());
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

        Log.d("NotificationTeachers", "Fetched UserId: " + senderId);
        Log.d("NotificationTeachers", "Fetched UserType: " + userType);

        if (senderId == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("NotificationTeachers", "Attempting to insert notification with type: " + userType);

        int notificationId = DatabaseHelper.insertNotification(senderId, title, message, userType);

        if (notificationId != -1) {
            // Hardcode userId = 10 for testing
            DatabaseHelper.insertNotificationRead(notificationId, 10); // Insert into Notification_Reads for user 10
            Toast.makeText(this, "Message sent successfully. Notification ID: " + notificationId, Toast.LENGTH_SHORT).show();
            messageNotification.setText("");
            messageTitle.setText("");
            Log.d("NotificationTeachers", "Notification inserted with ID: " + notificationId + " for User 10");
        } else {
            Toast.makeText(this, "Failed to send message.", Toast.LENGTH_SHORT).show();
        }
    }
}