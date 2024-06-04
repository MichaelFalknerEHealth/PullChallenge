package com.example.challenge_login;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    UserDatabase userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialising Buttons, TextViews, Intents
        setContentView(R.layout.activity_login);
        EditText ETUser = findViewById(R.id.etUser);
        EditText ETPass = findViewById(R.id.etPass);
        Button loginBtn = findViewById(R.id.loginBtn);
        TextView registerBtn = findViewById(R.id.TVregister1);
        Button adminBtn = findViewById(R.id.adminBtn);
        ImageButton IBvisibility = findViewById(R.id.IVvisible);
        Intent intent1 = new Intent(getApplicationContext(),RegisterClass.class);
        Intent intent2 = new Intent(getApplicationContext(),Admin.class);
        Intent intent3 = new Intent(getApplicationContext(),MainScreen.class);
        registerBtn.setPaintFlags(registerBtn.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message1");

        myRef.setValue("test1");


        //method make password in-/visible
        IBvisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    ETPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    IBvisibility.setImageResource(R.drawable.visibility_24px);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ETPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        IBvisibility.setImageResource(R.drawable.visibility_off_24px);
                    }
                }, 3000); // 3000 milliseconds = 3 seconds
            }
        });


        //login Button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = ETUser.getText().toString();
                String password = ETPass.getText().toString();

                //to check if all fields are filled
                if(uname.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"fill in all fields",Toast.LENGTH_LONG).show();
                }else {
                    //Perform Query
                    userDB = UserDatabase.getUserDatabase(getApplicationContext());
                    UserDAO userDAO = userDB.userDAO();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            User user = userDAO.login(uname,password);
                            if(user == null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"check password",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                String name = user.uname;
                                startActivity(intent3.putExtra("name",uname));
                            }
                        }
                    }).start();
                }
                }
                  });


        //starts intent on register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent1);
            }
        });

        //shows data in database, will be deleted in final outcome
        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent2);
            }
        });

    }
}