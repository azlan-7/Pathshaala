package com.example.loginpage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.R;
import com.example.loginpage.models.AcademicDetailsModel;

import java.util.List;

public class AcademicDetailsAdapter extends RecyclerView.Adapter<AcademicDetailsAdapter.ViewHolder> {

    private final Context context;
    private final List<AcademicDetailsModel> academicDetailsList;

    public AcademicDetailsAdapter(Context context, List<AcademicDetailsModel> academicDetailsList) {
        this.context = context;
        this.academicDetailsList = academicDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_academic_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AcademicDetailsModel details = academicDetailsList.get(position);

        holder.gradeEnrolled.setText(details.getGradeEnrolled());
        holder.yearOfPassing.setText(details.getYearOfPassing());
        holder.learningStream.setText(details.getLearningStream());
        holder.selectBoard.setText(details.getSelectBoard());
    }

    @Override
    public int getItemCount() {
        return academicDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gradeEnrolled, yearOfPassing, learningStream, selectBoard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gradeEnrolled = itemView.findViewById(R.id.textGradeEnrolled);
            yearOfPassing = itemView.findViewById(R.id.textYearOfPassing);
            learningStream = itemView.findViewById(R.id.textLearningStream);
            selectBoard = itemView.findViewById(R.id.textSelectBoard);
        }
    }
}
