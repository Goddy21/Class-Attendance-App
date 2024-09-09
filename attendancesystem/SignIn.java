package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    ProgressBar progressBar;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://classattendancedb-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final EditText phone = findViewById(R.id.phoneNum);
        final EditText password = findViewById(R.id.EditPassword);
        final Button login = findViewById(R.id.sign_in_btn);
        final TextView register = findViewById(R.id.no_account);
        progressBar = findViewById(R.id.progressbar);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String passwordTxt = password.getText().toString();
                final String phoneTxt = phone.getText().toString();
                final String registerTxt = getIntent().getStringExtra("registration");

                if(phoneTxt.isEmpty() || passwordTxt.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignIn.this, "Enter your mobile or password", Toast.LENGTH_SHORT).show();
                }
                else{
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(phoneTxt)){
                                final  String getPassword = snapshot.child(phoneTxt).child("password").getValue(String.class);
                                final  String getOccupation = snapshot.child(phoneTxt).child("occupation").getValue(String.class);
                                final  String getEmail = snapshot.child(phoneTxt).child("email").getValue(String.class);
                                final  String getUsername = snapshot.child(phoneTxt).child("username").getValue(String.class);
                                final  String getRegistration = snapshot.child(phoneTxt).child("registration").getValue(String.class);
                                String LecName = getIntent().getStringExtra("name");
                                if(getPassword.equals(passwordTxt)){
                                    if(getOccupation.equals("Student") || getOccupation.equals("student") || getOccupation.equals("STUDENT")){
                                        Toast.makeText(SignIn.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                                        //startActivity(new Intent(SignIn.this, ChatPlatForm.class));
                                        Intent intent = new Intent(getApplicationContext(), ChatPlatForm.class);
                                        intent.putExtra("phone", phoneTxt);
                                        intent.putExtra("Email", getEmail);
                                        intent.putExtra("Username", getUsername);
                                        intent.putExtra("register", getRegistration);
                                        startActivity(intent);
                                        finish();
                                    }else if(getOccupation.equals("Lecturer") || getOccupation.equals("lecturer") || getOccupation.equals("LECTURER")){
                                        Toast.makeText(SignIn.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                                        //startActivity(new Intent(SignIn.this, MiddleLecInterface.class));
                                        Intent n = new Intent(getApplicationContext(), MiddleLecInterface.class);
                                        n.putExtra("lecturer", getUsername);
                                        n.putExtra("phone", phoneTxt);
                                        startActivity(n);
                                        finish();
                                    }else {
                                        Toast.makeText(SignIn.this, "Enter correct occupation!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(SignIn.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(SignIn.this, "Invalid phone number!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });
    }
}