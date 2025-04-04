package com.example.loginpage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loginpage.R;

import java.util.HashMap;
import java.util.List;

public class TimeTableExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> groupList;
    private final HashMap<String, List<String>> itemMap;
    private boolean isEditMode = false;
    private int editingGroupPosition = -1;

    public TimeTableExpandableListAdapter(Context context, List<String> groupList, HashMap<String, List<String>> itemMap) {
        this.context = context;
        this.groupList = groupList;
        this.itemMap = itemMap;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemMap.get(groupList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itemMap.get(groupList.get(groupPosition)).get(childPosition);
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

    // Group view
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(title);
        return convertView;
    }

    // Child view
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        String item = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_child, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.classTimeTextView);
        EditText editText = convertView.findViewById(R.id.classTimeEditText);

        if ("Edit Timetable".equals(item)) {
            textView.setText(item);
            textView.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);

            convertView.setOnClickListener(v -> {
                isEditMode = true;
                editingGroupPosition = groupPosition;
                notifyDataSetChanged();
            });

        } else {
            textView.setText(item);
            editText.setText(item);

            if (isEditMode && groupPosition == editingGroupPosition) {
                textView.setVisibility(View.GONE);
                editText.setVisibility(View.VISIBLE);

                editText.setOnFocusChangeListener((v, hasFocus) -> {
                    if (!hasFocus) {
                        String updated = editText.getText().toString();
                        itemMap.get(groupList.get(groupPosition)).set(childPosition, updated);
                        textView.setText(updated);
                        notifyDataSetChanged();
                    }
                });

            } else {
                textView.setVisibility(View.VISIBLE);
                editText.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
