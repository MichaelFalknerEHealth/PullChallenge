package com.example.challenge_login;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 1;
    ActivityResultLauncher<Uri> takePictureLauncher;
    Uri ImageUri;
    DatabaseReference roomsRef1;
    private UserDatabase userDB;
    private ImageView IVImage;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        TextView TVName = findViewById(R.id.TVName);
        username = getIntent().getStringExtra("name");
        TVName.setText(username + "?");

        Button BTBack = findViewById(R.id.BTBack);
        Button BTChangePW = findViewById(R.id.BTChangePW);
        Button BTPicture = findViewById(R.id.BTPicture);
        EditText ETPWold = findViewById(R.id.ETPWold);
        EditText ETPWnew = findViewById(R.id.ETPWnew);
        IVImage = findViewById(R.id.IVImage);
        userDB = UserDatabase.getUserDatabase(getApplicationContext());
        roomsRef1 = FirebaseDatabase.getInstance().getReference();



        registerPictureLauncher();

        //Bild laden aus Firebase
        ImageUri = createUri();
        DatabaseReference ImageUriRef = roomsRef1.child("Accounts").child(username).child("ImageUri");
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

        //Button zum zurückgehen
        BTBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Button zum Bild auswählen
        BTPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDecisionDialog();
            }
        });

        //Button zum Passwort wächseln
        BTChangePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PWold = ETPWold.getText().toString();
                String PWnew = ETPWnew.getText().toString();
                if (validatePassword(PWnew)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            User user = userDB.userDAO().getUserByUsername(username);
                            if (user.getPassword().equals(PWold)) {
                                user.setPassword(PWnew);
                                userDB.userDAO().updateUser(user);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Profile.this, getString(R.string.update), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Profile.this, getString(R.string.wrong_old), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                } else {
                    if (!validatePassword(PWnew)) {
                        Toast.makeText(Profile.this, getString(R.string.req_pass), Toast.LENGTH_LONG).show();
                    }

                    if (PWold.equals(PWnew)) {
                        Toast.makeText(Profile.this, getString(R.string.req_same), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    //Methode um die Richtigkeit des Passworts zu prüfen
    private Boolean validatePassword(String password) {
        Pattern regex = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
        Matcher m = regex.matcher(password);
        return m.matches();
    }

    //Für die Auswahl des Bildes
    public void showDecisionDialog() {
        AlertDialog.Builder decisionDialBuilder = new AlertDialog.Builder(this);
        decisionDialBuilder.setTitle(getString(R.string.picture_choose));
        decisionDialBuilder.setPositiveButton(getString(R.string.open_cam), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkCameraPermissionAndOpenCamera();

            }
        });
        decisionDialBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        decisionDialBuilder.create().show();
    }

    //Methode um eine Uri zu erstellen
    private Uri createUri(){
        File imageFile = new File(getApplicationContext().getFilesDir(),"camera_photo.jpg");
        return FileProvider.getUriForFile(getApplicationContext(),"com.example.challenge_login.fileprovider",imageFile);
    }

    //RegisterPictureLauncher
    private void registerPictureLauncher(){
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean o) {
                        try{
                            if(o){
                                IVImage.setImageURI(null);
                                IVImage.setImageURI(ImageUri);
                                roomsRef1.child("Accounts").child(username).child("ImageUri").setValue(ImageUri.toString());

                            }
                        }catch (Exception exception){
                            exception.getStackTrace();
                        }
                    }
                }
        );
    }

    //Kamera Rechte
    private void checkCameraPermissionAndOpenCamera(){
        if (ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Profile.this,
                    new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }else {
            takePictureLauncher.launch(ImageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_CODE){
            if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                takePictureLauncher.launch(ImageUri);
            }else {
                Toast.makeText(this,getString(R.string.cam_perm), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
