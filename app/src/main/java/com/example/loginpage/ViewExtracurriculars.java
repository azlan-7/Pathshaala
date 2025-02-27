package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginpage.adapters.ExtracurricularAdapter;
import com.example.loginpage.models.ExtracurricularModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewExtracurriculars extends AppCompatActivity implements ExtracurricularAdapter.OnDeleteClickListener {

    private RecyclerView recyclerView;
    private ExtracurricularAdapter adapter;
    private List<ExtracurricularModel> extracurricularList;
    private SharedPreferences sharedPreferences;
    private ImageView addExtracurricularButton;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_extracurriculars);

        recyclerView = findViewById(R.id.recyclerViewExtracurriculars);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addExtracurricularButton = findViewById(R.id.imageView135);
        continueButton = findViewById(R.id.button35); // Reference to Continue button

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        extracurricularList = loadExtracurriculars();

        adapter = new ExtracurricularAdapter(extracurricularList, this);
        recyclerView.setAdapter(adapter);

        addExtracurricularButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewExtracurriculars.this, AddExtracurriculars.class);
            startActivity(intent);
        });

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewExtracurriculars.this, StudentsInfo.class);
            startActivity(intent);
        });


        if (extracurricularList.isEmpty()) {
            Toast.makeText(this, "No extracurriculars added yet!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(int position) {
        extracurricularList.remove(position);
        saveExtracurriculars(extracurricularList);
        adapter.notifyItemRemoved(position);
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
