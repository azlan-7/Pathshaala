package com.example.loginpage.adapters;

import android.content.Context;
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
    private final List<CertificationModel> certificationList;
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
        holder.btnDeleteCertification.setOnClickListener(v -> {
            deleteClickListener.onDeleteClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return certificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCertificationName, tvOrganisation, tvYear;
        ImageView btnDeleteCertification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCertificationName = itemView.findViewById(R.id.tvCertificationTitle);
            tvOrganisation = itemView.findViewById(R.id.tvOrganisation);
            tvYear = itemView.findViewById(R.id.tvYear);
            btnDeleteCertification = itemView.findViewById(R.id.btnDeleteCertification);

        }
    }
    public void updateData(List<CertificationModel> newData) {
        certificationList.clear();
        certificationList.addAll(newData);
        notifyDataSetChanged();
    }
}


















//package com.example.loginpage.adapters;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.loginpage.R;
//import com.example.loginpage.models.CertificationModel;
//import com.google.gson.Gson;
//import java.util.ArrayList;
//import java.util.List;
//
//public class CertificationsAdapter extends RecyclerView.Adapter<CertificationsAdapter.CertificationsViewHolder> {
//
//    private final Context context;
//    private final List<CertificationModel> certificationList;
//    private final SharedPreferences sharedPreferences;
//
//    public CertificationsAdapter(Context context, List<CertificationModel> certificationList) {
//        this.context = context;
//        this.certificationList = (certificationList != null) ? certificationList : new ArrayList<>();
//        this.sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
//    }
//
//    @NonNull
//    @Override
//    public CertificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_certification, parent, false);
//        return new CertificationsViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CertificationsViewHolder holder, int position) {
//        CertificationModel certification = certificationList.get(position);
//
//        holder.tvCertificationTitle.setText(certification.getTitle());
//        holder.tvOrganisation.setText(certification.getOrganisation());
//        holder.tvYear.setText(certification.getYear());
//        holder.tvCredentialUrl.setText(certification.getCredentialUrl());
//
//        holder.btnDeleteCertification.setOnClickListener(v -> {
//            deleteCertification(position);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return certificationList.size();
//    }
//
//    public static class CertificationsViewHolder extends RecyclerView.ViewHolder {
//        TextView tvCertificationTitle, tvOrganisation, tvYear, tvCredentialUrl;
//        ImageView btnDeleteCertification;
//
//        public CertificationsViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvCertificationTitle = itemView.findViewById(R.id.tvCertificationTitle);
//            tvOrganisation = itemView.findViewById(R.id.tvOrganisation);
//            tvYear = itemView.findViewById(R.id.tvYear);
//            tvCredentialUrl = itemView.findViewById(R.id.tvCredentialUrl);
//            btnDeleteCertification = itemView.findViewById(R.id.btnDeleteCertification);
//        }
//    }
//
//    private void deleteCertification(int position) {
//        certificationList.remove(position);
//        notifyItemRemoved(position);
//        saveCertificationData();
//        Toast.makeText(context, "Certification deleted!", Toast.LENGTH_SHORT).show();
//    }
//
//    private void saveCertificationData() {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        String updatedJson = gson.toJson(certificationList);
//        editor.putString("CERTIFICATION_LIST", updatedJson);
//        editor.apply();
//    }
//
//    public void updateData(List<CertificationModel> newData) {
//        certificationList.clear();
//        certificationList.addAll(newData);
//        notifyDataSetChanged();
//    }
//}
