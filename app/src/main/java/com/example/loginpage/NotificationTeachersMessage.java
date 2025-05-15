package com.example.loginpage;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.adapters.NotificationAdapter;
import com.example.loginpage.models.Notification;

import java.util.List;

public class NotificationTeachersMessage extends AppCompatActivity {

    private LinearLayout notificationsLayout;

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
            Log.d("NotificationTeacher", "Fetching notifications for user ID: " + userId);
            List<Notification> notifications = DatabaseHelper.getNotificationsForUser(userId);
            Log.d("NotificationTeacher", "Number of notifications fetched: " + (notifications != null ? notifications.size() : 0));
            if (notifications != null) {
                for (Notification notif : notifications) {
                    Log.d("NotificationTeacher", "Fetched Notification - ID: " + notif.getId() + ", Title: " + notif.getTitle() + ", Type: " + notif.getType());
                }
            }
            runOnUiThread(() -> {
                if (notifications != null && !notifications.isEmpty()) {
                    NotificationAdapter adapter = new NotificationAdapter(notifications);
                    recyclerView.setAdapter(adapter);
                    Log.d("NotificationTeacher", "Adapter set with " + notifications.size() + " notifications.");
                } else {
                    Toast.makeText(this, "No notifications found.", Toast.LENGTH_SHORT).show();
                    Log.d("NotificationTeacher", "No notifications found for user ID: " + userId);
                }
            });
        }).start();
    }
}