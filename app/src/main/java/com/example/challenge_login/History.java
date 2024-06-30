package com.example.challenge_login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class History extends AppCompatActivity {

    private String user;
    private Button BTBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        user = getIntent().getStringExtra("name");
        readDataFromFirebase();


        Button BTBack = findViewById(R.id.BTBack);

        //Button zum zurückgehen
        BTBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

    }

    //Daten von Firebase bekommen
    private void readDataFromFirebase() {
        TextView TVHistory = findViewById(R.id.TVHistory2);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference roomsRef3 = database.getReference("History").child(user);

        roomsRef3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Iteriere durch alle Kinder der Referenz und gib ihre Werte aus
                    String key = snapshot.getKey(); // Schlüssel des Eintrags
                    Object value = snapshot.getValue(); // Wert des Eintrags

                    Log.d("FirebaseData", "Key: " + key + ", Value: " + value.toString());
                    String formattedText = String.format("MatchID: %s\nErgebnis: %s\n\n", key, value.toString());

                    TVHistory.append(formattedText);
                }
                } else {
                    TVHistory.setText(getString(R.string.no_data) + user);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Fehlerbehandlung, wird nicht gebraucht

            }
        });
    }

}