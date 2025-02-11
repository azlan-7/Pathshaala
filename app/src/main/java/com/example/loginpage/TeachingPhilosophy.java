package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TeachingPhilosophy extends AppCompatActivity {

    private static final int MAX_CHAR_LIMIT = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teaching_philosophy);



        EditText etPhilosophy = findViewById(R.id.editTextText21);
        TextView charCount = findViewById(R.id.tv_word_count);

        Button saveButton = findViewById(R.id.button19);
        EditText editTextPhilosophy = findViewById(R.id.editTextText21);


        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedText = sharedPreferences.getString("PHILOSOPHY", "");
        editTextPhilosophy.setText(savedText);
        charCount.setText(savedText.length() + "/100");



        // Character count and limit logic
        editTextPhilosophy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentLength = s.length();
                charCount.setText(currentLength + "/100");

                if (currentLength > MAX_CHAR_LIMIT) {
                    charCount.setTextColor(Color.RED); // Turn counter red if limit exceeded
                    editTextPhilosophy.setText(s.subSequence(0, MAX_CHAR_LIMIT)); // Prevent exceeding limit
                    editTextPhilosophy.setSelection(MAX_CHAR_LIMIT); // Set cursor at the end
                } else {
                    charCount.setTextColor(Color.GRAY);
                }
            }


            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Save button logic
        saveButton.setOnClickListener(v -> {
            String philosophyText = editTextPhilosophy.getText().toString().trim();

            if (!philosophyText.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("PHILOSOPHY", philosophyText);
                editor.apply();
                Toast.makeText(this, "Philosophy Saved!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(TeachingPhilosophy.this, TeachersBasicInfo.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Please enter your philosophy", Toast.LENGTH_SHORT).show();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}