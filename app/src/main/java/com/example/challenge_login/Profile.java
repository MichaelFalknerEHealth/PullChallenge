package com.example.challenge_login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile extends AppCompatActivity {
    private static final String TAG = "Profile";
    private DatabaseReference roomsRef;


    UserDatabase userDB;
    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    ActivityResultLauncher<Uri> takePictureLauncher;

    ActivityResultLauncher<Intent> resultLauncher;
    ImageView IVPic;
    Uri photoURI;


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
        IVPic = findViewById(R.id.IVPic);
        userDB = UserDatabase.getUserDatabase(getApplicationContext());

        registerResult();
        registerTakePictureResult();
        if (photoURI!=null) {
            IVPic.setImageURI(photoURI);
        }else{
            Toast.makeText(Profile.this,getString(R.string.profile_pic), Toast.LENGTH_SHORT);
        }



        BTBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        BTPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDecisionDialog();
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
                                    Toast.makeText(Profile.this, getString(R.string.update),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Profile.this, getString(R.string.wrong_old),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                            }
                        }
                    }).start();
                }else{
                    if(!validatePassword(PWnew)){
                        Toast.makeText(Profile.this, getString(R.string.req_pass),
                                Toast.LENGTH_LONG).show();
                    }

                    if(PWold.equals(PWnew)){
                        Toast.makeText(Profile.this, getString(R.string.req_same),
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

    public void showDecisionDialog(){
        AlertDialog.Builder decisionDialBuilder = new AlertDialog.Builder(this);
        decisionDialBuilder.setTitle(getString(R.string.picture_choose));
        decisionDialBuilder.setMessage(getString(R.string.picture_rec));
        decisionDialBuilder.setPositiveButton(getString(R.string.recording), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ActivityCompat.checkSelfPermission(Profile.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || !Environment.isExternalStorageManager()) {
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R) {
                        ActivityCompat.requestPermissions(Profile.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        Intent intent = new Intent ();
                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    }
                } else {
                    openCamera();
                }

            }
        });
        decisionDialBuilder.setNegativeButton(getString(R.string.gallery), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pickImage();
            }
        });
        decisionDialBuilder.setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        decisionDialBuilder.create().show();

    }
    private void pickImage(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }
    private void openCamera() {
        Intent open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        resultLauncher.launch(open_camera);
        //startActivity(open_camera);
        photoURI = createUri();
        if (photoURI!=null){
            takePictureLauncher.launch(photoURI);
        }else {
            Toast.makeText(this, "error creating URI", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerResult(){
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()==RESULT_OK && result.getData()!=null){
                            Uri imageUri = result.getData().getData();
                            IVPic.setImageURI(imageUri);
                            Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                            intent.putExtra("ImageUri", imageUri.toString());
                            String username = getIntent().getStringExtra("name");
                            intent.putExtra("name",username);
                            startActivity(intent);
                        }else{
                            Toast.makeText(Profile.this,getString(R.string.no_pic), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
    private void registerTakePictureResult() {
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            IVPic.setImageURI(photoURI);
                            Intent intent = new Intent(Profile.this, MainScreen.class);
                            intent.putExtra("ImageUri", photoURI.toString());
                            String username = getIntent().getStringExtra("name");
                            intent.putExtra("name", username);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Profile.this, getString(R.string.no_photo), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private Uri createUri(){
        try {
            File imageFile = new File(getApplicationContext().getExternalFilesDir(null), "external_files.jpg");
            if (!imageFile.exists()) {
                imageFile.createNewFile();
            }
            return FileProvider.getUriForFile(
                    getApplicationContext(),
                    "com.example.challenge_login.fileProvider",
                    imageFile
            );
        }catch (Exception e){
            Log.e(TAG, "Error creating file URI", e);
            return null;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, getString(R.string.camerea_perm), Toast.LENGTH_SHORT).show();
            }
        }
    }

}





