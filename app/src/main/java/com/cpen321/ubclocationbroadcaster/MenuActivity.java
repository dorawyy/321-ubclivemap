package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {
    private Button viewMapButton;
    private Button listActivitesButton;
    private Button myActivityButton;
    private Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        viewMapButton = findViewById(R.id.view_map_btn);
        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(MenuActivity.this, MainMapsActivity.class);
                startActivity(mapIntent);
            }
        });

        listActivitesButton = findViewById(R.id.list_activites_btn);
        listActivitesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activitesIntent = new Intent(MenuActivity.this, ListScrollingActivity.class);
                startActivity(activitesIntent);
            }
        });

        myActivityButton = findViewById(R.id.my_activity_btn);
        myActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent(MenuActivity.this, ActivityStuffMenu.class);
                startActivity(activityIntent);
            }
        });

        signOutButton = findViewById(R.id.sign_out_btn);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(signInIntent);
            }
        });
    }

}