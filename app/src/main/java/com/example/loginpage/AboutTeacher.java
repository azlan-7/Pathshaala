package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutTeacher extends AppCompatActivity {

    private EditText etAbout;
    private TextView charCount;
    private static final int MAX_CHAR = 150; // Max character limit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about_teacher);

        etAbout = findViewById(R.id.editTextText22);
        charCount = findViewById(R.id.textView55);
        Button btnSave = findViewById(R.id.button20);


        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedText = sharedPreferences.getString("ABOUT_YOURSELF", "");
        etAbout.setText(savedText);
        charCount.setText(savedText.length() + "/" + MAX_CHAR);


        // Character counter logic
        etAbout.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                charCount.setText(length + "/" + MAX_CHAR);
                if (length > MAX_CHAR) {
                    charCount.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    charCount.setTextColor(getResources().getColor(android.R.color.black));
                }
            }


            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });



        btnSave.setOnClickListener(v -> {
            String aboutText = etAbout.getText().toString().trim();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ABOUT_YOURSELF", aboutText);
            editor.apply();

            Intent intent = new Intent(AboutTeacher.this, TeachersInfo.class);
            intent.putExtra("ABOUT_YOURSELF", aboutText);
            startActivity(intent);
            finish(); // Close AboutTeacher
        });






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}