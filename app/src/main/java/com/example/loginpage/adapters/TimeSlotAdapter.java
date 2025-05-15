package com.example.loginpage.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.R;
import com.example.loginpage.models.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    private List<TimeSlot> timeSlots;
    private Context context;
    private List<TimeSlot> optedInTimeSlots = new ArrayList<>(); // To keep track of opted-in slots
    private OnOptInOutListener onOptInOutListener;

    public TimeSlotAdapter(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    // Method to get the list of opted-in time slots
    public List<TimeSlot> getOptedInTimeSlots() {
        return optedInTimeSlots;
    }

    public void setOnOptInOutListener(OnOptInOutListener listener) {
        this.onOptInOutListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimeSlot slot = timeSlots.get(position);
        holder.subjectText.setText("Subject: " + slot.getSubject());
        holder.gradeText.setText("Grade: " + slot.getGrade());
        holder.dayText.setText("Day: " + slot.getDay());
        holder.timeText.setText("Time: " + slot.getTime());
        holder.courseFeeText.setText("Course Fee: " + slot.getCourseFee());
        holder.noOfStudentsText.setText("Batch Capacity: " + slot.getBatchCapacity());

        // Corrected Duration Display
        String duration = slot.getDuration(); // Get the String duration
        holder.durationTypeText.setText("Duration: " + duration);

        Drawable checkedDrawable = ContextCompat.getDrawable(context, R.drawable.check_box_24dp_ffffff_fill0_wght400_grad0_opsz24);
        Drawable uncheckedDrawable = ContextCompat.getDrawable(context, R.drawable.check_box_outline_blank_24dp_ffffff_fill0_wght400_grad0_opsz24);

        // Set initial state based on whether it was previously opted in (if you persist this data)
        boolean isOptedIn = optedInTimeSlots.contains(slot);
        if (isOptedIn) {
            holder.ivOptTimeTable.setImageDrawable(checkedDrawable);
            holder.tvOptTimeTable.setText("Chosen");
            holder.tvOptTimeTable.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.ivOptTimeTable.setImageDrawable(uncheckedDrawable);
            holder.tvOptTimeTable.setText("Choose Time");
            holder.tvOptTimeTable.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        }

        holder.linearLayoutOptTimeTable.setOnClickListener(v -> {
            Drawable currentDrawable = holder.ivOptTimeTable.getDrawable();
            boolean currentlyChecked = (currentDrawable != null && currentDrawable.getConstantState().equals(checkedDrawable.getConstantState()));

            if (currentlyChecked) {
                // User wants to opt out
                holder.ivOptTimeTable.setImageDrawable(uncheckedDrawable);
                holder.tvOptTimeTable.setText("Choose Time");
                holder.tvOptTimeTable.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                optedInTimeSlots.remove(slot); // Remove from the opted-in list
            } else {
                // User wants to opt in
                holder.ivOptTimeTable.setImageDrawable(checkedDrawable);
                holder.tvOptTimeTable.setText("Chosen");
                holder.tvOptTimeTable.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                optedInTimeSlots.add(slot); // Add to the opted-in list
            }

            // You might want to notify your activity about the change here
            if (onOptInOutListener != null) {
                onOptInOutListener.onOptInOut(slot, !currentlyChecked); // Send the new opt-in status
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectText, gradeText, dayText, timeText, courseFeeText, noOfStudentsText, durationTypeText;
        AppCompatButton tvOptTimeTable;
        ImageView ivOptTimeTable;
        LinearLayout linearLayoutOptTimeTable;

        public ViewHolder(View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.tvSubject);
            gradeText = itemView.findViewById(R.id.tvGrade);
            dayText = itemView.findViewById(R.id.tvDay);
            timeText = itemView.findViewById(R.id.tvTime);
            courseFeeText = itemView.findViewById(R.id.tvCourseFee);
            noOfStudentsText = itemView.findViewById(R.id.tvBatchCapacity);
            durationTypeText = itemView.findViewById(R.id.tvDuration);
            tvOptTimeTable = itemView.findViewById(R.id.tvOptTimeTable);
            ivOptTimeTable = itemView.findViewById(R.id.imageView);
            linearLayoutOptTimeTable = itemView.findViewById(R.id.linearLayoutOptTimeTable);
        }
    }

    // Interface to communicate opt-in/opt-out events to the activity
    public interface OnOptInOutListener {
        void onOptInOut(TimeSlot timeSlot, boolean isOptedIn);
    }
}