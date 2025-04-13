package com.example.loginpage.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.MySqliteDatabase.DatabaseHelper.TimeTableEntry;
import com.example.loginpage.R;

import java.util.HashMap;
import java.util.List;

public class TimeTableExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDays; // Group titles (e.g., Monday, Tuesday)
    private HashMap<String, List<TimeTableEntry>> listTimeTable;

    private int selectedGroupPosition = -1;
    private int selectedChildPosition = -1;


    public TimeTableExpandableListAdapter(Context context, List<String> listDays,
                                          HashMap<String, List<DatabaseHelper.TimeTableEntry>> listTimeTable) {
        this.context = context;
        this.listDays = listDays;
        this.listTimeTable = listTimeTable;
    }

    public void setSelectedItem(int groupPosition, int childPosition) {
        this.selectedGroupPosition = groupPosition;
        this.selectedChildPosition = childPosition;
        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return listDays.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String day = listDays.get(groupPosition);
        return listTimeTable.get(day).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDays.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String day = listDays.get(groupPosition);
        return listTimeTable.get(day).get(childPosition);
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

    // Group view (day)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String day = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(day);

        return convertView;
    }

    // Child view (class entry)
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TimeTableEntry entry = (TimeTableEntry) getChild(groupPosition, childPosition);

        if (groupPosition == selectedGroupPosition && childPosition == selectedChildPosition) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue)); // Highlight if selected
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT); // Reset to default
        }

        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        String subject = entry.subjectName != null ? entry.subjectName : "";
        String grade = entry.gradeName != null ? entry.gradeName : "";
        String start = entry.startTime != null ? entry.startTime : "";
        String end = entry.endTime != null ? entry.endTime : "";
        String room = entry.roomNo != null ? entry.roomNo : "";

        if ("Edit Timetable".equals(subject)) {
            text1.setText("Edit Timetable");
            text2.setText("");
        } else {
            // Show ✅ if selected via remark (not just adapter selection)
            String tick = (entry.remark != null && entry.remark.contains("✓")) ? " ✅" : "";
            text1.setText(subject + " - " + grade + tick);
            text2.setText(start + " to " + end + (room.isEmpty() ? "" : " | Room: " + room));
        }

        return convertView;
    }




    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
