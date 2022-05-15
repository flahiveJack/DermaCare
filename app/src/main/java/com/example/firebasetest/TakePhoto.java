package com.example.firebasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TakePhoto extends AppCompatActivity {
    private ImageView takePhoto;
    private TextView textView;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        takePhoto = findViewById(R.id.imagePhoto);
        textView = findViewById(R.id.textView7);
        next = findViewById(R.id.buttonNext);



        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TakePhoto.this, CameraActivity.class);

                startActivity(intent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TakePhoto.this, enterDetailsActivity.class);

                startActivity(intent);
            }
        });


    }
}