package com.example.challenge_login;

import android.os.Bundle;
import android.util.Log;
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

    private String user = "user1";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        user = getIntent().getStringExtra("name");
        readDataFromFirebase();

    }
    private void readDataFromFirebase() {
        TextView TVHistory = findViewById(R.id.TVHistory2);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference roomsRef2 = database.getReference("History");

        roomsRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Iteriere durch alle Kinder der Referenz und gib ihre Werte aus
                    String key = snapshot.getKey(); // Schlüssel des Eintrags
                    Object value = snapshot.getValue(); // Wert des Eintrags

                    Log.d("FirebaseData", "Key: " + key + ", Value: " + value.toString());
                    String formattedText = String.format("MatchID: %s\nErgebnis: %s\n\n", key, value.toString());

                    TVHistory.append(formattedText);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Fehlerbehandlung
                Log.e("FirebaseData", "Error reading data", error.toException());
            }
        });
    }

}