package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivities extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu_activities);

        Button viewActivitiesOnMapButton;
        Button currentActivityButton;
        Button createActivityButton;
        Button suggestedActivitiesButton;

        Animation topAnim;
        Animation bottomAnim;
        Animation topAnim2;
        Animation bottomAnim2;

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        topAnim2 = AnimationUtils.loadAnimation(this,R.anim.top_anime_another);
        bottomAnim2 = AnimationUtils.loadAnimation(this,R.anim.bottom_anime_another);

        viewActivitiesOnMapButton = findViewById(R.id.ViewActivitiesOnMap);
        viewActivitiesOnMapButton.setAnimation(topAnim);
        viewActivitiesOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivities.this, MainMapsActivity.class);
                startActivity(intent);
            }
        });

        currentActivityButton = findViewById(R.id.CurrentActivity);
        currentActivityButton.setAnimation(topAnim2);
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
        createActivityButton.setAnimation(bottomAnim);
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
        suggestedActivitiesButton.setAnimation(bottomAnim2);
        suggestedActivitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent(MenuActivities.this, GetMatchScore.class);
                startActivity(activityIntent);
            }
        });

    }
}