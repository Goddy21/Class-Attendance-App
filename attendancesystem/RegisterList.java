package com.example.attendancesystem;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegisterList extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://classattendancedb-default-rtdb.firebaseio.com/");

    ListView studentsList;

    public ArrayList<String> arrayList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_list);
        studentsList = (ListView) findViewById(R.id.attendancelist);
        final Button share = findViewById(R.id.share);
        //String admission = getIntent().getStringExtra("Admission");
        String phone = getIntent().getStringExtra("Phone");
        String email = getIntent().getStringExtra("Email");
        String username = getIntent().getStringExtra("Username");
        String unit = getIntent().getStringExtra("unit");
        String unitCode = getIntent().getStringExtra("unitCode");
        String lec = getIntent().getStringExtra("tutor");
        String registrationNumber = getIntent().getStringExtra("registrationNumber");
        String phoneTxt = getIntent().getStringExtra("lecturePhone");

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        assert phoneTxt != null;
                        databaseReference.child("users").child(phoneTxt).child("Student Name").push().setValue(username);
                        databaseReference.child("users").child(phoneTxt).child("Student Registration").push().setValue(registrationNumber);
                        Toast.makeText(RegisterList.this, "Records shared with lecturer successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        //lecturer.setText();
        //title.setText(values[2]);
        //code.setText(values[1]);
        String[] students = {"Name: "+username, "Phone: "+phone,"E-mail: "+email,"Registration: "+registrationNumber};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_view,R.id.list, students);
        studentsList.setAdapter(arrayAdapter);
        //Toast.makeText(RegisterList.this, lec +","+unit+","+unitCode, Toast.LENGTH_SHORT).show();
    }
}