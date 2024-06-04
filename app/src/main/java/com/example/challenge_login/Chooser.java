package com.example.challenge_login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Chooser extends AppCompatActivity {

    private DatabaseReference roomsRef;
    private EditText roomCodeInput;
    private TextView scoreView;
    private Button createRoomButton, joinRoomButton, updateScoreButton;
    private boolean isPlayer1;
    String user = getIntent().getStringExtra("name");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        roomCodeInput = findViewById(R.id.roomCodeInput);
        scoreView = findViewById(R.id.scoreView);
        createRoomButton = findViewById(R.id.createRoomButton);
        joinRoomButton = findViewById(R.id.joinRoomButton);
       // updateScoreButton = findViewById(R.id.updateScoreButton);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        roomsRef = database.getReference("rooms");



        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomCode = roomCodeInput.getText().toString();
                createRoom(roomCode);
            }
        });

        joinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomCode = roomCodeInput.getText().toString();
                joinRoom(roomCode);
            }
        });

        updateScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example: update the score to 10 for the current player
                updateScore(roomCodeInput.getText().toString(), 10, isPlayer1);
            }
        });
    }

    private void createRoom(String roomCode) {
        String userId = user;
        DatabaseReference roomRef = roomsRef.child(roomCode);
        roomRef.child("player1").setValue(userId);
        isPlayer1 = true;
        monitorScores(roomCode);
    }

    private void joinRoom(String roomCode) {
        String userId = user;
        DatabaseReference roomRef = roomsRef.child(roomCode);

        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("player1") && !snapshot.hasChild("player2")) {
                    roomRef.child("player2").setValue(userId);
                    isPlayer1 = false;
                    monitorScores(roomCode);
                } else {
                    // Handle errors, like room does not exist or already has two players
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void updateScore(String roomCode, int score, boolean isPlayer1) {
        DatabaseReference roomRef = roomsRef.child(roomCode);
        String scoreKey = isPlayer1 ? "player1_score" : "player2_score";
        roomRef.child(scoreKey).setValue(score);
    }

    private void monitorScores(String roomCode) {
        DatabaseReference roomRef = roomsRef.child(roomCode);

        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer player1Score = snapshot.child("player1_score").getValue(Integer.class);
                    Integer player2Score = snapshot.child("player2_score").getValue(Integer.class);
                    scoreView.setText("Player 1: " + player1Score + "\nPlayer 2: " + player2Score);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}