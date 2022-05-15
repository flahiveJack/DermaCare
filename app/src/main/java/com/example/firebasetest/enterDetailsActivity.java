package com.example.firebasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class enterDetailsActivity extends AppCompatActivity {

    Button next;
    EditText ans1, ans2;

    TextView quest1,quest2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_enter_details);

        ans1 = findViewById(R.id.answer1);
        ans2 = findViewById(R.id.answer2);
        next = findViewById(R.id.buttongoNext);

        quest1 = findViewById(R.id.question1);
        quest2 = findViewById(R.id.question2);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String a1 = ans1.getText().toString();
                String a2 = ans2.getText().toString();
                String q1 = quest1.getText().toString();
                String q2 = quest2.getText().toString();

                Intent intent = new Intent(enterDetailsActivity.this, MainActivity.class);
                intent.putExtra("firstAnswer",a1);
                intent.putExtra("secondAnswer",a2);
                intent.putExtra("firstQuestion",q1);
                intent.putExtra("secondQuestion",q2);

                startActivity(intent);


            }
        });










    }

}

