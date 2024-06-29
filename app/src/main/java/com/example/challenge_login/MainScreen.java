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
    ImageView IVHistory, IVProfile, IVPic;
    DatabaseReference roomsRef;
    DatabaseReference roomsRef1;
    DatabaseReference roomsRef2;




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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference roomsRef = database.getReference("Accounts");
        DatabaseReference roomsRef1 = roomsRef.child(name);
        //Tests

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

        private void getUri(){
        roomsRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String ImageUriString = snapshot.toString();
                    Uri ImageUri = Uri.parse(ImageUriString);
                    IVPic.setImageURI(ImageUri);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        }
    }
