package com.example.loginpage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginpage.R;
import java.util.List;

public class SubsectionAdapter extends RecyclerView.Adapter<SubsectionAdapter.ViewHolder> {
    private List<String> subsections;
    private Context context;

    public SubsectionAdapter(List<String> subsections, Context context) {
        this.subsections = subsections;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subsectionTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            subsectionTitle = itemView.findViewById(R.id.subsectionTitle);
        }
    }

    @Override
    public SubsectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subsection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.subsectionTitle.setText(subsections.get(position));
    }

    @Override
    public int getItemCount() {
        return subsections.size();
    }
}
