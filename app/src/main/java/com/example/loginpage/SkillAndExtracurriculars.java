package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SkillAndExtracurriculars extends AppCompatActivity {

    private LinearLayout container1, container2; // The container where new fields will be added

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_and_extracurriculars);

        // Find the container layout where new items will be added
        container1 = findViewById(R.id.linearLayout4);
        container2 = findViewById(R.id.linearLayout6);

        // Find the Plus button and set click listener
        ImageButton plusButton1 = findViewById(R.id.imageButton2);
        plusButton1.setOnClickListener(v -> addNewField(plusButton1));

        ImageButton plusButton2 = findViewById(R.id.imageButton4);
        plusButton2.setOnClickListener(v -> addNewField(plusButton2));


        // Find the Plus button and set click listener
        // This plus button will redirect to a new page where we will can add skills
        ImageButton plusButton3 = findViewById(R.id.imageButton6);
        plusButton3.setOnClickListener(v -> {
            Intent intent = new Intent(SkillAndExtracurriculars.this, AddSkillSection.class);
            startActivity(intent);
        });

    }

    // Method to dynamically add a new EditText and a Minus button
    private void addNewField(ImageButton buttonStatus) {
        // Create a new horizontal LinearLayout
        LinearLayout newFieldLayout = new LinearLayout(this);
        newFieldLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        newFieldLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Create an EditText
        EditText newEditText = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                200,  // Width in pixels (you may need to convert from dp)
                100   // Approximate height to match XML's match_parent
        );
        params.weight = 1; // Maintain weight distribution if needed
        params.setMargins(0, 16, 0, 0); // Add left margin (16dp padding before EditText)

        newEditText.setLayoutParams(params);
        newEditText.setHint("Other information");
        newEditText.setBackgroundResource(R.drawable.edittext_border);
        newEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT); // Match input type


        // Create a Minus button
        // Create a Minus button
        ImageButton minusButton = new ImageButton(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                100, // Width reduced to 40dp
                100  // Height reduced to 40dp
        );
        buttonParams.setMargins(15, 15, 15, 15); // Reduce margin if needed
        minusButton.setLayoutParams(buttonParams);
        minusButton.setContentDescription("Remove Hobby");
        minusButton.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        minusButton.setAdjustViewBounds(true);
        minusButton.setImageResource(R.drawable.minus_image); // Ensure this drawable exists
        minusButton.setBackground(null); // Remove background
        minusButton.setPadding(2, 2, 2, 2); // Reduce padding


        // Set click listener to remove the field
        if(buttonStatus == findViewById(R.id.imageButton2)){
            minusButton.setOnClickListener(v -> container1.removeView(newFieldLayout));
        }
        else{
            minusButton.setOnClickListener(v -> container2.removeView(newFieldLayout));
        }

        // Add EditText and Minus button to new layout
        newFieldLayout.addView(newEditText);
        newFieldLayout.addView(minusButton);

        // Add the new layout to the container
        if(buttonStatus == findViewById(R.id.imageButton2)) {
            container1.addView(newFieldLayout);
        }
        else{
            container2.addView(newFieldLayout);
        }
    }
}
