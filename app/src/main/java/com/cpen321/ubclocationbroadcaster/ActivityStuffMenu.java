package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ActivityStuffMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuff_menu);

        Button go_to_getMatchScore;

        go_to_getMatchScore = findViewById(R.id.getMatchScore_button);

        go_to_getMatchScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_in_Intent = new Intent(ActivityStuffMenu.this, GetMatchScore.class);
                startActivity(sign_in_Intent);
                Log.d("ActivityStuffMenu", "Get Match Score Button has been pressed");
            }
        });
    }
}