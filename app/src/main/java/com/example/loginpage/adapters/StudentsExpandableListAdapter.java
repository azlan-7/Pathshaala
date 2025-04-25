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


import com.example.loginpage.GradesStudentAdd;
import com.example.loginpage.GradesStudentView;
import com.example.loginpage.R;
import com.example.loginpage.StudentsAcademicDetails;
import com.example.loginpage.StudentsAcademicDetailsView;
import com.example.loginpage.StudentsGrade;
import com.example.loginpage.StudentsParentInfo;
import com.example.loginpage.StudentsParentInfoView;

import java.util.HashMap;
import java.util.List;

public class StudentsExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> sectionTitles;
    private HashMap<String, List<String>> sectionItems;

    public StudentsExpandableListAdapter(Context context, List<String> sectionTitles, HashMap<String, List<String>> sectionItems) {
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

        TextView sectionTitle = convertView.findViewById(R.id.listTitle);
        sectionTitle.setText((String) getGroup(groupPosition));

        ImageView groupIcon = convertView.findViewById(R.id.groupIcon);
        ImageView groupIndicator = convertView.findViewById(R.id.groupIndicator);

        String groupName = (String) getGroup(groupPosition);

        // 🔹 Set different icons for different section titles
        switch (groupName) {
            case "Account Info":
                groupIcon.setImageResource(R.drawable.person_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Academic Details":
                groupIcon.setImageResource(R.drawable.school_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Grade":
                groupIcon.setImageResource(R.drawable.local_library_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Learning Preference":
                groupIcon.setImageResource(R.drawable.import_contacts_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Parent Guardian Details":
                groupIcon.setImageResource(R.drawable.supervisor_account_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Skills and Extracurriculars":
                groupIcon.setImageResource(R.drawable.star_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Progress and Performance":
                groupIcon.setImageResource(R.drawable.trending_up_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Attendance and Participation":
                groupIcon.setImageResource(R.drawable.equalizer_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            case "Dashboard":
                groupIcon.setImageResource(R.drawable.space_dashboard_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;

            case "Communication Preferences":
                groupIcon.setImageResource(R.drawable.notifications_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;

            case "Feedback and Ratings":
                groupIcon.setImageResource(R.drawable.thumb_up_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
            default:
                groupIcon.setImageResource(R.drawable.star_24dp_0f4d73_fill0_wght400_grad0_opsz24); // Default icon
        }

        // 🔹 Hide arrow for "Dashboard" and "Promotional Activities"
        if (groupName.equals("Dashboard") || groupName.equals("Account Info") || groupName.equals("Feedback and Ratings") || groupName.equals("Attendance and Participation") || groupName.equals("Progress and Performance") || groupName.equals("Communication Preferences")) {
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

        String sectionTitle = sectionTitles.get(groupPosition);
        LinearLayout subjectContainer = convertView.findViewById(R.id.subjectContainer);
        subjectContainer.removeAllViews();

        ImageView itemIcon = convertView.findViewById(R.id.itemIcon);
        String itemName = (String) getChild(groupPosition, childPosition);

        TextView listItemText = new TextView(context);
        listItemText.setText(itemName);
        listItemText.setTextColor(Color.parseColor("#0F4D73"));
        listItemText.setTextSize(16);
        listItemText.setPadding(10, 0, 10, 0);
        subjectContainer.addView(listItemText);

        if (itemName.toLowerCase().contains("view")) {
            itemIcon.setImageResource(R.drawable.visibility_24dp_0f4d73_fill0_wght400_grad0_opsz24);
        } else if (itemName.toLowerCase().contains("add")) {
            itemIcon.setImageResource(R.drawable.add_24dp_0f4d73_fill0_wght400_grad0_opsz24);

            itemIcon.setOnClickListener(v -> {
                if (context != null) { // Add null check for context
                    switch (sectionTitle) {
                        case "Academic Details":
                            context.startActivity(new Intent(context, StudentsAcademicDetails.class));
                            break;
                        case "Grade":
                            context.startActivity(new Intent(context, GradesStudentAdd.class));
                            break;
                        case "Parent Guardian Details":
                            context.startActivity(new Intent(context, StudentsParentInfo.class));
                            break;
                        default:
                            Toast.makeText(context, "Add functionality not set for this section", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Context is null", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (itemName.toLowerCase().contains("edit")) {
            itemIcon.setImageResource(R.drawable.pencilvector);
        } else {
            itemIcon.setImageResource(R.drawable.dot_single_svgrepo_com);
            itemIcon.setOnClickListener(v -> {
                if (context != null) { // Add null check for context
                    switch (sectionTitle) {
                        case "Academic Details":
                            context.startActivity(new Intent(context, StudentsAcademicDetailsView.class));
                            break;
                        case "Grade":
                            context.startActivity(new Intent(context, GradesStudentView.class)); //changed to view
                            break;
                        case "Parent Guardian Details":
                            context.startActivity(new Intent(context, StudentsParentInfoView.class));  //changed to view
                            break;
                        default:
                            Toast.makeText(context, "View functionality not set for this section", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Context is null", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
