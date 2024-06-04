package com.example.challenge_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainScreen extends AppCompatActivity {

    TextView TVname;
    Button pull_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mainscreen);
        TVname = findViewById(R.id.TVname);
        String name = getIntent().getStringExtra("name");
        TVname.setText(name);


        pull_up = findViewById(R.id.pull_up);
        pull_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooserAndCounter.class);
                //intent.putExtra("name",  name);
               //startActivity(intent);
                startActivity(intent.putExtra("name",name));
            }
        });




        }
    }
