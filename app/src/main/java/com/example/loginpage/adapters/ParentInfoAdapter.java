package com.example.loginpage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.R;
import com.example.loginpage.models.ParentInfoModel;

import java.util.List;

public class ParentInfoAdapter extends RecyclerView.Adapter<ParentInfoAdapter.ViewHolder> {

    private List<ParentInfoModel> parentList;

    public ParentInfoAdapter(List<ParentInfoModel> list) {
        this.parentList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFatherName, tvFatherContact, tvMotherName, tvMotherContact,
                tvGuardianName, tvGuardianRelation, tvGuardianContact;

        public ViewHolder(View view) {
            super(view);
            tvFatherName = view.findViewById(R.id.tvFatherName);
            tvFatherContact = view.findViewById(R.id.tvFatherContact);
            tvMotherName = view.findViewById(R.id.tvMotherName);
            tvMotherContact = view.findViewById(R.id.tvMotherContact);
            tvGuardianName = view.findViewById(R.id.tvGuardianName);
            tvGuardianRelation = view.findViewById(R.id.tvGuardianRelation);
            tvGuardianContact = view.findViewById(R.id.tvGuardianContact);
        }
    }

    @Override
    public ParentInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_parent_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParentInfoModel info = parentList.get(position);
        holder.tvFatherName.setText("Father: " + info.fatherName);
        holder.tvFatherContact.setText("Contact: " + info.fatherContact);
        holder.tvMotherName.setText("Mother: " + info.motherName);
        holder.tvMotherContact.setText("Contact: " + info.motherContact);
        holder.tvGuardianName.setText("Guardian: " + info.guardianName);
        holder.tvGuardianRelation.setText("Relation: " + info.guardianRelation);
        holder.tvGuardianContact.setText("Contact: " + info.guardianContact);
    }

    @Override
    public int getItemCount() {
        return parentList.size();
    }
}
