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

        if (userId == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            List<Notification> notifications = DatabaseHelper.getNotificationsForUser(userId);
            runOnUiThread(() -> {
                if (notifications != null && !notifications.isEmpty()) {
                    NotificationAdapter adapter = new NotificationAdapter(notifications);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "No notifications found.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void displayNotifications(List<Notification> notifications) {
        notificationsLayout.removeAllViews(); //clear the previous views.
        for (Notification notification : notifications) {
            Log.d("NotificationTeachersMessage", "Displaying notification: " + notification.getTitle()); //Log each notification.
            TextView titleTextView = new TextView(this);
            titleTextView.setText(notification.getTitle());
            titleTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            notificationsLayout.addView(titleTextView);

            TextView messageTextView = new TextView(this);
            messageTextView.setText(notification.getMessage());
            messageTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            notificationsLayout.addView(messageTextView);
        }
        Log.d("NotificationTeachersMessage", "Notifications displayed.");//log when finished.
    }
}
