package com.example.loginpage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.R;
import com.example.loginpage.models.TimeSlot;

import java.util.List;

public class TimeSlotAdapterTeacher extends RecyclerView.Adapter<TimeSlotAdapterTeacher.ViewHolder> {

    private List<TimeSlot> timeSlots;
    private OnDeleteClickListener onDeleteClickListener;

    public TimeSlotAdapterTeacher(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot_teacher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimeSlot slot = timeSlots.get(position);
        holder.subjectTextTeacher.setText("Subject: " + slot.getSubject());
        holder.gradeTextTeacher.setText("Grade: " + slot.getGrade());
        holder.dayTextTeacher.setText("Day: " + slot.getDay());
        holder.timeTextTeacher.setText("Time: " + slot.getTime());
        holder.courseFeeTextTeacher.setText("Fee: " + slot.getCourseFee());
        holder.batchCapacityTextTeacher.setText("Students: " + slot.getBatchCapacity());
        holder.durationTextTeacher.setText("Duration: " + slot.getDuration());

        holder.deleteIconTeacher.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTextTeacher, gradeTextTeacher, dayTextTeacher, timeTextTeacher, courseFeeTextTeacher, batchCapacityTextTeacher, durationTextTeacher;
        ImageView deleteIconTeacher;

        public ViewHolder(View itemView) {
            super(itemView);
            subjectTextTeacher = itemView.findViewById(R.id.tvSubjectTeacher);
            gradeTextTeacher = itemView.findViewById(R.id.tvGradeTeacher);
            dayTextTeacher = itemView.findViewById(R.id.tvDayTeacher);
            timeTextTeacher = itemView.findViewById(R.id.tvTimeTeacher);
            courseFeeTextTeacher = itemView.findViewById(R.id.tvCourseFeeTeacher);
            batchCapacityTextTeacher = itemView.findViewById(R.id.tvBatchCapacityTeacher);
            durationTextTeacher = itemView.findViewById(R.id.tvDurationTeacher);
            deleteIconTeacher = itemView.findViewById(R.id.imageView108);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
}