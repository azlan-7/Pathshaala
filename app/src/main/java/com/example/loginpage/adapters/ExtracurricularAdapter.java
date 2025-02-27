package com.example.loginpage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginpage.R;
import com.example.loginpage.models.ExtracurricularModel;
import java.util.List;

public class ExtracurricularAdapter extends RecyclerView.Adapter<ExtracurricularAdapter.ViewHolder> {

    private List<ExtracurricularModel> extracurricularList;
    private OnDeleteClickListener deleteClickListener;

    public ExtracurricularAdapter(List<ExtracurricularModel> extracurricularList, OnDeleteClickListener deleteClickListener) {
        this.extracurricularList = extracurricularList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_extracurricular, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExtracurricularModel extracurricular = extracurricularList.get(position);

        // Set hobby and extracurricular text
        holder.tvHobby.setText(extracurricular.getHobby());
        holder.tvExtracurricular.setText(extracurricular.getActivityName());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return extracurricularList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHobby, tvExtracurricular;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHobby = itemView.findViewById(R.id.tvHobby);
            tvExtracurricular = itemView.findViewById(R.id.tvExtracurricular);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
}
