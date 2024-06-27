package com.example.challenge_login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainScreen extends AppCompatActivity {

    TextView TVname;
    Button pull_up;
    ImageView IVHistory, IVProfile, IVPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mainscreen);

        IVHistory = findViewById(R.id.IVHistory);
        IVProfile = findViewById(R.id.IVProfile);
        IVPic = findViewById(R.id.IVPic);
        TVname = findViewById(R.id.TVname);
        String name = getIntent().getStringExtra("name");
        String ImageUriString = getIntent().getStringExtra("ImageUri");
        if (ImageUriString!=null) {
            Uri ImageUri = Uri.parse(ImageUriString);
            IVPic.setImageURI(ImageUri);
        }else{
            Toast.makeText(MainScreen.this,"No profile picture found", Toast.LENGTH_SHORT);
        }
        TVname.setText(name+ "!");


        pull_up = findViewById(R.id.pull_up);
        pull_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooserAndCounter.class);
                startActivity(intent.putExtra("name",name));
            }
        });

        IVProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),Profile.class);
                startActivity(intent1.putExtra("name",name));
            }
        });
        IVHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(),History.class);
                startActivity(intent2.putExtra("name",name));
            }
        });



        }
    }
