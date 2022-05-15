package com.example.firebasetest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminActivity extends AppCompatActivity {

    private ImageView profileImage;
    private Button submit;
   // private EditText userName, userAddress, userPhone, userWebsite;
    private FirebaseFirestore fireStore;
    private String user_id;
    private Bitmap compressedImageBitmap;
    private StorageReference storageRef;
    private ProgressBar progressBar;
    private CircleImageView circleImageView;
    private EditText mProfileName, mProfileAddress, mProfilePhone, mProfileWebsite;
    private Button mSaveBtn;
    private FirebaseAuth auth;
    private Uri mImageUri = null;

    //private Uri mImageUri;
    private boolean isPhotoSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        storageRef = FirebaseStorage.getInstance().getReference();
        fireStore = FirebaseFirestore.getInstance();
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();



        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        circleImageView = findViewById(R.id.circleImageView);
        mProfileName = findViewById(R.id.profile_name);
        mProfileAddress = findViewById(R.id.profile_address);
        mProfilePhone = findViewById(R.id.profile_phone);
        mProfileWebsite = findViewById(R.id.profile_website);
        mSaveBtn = findViewById(R.id.save_btn);

        auth = FirebaseAuth.getInstance();

        if(mImageUri !=null) {

            fireStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String name = task.getResult().getString("name");
                            String address = task.getResult().getString("address");
                            String phone = task.getResult().getString("phone");
                            String website = task.getResult().getString("website");
                            String imageUrl = task.getResult().getString("image");
                            mProfileName.setText(name);
                            mProfileAddress.setText(address);
                            mProfilePhone.setText(phone);
                            mProfileWebsite.setText(website);
                            mImageUri = Uri.parse(imageUrl);

                            Glide.with(AdminActivity.this).load(imageUrl).into(circleImageView);
                        }
                    }
                }
            });
        }



        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String name = mProfileName.getText().toString();
                String address = mProfileAddress.getText().toString();
                String phone = mProfilePhone.getText().toString();
                String website = mProfileWebsite.getText().toString();
                StorageReference imageRef = storageRef.child("Profile_pics").child(user_id + ".jpg");
                if (isPhotoSelected) {
                    if (!name.isEmpty() && mImageUri != null) {
                        imageRef.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            saveToFireStore(task, name, address, phone, website, uri);
                                        }
                                    });

                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(AdminActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(AdminActivity.this, "Please Select picture and write your name", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    saveToFireStore(null , name , address, phone, website, mImageUri);
                }
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if (ContextCompat.checkSelfPermission(AdminActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(AdminActivity.this , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} , 1);
                    }else{
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(AdminActivity.this);
                    }
                }
            }
        });
    }

    private void saveToFireStore(Task<UploadTask.TaskSnapshot> task, String name, String address, String phone, String website, Uri downloadUri) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("address", address);
        map.put("phone", phone);
        map.put("website", website);
        map.put("image", downloadUri.toString());
        fireStore.collection("Users").document(user_id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(AdminActivity.this, "Profile Settings Saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminActivity.this, companyHome.class));
                    finish();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(AdminActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                mImageUri = result.getUri();
                circleImageView.setImageURI(mImageUri);

                isPhotoSelected = true;
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


    }

}




