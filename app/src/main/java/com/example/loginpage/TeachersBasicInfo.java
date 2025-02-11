package com.example.loginpage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


import java.io.IOException;
import java.util.Calendar;

public class TeachersBasicInfo extends AppCompatActivity {

    private EditText etDOB;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private ImageView cameraIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teachers_basic_info);


        TextView philosophyTextView = findViewById(R.id.textView52);

        ImageView editPhilosophy = findViewById(R.id.imageView52);

        editPhilosophy.setOnClickListener(v -> {
            Intent intent = new Intent(TeachersBasicInfo.this, TeachingPhilosophy.class);
            startActivity(intent);
        });

        profileImageView = findViewById(R.id.imageView50);
        cameraIcon = findViewById(R.id.imageView54);

        cameraIcon.setOnClickListener(v -> openGallery());

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String fullPhilosophy = sharedPreferences.getString("PHILOSOPHY", "Not set");

        // Show only first 15 characters followed by "..." if longer
        String previewText = fullPhilosophy.length() > 45 ? fullPhilosophy.substring(0, 45) + "..." : fullPhilosophy;
        philosophyTextView.setText(previewText);


        if (!fullPhilosophy.equals("Not Set")) {
            String shortPhilosophy = fullPhilosophy.length() > 45 ? fullPhilosophy.substring(0, 45) + "..." : fullPhilosophy;
            philosophyTextView.setText(shortPhilosophy);
        }




        RadioButton radioMale = findViewById(R.id.radioMale);
        RadioButton radioFemale = findViewById(R.id.radioFemale);
        RadioButton radioOther = findViewById(R.id.radioOther);

        int colorChecked = ContextCompat.getColor(this, R.color.radio_selected);
        int colorUnchecked = ContextCompat.getColor(this, R.color.gray); // Change to your default color

        radioMale.setButtonTintList(ColorStateList.valueOf(colorChecked));
        radioFemale.setButtonTintList(ColorStateList.valueOf(colorChecked));
        radioOther.setButtonTintList(ColorStateList.valueOf(colorChecked));


        SharedPreferences.Editor editor = sharedPreferences.edit();
        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        EditText etContact = findViewById(R.id.editTextText10);
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            etContact.setText(phoneNumber);
        } else {
            etContact.setHint("Contact No.");
        }
        etContact.setEnabled(true);

        etDOB = findViewById(R.id.editTextText13);
        etDOB.setOnClickListener(v -> showDatePicker());



        etDOB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2; // Right-side drawable index

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etDOB.getRight() - etDOB.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        showDatePicker();
                        v.performClick(); // Fixes warning
                        return true;
                    }
                }
                return false;
            }
        });

        Button btnSave = findViewById(R.id.button12);

        btnSave.setOnClickListener(v -> {
            Intent intent = new Intent(TeachersBasicInfo.this, TeachersInfo.class);
            startActivity(intent);
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);  // Set the chosen image as profile picture
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDOB.setText(formattedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
}