package com.example.challenge_login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainScreen extends AppCompatActivity {

    TextView TVname;
    Button pull_up;
    ImageView IVHistory, IVBTProfile, IVImage;
    DatabaseReference roomsRef;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mainscreen);

        IVHistory = findViewById(R.id.IVHistory);
        IVBTProfile = findViewById(R.id.IVBTProfile);
        IVImage = findViewById(R.id.IVImage);
        TVname = findViewById(R.id.TVname);
        name = getIntent().getStringExtra("name");

        roomsRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ImageUriRef = roomsRef.child("Accounts").child(name).child("ImageUri");
        ImageUriRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String ImageUriString = snapshot.getValue(String.class);
                    if (ImageUriString != null && !ImageUriString.isEmpty()) {
                        Log.d("MainScreen", "Image URI: " + ImageUriString);
                        // Verwenden Sie eine Bibliothek wie Picasso oder Glide zum Laden des Bildes
                        Uri ImageUri = Uri.parse(ImageUriString);
                        IVImage.setImageURI(ImageUri);
                    } else {
                        Log.d("MainScreen", "Image URI is null or empty");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        //Eigentlicher Wert
        TVname.setText(name+ "!");

        pull_up = findViewById(R.id.pull_up);
        pull_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooserAndCounter.class);
                startActivity(intent.putExtra("name",name));
            }
        });

        IVBTProfile.setOnClickListener(new View.OnClickListener() {
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
