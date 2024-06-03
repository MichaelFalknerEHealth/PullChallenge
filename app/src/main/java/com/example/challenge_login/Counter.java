package com.example.challenge_login;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Counter extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isMovingUp = false;
    private boolean isMovingDown = false;
    private int pullUpCount = 0;
    private static final float THRESHOLD_UP = 10.0f; // Alte Methode zum Messen des Accelerometers
    private static final float THRESHOLD_DOWN = 8.0f; // Alte Methode zum Messen des Accelerometers
    private Handler handler = new Handler(); // Zum Zeitintervall stoppen und Ändern
    private Runnable checkPullUpRunnable;
    private Button startButton;
    private float gravitation;
    private boolean isGravitationSet = false;
    private TextView counter;
    private TextView momentan;
    private TextView movement;
    private TextView text_gravitation; //Zum Testen, ob der Sensor richtig wahrnimmt
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter);

        // Initialisierung des Buttons und des Click-Listener
        startButton = findViewById(R.id.startButton);
        counter = findViewById(R.id.counter);
        momentan = findViewById(R.id.momentan);
        movement = findViewById(R.id.movement);
        text_gravitation = findViewById(R.id.gravitation);


        // Initialisiere den SensorManager und den Beschleunigungssensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mediaPlayer = MediaPlayer.create(this, R.raw.start); //Soundfile nur als Platzhalter


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(false);//Button wird beim drücken deaktiviert

                // Starte den Sensor-Listener nach 5 Sekunden
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (accelerometer != null) {
                            sensorManager.registerListener(Counter.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                            if (mediaPlayer != null) {
                                mediaPlayer.start();}
                        }
                    }
                }, 5000); // 5 Sekunde Verzögerung

            }



        });



        // Der Runnable, der ausgeführt wird, wenn innerhalb von 3 Sekunden kein Klimmzug erkannt wird
        checkPullUpRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Counter.this, End_Screen.class);
                intent.putExtra("COUNTER",  pullUpCount);
                startActivity(intent);
                // Hier wird dann der Finish_Screen angezeigt, sobald kein Klimmzug mehr erkannt wurde
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(checkPullUpRunnable); // Stoppt den Handler, wenn die Aktivität pausiert wird


        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
            momentan.setText(zAxis + " " + getString(R.string.geschwindigkeit));


            if (zAxis > (gravitation-2)) {
                if (!isMovingUp) {
                    isMovingUp = true;
                    isMovingDown = false;
                    movement.setText(R.string.rauf); //Text nur für Kontrollzwecke
                }
            } else if (zAxis < (gravitation+2)) {
                if (!isMovingDown) {
                    isMovingDown = true;
                    if (isMovingUp) {
                        pullUpCount++;
                        isMovingUp = false;
                        movement.setText(R.string.runter); //Text nur für Kontrollzwecke
                        counter.setText(pullUpCount + " " + getString(R.string.pullup));
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
        // Nicht benötigt
    }



    private void saveSensorValues(float[] values) {
        // Nur eine Methode zum Testen
        gravitation = values[2];
        text_gravitation.setText("Sensor Wert" + " Z: " + values[2]);

    }
}
