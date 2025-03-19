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
import com.example.loginpage.models.GradesTaughtModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GradesTaughtAdapter extends RecyclerView.Adapter<GradesTaughtAdapter.ViewHolder> {

    private final Context context;
    private final List<GradesTaughtModel> gradesList;
    private final SharedPreferences sharedPreferences;
    private final OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }


    public GradesTaughtAdapter(Context context, List<GradesTaughtModel> gradesList, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.gradesList = gradesList;
        this.sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grades_taught, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GradesTaughtModel grade = gradesList.get(position);

        holder.tvSubject.setText(grade.getSubject());
//        holder.tvTopic.setText(grade.getTopic());
        holder.tvGradeLevel.setText(grade.getGradeLevel());

        holder.btnDelete.setOnClickListener(v -> deleteClickListener.onDeleteClick(position));

    }

    @Override
    public int getItemCount() {
        return gradesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject, tvTopic, tvGradeLevel;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvGradeLevel = itemView.findViewById(R.id.tvGradeLevel);
            btnDelete = itemView.findViewById(R.id.btnDeleteGrade);
        }
    }

    private void deleteGrade(int position) {
        if (position >= 0 && position < gradesList.size()) {
            gradesList.remove(position);
            notifyItemRemoved(position);
            saveUpdatedGrades();  // Save the updated list in SharedPreferences
            Toast.makeText(context, "Grade Deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUpdatedGrades() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString("GRADES_TAUGHT_LIST", gson.toJson(gradesList));
        editor.apply();
    }

    public void updateData(List<GradesTaughtModel> newData) {
        gradesList.clear();
        gradesList.addAll(newData);
        notifyDataSetChanged();
    }
}
