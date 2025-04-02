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
                new TeachersList("Mr. Sen", Arrays.asList("Computer Science", "English"))
        );
    }

}
