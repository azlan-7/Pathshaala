package com.example.loginpage.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginpage.R;
import com.example.loginpage.models.Education;
import com.google.gson.Gson;

import java.util.List;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.EducationViewHolder> {

    private final List<Education> educationList;
    private final Context context;
    private SharedPreferences sharedPreferences;

    public EducationAdapter(Context context,List<Education> educationList) {
        this.educationList = educationList;
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public EducationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_education, parent, false);
        return new EducationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EducationViewHolder holder, int position) {
        Education education = educationList.get(position);
        holder.tvInstitution.setText(education.getInstitution());
        holder.tvDegree.setText(education.getDegree());
        holder.tvYear.setText(education.getYear());


        holder.btnDelete.setOnClickListener(v -> {
            educationList.remove(position);  // Remove item from list
            notifyItemRemoved(position);    // Notify RecyclerView
            notifyItemRangeChanged(position, educationList.size());

            saveUpdatedList();  // Save updated list
            Toast.makeText(context, "Education entry deleted", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return educationList.size();
    }

    private void saveUpdatedList() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(educationList);
        editor.putString("EDUCATION_LIST", json);
        editor.apply();
    }

    public void updateData(List<Education> newEducationList) {
        this.educationList.clear();
        this.educationList.addAll(newEducationList);
        notifyDataSetChanged();
    }

    static class EducationViewHolder extends RecyclerView.ViewHolder {
        TextView tvInstitution, tvDegree, tvYear;
        ImageView btnDelete;

        public EducationViewHolder(View itemView) {
            super(itemView);
            tvInstitution = itemView.findViewById(R.id.tvInstitution);
            tvDegree = itemView.findViewById(R.id.tvDegree);
            tvYear = itemView.findViewById(R.id.tvYear);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
