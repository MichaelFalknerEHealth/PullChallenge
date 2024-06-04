package com.example.challenge_login;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class End_Screen extends AppCompatActivity {
    

Button bt_back;
private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_screen);

        int counter = getIntent().getIntExtra("COUNTER", 0);
        TextView counterText = findViewById(R.id.counter);
        counterText.setText(String.format(getString(R.string.endscore) + " %d " + getString(R.string.pullup), counter));
        bt_back=findViewById(R.id.bt_back);

        mediaPlayer = MediaPlayer.create(this, R.raw.ende); //Soundfile nur als Platzhalter

        if (mediaPlayer != null) {
            mediaPlayer.start();}


        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(End_Screen.this, MainScreen.class);
                startActivity(intent);
            }
        });

    }
}
