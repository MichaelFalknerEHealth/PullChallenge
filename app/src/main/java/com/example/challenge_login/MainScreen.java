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

/**
 * Diese Klasse verwaltet die Hauptbildschirmaktivität nach dem Anmelden.
 * Es zeigt den Benutzernamen, das Profilbild an und bietet Zugriff auf verschiedene Funktionen wie Match-Start, Profilanzeige und Historie.
 */
public class MainScreen extends AppCompatActivity {

    TextView TVname;
    Button pull_up;
    ImageView IVHistory, IVBTProfile, IVImage;
    DatabaseReference roomsRef;
    String name;

    /**
     * Initialisiert die Hauptbildschirmaktivität und lädt den Benutzernamen sowie das Profilbild.
     *
     * @param savedInstanceState Der gespeicherte Zustand der Aktivität
     */
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


        //Zum Profilbild laden
        DatabaseReference ImageUriRef = roomsRef.child("Accounts").child(name).child("ImageUri");
        ImageUriRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String ImageUriString = snapshot.getValue(String.class);
                    if (ImageUriString != null && !ImageUriString.isEmpty()) {
                        Uri ImageUri = Uri.parse(ImageUriString);
                        IVImage.setImageURI(ImageUri);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        //Name anzeigen
        TVname.setText(name+ "!");

        pull_up = findViewById(R.id.pull_up);
        //Button zum Match starten
        pull_up.setOnClickListener(new View.OnClickListener() {
            /**
             * Implementiert die Aktion, die beim Klicken auf den "Match starten"-Button ausgeführt wird.
             * Startet eine neue Aktivität, um das Spiel "ChooserAndCounter" zu beginnen.
             *
             * @param v Die Ansicht, die das Ereignis ausgelöst hat (in diesem Fall der Button "pull_up")
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooserAndCounter.class);
                startActivity(intent.putExtra("name",name));
            }
        });

        //Button zum Profil anzeigen
        IVBTProfile.setOnClickListener(new View.OnClickListener() {
            /**
             * Implementiert die Aktion, die beim Klicken auf das Profilbild ausgeführt wird.
             * Startet eine neue Aktivität, um das Profil des Benutzers anzuzeigen.
             *
             * @param v Die Ansicht, die das Ereignis ausgelöst hat (in diesem Fall das Profilbild "IVBTProfile")
             */
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),Profile.class);
                startActivity(intent1.putExtra("name",name));
            }
        });

        //Button um die History anzuzeigen
        IVHistory.setOnClickListener(new View.OnClickListener() {
            /**
             * Implementiert die Aktion, die beim Klicken auf das Historie-Bild ausgeführt wird.
             * Startet eine neue Aktivität, um die Spielhistorie des Benutzers anzuzeigen.
             *
             * @param v Die Ansicht, die das Ereignis ausgelöst hat (in diesem Fall das Historie-Bild "IVHistory")
             */
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(),History.class);
                startActivity(intent2.putExtra("name",name));
            }
        });
        }

    }
