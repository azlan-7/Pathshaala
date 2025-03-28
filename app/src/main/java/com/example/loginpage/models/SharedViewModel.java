package com.example.loginpage.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<String>> selectedSubjects = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<String>> getSelectedSubjects() {
        return selectedSubjects;
    }

    public void addSubject(String subject) {
        List<String> currentSubjects = selectedSubjects.getValue();
        if (currentSubjects != null && !currentSubjects.contains(subject)) {
            currentSubjects.add(subject);
            selectedSubjects.setValue(currentSubjects);
        }
    }

    public void clearSubjects() {
        selectedSubjects.setValue(new ArrayList<>());
    }
}
