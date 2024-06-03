package com.example.challenge_login;

import static android.app.PendingIntent.getActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Admin extends AppCompatActivity {
    UserDatabase userDB;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        Button showData = findViewById(R.id.btnShowData);



        RoomDatabase.Callback myCallback = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }

            @Override
            public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
                super.onDestructiveMigration(db);
            }
        };

        userDB = Room.databaseBuilder(getApplicationContext(),UserDatabase.class,
                "user").addCallback(myCallback).build();

        showData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPersonListInBackground();
            }
        });
        }

    public void getPersonListInBackground(){

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                //background task
                userList = userDB.userDAO().getAllUser();


                //on finishing task
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder sb = new StringBuilder();
                        for (User u : userList){
                            sb.append(u.getUname()+ ": "+ u.getPassword());
                            sb.append("\n");
                        }
                        String finalData = sb.toString();
                        TextView database = findViewById(R.id.database);
                        database.setText(finalData);
                    }
                });
            }
        });
    }
    }
