package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivities extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_activities);

        Button viewActivitiesOnMapButton;
        Button currentActivityButton;
        Button createActivityButton;
        Button suggestedActivitiesButton;

        viewActivitiesOnMapButton = findViewById(R.id.ViewActivitiesOnMap);
        viewActivitiesOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivities.this, MainMapsActivity.class);
                startActivity(intent);
            }
        });

        currentActivityButton = findViewById(R.id.CurrentActivity);
        currentActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!UserdetailsUtil.inactivity){
                    Toast.makeText(MenuActivities.this, " :( Please Join or Create a new Activity :(", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(MenuActivities.this, ActivityPage.class);
                    startActivity(intent);
                }
            }
        });

        createActivityButton = findViewById(R.id.CreateActivity);
        createActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!UserdetailsUtil.inactivity){
                    Intent intent = new Intent(MenuActivities.this, CreateActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MenuActivities.this, "You are already in an Activity!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        suggestedActivitiesButton = findViewById(R.id.SuggestedActivites);
        suggestedActivitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent(MenuActivities.this, GetMatchScore.class);
                startActivity(activityIntent);
            }
        });

    }
}