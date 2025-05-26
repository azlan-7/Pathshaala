package com.example.loginpage;

// In NotificationStudents.java
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.R;
import com.example.loginpage.adapters.NotificationWithSenderAdapter;
import com.example.loginpage.models.NotificationWithSender;

import java.util.List;

public class NotificationStudents extends AppCompatActivity {

    private List<NotificationWithSender> currentNotifications; // To hold the fetched notifications
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_students);

        RecyclerView recyclerView = findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            Log.d("NotificationStudent", "Fetching notifications for user ID: " + userId + " with sender info");
            currentNotifications = DatabaseHelper.getNotificationsWithSender(userId); // Fetch notifications
            Log.d("NotificationStudent", "Number of notifications fetched: " + (currentNotifications != null ? currentNotifications.size() : 0));
            if (currentNotifications != null) {
                for (NotificationWithSender notif : currentNotifications) {
                    Log.d("NotificationStudent", "Fetched Notification - ID: " + notif.getId() + ", Title: " + notif.getTitle() + ", Type: " + notif.getType() + ", Sender: " + notif.getSenderName());
                }
                // Mark all fetched notifications as read
                for (NotificationWithSender notification : currentNotifications) {
                    DatabaseHelper.markNotificationRead(notification.getId(), userId);
                    Log.d("NotificationStudent", "Marked notification as read - ID: " + notification.getId());
                }
            }
            runOnUiThread(() -> {
                if (currentNotifications != null && !currentNotifications.isEmpty()) {
                    NotificationWithSenderAdapter adapter = new NotificationWithSenderAdapter(currentNotifications);
                    recyclerView.setAdapter(adapter);
                    Log.d("NotificationStudent", "Adapter set with " + currentNotifications.size() + " notifications (with sender info).");
                } else {
                    Toast.makeText(this, "No notifications found.", Toast.LENGTH_SHORT).show();
                    Log.d("NotificationStudent", "No notifications found for user ID: " + userId);
                }
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Optionally, you might want to refresh the notification list here if new ones could have arrived.
        // If so, you'd need to call the fetching and marking logic again.
    }
}