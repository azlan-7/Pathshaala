package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TeachersAddress extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_address);

        Button saveButton = findViewById(R.id.button13);

        saveButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeachersAddress.this, TeachersAddSubject.class);
            startActivity(intent);
            finish();  // Optional: Closes the location page so user can't go back
        });

        AutoCompleteTextView etCity = findViewById(R.id.et_city);
        AutoCompleteTextView etState = findViewById(R.id.et_state);

        String[] cities = {"New Delhi", "Mumbai", "Bangalore", "Kolkata"};
        String[] states = {"Andhra Pradesh", "Odhisha", "Haryana", "Uttar Pradesh"};


        ArrayAdapter<String> adapterCity = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        etCity.setAdapter(adapterCity);

        ArrayAdapter<String> adapterState = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, states);
        etState.setAdapter(adapterState);

        etCity.setOnClickListener(v -> etCity.showDropDown());
        etState.setOnClickListener(v -> etState.showDropDown());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}