package com.example.loginpage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loginpage.NextGWhatsAppService;

public class BasicTestPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_test_page);

        Button sendMessageButton = findViewById(R.id.sendMessageBtn);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call API when button is clicked
                NextGWhatsAppService.sendWhatsAppMessage("919876543210", "John Doe");
            }
        });
    }
}