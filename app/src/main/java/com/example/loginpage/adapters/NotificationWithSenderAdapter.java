package com.example.loginpage.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.R;
import com.example.loginpage.models.NotificationWithSender;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationWithSenderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NotificationWithSender> notifications;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private static final int TYPE_MESSAGE = 0;
    private static final int TYPE_TIMETABLE_OPT_IN = 1;

    public NotificationWithSenderAdapter(List<NotificationWithSender> notifications) {
        this.notifications = notifications;
    }

    @Override
    public int getItemViewType(int position) {
        NotificationWithSender notification = notifications.get(position);
        if (notification.getTitle() != null && notification.getTitle().contains("Timetable Opt-In")) {
            return TYPE_TIMETABLE_OPT_IN;
        } else {
            return TYPE_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_TIMETABLE_OPT_IN) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification_time_table, parent, false);
            return new TimetableOptInViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new MessageViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotificationWithSender notification = notifications.get(position);
        if (holder instanceof TimetableOptInViewHolder) {
            TimetableOptInViewHolder viewHolder = (TimetableOptInViewHolder) holder;
            viewHolder.titleTextView.setText("Timetable Opt-In");
            viewHolder.messageTextView.setText(notification.getSenderName() + " opted for: "); // Display sender name here
            viewHolder.subjectTextView.setText("Subject: " + getSubjectFromMessage(notification.getMessage()));
            viewHolder.daysTextView.setText("Days: " + getDaysFromMessage(notification.getMessage()));
            viewHolder.timeTextView.setText("Time: " + getTimeFromMessage(notification.getMessage()));
            viewHolder.gradeTextView.setText("Grade: " + getGradeFromMessage(notification.getMessage()));
            Log.d("NotificationWSA", "Timetable Opt-In - Sender: " + notification.getSenderName());
        } else if (holder instanceof MessageViewHolder) {
            MessageViewHolder viewHolder = (MessageViewHolder) holder;
            viewHolder.titleTextView.setText(notification.getTitle());
            viewHolder.messageTextView.setText((notification.getMessage() + " From: " + notification.getSenderName())); // Display sender name
            Log.d("NotificationWSA", "Message - Sender: " + notification.getSenderName());
        }
    }

    // Helper methods (same as before)
    private String getSubjectFromMessage(String message) {
        try {
            return message.substring(message.indexOf("Subject: ") + 9, message.indexOf(", Day(s):"));
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String getDaysFromMessage(String message) {
        try {
            return message.substring(message.indexOf("Day(s): ") + 7, message.indexOf(", Time:"));
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String getTimeFromMessage(String message) {
        try {
            return message.substring(message.indexOf("Time: ") + 6, message.indexOf(", Grade:"));
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String getGradeFromMessage(String message) {
        try {
            return message.substring(message.indexOf("Grade: ") + 7);
        } catch (Exception e) {
            return "N/A";
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvTitle);
            messageTextView = itemView.findViewById(R.id.tvMessage);
        }
    }

    public static class TimetableOptInViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView messageTextView;
        public TextView subjectTextView;
        public TextView daysTextView;
        public TextView timeTextView;
        public TextView gradeTextView;

        public TimetableOptInViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notificationTitle);
            messageTextView = itemView.findViewById(R.id.notificationMessage);
            subjectTextView = itemView.findViewById(R.id.notificationSubject);
            daysTextView = itemView.findViewById(R.id.notificationDays);
            timeTextView = itemView.findViewById(R.id.notificationTime);
            gradeTextView = itemView.findViewById(R.id.notificationGrade);
        }
    }
}