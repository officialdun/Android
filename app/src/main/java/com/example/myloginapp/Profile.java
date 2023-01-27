package com.example.myloginapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class Profile extends AppCompatActivity {

    private Button btnLogOut,btnUploadImg;
    private ImageView imageProfile;
    private TextView userEmail;
    private Uri imagePath;

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnLogOut = findViewById(R.id.btnLogout);
        btnUploadImg = findViewById(R.id.btnUploadImg);
        imageProfile =findViewById(R.id.profile_image);

        userEmail = findViewById(R.id.userEmail);

        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        btnUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                //noinspection deprecation
                startActivityForResult(photoIntent,1);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1&& resultCode ==RESULT_OK &&data!=null){
         imagePath = data.getData();
        getImageInImageView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getImageInImageView() {

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        imageProfile.setImageBitmap(bitmap);
    }

    private void uploadImage(){


        if(imagePath!=null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString())
                    .putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            updateProfilePicture(task.getResult().toString());
                                            }
                                        else{
                                            Toast.makeText(Profile.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                Toast.makeText(Profile.this, "Upload complete", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Profile.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = 100.0* snapshot.getBytesTransferred()/ snapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded: "+(int)progress+"%");
                        }
                    });
        }
        else{
            Toast.makeText(this, "No image to upload", Toast.LENGTH_SHORT).show();
        }
}
    private void updateProfilePicture(String url){
        FirebaseDatabase.getInstance().getReference("user"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/profilePicture").setValue(url);

    }


}