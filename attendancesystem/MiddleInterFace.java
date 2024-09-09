package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MiddleInterFace extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://classattendancedb-default-rtdb.firebaseio.com/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle_inter_face);

        final EditText registration = (EditText) findViewById(R.id.registration);
        final Button submit = (Button) findViewById(R.id.regist_btn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String registrationTxt = registration.getText().toString();
                final String phoneTxt = getIntent().getStringExtra("number");

                if(registrationTxt.isEmpty()){
                    Toast.makeText(MiddleInterFace.this, "Enter you registration Number!", Toast.LENGTH_SHORT).show();
                }else{
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            databaseReference.child("users").child(phoneTxt).child("registration").setValue(registrationTxt);
                            //startActivity(new Intent(MiddleInterFace.this, SignIn.class));
                            Intent e = new Intent(getApplicationContext(), SignIn.class);
                            e.putExtra("registration", registrationTxt);
                            startActivity(e);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
}