package com.example.user.crm_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity_CRM_Project extends AppCompatActivity implements View.OnClickListener {

    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("myStart", "Main Activity");

        //***Button***
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(this);
        Intent intent = new Intent(MainActivity_CRM_Project.this, Activity_Add_Customer.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        //Intent intent = new Intent(MainActivity_CRM_Project.this, Activity_Search_Customer.class);
        //startActivity(intent);
    }
}
