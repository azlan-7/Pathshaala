package com.example.loginpage;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class Extracurriculars extends AppCompatActivity {

    private LinearLayout hobbiesContainer, activitiesContainer;
    private ImageView addHobbyButton, addActivityButton;
    private int hobbyCount = 1, activityCount = 1;  // Default EditTexts are already present
    private final int MAX_ENTRIES = 3; // Max limit (1 default + 2 more)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_extracurriculars);

        hobbiesContainer = findViewById(R.id.hobbiesContainer);
        activitiesContainer = findViewById(R.id.activitiesContainer);
        addHobbyButton = findViewById(R.id.imageView133);
        addActivityButton = findViewById(R.id.imageView134);

        addHobbyButton.setOnClickListener(v -> {
            if (hobbyCount < MAX_ENTRIES) {
                addEditText(hobbiesContainer, "Enter Hobby");
                hobbyCount++;
            } else {
                Toast.makeText(this, "Maximum 3 hobbies allowed!", Toast.LENGTH_SHORT).show();
                addHobbyButton.setEnabled(false);
            }
        });

        addActivityButton.setOnClickListener(v -> {
            if (activityCount < MAX_ENTRIES) {
                addEditText(activitiesContainer, "Enter Activity");
                activityCount++;
            } else {
                Toast.makeText(this, "Maximum 3 activities allowed!", Toast.LENGTH_SHORT).show();
                addActivityButton.setEnabled(false);
            }
        });
    }

    private void addEditText(LinearLayout container, String hint) {
        EditText editText = new EditText(this);

        // Set fixed width and height
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                300,  // Fixed width
                70    // Fixed height
        );
        params.gravity = Gravity.CENTER;  // Center the EditText
        params.setMargins(0, 12, 0, 12);  // Add spacing between EditTexts
        editText.setLayoutParams(params);

        // Set properties
        editText.setHint(hint);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setBackgroundResource(R.drawable.cornered_rectangle);
        editText.setGravity(Gravity.CENTER);
        editText.setPadding(12, 12, 12, 12);
        editText.setTextSize(16);

        // Set delete button inside EditText
        Drawable deleteIcon = ContextCompat.getDrawable(this, R.drawable.delete_24dp_0f4d73_fill0_wght400_grad0_opsz24);
        if (deleteIcon != null) {
            deleteIcon.setBounds(0, 0, 70, 70);  // Proper delete icon size
        }
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, deleteIcon, null);

        // Delete functionality when clicked
        editText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    container.removeView(editText);
                    if (container == hobbiesContainer) hobbyCount--;
                    else activityCount--;
                    return true;
                }
            }
            return false;
        });

        container.addView(editText);
    }


}
