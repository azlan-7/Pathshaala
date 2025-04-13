package com.example.loginpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.VirtualClassClass;

import java.util.UUID;

public class SampleGoLiveZego extends AppCompatActivity {

    private Button goLiveBtn;
    public EditText liveClassIdInput, yourNameInput;

    public SharedPreferences sharedPreferences;

    String liveID, name, userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sample_go_live_zego);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);


        goLiveBtn = findViewById(R.id.goLiveBtn);
        liveClassIdInput = findViewById(R.id.liveClassIdInput);
        yourNameInput = findViewById(R.id.yourClassNameInput);

        yourNameInput.setText(sharedPreferences.getString("selfreferralcode", ""));

        liveClassIdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                liveID = liveClassIdInput.getText().toString();
                if(liveID.length() == 0){
                    goLiveBtn.setText("Start new live");
                }else{
                    goLiveBtn.setText("Join Live");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        goLiveBtn.setOnClickListener(v -> {
            liveID = liveClassIdInput.getText().toString();
            if(liveID.length() > 0 && liveID.length() != 5){
                liveClassIdInput.setError("Please enter a valid LIVE ID");
                liveClassIdInput.requestFocus();
                return;
            }
            startMeeting();
            insertLiveID(1);
        });
    }

    void startMeeting(){
        Log.i("LOG", "Starting Meeting");
        sharedPreferences.edit().putString("name", name).apply();

        boolean isHost = true;
        if(liveID.length() == 5){
            isHost = false;
        }else{
            liveID = generateLiveID();
        }

        userID = UUID.randomUUID().toString();
        Intent intent = new Intent(this, SampleLiveZego.class);
        intent.putExtra("user_id", userID);
        intent.putExtra("name", name);
        intent.putExtra("live_id", liveID);
        intent.putExtra("host", isHost);
        startActivity(intent);
    }

    String generateLiveID(){
        StringBuilder id = new StringBuilder();
        while(id.length() < 5){
            int random = (int)(Math.random() * 10);
            id.append(random);
        }
        return id.toString();
    }


    private void insertLiveID(int qryStatus) {
        VirtualClassClass virtualClass = new VirtualClassClass();
        virtualClass.setUserId(sharedPreferences.getInt("USER_ID",-1)); // Assuming user ID is not needed
        virtualClass.setSelfReferralCode(sharedPreferences.getString("selfreferralcode", "")); // No referral code needed
        virtualClass.setClassId(0); // No class ID needed
        virtualClass.setTimeTableId(0); // No timetable ID needed
        virtualClass.setLiveId(liveID); // Only storing the live ID
        virtualClass.setClassStartTime(null); // No start time needed

        DatabaseHelper.VirtualClassInsert(this, qryStatus, virtualClass, result -> {
            Log.i("DB", "Live ID Inserted: " + liveID);
        });
    }
}