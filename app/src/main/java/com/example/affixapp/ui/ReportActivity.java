package com.example.affixapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.affixapp.LogInActivity;
import com.example.affixapp.R;
import com.example.affixapp.ReportedUser;
import com.example.affixapp.ui.model.ActiveUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReportActivity extends AppCompatActivity {

    private Button btnNewUser;
    private  Button btnLogout;
    private Button btnReportedUser;
    private Button btnActiveUser;

    private Button btnReportRequests;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_report);

        //Initialize buttons and text view
        btnNewUser=findViewById(R.id.button2);
        btnActiveUser=findViewById(R.id.button3);
        btnReportedUser=findViewById(R.id.button1);
        btnReportRequests=findViewById(R.id.button7);
        btnLogout=findViewById(R.id.logout_btn);

        //Set click listeners for each button
        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //Redirect to NewUsersReport Page
                Intent intent=new Intent(ReportActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });
        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Redirect to NewUsersReport Page
                Intent intent=new Intent(ReportActivity.this,NewUser.class);
                startActivity(intent);
            }

        });

        btnReportedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Redirect to ReportedUsersReport Page
                Intent intent=new Intent(ReportActivity.this, ReportedUser.class);
                startActivity(intent);
            }
        });

        btnActiveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Redirect to ActiveUsersReport Page
                Intent intent=new Intent(ReportActivity.this, ActiveUser.class);
                startActivity(intent);
            }
        });

        btnReportRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Redirect to ReportRequests Page
                Intent intent=new Intent(ReportActivity.this, RequestsActivity.class);
                startActivity(intent);
            }
        });


    }
}