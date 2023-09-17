package com.example.loginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Myinfo extends AppCompatActivity {

    private TextView info_id,info_pw,info_add,info_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        Intent intent3 = getIntent();
        String userID = intent3.getStringExtra("userID");
        String userPassword = intent3.getStringExtra("userPassword");
        String userName = intent3.getStringExtra("userName");
        String userCall = intent3.getStringExtra("userCall");
        info_id=findViewById(R.id.info_id);
        info_pw=findViewById(R.id.info_pw);
        info_add=findViewById(R.id.info_add);
        info_phone=findViewById(R.id.info_phone);

        info_id.setText(userID);
        info_pw.setText(userPassword);
        info_add.setText(userName);
        info_phone.setText(userCall);
    }
}