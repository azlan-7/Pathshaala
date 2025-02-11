package com.example.loginpage.adapters;

import com.example.loginpage.R;

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

public class SubjectAdapter  extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>{
    private final List<String> subjectList;
    private final Set<String> selectedSubjects;
    private final Context context;
    private final String category;

    public SubjectAdapter(Context context, List<String> subjectList, Set<String> selectedSubjects, String category) {
        this.context = context;
        this.category = category;
        this.subjectList = subjectList;
        this.selectedSubjects = new HashSet<>(selectedSubjects); // Copy existing selection
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        String subject = subjectList.get(position);
        holder.checkBox.setText(subject);
        holder.checkBox.setOnCheckedChangeListener(null); // Prevents unwanted triggering
        holder.checkBox.setChecked(selectedSubjects.contains(subject));

        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.itemView.setLayoutParams(layoutParams);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedSubjects.add(subject);
            } else {
                selectedSubjects.remove(subject);
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public Set<String> getSelectedSubjects() {
        return selectedSubjects;
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        SubjectViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.subjectCheckBox);
        }
    }

}
