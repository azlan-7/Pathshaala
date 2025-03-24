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
import com.example.loginpage.models.CertificationModel;

import java.util.ArrayList;
import java.util.List;

public class CertificationsAdapter extends RecyclerView.Adapter<CertificationsAdapter.ViewHolder> {

    private final Context context;
    private List<CertificationModel> certificationList;
    private final OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }


    public CertificationsAdapter(Context context, List<CertificationModel> certificationList, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.certificationList = (certificationList != null) ? certificationList : new ArrayList<>();
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_certification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CertificationModel certification = certificationList.get(position);
        holder.tvCertificationName.setText(certification.getName());
        holder.tvOrganisation.setText(certification.getOrganisation());
        holder.tvYear.setText(certification.getYear());
        holder.tvCredentialUrl.setText(certification.getCredentialUrl());
        holder.btnDeleteCertification.setOnClickListener(v -> {
            deleteClickListener.onDeleteClick(position);

        });
        Log.d("CertificationsAdapter", "Binding: " + certification.getName() + ", Org: " + certification.getOrganisation());
    }

    @Override
    public int getItemCount() {
        Log.d("CertificationsAdapter", "Final Item count: " + certificationList.size());
        return certificationList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCertificationName, tvOrganisation, tvYear,tvCredentialUrl;
        ImageView btnDeleteCertification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCertificationName = itemView.findViewById(R.id.tvCertificationTitle);
            tvOrganisation = itemView.findViewById(R.id.tvOrganisation);
            tvYear = itemView.findViewById(R.id.tvYear);
            btnDeleteCertification = itemView.findViewById(R.id.btnDeleteCertification);
            tvCredentialUrl = itemView.findViewById(R.id.tvCredentialUrl);


        }
    }


    public void updateData(List<CertificationModel> newData) {
        Log.d("CertificationsAdapter", "Updating adapter with " + newData.size() + " items.");

        // Ensure list is cleared and repopulated correctly
        this.certificationList.clear();
        this.certificationList.addAll(newData);

        // Debugging: Log each certificate added to adapter
        for (CertificationModel cert : certificationList) {
            Log.d("CertificationsAdapter", "Adapter Item: " + cert.getName() + ", Year: " + cert.getYear());
        }

        Log.d("CertificationsAdapter", "Final List Size in Adapter: " + certificationList.size());

        notifyDataSetChanged();  // Ensure UI refresh
    }



}

