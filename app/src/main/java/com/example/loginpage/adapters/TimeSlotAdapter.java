package com.example.loginpage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.R;
import com.example.loginpage.models.TimeSlot;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    private List<TimeSlot> timeSlots;

    public TimeSlotAdapter(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectText, gradeText, dayText, timeText;

        public ViewHolder(View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.tvSubject);
            gradeText = itemView.findViewById(R.id.tvGrade);
            dayText = itemView.findViewById(R.id.tvDay);
            timeText = itemView.findViewById(R.id.tvTime);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TimeSlot slot = timeSlots.get(position);
        holder.subjectText.setText("Subject: " + slot.getSubject());
        holder.gradeText.setText("Grade: " + slot.getGrade());
        holder.dayText.setText("Day: " + slot.getDay());
        holder.timeText.setText("Time: " + slot.getTime());
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }
}

