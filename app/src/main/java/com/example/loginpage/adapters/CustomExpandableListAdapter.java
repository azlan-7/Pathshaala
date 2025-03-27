package com.example.loginpage.adapters;  // Make sure this matches your actual package structure

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.example.loginpage.R;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> sectionTitles;
    private HashMap<String, List<String>> sectionItems;

    public CustomExpandableListAdapter(Context context, List<String> sectionTitles, HashMap<String, List<String>> sectionItems) {
        this.context = context;
        this.sectionTitles = sectionTitles;
        this.sectionItems = sectionItems;
    }

    @Override
    public int getGroupCount() {
        return sectionTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return sectionItems.get(sectionTitles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return sectionTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return sectionItems.get(sectionTitles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView listTitle = convertView.findViewById(R.id.listTitle);
        listTitle.setText((String) getGroup(groupPosition));

        ImageView groupIcon = convertView.findViewById(R.id.groupIcon);
        ImageView groupIndicator = convertView.findViewById(R.id.groupIndicator);

        String groupName = (String) getGroup(groupPosition);

        // 🔹 Set different icons for different section titles
        switch (groupName) {
            case "Subject Expertise":
                groupIcon.setImageResource(R.drawable.import_contacts_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Education":
                groupIcon.setImageResource(R.drawable.school_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Work Experience":
                groupIcon.setImageResource(R.drawable.work_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Certifications":
                groupIcon.setImageResource(R.drawable.verified_user_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Grades Taught":
                groupIcon.setImageResource(R.drawable.local_library_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Awards/Achievements":
                groupIcon.setImageResource(R.drawable.trophy_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Promotional Activities":
                groupIcon.setImageResource(R.drawable.videocam_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Dashboard":
                groupIcon.setImageResource(R.drawable.space_dashboard_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            default:
                groupIcon.setImageResource(R.drawable.star_24dp_0f4d73_fill0_wght400_grad0_opsz24); // Default icon
        }

        // 🔹 Hide arrow for "Dashboard" and "Promotional Activities"
        if (groupName.equals("Dashboard") || groupName.equals("Promotional Activities")) {
            groupIndicator.setVisibility(View.GONE); // Hide arrow
        } else {
            groupIndicator.setVisibility(View.VISIBLE);
            if (isExpanded) {
                groupIndicator.setImageResource(R.drawable.keyboard_arrow_up_24dp_0f4d73_fill0_wght400_grad0_opsz24);
            } else {
                groupIndicator.setImageResource(R.drawable.keyboard_arrow_down_24dp_0f4d73_fill0_wght400_grad0_opsz24);
            }
        }

        return convertView;
    }



    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView listItemText = convertView.findViewById(R.id.listItemText);
        ImageView itemIcon = convertView.findViewById(R.id.itemIcon);

        String itemName = (String) getChild(groupPosition, childPosition);
        listItemText.setText(itemName);

        // 🔹 Assign icons based on keywords
        if (itemName.toLowerCase().contains("view")) {
            itemIcon.setImageResource(R.drawable.visibility_24dp_0f4d73_fill0_wght400_grad0_opsz24); // View icon
        } else if (itemName.toLowerCase().contains("add")) {
            itemIcon.setImageResource(R.drawable.add_24dp_0f4d73_fill0_wght400_grad0_opsz24); // Add icon
        } else {
            itemIcon.setImageResource(R.drawable.star_24dp_0f4d73_fill0_wght400_grad0_opsz24); // Default fallback icon
        }

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
