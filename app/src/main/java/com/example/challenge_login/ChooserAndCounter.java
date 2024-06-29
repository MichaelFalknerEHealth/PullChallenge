package com.example.challenge_login;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class ChooserAndCounter extends AppCompatActivity implements SensorEventListener {

    private DatabaseReference roomsRef;
    private DatabaseReference roomsRef2;
    private EditText roomCodeInput;
    private TextView scoreView;
    private TextView scoreViewPlayer2;
    private TextView TVCountdown;
    private TextView TVPlayerlist;
    private TextView TVPlayerlist2;
    private TextView displayCode;
    private Button createRoomButton, joinRoomButton, okButton, BTSave;
    private boolean isPlayer1;
    private String user = "user1";
    private boolean isGravitationSet = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isMovingUp = false;
    private boolean isMovingDown = false;
    public boolean onesave = true;
    private int pullUpCount;
    private Handler handler = new Handler();
    private Runnable checkPullUpRunnable;

    private MediaPlayer mediaPlayer;

    private Button BTBack;

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
        TVCountdown = findViewById(R.id.TVCountdown);

        scoreView = findViewById(R.id.scoreView);
        scoreViewPlayer2 = findViewById(R.id.scoreViewPlayer2);
        displayCode = findViewById(R.id.displaycode);
        createRoomButton = findViewById(R.id.createRoomButton);
        joinRoomButton = findViewById(R.id.joinRoomButton);
        okButton = findViewById(R.id.okButton);
        BTSave = findViewById(R.id.BTSave);
        BTBack = findViewById(R.id.BTBack);

        //Media Players bzw. Sounds


        //Test
        text_gravitation = findViewById(R.id.gravitation);
        counter = findViewById(R.id.counter);

        pullUpCount = 0;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        roomsRef = database.getReference("rooms");
        roomsRef2 = database.getReference("History");



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
                TVCountdown.setVisibility(View.VISIBLE); // Mache TVCountdown sichtbar

                // Countdown von 3 herunterzählen
                new CountDownTimer(3000, 1000) { // 3000 Millisekunden (3 Sekunden), 1000 Millisekunden (1 Sekunde Intervall)
                    int seconds = 3;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        TVCountdown.setText(String.valueOf(seconds));
                        seconds=seconds-1;
                        // Hier Sound abspielen
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);
                        mediaPlayer.start();
                    }

                    @Override
                    public void onFinish() {
                        // Countdown abgeschlossen, zeige "START!"
                        TVCountdown.setText(getString(R.string.start));
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.start);
                        mediaPlayer.start();
                        // Hier die Methode aufrufen, um den Counter zu starten
                        checkAndStartCounter(displayCode.getText().toString());
                    }
                }.start(); // Starte den Countdown
            }
        });
        BTSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResultsToHistory();
            }
        });

        BTBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();


            }
        });


        // Initialize sensor manager and accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        checkPullUpRunnable = new Runnable() {
            @Override
            public void run() {
                saveResultsToFirebase();
                // Hier Sound abspielen
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ende);
                mediaPlayer.start();

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
        //Normal
        //roomRef.child("Player 1").child(userId).child("player1_score").setValue(0);
        //Für Testzwecke ohne Android Handy
        roomRef.child("Player 1").child(userId).child("player1_score").setValue(6);
        TVPlayerlist.setText(getString(R.string.Player1) +": "+ userId);
        isPlayer1 = true;
        monitorScores(roomCode);
    }

    private void joinRoom(String roomCode) {
        String userId = user;

        TVPlayerlist2.setText(getString(R.string.Player2) +": "+ userId);

        DatabaseReference roomRef = roomsRef.child(roomCode);

        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("Player 1") && !snapshot.hasChild("Player 2")) {
                    //Normal
                    roomRef.child("Player 2").child(userId).child("player2_score").setValue(0);

                    //Test mit der Variable 7
                    //roomRef.child("Player 2").child(userId).child("player2_score").setValue(7);
                    isPlayer1 = false;
                    displayCode.setText(roomCode);
                    monitorScores(roomCode);


                } else {
                    // Error Handling, wenn der Raum nicht existiert oder schon zwei Spieler gejoint sind
                    Toast.makeText(getApplicationContext(), getString(R.string.error_room), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // FehlerHandling (wird nicht gebraucht)
            }
        });
    }

    private void checkAndStartCounter(String roomCode) {
        DatabaseReference roomRef = roomsRef.child(roomCode);
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("Player 1") && snapshot.hasChild("Player 2")) {
                    startCounting();
                } else {
                    //Fehlermeldung wenn nur ein Spieler im Raum ist und gestartet werden soll
                    Toast.makeText(getApplicationContext(), getString(R.string.error_room_one_player), Toast.LENGTH_LONG).show();
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
                    okButton.setEnabled(false);
                    joinRoomButton.setEnabled(false);
                    createRoomButton.setEnabled(false);
                }
            }
        }, 0);// 0 seconds delay

    }

    private void monitorScores(String roomCode) {
        DatabaseReference roomRef = roomsRef.child(roomCode);

        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer player1Score = null;
                    Integer player2Score = null;
                    int player1ScoreValue;
                    int player2ScoreValue;
                    if (snapshot.hasChild("Player 1")) {
                        DataSnapshot player1Snapshot = snapshot.child("Player 1");
                        for (DataSnapshot childSnapshot : player1Snapshot.getChildren()) {
                            String player1Name = childSnapshot.getKey();
                            if (childSnapshot.hasChild("player1_score")) {
                                player1Score = childSnapshot.child("player1_score").getValue(Integer.class);
                                player1ScoreValue = player1Score != null ? player1Score : 0;
                                scoreView.setText(getString(R.string.Player1) +  " (" + player1Name + "): " + player1ScoreValue);
                                TVPlayerlist.setText(getString(R.string.Player1) + ": " + player1Name);

                            }
                        }

                    }
                    if (snapshot.hasChild("Player 2")) {
                        DataSnapshot player2Snapshot = snapshot.child("Player 2");
                        for (DataSnapshot childSnapshot : player2Snapshot.getChildren()) {
                            String player2Name = childSnapshot.getKey();
                            if (childSnapshot.hasChild("player2_score")) {
                                player2Score = childSnapshot.child("player2_score").getValue(Integer.class);
                                player2ScoreValue = player2Score != null ? player2Score : 0;
                                scoreViewPlayer2.setText(getString(R.string.Player2) +  " (" + player2Name + "): " + player2ScoreValue);
                                TVPlayerlist2.setText(getString(R.string.Player2) + ": " + player2Name);
                            }
                        }
                    }

                    if (player1Score != null && player1Score != 0 && player2Score != null && player2Score != 0) {
                        if (isPlayer1){
                            if (player1Score>player2Score){
                                TVCountdown.setText(player1Score + "  -  " + player2Score + "\n" + getString(R.string.gewonnen) + " !");
                            }else if(player2Score>player1Score){
                                TVCountdown.setText(player1Score + "  -  " + player2Score + "\n" + getString(R.string.verloren) + " !");
                            }else{
                                TVCountdown.setText(player1Score + "  -  " + player2Score + "\n" + getString(R.string.unentschieden) + " !");
                            }

                        }else{
                            if (player1Score<player2Score){
                                TVCountdown.setText(player2Score + "  -  " + player1Score + "\n" + getString(R.string.gewonnen) + " !");
                            }else if(player2Score<player1Score){
                                TVCountdown.setText(player2Score + "  -  " + player1Score + "\n" + getString(R.string.verloren) + " !");
                            }else{
                                TVCountdown.setText(player2Score + "  -  " + player1Score + "\n" + getString(R.string.unentschieden) + " !");
                            }

                        }


                        // Ergebnisse in Firebase speichern, wenn beide Scores vorhanden sind
                        saveResultsToHistory();

                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.game_over);
                        mediaPlayer.start();
                        okButton.setEnabled(true);
                        joinRoomButton.setEnabled(true);
                        createRoomButton.setEnabled(true);
                    }
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
                //Ein Text für Überprüfungszwecke
                text_gravitation.setText(getString(R.string.gravitation) + " " + gravitation + " " + getString(R.string.geschwindigkeit));
            }


            float zAxis = event.values[1];
            if (zAxis > (gravitation - 1)) {
                if (!isMovingUp) {
                    isMovingUp = true;
                    isMovingDown = false;

                }
            } else if (zAxis < (gravitation + 1)) {
                if (!isMovingDown) {
                    isMovingDown = true;
                    if (isMovingUp) {
                        pullUpCount++;
                        isMovingUp = false;
                        TVCountdown.setText(pullUpCount + " " + getString(R.string.pullup));

                        // Timer zurücksetzen, wenn ein Klimmzug bzw. Pull Up erkannt wurde
                        handler.removeCallbacks(checkPullUpRunnable);
                        handler.postDelayed(checkPullUpRunnable, 3000); // 3 Sekunden Verzögerung
                    }
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
                roomRef.child("Player 1").child(user).child("player1_score").setValue(pullUpCount);
            }
        else {
            roomRef.child("Player 2").child(user).child("player2_score").setValue(pullUpCount);
        }
        sensorManager.unregisterListener(this);
    }

    private void saveResultsToHistory() {
        String roomCode = displayCode.getText().toString();
        DatabaseReference roomRef = roomsRef.child(roomCode);
        Integer player1Score = null;
        Integer player2Score = null;
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Integer player1Score = null;
                Integer player2Score = null;
                if (snapshot.exists()) {
                    // Erhalte die aktuellen Spielergebnisse
                    if (snapshot.hasChild("Player 1")) {
                        DataSnapshot player1Snapshot = snapshot.child("Player 1");
                        for (DataSnapshot childSnapshot : player1Snapshot.getChildren()) {
                            String playerName = childSnapshot.getKey();
                            player1Score = snapshot.child("Player 1").child(playerName).child("player1_score").getValue(Integer.class);
                        }

                    }
                    if (snapshot.hasChild("Player 2")) {
                        DataSnapshot player2Snapshot = snapshot.child("Player 2");
                        for (DataSnapshot childSnapshot : player2Snapshot.getChildren()) {
                            String player2Name = childSnapshot.getKey();
                            player2Score = snapshot.child("Player 2").child(player2Name).child("player2_score").getValue(Integer.class);
                        }

                    }



                    // Standardwerte, falls keine Ergebnisse gefunden wurden
                    int player1ScoreValue = player1Score != null ? player1Score : 0;
                    int player2ScoreValue = player2Score != null ? player2Score : 0;

                    // Speichere die aktuellen Spielergebnisse in der History
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference historyRef = database.getReference("History").child(user);

                    historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                int maxID = 0;
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    int ID = Integer.parseInt(child.getKey());
                                    if (ID > maxID) {
                                        maxID = ID;
                                    }
                                }
                                int newID = maxID + 1;
                                if (isPlayer1){
                                historyRef.child(String.valueOf(newID)).setValue(player1ScoreValue + " - " + player2ScoreValue);}
                                else{historyRef.child(String.valueOf(newID)).setValue(player2ScoreValue + " - " + player1ScoreValue);}
                            } else {
                                if(isPlayer1){
                                historyRef.child("1").setValue(player1ScoreValue + " - " + player2ScoreValue);}
                                else{historyRef.child("1").setValue(player2ScoreValue + " - " + player1ScoreValue);}
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Handle possible errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle possible errors
            }
        });

    }




}
