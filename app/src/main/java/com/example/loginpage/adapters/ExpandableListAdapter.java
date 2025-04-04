package com.example.loginpage.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.AddAwards;
import com.example.loginpage.AddPromotionalMedia;
import com.example.loginpage.CertificationsAdd;
import com.example.loginpage.GradesTaught;
import com.example.loginpage.R;
import com.example.loginpage.SubjectExpertise;
import com.example.loginpage.TeachersEducation;
import com.example.loginpage.WorkExperience;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listDataHeader; // Parent titles
    private HashMap<String, List<String>> listDataChild; // Child items

    // 🔹 Declare HashMap for subsection-specific Add buttons
    private Map<String, Class<?>> addActivityMap;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listDataChild) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;

        // 🔹 Initialize HashMap with subsections and their "Add" activities
        addActivityMap = new HashMap<>();
        addActivityMap.put("Subject Expertise", SubjectExpertise.class);
        addActivityMap.put("Education", TeachersEducation.class);
        addActivityMap.put("Work Experience", WorkExperience.class);
        addActivityMap.put("Certifications", CertificationsAdd.class);
        addActivityMap.put("Grades Taught", GradesTaught.class);
        addActivityMap.put("Achievements", AddAwards.class);
        addActivityMap.put("Promotional Activities", AddPromotionalMedia.class);
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
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

        TextView lblListHeader = convertView.findViewById(R.id.listTitle);
        lblListHeader.setText((String) getGroup(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        LinearLayout subjectContainer = convertView.findViewById(R.id.subjectContainer);
        subjectContainer.removeAllViews(); // Clear previous views

        ImageView itemIcon = convertView.findViewById(R.id.itemIcon);
        TextView subjectTextView = new TextView(context);

        // 🔹 Get section title dynamically
        String sectionTitle = (String) getGroup(groupPosition);
        String childText = getChild(groupPosition, childPosition).toString();

        subjectTextView.setText(childText);
        subjectTextView.setTextColor(Color.parseColor("#0F4D73"));
        subjectTextView.setTextSize(16);
        subjectTextView.setPadding(10, 0, 10, 0);

        subjectContainer.addView(subjectTextView);

        // 🔹 Set click listener for "Add" button
        itemIcon.setOnClickListener(v -> {
            Class<?> targetActivity = addActivityMap.get(sectionTitle);
            if (targetActivity != null) {
                Intent intent = new Intent(context, targetActivity);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No Add screen defined for " + sectionTitle, Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

