package com.example.loginpage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.adapters.MediaAdapter;

import java.util.Arrays;
import java.util.List;

public class AddPromotionalMedia extends AppCompatActivity {

    private ImageView backButton;  // Image to trigger video selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_promotional_media);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewMedia);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        List<Uri> mediaList = Arrays.asList(
                Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.teaching1),
                Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.teaching2),
                Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.teaching3),
                Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.samplevideo) // For video
        );


        MediaAdapter adapter = new MediaAdapter(this, mediaList);
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button saveButton = findViewById(R.id.button15);
        backButton = findViewById(R.id.imageView156);

        backButton.setOnClickListener(v -> finish());

        saveButton.setOnClickListener(v -> {
            Toast.makeText(AddPromotionalMedia.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddPromotionalMedia.this, TeachersInfoSubSection.class);
            startActivity(intent);
            finish(); // Close current activity
        });
    }

}