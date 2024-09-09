package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MiddleLecInterface extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://classattendancedb-default-rtdb.firebaseio.com/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle_lec_interface);
        final TextInputLayout unit = (TextInputLayout) findViewById(R.id.unitInput);
        final TextInputLayout unitCode = (TextInputLayout) findViewById(R.id.unitCodeInput);
        final Button proceed = (Button) findViewById(R.id.proceed);
        String lec = getIntent().getStringExtra("lecturer");
        String phoneTxt = getIntent().getStringExtra("phone");

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String input = String.valueOf(unit.getEditText().getText());
               String inputCode = String.valueOf(unitCode.getEditText().getText());
                //Toast.makeText(MiddleLecInterface.this, input + ","+ inputCode, Toast.LENGTH_SHORT).show();
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReference.child("users").child(phoneTxt).child("Unit").setValue(input);
                        databaseReference.child("users").child(phoneTxt).child("UnitCode").setValue(inputCode);
                        databaseReference.child("users").child(phoneTxt).child("Lecturer").setValue(lec);
                        Intent p = new Intent(getApplicationContext(), LecturerDashboard.class);
                        //p.putExtra("unit", input);
                        //p.putExtra("unitCode", inputCode);
                        //p.putExtra("tutor", lec);
                        startActivity(p);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}