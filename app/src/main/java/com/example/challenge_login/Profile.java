package com.example.challenge_login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile extends AppCompatActivity {
    UserDatabase userDB;
    private EditText ETPWold, ETPWnew;


    private Button BTBack, BTChangePW, BTPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        TextView TVName = findViewById(R.id.TVName);
        String username = getIntent().getStringExtra("name");
        TVName.setText(username + "?");

        Button BTBack = findViewById(R.id.BTBack);
        Button BTChangePW = findViewById(R.id.BTChangePW);
        Button BTPicture = findViewById(R.id.BTPicture);
        EditText ETPWold = findViewById(R.id.ETPWold);
        EditText ETPWnew = findViewById(R.id.ETPWnew);
        userDB = UserDatabase.getUserDatabase(getApplicationContext());




        BTBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        BTChangePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
        BTChangePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PWold = ETPWold.getText().toString();
                String PWnew = ETPWnew.getText().toString();
                if(validatePassword(PWnew)) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            User user = userDB.userDAO().getUserByUsername(username);
                            if(user.getPassword().equals(PWold)){
                            user.setPassword(PWnew);
                            userDB.userDAO().updateUser(user);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Profile.this, "Password updated",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Profile.this, "old password is incorrect",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                            }
                        }
                    }).start();
                }else{
                    if(!validatePassword(PWnew)){
                        Toast.makeText(Profile.this, "password has to contain at least 8 figures, upper and lower case, numbers and special characters",
                                Toast.LENGTH_LONG).show();
                    }

                    if(PWold.equals(PWnew)){
                        Toast.makeText(Profile.this, "new password must not match with the old one",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private Boolean validatePassword(String password){
        //8 figures, upper and lower case,numbers, special character
        Pattern regex = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
        Matcher m = regex.matcher(password);
        return (m.matches());
    }
    }





