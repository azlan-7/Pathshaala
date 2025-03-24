package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;



public class CertificateViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_viewer);

        ImageView certificateImageView = findViewById(R.id.certificateImageView);

        // Get the file name from the intent
        Intent intent = getIntent();
        String fileName = intent.getStringExtra("CERTIFICATE_FILE_NAME");

        if (fileName != null && !fileName.isEmpty()) {
            Log.d("CertificateViewer", "📄 Displaying Certificate: " + fileName);

            // Load the image (Replace API_URL with actual file storage path)
            String imageUrl = "http://129.154.238.214/Pathshaala/uploads/" + fileName;

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(certificateImageView);
        } else {
            Toast.makeText(this, "No certificate found.", Toast.LENGTH_SHORT).show();
        }
    }
}
