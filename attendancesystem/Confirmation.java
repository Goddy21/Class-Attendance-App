package com.example.attendancesystem;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Confirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        final TextView lec = (TextView) findViewById(R.id.lecturer);
        final TextView code = (TextView) findViewById(R.id.code);
        final TextView unit = (TextView) findViewById(R.id.title);

        /*String tutor = getIntent().getStringExtra("Tutor");
        String unit_code = getIntent().getStringExtra("Code");
        String unit_title = getIntent().getStringExtra("Title");

        lec.setText(tutor);
        code.setText(unit_code);
        unit.setText(unit_title);*/

    }
}