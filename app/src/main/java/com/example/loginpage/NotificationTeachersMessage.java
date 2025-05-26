package com.example.loginpage;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.NotificationWithSenderAdapter;
import com.example.loginpage.models.NotificationWithSender;

import java.util.List;

public class NotificationTeachersMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_teachers_message);

        RecyclerView recyclerView = findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);
        Log.d("NotificationTeacher", "Teacher User ID from SharedPreferences: " + userId);

        if (userId == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            Log.d("NotificationTeacher", "Fetching notifications for user ID: " + userId + " with sender info");
            List<NotificationWithSender> notifications = DatabaseHelper.getNotificationsWithSender(userId);
            Log.d("NotificationTeacher", "Number of notifications fetched: " + (notifications != null ? notifications.size() : 0));
            if (notifications != null) {
                for (NotificationWithSender notif : notifications) {
                    Log.d("NotificationTeacher", "Fetched Notification - ID: " + notif.getId() + ", Title: " + notif.getTitle() + ", Type: " + notif.getType() + ", Sender: " + notif.getSenderName());
                }
                // **MARK AS READ FOR TEACHER (HERE)**
                for (NotificationWithSender notification : notifications) {
                    DatabaseHelper.markNotificationRead(notification.getId(), userId); // Assuming markNotificationRead works for teachers too
                    Log.d("NotificationTeacher", "Marked notification as read - ID: " + notification.getId());
                }
            }
            runOnUiThread(() -> {
                if (notifications != null && !notifications.isEmpty()) {
                    NotificationWithSenderAdapter adapter = new NotificationWithSenderAdapter(notifications);
                    recyclerView.setAdapter(adapter);
                    Log.d("NotificationTeacher", "Adapter set with " + notifications.size() + " notifications (with sender info).");
                } else {
                    Toast.makeText(this, "No notifications found.", Toast.LENGTH_SHORT).show();
                    Log.d("NotificationTeacher", "No notifications found for user ID: " + userId);
                }
            });
        }).start();
    }
}