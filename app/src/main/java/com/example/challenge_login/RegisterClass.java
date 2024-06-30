package com.example.challenge_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterClass extends AppCompatActivity {
    private DatabaseReference roomsRef;


    UserDatabase userDB;
    String UriFiller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialisiere EditTexts, Buttons, Intents
        setContentView(R.layout.activity_register);
        EditText ETUser = findViewById(R.id.etUser);
        EditText ETPass = findViewById(R.id.edPass);
        EditText ETPass2 = findViewById(R.id.etPass2);
        Button registerBtn = findViewById(R.id.registerBtn);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        roomsRef = database.getReference("Accounts");




        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //User Erstellung
                final User user = new User();
                String uname = ETUser.getText().toString();
                String password = ETPass.getText().toString();
                String password2 = ETPass2.getText().toString();
                UriFiller = "";
                DatabaseReference roomRef = roomsRef.child(uname);


                user.setUname(uname);
                user.setPassword(password);
                if(validateInput(user)&& password.equals(password2)&&validatePassword(password)){
                    userDB = UserDatabase.getUserDatabase(getApplicationContext());
                    final UserDAO userDAO = userDB.userDAO();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //Registriert User und fügt Usernamen zu Firebase hinzu
                            userDB.userDAO().addUser(user);
                            roomRef.child("Username").setValue(uname);
                            roomRef.child("Password").setValue(password);
                            roomRef.child("ImageUri").setValue(UriFiller);


                            startActivity(intent);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterClass.this, getString(R.string.user_reg_suc),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).start();
                }else{
                    //Prüft ob alle Felder befüllt wurden
                    if(!validateInput(user)) {
                        Toast.makeText(RegisterClass.this, getString(R.string.fields),
                                Toast.LENGTH_LONG).show();
                    }
                    //Prüft Passwort
                    if(!validatePassword(password)){
                        Toast.makeText(RegisterClass.this, getString(R.string.req_pass),
                                Toast.LENGTH_LONG).show();
                    }
                    //Prüft das wiederholte Passwort
                    if(!(password.equals(password2))){
                        Toast.makeText(RegisterClass.this, getString(R.string.rep_pas),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    //Passwortvalidierung durch Regex
    private Boolean validatePassword(String password){
        //8 Zeichen, Groß- und Kleinbuchstaben, Nummern, Sonderzeichen
        Pattern regex = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
        Matcher m = regex.matcher(password);
      return (m.matches());
        //return true;
    }
    //Update das Passwort durch input
    private Boolean validateInput(User user){
        if(user.getUname().isEmpty()||
        user.getPassword().isEmpty()){
            //wieder auf false setzen
            return false;
        }
        return true;
    }


}