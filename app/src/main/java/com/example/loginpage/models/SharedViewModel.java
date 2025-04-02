package com.example.loginpage.models;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.loginpage.LearningPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SharedViewModel extends AndroidViewModel {
    private final MutableLiveData<List<String>> selectedSubjects = new MutableLiveData<>(new ArrayList<>());
    private final SharedPreferences sharedPreferences;

    public SharedViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences("UserPrefs", Application.MODE_PRIVATE);
        loadSubjectsFromPreferences();  // Load subjects at initialization
    }

    // Fetch selected subjects
    public LiveData<List<String>> getSelectedSubjects() {
        return selectedSubjects;
    }

    // Add a subject and update preferences
    public void addSubject(String subject) {
        List<String> currentSubjects = selectedSubjects.getValue();
        if (currentSubjects != null && !currentSubjects.contains(subject)) {
            currentSubjects.add(subject);
            selectedSubjects.setValue(currentSubjects);
            saveSubjectsToPreferences(currentSubjects);
        }
    }

    // Remove a subject and update preferences
    public void removeSubject(String subject) {
        List<String> currentSubjects = selectedSubjects.getValue();
        if (currentSubjects != null && currentSubjects.contains(subject)) {
            currentSubjects.remove(subject);
            selectedSubjects.setValue(currentSubjects);
            saveSubjectsToPreferences(currentSubjects);
        }
    }

    // Set subjects from external sources (e.g., LearningPreferences)
    public void setSubjects(List<String> subjects) {
        selectedSubjects.setValue(subjects);
        saveSubjectsToPreferences(subjects);
    }

    // Clear all selected subjects
    public void clearSubjects() {
        selectedSubjects.setValue(new ArrayList<>());
        sharedPreferences.edit().remove("PREFERRED_SUBJECTS").apply();
    }

    // Load subjects from SharedPreferences
    private void loadSubjectsFromPreferences() {
        Set<String> storedSubjects = sharedPreferences.getStringSet("PREFERRED_SUBJECTS", new HashSet<>());
        selectedSubjects.setValue(new ArrayList<>(storedSubjects));
    }

    // Save subjects to SharedPreferences
    public void saveSubjectsToPreferences(List<String> subjects) {
        Set<String> subjectSet = new HashSet<>(subjects);
        sharedPreferences.edit().putStringSet("PREFERRED_SUBJECTS", subjectSet).apply();
    }
}
