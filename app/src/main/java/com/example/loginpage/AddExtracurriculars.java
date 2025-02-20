package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loginpage.models.ExtracurricularModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddExtracurriculars extends AppCompatActivity {

    private EditText etActivityName, etHobby;
    private Button btnSaveActivity;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_extracurriculars);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        etActivityName = findViewById(R.id.etActivityName);
        etHobby = findViewById(R.id.etCategory);
        btnSaveActivity = findViewById(R.id.btnSaveActivity);

        btnSaveActivity.setOnClickListener(v -> {
            String activityName = etActivityName.getText().toString().trim();
            String hobby = etHobby.getText().toString().trim();

            if (activityName.isEmpty() || hobby.isEmpty()) {
                Toast.makeText(AddExtracurriculars.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<ExtracurricularModel> extracurricularList = loadExtracurriculars();
            extracurricularList.add(new ExtracurricularModel(activityName, hobby));
            saveExtracurriculars(extracurricularList);

            Toast.makeText(AddExtracurriculars.this, "Activity Added!", Toast.LENGTH_SHORT).show();

            // Redirect to ViewExtracurriculars
            startActivity(new Intent(AddExtracurriculars.this, ViewExtracurriculars.class));
            finish();
        });
    }

    private void saveExtracurriculars(List<ExtracurricularModel> list) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString("EXTRACURRICULAR_LIST", gson.toJson(list));
        editor.apply();
    }

    private List<ExtracurricularModel> loadExtracurriculars() {
        String json = sharedPreferences.getString("EXTRACURRICULAR_LIST", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<ExtracurricularModel>>() {}.getType();
        return (json == null) ? new ArrayList<>() : gson.fromJson(json, type);
    }
}
