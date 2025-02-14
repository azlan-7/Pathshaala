package com.example.loginpage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AwardAdapter extends RecyclerView.Adapter<AwardAdapter.AwardViewHolder> {

    private List<Award> awardList;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public AwardAdapter(List<Award> awardList, OnDeleteClickListener onDeleteClickListener) {
        this.awardList = awardList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public AwardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_award, parent, false);
        return new AwardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AwardViewHolder holder, int position) {
        Award award = awardList.get(position);
        holder.awardName.setText(award.getAwardName());
        holder.organization.setText(award.getOrganization() + ", " + award.getYear());
        holder.description.setText(award.getDescription());

        holder.deleteIcon.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return awardList.size();
    }

    public static class AwardViewHolder extends RecyclerView.ViewHolder {
        TextView awardName, organization, description;
        ImageView deleteIcon;

        public AwardViewHolder(@NonNull View itemView) {
            super(itemView);
            awardName = itemView.findViewById(R.id.awardName);
            organization = itemView.findViewById(R.id.organization);
            description = itemView.findViewById(R.id.description);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}
