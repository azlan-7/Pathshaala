package com.example.loginpage.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.R;
import com.example.loginpage.models.AwardModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AwardsAdapter extends RecyclerView.Adapter<AwardsAdapter.AwardsViewHolder> {

    private final Context context;
    private final List<AwardModel> awardList;

    private final SharedPreferences sharedPreferences;

    public AwardsAdapter(Context context, List<AwardModel> awardsList) {
        this.context = context;
        this.awardList = (awardsList != null) ? awardsList : new ArrayList<>();  // Fixed initialization
        this.sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public AwardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_award, parent, false);
        return new AwardsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AwardsViewHolder holder, int position) {
        AwardModel award = awardList.get(position);
        if (award == null) {
            Log.e("AwardsAdapter", "Null AwardModel at position: " + position);
            return;
        }


        holder.tvAwardTitle.setText(award.getTitle());
        holder.tvOrganisation.setText(award.getOrganisation());
        holder.tvYear.setText(award.getYear());
        holder.tvDescription.setText(award.getDescription());


        holder.btnDeleteAward.setOnClickListener(v -> {
            deleteAward(position);
        });
    }

    @Override
    public int getItemCount() {
        return awardList.size();
    }

    public static class AwardsViewHolder extends RecyclerView.ViewHolder {
        TextView tvAwardTitle, tvOrganisation, tvYear, tvDescription;
        ImageView btnDeleteAward;

        public AwardsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAwardTitle = itemView.findViewById(R.id.tvAwardTitle);
            tvOrganisation = itemView.findViewById(R.id.tvOrganisation);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnDeleteAward = itemView.findViewById(R.id.btnDeleteAward);
        }
    }

    private void deleteAward(int position) {
        awardList.remove(position);
        notifyItemRemoved(position);
        saveAwardData(); // Save updated list

        Toast.makeText(context, "Award deleted!", Toast.LENGTH_SHORT).show();
    }


    private void saveAwardData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String updatedJson = gson.toJson(awardList);
        editor.putString("AWARD_LIST", updatedJson);
        editor.apply();
    }
    public void updateData(List<AwardModel> newData) {
        awardList.clear();
        awardList.addAll(newData);
        notifyDataSetChanged();
    }
}
