package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuProfiles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_profiles);

        Button updateProfileButton;
        Button viewProfileButton;
        Button deleteAccountButton;

        updateProfileButton = findViewById(R.id.UpdateProfile);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuProfiles.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        viewProfileButton = findViewById(R.id.ViewProfile);
        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuProfiles.this, ViewProfile.class);
                startActivity(intent);

            }
        });

        deleteAccountButton = findViewById(R.id.DeleteAccount);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MenuProfiles","Yet to implement2");
            }
        });

    }
}