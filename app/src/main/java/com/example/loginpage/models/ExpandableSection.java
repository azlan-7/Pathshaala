package com.example.loginpage.models;

import java.util.List;

public class ExpandableSection {
    private String title;
    private List<String> subsections;
    private boolean isExpanded;

    public ExpandableSection(String title, List<String> subsections) {
        this.title = title;
        this.subsections = subsections;
        this.isExpanded = false;
    }

    public String getTitle() { return title; }
    public List<String> getSubsections() { return subsections; }
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}
