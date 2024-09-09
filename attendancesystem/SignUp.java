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

public class SignUp extends AppCompatActivity {
    ProgressBar progressBar;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://classattendancedb-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText username = findViewById(R.id.editTextUsername);
        final EditText email = findViewById(R.id.editTextEmail);
        final EditText phone = findViewById(R.id.phone);
        final EditText password = findViewById(R.id.editTextPassword);
        final EditText confPassword = findViewById(R.id.editConfirmPassword);
        final EditText occupation = findViewById(R.id.occupation);
        progressBar = findViewById(R.id.progressbar);

        final Button register = findViewById(R.id.sign_up_btn);
        final TextView login = findViewById(R.id.login_back);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String usernameTxt = username.getText().toString();
                final String emailTxt = email.getText().toString();
                final String phoneTxt = phone.getText().toString();
                final String occupationTxT = occupation.getText().toString();
                final String passwordTxt = password.getText().toString();
                final String confirmPasswordTxt = confPassword.getText().toString();

                if(usernameTxt.isEmpty() || emailTxt.isEmpty() || phoneTxt.isEmpty() || occupationTxT.isEmpty() || passwordTxt.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUp.this, "Fill in all the fields!", Toast.LENGTH_SHORT).show();
                }
                else if (!passwordTxt.equals(confirmPasswordTxt)){
                    Toast.makeText(SignUp.this, "Passwords are not similar!", Toast.LENGTH_SHORT).show();
                }else if(!isValid(passwordTxt) || !isValid(confirmPasswordTxt)){
                    Toast.makeText(SignUp.this, "Password must contain at least 8 characters, having a letter, a digit and a number!", Toast.LENGTH_SHORT).show();
                }
                else {
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(phoneTxt)){
                                Toast.makeText(SignUp.this, "Phone number already registered!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                databaseReference.child("users").child(phoneTxt).child("username").setValue(usernameTxt);
                                databaseReference.child("users").child(phoneTxt).child("email").setValue(emailTxt);
                                databaseReference.child("users").child(phoneTxt).child("occupation").setValue(occupationTxT);
                                databaseReference.child("users").child(phoneTxt).child("password").setValue(passwordTxt);
                                Toast.makeText(SignUp.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                                if(occupationTxT.equals("lecturer") || occupationTxT.equals("Lecturer") || occupationTxT.equals("LECTURER")){
                                    Intent v = new Intent(getApplicationContext(), SignIn.class);
                                    v.putExtra("number", phoneTxt);
                                    v.putExtra("name", usernameTxt);
                                    startActivity(v);
                                    finish();
                                }else if (occupationTxT.equals("student") || occupationTxT.equals("Student") || occupationTxT.equals("STUDENT")){
                                    Intent t = new Intent(getApplicationContext(), MiddleInterFace.class);
                                    t.putExtra("number", phoneTxt);
                                    startActivity(t);
                                    finish();
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public static boolean isValid(String passworddhere){
        int f1=0,f2=0,f3=0;
        if(passworddhere.length() < 8){
            return false;
        }else{
            for(int p = 0; p < passworddhere.length(); p++){
                if(Character.isLetter(passworddhere.charAt(p))){
                    f1=1;
                }
            }
            for(int r = 0; r < passworddhere.length(); r++){
                if(Character.isDigit(passworddhere.charAt(r))){
                    f2=1;
                }
            }
            for(int s = 0; s < passworddhere.length(); s++){
                char c = passworddhere.charAt(s);
                if(c>=33&&c<=46 || c==64){
                    f3=1;
                }
            }
            if(f1==1 && f2==1 && f3==1)
                return true;
            return false;

        }
    }

}