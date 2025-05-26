package com.example.loginpage;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.NotificationWithSenderAdapter; // Import the new adapter
import com.example.loginpage.models.NotificationWithSender; // Import the correct model

import java.util.List;

public class NotificationStudents extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_students);

        RecyclerView recyclerView = findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            Log.d("NotificationStudent", "Fetching notifications for user ID: " + userId + " with sender info");
            List<NotificationWithSender> notifications = DatabaseHelper.getNotificationsWithSender(userId); // Use the method that fetches sender info
            Log.d("NotificationStudent", "Number of notifications fetched: " + (notifications != null ? notifications.size() : 0));
            if (notifications != null) {
                for (NotificationWithSender notif : notifications) {
                    Log.d("NotificationStudent", "Fetched Notification - ID: " + notif.getId() + ", Title: " + notif.getTitle() + ", Type: " + notif.getType() + ", Sender: " + notif.getSenderName());
                }
            }
            runOnUiThread(() -> {
                if (notifications != null && !notifications.isEmpty()) {
                    NotificationWithSenderAdapter adapter = new NotificationWithSenderAdapter(notifications); // Use the adapter for notifications with sender
                    recyclerView.setAdapter(adapter);
                    Log.d("NotificationStudent", "Adapter set with " + notifications.size() + " notifications (with sender info).");
                } else {
                    Toast.makeText(this, "No notifications found.", Toast.LENGTH_SHORT).show();
                    Log.d("NotificationStudent", "No notifications found for user ID: " + userId);
                }
            });
        }).start();
    }
}