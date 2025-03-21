package com.example.loginpage.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginpage.R;
import com.example.loginpage.models.WorkExperienceModel;

import java.util.ArrayList;
import java.util.List;

public class WorkExperienceAdapter extends RecyclerView.Adapter<WorkExperienceAdapter.WorkExperienceViewHolder> {


    private final Context context;
    private final List<WorkExperienceModel> workExperienceList;
    private OnDeleteClickListener onDeleteClickListener;


    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public WorkExperienceAdapter(Context context, List<WorkExperienceModel> workExperienceList) {
        this(context, workExperienceList, null); // Calls the main constructor
    }
    public WorkExperienceAdapter(Context context, List<WorkExperienceModel> workExperienceList, OnDeleteClickListener listener) {
        this.context = context;
        this.workExperienceList = (workExperienceList != null) ? workExperienceList : new ArrayList<>();
        this.onDeleteClickListener = listener;
    }


    @NonNull
    @Override
    public WorkExperienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("WorkExperienceAdapter", "testing adapter 123 ");
        View view = LayoutInflater.from(context).inflate(R.layout.item_work_experience, parent, false);
        return new WorkExperienceViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull WorkExperienceViewHolder holder, int position) {
        WorkExperienceModel experience = workExperienceList.get(position);

        if (experience != null) {
            Log.d("WorkExperienceAdapter", "Binding data -> Position: " + position +
                    " | Profession: " + experience.getProfessionName() +
                    " | Institution: " + experience.getInstitutionName() +
                    " | Designation: " + experience.getDesignationName() +
                    " | Experience: " + experience.getWorkExperience());

            holder.tvProfession.setText(experience.getProfessionName() != null ? experience.getProfessionName() : "N/A");
            holder.tvInstitution.setText(experience.getInstitutionName() != null ? experience.getInstitutionName() : "N/A");
            holder.tvDesignation.setText(experience.getDesignationName() != null ? experience.getDesignationName() : "N/A");
            holder.tvExperience.setText(experience.getWorkExperience() != null ? "Experience: " + experience.getWorkExperience() : "Experience: N/A");
        } else {
            Log.e("WorkExperienceAdapter", "Null data at position: " + position);
        }
    }






    public void updateData(List<WorkExperienceModel> newData) {
        workExperienceList.clear();
        workExperienceList.addAll(newData);

        Log.d("WorkExperienceAdapter", "Updating RecyclerView. Total items: " + workExperienceList.size());

        if (workExperienceList.isEmpty()) {
            Log.e("WorkExperienceAdapter", "❌ Data list is still empty after update!");
        } else {
            for (int i = 0; i < workExperienceList.size(); i++) {
                WorkExperienceModel item = workExperienceList.get(i);
                Log.d("WorkExperienceAdapter", "Item " + i + " -> Profession: " + item.getProfessionName() +
                        ", Institution: " + item.getInstitutionName() +
                        ", Designation: " + item.getDesignationName() +
                        ", Experience: " + item.getWorkExperience());
            }
        }

        notifyDataSetChanged(); // Ensure UI refresh
    }








    @Override
    public int getItemCount() {
        return workExperienceList.size();
    }

    public static class WorkExperienceViewHolder extends RecyclerView.ViewHolder {
        TextView tvProfession, tvInstitution, tvDesignation, tvExperience;
        ImageView btnDelete;
        public WorkExperienceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProfession = itemView.findViewById(R.id.tvProfession);
            tvInstitution = itemView.findViewById(R.id.tvInstitution);
            tvDesignation = itemView.findViewById(R.id.tvDesignation);
            tvExperience = itemView.findViewById(R.id.tvExperience);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            // Debugging log to check if views are properly initialized
            if (tvProfession == null) Log.e("Adapter", "tvProfession is NULL!");
            if (tvInstitution == null) Log.e("Adapter", "tvInstitution is NULL!");
            if (tvDesignation == null) Log.e("Adapter", "tvDesignation is NULL!");
            if (tvExperience == null) Log.e("Adapter", "tvExperience is NULL!");
        }
    }
}
