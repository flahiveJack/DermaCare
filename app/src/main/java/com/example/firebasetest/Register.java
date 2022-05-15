package com.example.firebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText email,password;
    Button registerBtn,goToLogin, profileImage;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    CheckBox isCompanyBox;
    CheckBox isUserBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        //fDatabase = FirebaseDatabase.getInstance();


        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);

        registerBtn = findViewById(R.id.registerBtn);
        goToLogin = findViewById(R.id.gotoLogin);



        isCompanyBox = findViewById(R.id.isCompany);
        isUserBox = findViewById(R.id.isUser);







        //check boxes logics
        /*
        isStudentBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isTeacherBox.setChecked(false);
                }
            }
        });




        isTeacherBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isStudentBox.setChecked(false);
                }

            }
        });

         */

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkField(email);
                checkField(password);


                //checkbox validation
                /*
                if(!isTeacherBox.isChecked()) {
                    Toast.makeText(Register.this, "Select the account type", Toast.LENGTH_SHORT).show();
                    return;

                }
                if(!isStudentBox.isChecked()) {
                    Toast.makeText(Register.this, "Select the account type", Toast.LENGTH_SHORT).show();
                    return;

                }

                 */




                if(valid){
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            String user_id = fAuth.getCurrentUser().getUid();
                            Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
                            DocumentReference df = fStore.collection("Users").document(user.getUid());


                            Map<String,Object> userInfo = new HashMap<>();
                           // userInfo.put("FullName", fullName.getText().toString());
                            userInfo.put("UserEmail", email.getText().toString());
                            //userInfo.put("PhoneNumber", phone.getText().toString());
                            userInfo.put("user_id", user_id);
                            //SPECIFY IF THE USER IS ADMIN
                            //userInfo.put("isUser", "1");
                            if(isCompanyBox.isChecked()){
                                userInfo.put("isCompany", "1");
                            }else{
                                userInfo.put("isUser", "1");
                            }
                           /* if(isStudentBox.isChecked()){
                                userInfo.put("isUser", "1");
                            }

                            */



                            df.set(userInfo);



                            if(isCompanyBox.isChecked()){

                                Intent intent = new Intent(Register.this, AdminActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                
                                Intent intent = new Intent(Register.this, userHome.class);
                                startActivity(intent);
                                finish();

                            }

                            /*if(isStudentBox.isChecked()){
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }

                             */
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Failed to register account", Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }
}