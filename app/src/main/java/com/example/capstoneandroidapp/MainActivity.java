package com.example.capstoneandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv_id = (TextView) findViewById(R.id.tv_id);
        TextView tv_pass = (TextView) findViewById(R.id.tv_pass);
        Button managementButton = (Button) findViewById(R.id.managementButton);


        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String userPass = intent.getStringExtra("userPass");
        String message = "Welcome, " + userID;


        tv_id.setText(userID);
        tv_pass.setText(userPass);

        if(!userID.equals("admin")){
            managementButton.setVisibility(View.GONE);
        }


    }
}