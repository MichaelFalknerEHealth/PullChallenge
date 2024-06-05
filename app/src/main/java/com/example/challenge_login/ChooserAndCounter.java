package com.example.challenge_login;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.Random;

public class ChooserAndCounter extends AppCompatActivity implements SensorEventListener {

    private DatabaseReference roomsRef;
    private EditText roomCodeInput;
    private TextView scoreView;
    private TextView TVPlayerlist;
    private TextView TVPlayerlist2;

    private TextView displayCode;
    private Button createRoomButton, joinRoomButton, okButton;
    private boolean isPlayer1;
    private String user = "user1";
    private boolean isGravitationSet = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isMovingUp = false;
    private boolean isMovingDown = false;
    private int pullUpCount = 0;
    private Handler handler = new Handler();
    private Runnable checkPullUpRunnable;

    //Tests

    private TextView text_gravitation;
    private float gravitation;

    private TextView counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser_and_counter);

        user = getIntent().getStringExtra("name");
        roomCodeInput = findViewById(R.id.roomCodeInput);
        TVPlayerlist = findViewById(R.id.TVPlayerlist);
        TVPlayerlist2 = findViewById(R.id.TVPlayerlist2);

        scoreView = findViewById(R.id.scoreView);
        displayCode = findViewById(R.id.displaycode);
        createRoomButton = findViewById(R.id.createRoomButton);
        joinRoomButton = findViewById(R.id.joinRoomButton);
        okButton = findViewById(R.id.okButton);

        //Test
        text_gravitation = findViewById(R.id.gravitation);
        counter = findViewById(R.id.counter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        roomsRef = database.getReference("rooms");

        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomCode = generateRoomCode();
                createRoom(roomCode);
                displayCode.setText(roomCode);
            }
        });

        joinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomCode = roomCodeInput.getText().toString();
                joinRoom(roomCode);
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndStartCounter(displayCode.getText().toString());
            }
        });

        // Initialize sensor manager and accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        checkPullUpRunnable = new Runnable() {
            @Override
            public void run() {
                saveResultsToFirebase();
                // You can also transition to another activity or screen here if needed
            }
        };




    }

    private String generateRoomCode() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String number = "1234567890";
        String comb = upper + lower + number;
        int len = 4;
        char[] roomCode = new char[len];
        Random r = new Random();
        for (int i = 0; i < len; i++) {
            roomCode[i] = comb.charAt(r.nextInt(comb.length()));
        }
        return new String(roomCode);
    }

    private void createRoom(String roomCode) {
        String userId = user;
        DatabaseReference roomRef = roomsRef.child(roomCode);
        roomRef.child("Player 1").setValue(userId);
        TVPlayerlist.setText("Player 1: " + userId);
        isPlayer1 = true;
        monitorScores(roomCode);
    }

    private void joinRoom(String roomCode) {
        String userId = user;
        TVPlayerlist.setText("Player 1: " + userId);
        TVPlayerlist2.setText("Player 2: " + userId);

        DatabaseReference roomRef = roomsRef.child(roomCode);

        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("Player 1") && !snapshot.hasChild("Player 2")) {
                    roomRef.child("Player 2").setValue(userId);
                    isPlayer1 = false;
                    displayCode.setText(roomCode);
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

    private void checkAndStartCounter(String roomCode) {
        DatabaseReference roomRef = roomsRef.child(roomCode);
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("player1") && snapshot.hasChild("player2")) {
                    startCounting();
                } else {
                    // Handle errors, like room does not have both players yet
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void startCounting() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accelerometer != null) {
                    sensorManager.registerListener(ChooserAndCounter.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        }, 5); // 5 seconds delay
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (!isGravitationSet) {
                gravitation = event.values[1];
                isGravitationSet = true;
                text_gravitation.setText(getString(R.string.gravitation) + " " + gravitation + " " + getString(R.string.geschwindigkeit));

            }


            float zAxis = event.values[1];
            if (zAxis > 10.0f && !isMovingUp) {
                isMovingUp = true;
                isMovingDown = false;
            } else if (zAxis < 8.0f && !isMovingDown) {
                isMovingDown = true;
                if (isMovingUp) {
                    pullUpCount++;
                    isMovingUp = false;
                    counter.setText(pullUpCount + " " + getString(R.string.pullup));
                    handler.removeCallbacks(checkPullUpRunnable);
                    handler.postDelayed(checkPullUpRunnable, 3000); // 3 seconds delay
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed
    }

    private void saveResultsToFirebase() {
        String roomCode = displayCode.getText().toString();
        DatabaseReference roomRef = roomsRef.child(roomCode);
        if (isPlayer1) {
            roomRef.child("player1_score").setValue(pullUpCount);
        } else {
            roomRef.child("player2_score").setValue(pullUpCount);
        }
        sensorManager.unregisterListener(this);
    }


}
