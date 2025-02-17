package com.example.loginpage.adapters;

import com.example.loginpage.R;
import com.example.loginpage.SubjectExpertiseNewOne;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubjectAdapterOne extends RecyclerView.Adapter<SubjectAdapterOne.SubjectViewHolder> {

    private final Context context;
    private final String[] subjects;
    private Set<String> selectedSubjects; // Now mutable for updates

    public SubjectAdapterOne(Context context, String[] subjects, Set<String> selectedSubjects) {
        this.context = context;
        this.subjects = subjects;
        this.selectedSubjects = selectedSubjects;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        String subject = subjects[position];
        holder.checkBox.setText(subject);
        holder.checkBox.setOnCheckedChangeListener(null); // Prevent unwanted triggers
        holder.checkBox.setChecked(selectedSubjects.contains(subject));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedSubjects.add(subject);
            } else {
                selectedSubjects.remove(subject);
            }

            // Update SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            sharedPreferences.edit().putStringSet("SELECTED_SUBJECTS", selectedSubjects).apply();

            // Notify UI to update the chip list in the activity
            if (context instanceof SubjectExpertiseNewOne) {
                ((SubjectExpertiseNewOne) context).loadSelectedSubjects();
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjects.length;
    }
    public void updateSelection(Set<String> updatedSelectedSubjects) {
        this.selectedSubjects = new HashSet<>(updatedSelectedSubjects); // Clone the set to prevent modification issues
        notifyDataSetChanged(); // Refresh UI
    }


    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.subjectCheckBox);
        }
    }
}
