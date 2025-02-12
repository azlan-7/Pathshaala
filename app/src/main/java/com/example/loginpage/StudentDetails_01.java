package com.example.loginpage;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Calendar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StudentDetails_01 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_details01);

        // Initialize all the Spinner
        Spinner spinner1 = findViewById(R.id.spinner1);     // for selecting the class
        Spinner spinner2 = findViewById(R.id.spinner2);     // for selecting the year
        Spinner spinner3 = findViewById(R.id.spinner3);     // for selecting the month
        Spinner spinner4 = findViewById(R.id.spinner4);     // for selecting the learning stream
        Spinner spinner5 = findViewById(R.id.spinner5);     // for selecting the medium of instruction

        String[] classOptions = {"Select Class", "9th", "10th", "11th", "12th"};    // Class Options
        String[] monthOptions = {"Select Month", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};     // All the month options
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);        // Get the current year
        int[] passYearOptions = {currentYear, currentYear+1, currentYear+2, currentYear+3, currentYear+4, currentYear+5, currentYear+6}; // Current Year + 6 year options
        String[] streamOptions = {"PCM", "PCMB", "PCB", "Commerce", "Arts", "Others"};
        String[] mediumOptions = {"English", "Hindi", "Tamil", "Bengali", "Marathi"};

        // ✅ Proper adapter for Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);

        // ✅ Correct reference to `this`
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedClass = parent.getItemAtPosition(position).toString();

                // ✅ Avoid showing "Select Class" in Toast
                if (position != 0) {
                    Toast.makeText(StudentDetails_01.this, "Selected: " + selectedClass, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });

        // ✅ Apply window insets correctly
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
