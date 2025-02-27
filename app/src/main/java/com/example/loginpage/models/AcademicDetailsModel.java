package com.example.loginpage.models;

public class AcademicDetailsModel {
    private String gradeEnrolled;
    private String yearOfPassing;
    private String learningStream;
    private String selectBoard;

    public AcademicDetailsModel(String gradeEnrolled, String yearOfPassing, String learningStream, String selectBoard) {
        this.gradeEnrolled = gradeEnrolled;
        this.yearOfPassing = yearOfPassing;
        this.learningStream = learningStream;
        this.selectBoard = selectBoard;
    }

    public String getGradeEnrolled() {
        return gradeEnrolled;
    }

    public void setGradeEnrolled(String gradeEnrolled) {
        this.gradeEnrolled = gradeEnrolled;
    }

    public String getYearOfPassing() {
        return yearOfPassing;
    }

    public void setYearOfPassing(String yearOfPassing) {
        this.yearOfPassing = yearOfPassing;
    }

    public String getLearningStream() {
        return learningStream;
    }

    public void setLearningStream(String learningStream) {
        this.learningStream = learningStream;
    }

    public String getSelectBoard() {
        return selectBoard;
    }

    public void setSelectBoard(String selectBoard) {
        this.selectBoard = selectBoard;
    }
}
