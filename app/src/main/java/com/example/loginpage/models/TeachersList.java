package com.example.loginpage.models;

import java.util.Collections;
import java.util.List;
public class TeachersList {
    private String name;
    private List<String> subjects;

    public TeachersList(String name, List<String> subjects) {
        this.name = name;
        this.subjects = subjects;
    }

    public String getName() {
        return name;
    }

    public List<String> getSubjects() {
        Collections.sort(subjects);
        return subjects;
    }
}