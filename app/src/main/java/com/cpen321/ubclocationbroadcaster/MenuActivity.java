package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button viewMapButton;
        Button myActivityButton;
        Button editProfileButton;
        Button signOutButton;
        Button createActivityButton;

        viewMapButton = findViewById(R.id.view_map_btn);
        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(MenuActivity.this, MainMapsActivity.class);
                startActivity(mapIntent);
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

        editProfileButton = findViewById(R.id.edit_profile_btn);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfileIntent = new Intent(MenuActivity.this, ProfileActivity.class);
                startActivity(editProfileIntent);
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

        createActivityButton = findViewById(R.id.create_activity_button);
        createActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!userDetails.inactivity){
                    Intent signInIntent = new Intent(MenuActivity.this, CreateActivity.class);
                    startActivity(signInIntent);
                }
                else{
                    Toast.makeText(MenuActivity.this, "You are already in an Activity!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}