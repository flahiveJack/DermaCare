package com.example.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class userHome extends AppCompatActivity implements View.OnClickListener {

    public CardView card1, card2, card3, card4;
    FloatingActionButton Fab;
    private Button scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        Fab = findViewById(R.id.fab);



        card1 = (CardView) findViewById(R.id.profileTV);
        card2 = (CardView) findViewById(R.id.results);
        card3 = (CardView) findViewById(R.id.posts);
        card4 = (CardView) findViewById(R.id.logout);

        card1.setOnClickListener(this);
        card2.setOnClickListener(this);
        card3.setOnClickListener(this);
        card4.setOnClickListener(this);

        Fab.setClickable(true);


        Fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userHome.this, TakePhoto.class);
                startActivity(intent);

            }

        });



       // scan.setOnClickListener();
    }

    @Override
    public void onClick(View view) {

        Intent i;

        switch (view.getId()) {
            case R.id.profileTV:
                i = new Intent(this, userHome.class);
                startActivity(i);
                break;

            case R.id.results:
                i = new Intent(this, ResultsActivity.class);
                startActivity(i);
                break;

            case R.id.posts:
                i = new Intent(this, PostsActivity.class);
                startActivity(i);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(userHome.this, Login.class);
                startActivity(intent);


        }








    }
}