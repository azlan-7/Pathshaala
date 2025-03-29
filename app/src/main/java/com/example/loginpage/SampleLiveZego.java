package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingConfig;
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingFragment;

public class SampleLiveZego extends AppCompatActivity {
    String userID, name, liveID;
    boolean isHost;

    TextView liveIDText;
    ImageView shareBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sample_live_zego);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        liveIDText = findViewById(R.id.liveIDText);
        shareBtn = findViewById(R.id.shareBtn);

        userID = getIntent().getStringExtra("user_id");
        name = getIntent().getStringExtra("name");
        liveID = getIntent().getStringExtra("live_id");
        isHost = getIntent().getBooleanExtra("host", false);

        liveIDText.setText(liveID);

        AddFragment();

        shareBtn.setOnClickListener(v->{
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT,  "Join the live class in the Pathshaala app.\n Live ID: "+liveID);
            startActivity(sendIntent.createChooser(sendIntent, "Share via"));
        });
    }

    void AddFragment(){
        ZegoUIKitPrebuiltLiveStreamingConfig config;
        if(isHost){
            config = ZegoUIKitPrebuiltLiveStreamingConfig.host();
        }else{
            config = ZegoUIKitPrebuiltLiveStreamingConfig.audience();
        }

        ZegoUIKitPrebuiltLiveStreamingFragment fragment = ZegoUIKitPrebuiltLiveStreamingFragment.newInstance(
                AppConstants.appId, AppConstants.appSign, userID, name, liveID, config);
        getSupportFragmentManager().beginTransaction().replace(R.id.liveFragmentContainer, fragment).commitNow();
    }
}