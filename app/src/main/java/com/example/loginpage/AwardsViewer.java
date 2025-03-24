package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.loginpage.R;

public class AwardsViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awards_viewer); // Ensure correct layout file

        // ✅ Find the ImageView
        ImageView awardImageView = findViewById(R.id.awardImageView);

        // ✅ Get file name from intent
        Intent intent = getIntent();
        String awardFileName = intent.getStringExtra("AWARD_FILE_NAME");

        if (awardFileName != null && !awardFileName.isEmpty()) {
            String imageUrl = "http://129.154.238.214/Pathshaala/uploads/" + awardFileName.trim();

            // ✅ Load image with Glide
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)  // Show this while loading
                    .error(android.R.drawable.ic_dialog_alert)        // Show this if loading fails
                    .into(awardImageView);
        } else {
            Toast.makeText(this, "No award image found.", Toast.LENGTH_SHORT).show();
        }
    }
}
