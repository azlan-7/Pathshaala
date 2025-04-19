package com.example.loginpage;

import static com.example.loginpage.MySqliteDatabase.DatabaseHelper.getConnection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.loginpage.models.UserDetailsClass;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;
import com.example.loginpage.models.VirtualClassClass;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SampleGoLiveZegoStudent extends AppCompatActivity {

    private Button goLiveBtn;
    public EditText liveClassIdInput, yourNameInput;

    public SharedPreferences sharedPreferences;
    private UserDetailsClass user;

    String liveID, name, userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sample_go_live_zego_student);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("UserId", 0); // Fetch user ID from shared preferences

        DatabaseHelper.fetchLatestLiveId(this, userId, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (!result.isEmpty()) {
                    String liveId = result.get(0).get("LiveId");
                    if (liveId != null) {
                        liveClassIdInput.setText(liveId);
                        Log.d("SampleGoLiveZego", "Fetched LiveId: " + liveId);
                    } else {
                        Log.d("SampleGoLiveZego", "No LiveId found");
                    }
                }
            }
        });


        Log.d("SampleGoLiveZego", "SampleGoLiveZegoStudent activity started.");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("FIRST_NAME", "User"); // Default "User"

        goLiveBtn = findViewById(R.id.goLiveBtn);
        liveClassIdInput = findViewById(R.id.liveClassIdInput);
        yourNameInput = findViewById(R.id.yourClassNameInput);

        yourNameInput.setText(firstName);


        liveClassIdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                liveID = liveClassIdInput.getText().toString();
                Log.d("SampleGoLiveZego", "Live ID: " + liveID);
                if(liveID.length() == 0){
                    goLiveBtn.setText("Start new live");
                } else {
                    goLiveBtn.setText("Join Live");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        goLiveBtn.setOnClickListener(v -> {
            name = yourNameInput.getText().toString();
            if(name.isEmpty()){
                yourNameInput.setError("Please enter your name");
                yourNameInput.requestFocus();
                return;
            }
            liveID = liveClassIdInput.getText().toString();
            if(liveID.length() > 0 && liveID.length() != 5){
                liveClassIdInput.setError("Please enter a valid LIVE ID");
                liveClassIdInput.requestFocus();
                return;
            }

            startMeeting();
        });
    }

    void startMeeting() {
        int userId = sharedPreferences.getInt("UserId", 0);

        DatabaseHelper.fetchLatestLiveId(this, userId, new DatabaseHelper.QueryResultListener() {
            @Override
            public void onQueryResult(List<Map<String, String>> result) {
                if (!result.isEmpty()) {
                    String liveId = result.get(0).get("LiveId");
                    if (liveId != null) {
                        liveID = liveId;
                        Log.d("SampleGoLiveZego", "Fetched LiveId: " + liveId);
                    }
                }

                proceedToMeeting();
            }
        });
    }

    void proceedToMeeting() {
        boolean isHost = liveID.length() != 5;
        userID = UUID.randomUUID().toString();
        Intent intent = new Intent(this, SampleLiveZego.class);
        intent.putExtra("user_id", userID);
        intent.putExtra("name", name);
        intent.putExtra("live_id", liveID);
        intent.putExtra("host", isHost);
        startActivity(intent);
    }
}