package com.example.loginpage;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectGradeTopic extends AppCompatActivity {
    private static final String TAG = "SubjectGradeTopic";
    private AutoCompleteTextView gradeDropdown, subjectDropdown, topicDropdown;
    private Map<String, String> gradeMap = new HashMap<>(); // GradeName -> GradeID
    private Map<String, String> subjectMap = new HashMap<>(); // SubjectName -> SubjectID
    private Map<String, String> topicMap = new HashMap<>(); // TopicName -> TopicID

    private String selectedSubjectID = null; // Stores the selected Subject ID
    private String selectedGradeID = null; // Stores the selected Grade ID


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_grade_topic);

        gradeDropdown = findViewById(R.id.gradesDropdown);
        subjectDropdown = findViewById(R.id.subjectsDropdown);
        topicDropdown = findViewById(R.id.topicsDropdown);

        Log.d(TAG, "onCreate: Activity Started");

        loadSubjects();
    }

    private void loadSubjects() {
        String query = "SELECT SubjectID, SubjectName FROM Subject WHERE active = 'true' ORDER BY SubjectName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
            if (result == null || result.isEmpty()) {
                Log.e(TAG, "No subjects found in the database.");
                Toast.makeText(this, "No Subjects Found!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> subjects = new ArrayList<>();
            subjectMap.clear();

            for (Map<String, String> row : result) {
                String id = row.get("SubjectID");
                String name = row.get("SubjectName");
                Log.d(TAG, "Subject Retrieved - ID: " + id + ", Name: " + name);
                subjects.add(name);
                subjectMap.put(name, id);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects);
            subjectDropdown.setAdapter(adapter);
        });

        subjectDropdown.setOnClickListener(v -> subjectDropdown.showDropDown());

        subjectDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSubject = (String) parent.getItemAtPosition(position);
            selectedSubjectID = subjectMap.get(selectedSubject);
            Log.d(TAG, "Selected Subject: " + selectedSubject + " (ID: " + selectedSubjectID + ")");

            if (selectedSubjectID != null) {
                loadGrades(selectedSubjectID);
            }
        });
    }




    private void loadGrades(String subjectID) {
        String query = "SELECT GradesID, GradesName FROM Grades WHERE active = 'true' AND SubjectID = '" + subjectID + "' ORDER BY GradesName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
            if (result == null || result.isEmpty()) {
                Log.e(TAG, "No grades found for Subject ID: " + subjectID);
                Toast.makeText(this, "No Grades Found!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> grades = new ArrayList<>();
            gradeMap.clear();

            for (Map<String, String> row : result) {
                String id = row.get("GradesID");
                String name = row.get("GradesName");
                Log.d(TAG, "Grade Retrieved - ID: " + id + ", Name: " + name);
                grades.add(name);
                gradeMap.put(name, id);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, grades);
            gradeDropdown.setAdapter(adapter);
        });

        gradeDropdown.setOnClickListener(v -> gradeDropdown.showDropDown());

        gradeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedGrade = (String) parent.getItemAtPosition(position);
            selectedGradeID = gradeMap.get(selectedGrade);
            Log.d(TAG, "Selected Grade: " + selectedGrade + " (ID: " + selectedGradeID + ")");

            if (selectedSubjectID != null && selectedGradeID != null) {
                loadTopics(selectedSubjectID, selectedGradeID);
            }
        });
    }



    private void loadTopics(String subjectID, String gradeID) {
        String query = "SELECT topicsID, topicsName FROM topics WHERE active = 'true' AND SubjectID = '" + subjectID + "' AND GradesID = '" + gradeID + "' ORDER BY topicsName";
        Log.d(TAG, "Executing query: " + query);

        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
            if (result == null || result.isEmpty()) {
                Log.e(TAG, "No topics found for Subject ID: " + subjectID + " and Grade ID: " + gradeID);
                Toast.makeText(this, "No Topics Found!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> topics = new ArrayList<>();
            topicMap.clear();

            for (Map<String, String> row : result) {
                String id = row.get("topicsID");
                String name = row.get("topicsName");
                Log.d(TAG, "Topic Retrieved - ID: " + id + ", Name: " + name);
                topics.add(name);
                topicMap.put(name, id);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, topics);
            topicDropdown.setAdapter(adapter);
            topicDropdown.showDropDown();
        });

        topicDropdown.setOnClickListener(v -> topicDropdown.showDropDown());

        topicDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTopic = (String) parent.getItemAtPosition(position);
            String topicID = topicMap.get(selectedTopic);
            Log.d(TAG, "Selected Topic: " + selectedTopic + " (ID: " + topicID + ")");
        });
    }

}















