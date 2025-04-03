package com.example.loginpage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginpage.R;
import com.example.loginpage.models.ExpandableSection;
import java.util.List;

public class ExpandableAdapter extends RecyclerView.Adapter<ExpandableAdapter.ViewHolder> {

    private List<ExpandableSection> sectionList;
    private Context context;

    public ExpandableAdapter(List<ExpandableSection> sectionList, Context context) {
        this.sectionList = sectionList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView arrowIcon;
        RecyclerView subRecyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.sectionTitle);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);
            subRecyclerView = itemView.findViewById(R.id.subRecyclerView);
        }
    }

    @Override
    public ExpandableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expandable_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ExpandableSection section = sectionList.get(position);
        holder.title.setText(section.getTitle());
        holder.subRecyclerView.setVisibility(section.isExpanded() ? View.VISIBLE : View.GONE);
        holder.arrowIcon.setImageResource(section.isExpanded() ? R.drawable.keyboard_arrow_up_24dp_0f4d73_fill0_wght400_grad0_opsz24 : R.drawable.keyboard_arrow_down_24dp_0f4d73_fill0_wght400_grad0_opsz24);

        holder.title.setOnClickListener(v -> {
            section.setExpanded(!section.isExpanded());
            notifyItemChanged(position);
        });

        // Populate subsections inside the RecyclerView
        holder.subRecyclerView.setAdapter(new SubsectionAdapter(section.getSubsections(), context));
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }
}
