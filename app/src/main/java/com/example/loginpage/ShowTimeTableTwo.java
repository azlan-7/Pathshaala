package com.example.loginpage;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.R;

public class ShowTimeTableTwo extends AppCompatActivity {

    private TextView subjectTextView;
    private TextView gradeTextView;
    private TextView dayTextView;
    private TextView timeTextView;
    private CheckBox disableSliderCheckBox;

    private LinearLayout priceSliderLayout; // Declare it here
    private TextView priceValueTextView;
    private int maxPrice = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_time_table_two);


        // Batch No. Dropdown
        AutoCompleteTextView batchNoDropdown = findViewById(R.id.editTextText54);
        String[] batchNoOptions = {"1", "2", "3", "4", "5"};
        ArrayAdapter<String> batchNoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, batchNoOptions);
        batchNoDropdown.setAdapter(batchNoAdapter);
        batchNoDropdown.setOnClickListener(v -> batchNoDropdown.showDropDown()); // Show dropdown on click

        // No. of Students Dropdown
        AutoCompleteTextView studentCountDropdown = findViewById(R.id.editTextText55);
        String[] studentCountOptions = {"1", "10", "20", "50", "100", "150", "400"};
        ArrayAdapter<String> studentCountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, studentCountOptions);
        studentCountDropdown.setAdapter(studentCountAdapter);
        studentCountDropdown.setOnClickListener(v -> studentCountDropdown.showDropDown()); // Show dropdown on click


        // Initialize CheckBox and LinearLayout
        disableSliderCheckBox = findViewById(R.id.disableSliderCheckBox);
        priceSliderLayout = findViewById(R.id.priceSliderLayout); // Initialize it here!

        SeekBar priceSeekBar = findViewById(R.id.priceSeekBar);
        int progressColor = ContextCompat.getColor(this, R.color.blueGradientEnd);
        int backgroundColor = ContextCompat.getColor(this, R.color.gray);
        int thumbColor = ContextCompat.getColor(this, R.color.blue);
        priceValueTextView = findViewById(R.id.priceValueTextView);


        // Set initial state of the slider layout based on the checkbox
        priceSliderLayout.setEnabled(!disableSliderCheckBox.isChecked());
        priceSeekBar.setEnabled(!disableSliderCheckBox.isChecked());

        // Set listener for the CheckBox
        disableSliderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            priceSliderLayout.setEnabled(!isChecked);
            priceSeekBar.setEnabled(!isChecked);
            // Optionally, you can also change the visual appearance (e.g., alpha)
            float alpha = isChecked ? 0.5f : 1.0f;
            priceSliderLayout.setAlpha(alpha);
            priceSeekBar.setAlpha(alpha);
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            priceSeekBar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.blueGradientEnd));
            priceSeekBar.setProgressBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
            priceSeekBar.setThumbTintList(ContextCompat.getColorStateList(this, R.color.blue));
        } else {
            // For older versions, you might need to use PorterDuff color filters
            Drawable progressDrawable = priceSeekBar.getProgressDrawable();
            progressDrawable.setColorFilter(progressColor, PorterDuff.Mode.SRC_IN);

            Drawable backgroundDrawable = priceSeekBar.getBackground();
            backgroundDrawable.setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN);

            Drawable thumbDrawable = priceSeekBar.getThumb();
            thumbDrawable.setColorFilter(thumbColor, PorterDuff.Mode.SRC_IN);
        }

        // Set the initial display
        int initialProgress = priceSeekBar.getProgress();
        updatePriceTextView(initialProgress);

        priceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePriceTextView(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Handle when the user starts dragging the thumb
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Handle when the user stops dragging the thumb
            }

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize TextViews from your activity_show_time_table_two.xml
        subjectTextView = findViewById(R.id.textViewSubject);
        gradeTextView = findViewById(R.id.textViewGrade);
        dayTextView = findViewById(R.id.textViewDay);
        timeTextView = findViewById(R.id.textViewTime);

        // Get the intent that started this activity
        Intent intent = getIntent();

        // Check if there are extras
        if (intent.getExtras() != null) {
            String subject = intent.getStringExtra("subjectName");
            String grade = intent.getStringExtra("gradeName");
            String day = intent.getStringExtra("dayOfWeek");
            String time = intent.getStringExtra("timeSlot");

            // Display the retrieved data
            subjectTextView.setText("Subject: " + subject);
            gradeTextView.setText("Grade: " + grade);
            dayTextView.setText("Day: " + day);
            timeTextView.setText("Time: " + time);
        } else {
            // Handle the case where no data was passed (e.g., if the activity was started directly)
            subjectTextView.setText("No timetable data received.");
            gradeTextView.setText("");
            dayTextView.setText("");
            timeTextView.setText("");
        }
    }

    private void updatePriceTextView(int price) {
        priceValueTextView.setText("Price: Rs " + price + "/-");
    }
}