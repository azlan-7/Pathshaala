package com.example.loginpage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.loginpage.fragments.FilterDialogFragment;

public class SearchTeachersDashboard extends AppCompatActivity {

    private ImageButton whatsAppButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_teachers_dashboard);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button filterButton = findViewById(R.id.button36);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                FilterDialogFragment filterDialog = new FilterDialogFragment();
                filterDialog.show(fm, "filter_dialog");
            }
        });


        CardView cardView1 = findViewById(R.id.cardStudent1);
        cardView1.setCardBackgroundColor(Color.parseColor("#D3E2F1"));
        whatsAppButton = cardView1.findViewById(R.id.whatsappButton);
        whatsAppButton.setOnClickListener(v->{
            MoveToWhatsAppScreen();
        });

        CardView cardView2 = findViewById(R.id.cardStudent2);
        cardView2.setCardBackgroundColor(Color.parseColor("#D3E2F1"));
        whatsAppButton = cardView2.findViewById(R.id.whatsappButton);
        whatsAppButton.setOnClickListener(v->{
            MoveToWhatsAppScreen();
        });

        CardView cardView3 = findViewById(R.id.cardStudent3);
        cardView3.setCardBackgroundColor(Color.parseColor("#D3E2F1"));
        whatsAppButton = cardView3.findViewById(R.id.whatsappButton);
        whatsAppButton.setOnClickListener(v->{
            MoveToWhatsAppScreen();
        });

        CardView cardView4 = findViewById(R.id.cardStudent4);
        cardView4.setCardBackgroundColor(Color.parseColor("#D3E2F1"));
        whatsAppButton = cardView4.findViewById(R.id.whatsappButton);
        whatsAppButton.setOnClickListener(v->{
            MoveToWhatsAppScreen();
        });

        CardView cardView5 = findViewById(R.id.cardStudent5);
        cardView5.setCardBackgroundColor(Color.parseColor("#D3E2F1"));
        whatsAppButton = cardView5.findViewById(R.id.whatsappButton);
        whatsAppButton.setOnClickListener(v->{
            MoveToWhatsAppScreen();
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void MoveToWhatsAppScreen(){
        Intent intent = new Intent(this, WhatsAppScreen.class);
        startActivity(intent);
    }
}