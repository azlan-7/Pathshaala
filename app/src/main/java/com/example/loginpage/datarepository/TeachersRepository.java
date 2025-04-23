package com.example.loginpage.datarepository;

import com.example.loginpage.models.TeachersList;

import java.util.Arrays;
import java.util.List;

public class TeachersRepository {
    public static List<TeachersList> getTeachersList() {
        return Arrays.asList(
                new TeachersList("Mr. Sharma", Arrays.asList("Mathematics", "Statistics")),
                new TeachersList("Ms. Verma", Arrays.asList("Art", "Chemistry", "Accounting", "Biology", "Business Studies")),
                new TeachersList("Dr. Khan", Arrays.asList("Biology", "Environmental Science")),
                new TeachersList("Mrs. Patel", Arrays.asList("Economics", "Business Studies")),
                new TeachersList("Mr. Sen", Arrays.asList("Computer Science", "English")),
                new TeachersList("Ms. Iyer", Arrays.asList("History", "Philosophy")),
                new TeachersList("Mr. Joseph", Arrays.asList("Political Science", "Sociology")),
                new TeachersList("Dr. Mehta", Arrays.asList("Physics", "Mathematics")),
                new TeachersList("Ms. Nair", Arrays.asList("English", "Psychology")),
                new TeachersList("Mr. Das", Arrays.asList("Music", "Art")),
                new TeachersList("Ms. Chawla", Arrays.asList("Chemistry", "Biology")),
                new TeachersList("Dr. Bhatt", Arrays.asList("Environmental Science", "Geography")),
                new TeachersList("Mr. Kapoor", Arrays.asList("History", "Political Science")),
                new TeachersList("Mrs. Singh", Arrays.asList("Mathematics", "Computer Science")),
                new TeachersList("Mr. Reddy", Arrays.asList("Statistics", "Accounting")),
                new TeachersList("Ms. Joshi", Arrays.asList("Philosophy", "English")),
                new TeachersList("Dr. Rao", Arrays.asList("Physics", "Statistics")),
                new TeachersList("Ms. Malhotra", Arrays.asList("Psychology", "Sociology")),
                new TeachersList("Mr. Bansal", Arrays.asList("Economics", "Mathematics")),
                new TeachersList("Ms. Sinha", Arrays.asList("Geography", "Environmental Science")),
                new TeachersList("Mr. Roy", Arrays.asList("Chemistry", "Physics")),
                new TeachersList("Dr. Dutta", Arrays.asList("Computer Science", "Mathematics")),
                new TeachersList("Ms. Kaur", Arrays.asList("Music", "Philosophy")),
                new TeachersList("Mr. Chauhan", Arrays.asList("Business Studies", "Economics")),
                new TeachersList("Mrs. Ali", Arrays.asList("English", "History"))
        );
    }
}
