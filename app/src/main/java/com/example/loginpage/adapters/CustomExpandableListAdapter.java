package com.example.loginpage.adapters;  // Make sure this matches your actual package structure

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.AddAwards;
import com.example.loginpage.AddPromotionalMedia;
import com.example.loginpage.Awards;
import com.example.loginpage.CertificationsAdd;
import com.example.loginpage.CertificationsView;
import com.example.loginpage.GradesTaught;
import com.example.loginpage.GradesTaughtView;
import com.example.loginpage.R;
import com.example.loginpage.SubjectExpertise;
import com.example.loginpage.SubjectExpertiseNewOne;
import com.example.loginpage.TeachersBasicInfo;
import com.example.loginpage.TeachersDashboardNew;
import com.example.loginpage.TeachersEducation;
import com.example.loginpage.TeachersEducationView;
import com.example.loginpage.WorkExperience;
import com.example.loginpage.WorkExperienceView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listDataHeader; // Parent titles
    private HashMap<String, List<String>> listDataChild; // Child items

    // 🔹 Declare HashMap for subsection-specific Add buttons
    private Map<String, Class<?>> addActivityMap;
    private Map<String, Class<?>> viewActivityMap;

    public CustomExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listDataChild) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;

        // 🔹 Initialize HashMap with subsections and their "Add" activities
        addActivityMap = new HashMap<>();
        addActivityMap.put("Subject Expertise", SubjectExpertiseNewOne.class);
        addActivityMap.put("Education", TeachersEducation.class);
        addActivityMap.put("Work Experience", WorkExperience.class);
        addActivityMap.put("Certifications", CertificationsAdd.class);
        addActivityMap.put("Grades Taught", GradesTaught.class);
        addActivityMap.put("Awards/Achievements", AddAwards.class);
        addActivityMap.put("Promotional Activities", AddPromotionalMedia.class);


        // 🔹 Initialize HashMap with subsections and their "View" activities
        viewActivityMap = new HashMap<>();
        viewActivityMap.put("Subject Expertise", SubjectExpertiseNewOne.class);
        viewActivityMap.put("Education", TeachersEducationView.class);
        viewActivityMap.put("Work Experience", WorkExperienceView.class);
        viewActivityMap.put("Certifications", CertificationsView.class);
        viewActivityMap.put("Grades Taught", GradesTaughtView.class);
        viewActivityMap.put("Awards/Achievements", Awards.class);
        viewActivityMap.put("Promotional Activities", AddPromotionalMedia.class);
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

        TextView listTitle = convertView.findViewById(R.id.listTitle);
        listTitle.setText((String) getGroup(groupPosition));

        ImageView groupIcon = convertView.findViewById(R.id.groupIcon);
        ImageView groupIndicator = convertView.findViewById(R.id.groupIndicator);

        String groupName = (String) getGroup(groupPosition);

        // 🔹 Set different icons for different section titles
        switch (groupName) {
            case "Account Info":
                groupIcon.setImageResource(R.drawable.person_24dp_0f4d73_fill0_wght400_grad0_opsz24);
                break;
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
        if (groupName.equals("Dashboard") || groupName.equals("Promotional Activities") || groupName.equals("Account Info")) {
            groupIndicator.setVisibility(View.GONE); // Hide arrow

            // 🔹 Add Click Listener to Navigate
            convertView.setOnClickListener(v -> {
                Class<?> targetActivity = null;
                if (groupName.equals("Dashboard")) {
                    targetActivity = TeachersDashboardNew.class;
                } else if (groupName.equals("Promotional Activities")) {
                    targetActivity = AddPromotionalMedia.class;
                }else if (groupName.equals("Account Info")) {
                    targetActivity = TeachersBasicInfo.class;
                }

                if (targetActivity != null) {
                    Intent intent = new Intent(context, targetActivity);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Activity not found!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 🔹 Reset click listener to avoid unwanted behaviors
            // Only reset click listener for non-clickable items
            if (!groupName.equals("Dashboard") && !groupName.equals("Promotional Activities")) {
                convertView.setOnClickListener(v -> {
                    ((ExpandableListView) parent).expandGroup(groupPosition, !isExpanded);
                });
            }

            // 🔹 Show expand/collapse indicator
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

        // 🔹 Reset icon before setting it dynamically
        itemIcon.setImageDrawable(null);  // Clears previous icon to prevent reuse issue

        // 🔹 Assign icons based on keywords
        if (childText.toLowerCase().contains("view")) {
            itemIcon.setImageResource(R.drawable.visibility_24dp_0f4d73_fill0_wght400_grad0_opsz24); // View icon
            itemIcon.setOnClickListener(v -> {
                Class<?> targetActivity = viewActivityMap.get(sectionTitle);
                if (targetActivity != null) {
                    Intent intent = new Intent(context, targetActivity);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "No View screen defined for " + sectionTitle, Toast.LENGTH_SHORT).show();
                }
            });
        } else if(childText.toLowerCase().contains("add")) {
            itemIcon.setImageResource(R.drawable.add_24dp_0f4d73_fill0_wght400_grad0_opsz24); // View icon
            itemIcon.setOnClickListener(v -> {
                Class<?> targetActivity = addActivityMap.get(sectionTitle);
                if (targetActivity != null) {
                    Intent intent = new Intent(context, targetActivity);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "No Add screen defined for " + sectionTitle, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            itemIcon.setImageResource(R.drawable.dot_single_svgrepo_com); // Default Add icon
            itemIcon.setOnClickListener(v -> {
                Class<?> targetActivity = viewActivityMap.get(sectionTitle);
                if (targetActivity != null) {
                    Intent intent = new Intent(context, targetActivity);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "No View screen defined for " + sectionTitle, Toast.LENGTH_SHORT).show();
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