//package com.example.loginpage;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class SubjectGradeTopic extends AppCompatActivity {
//    private static final String TAG = "SubjectGradeTopic";
//    private AutoCompleteTextView subjectsDropdown, gradesDropdown, topicsDropdown;
//    private Map<String, String> subjectMap = new HashMap<>();
//    private Map<String, String> gradeMap = new HashMap<>();
//    private Map<String, String> topicMap = new HashMap<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_subject_grade_topic);
//
//        subjectsDropdown = findViewById(R.id.subjectsDropdown);
//        gradesDropdown = findViewById(R.id.gradesDropdown);
//        topicsDropdown = findViewById(R.id.topicsDropdown);
//
//        loadSubjects();
//    }
//
//    private void loadSubjects() {
//        String query = "SELECT SubjectID, SubjectName FROM Subject WHERE active = 'true' ORDER BY SubjectName";
//        Log.d(TAG, "Executing query: " + query);
//
//        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
//            if (result == null || result.isEmpty()) {
//                Toast.makeText(this, "No Subjects Found!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            List<String> subjects = new ArrayList<>();
//            subjectMap.clear();
//
//            for (Map<String, String> row : result) {
//                String id = row.get("SubjectID");
//                String name = row.get("SubjectName");
//                subjects.add(name);
//                subjectMap.put(name, id);
//            }
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects);
//            subjectsDropdown.setAdapter(adapter);
//        });
//
//        subjectsDropdown.setOnClickListener(v -> subjectsDropdown.showDropDown());
//
//        subjectsDropdown.setOnItemClickListener((parent, view, position, id) -> {
//            String selectedSubject = (String) parent.getItemAtPosition(position);
//            String subjectID = subjectMap.get(selectedSubject);
//            if (subjectID != null) {
//                loadGrades(subjectID);
//            }
//        });
//    }
//
//    private void loadGrades(String subjectID) {
//        String query = "SELECT GradesID, GradesName FROM Grades WHERE active = 'true' AND SubjectID = '" + subjectID + "' ORDER BY GradesName";
//        Log.d(TAG, "Executing query: " + query);
//
//        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
//            if (result == null || result.isEmpty()) {
//                Toast.makeText(this, "No Grades Found!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            List<String> grades = new ArrayList<>();
//            gradeMap.clear();
//
//            for (Map<String, String> row : result) {
//                String id = row.get("GradesID");
//                String name = row.get("GradesName");
//                grades.add(name);
//                gradeMap.put(name, id);
//            }
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, grades);
//            gradesDropdown.setAdapter(adapter);
//        });
//
//        gradesDropdown.setOnClickListener(v -> gradesDropdown.showDropDown());
//
//        gradesDropdown.setOnItemClickListener((parent, view, position, id) -> {
//            String selectedGrade = (String) parent.getItemAtPosition(position);
//            String gradeID = gradeMap.get(selectedGrade);
//            if (gradeID != null) {
//                loadTopics(subjectID, gradeID);
//            }
//        });
//    }
//
//    private void loadTopics(String subjectID, String gradeID) {
//        String query = "SELECT topicsID, topicsName FROM topics WHERE active = 'true' AND SubjectID = '" + subjectID + "' AND GradesID = '" + gradeID + "' ORDER BY topicsName";
//        Log.d(TAG, "Executing query: " + query);
//
//        DatabaseHelper.loadDataFromDatabase(this, query, result -> {
//            if (result == null || result.isEmpty()) {
//                Toast.makeText(this, "No Topics Found!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            List<String> topics = new ArrayList<>();
//            topicMap.clear();
//
//            for (Map<String, String> row : result) {
//                String id = row.get("topicsID");
//                String name = row.get("topicsName");
//                topics.add(name);
//                topicMap.put(name, id);
//            }
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, topics);
//            topicsDropdown.setAdapter(adapter);
//        });
//
//        topicsDropdown.setOnClickListener(v -> topicsDropdown.showDropDown());
//    }
//}
