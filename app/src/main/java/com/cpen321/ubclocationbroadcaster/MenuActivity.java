package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);

        Button activitesMenuButton;
        Button profileMenuButton;
        Button signOutButton;
        Button settingsButton;

        Animation topAnim;
        Animation bottomAnim;
        Animation topAnim2;
        Animation bottomAnim2;

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        topAnim2 = AnimationUtils.loadAnimation(this,R.anim.top_anime_another);
        bottomAnim2 = AnimationUtils.loadAnimation(this,R.anim.bottom_anime_another);

        activitesMenuButton = findViewById(R.id.ActivityMenu);
        activitesMenuButton.setAnimation(topAnim);
        activitesMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MenuActivities.class);
                startActivity(intent);
            }
        });

        profileMenuButton = findViewById(R.id.ProfileMenu);
        profileMenuButton.setAnimation(topAnim2);
        profileMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent(MenuActivity.this, MenuProfiles.class);
                startActivity(activityIntent);
            }
        });

        settingsButton = findViewById(R.id.Settings);
        settingsButton.setAnimation(bottomAnim);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityIntent = new Intent(MenuActivity.this, Settings.class);
                startActivity(activityIntent);
            }
        });

        signOutButton = findViewById(R.id.SignOut);
        signOutButton.setAnimation(bottomAnim2);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserdetailsUtil.cleanup();
                SortedlistclassUtil.cleanup();
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


    }

}