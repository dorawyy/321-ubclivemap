package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

public class ViewProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_profile);
        TextView name = findViewById(R.id.textView100);
        TextView username = findViewById(R.id.textView101);
        TextView major = findViewById(R.id.textView102);
        TextView courseRegistered = findViewById(R.id.textView103);
        TextView school = findViewById(R.id.textView104);
        TextView phone = findViewById(R.id.textView105);
        TextView inActivity = findViewById(R.id.textView106);
        TextView activityId = findViewById(R.id.textView107);
        String coursesInStringFormat = Arrays.toString(UserdetailsUtil.courseRegistered);
        Button back = findViewById(R.id.backFromViewProfile);

        name.setText("Hi " + UserdetailsUtil.name);
        username.setText("Username: " + UserdetailsUtil.username);
        major.setText("Major: " + UserdetailsUtil.major);

        courseRegistered.setText("Courses: " + coursesInStringFormat.substring(1,coursesInStringFormat.length()-1));
        school.setText("School: " + UserdetailsUtil.school);
        phone.setText("Phone #: " + UserdetailsUtil.phone);
        if(UserdetailsUtil.inactivity){
            inActivity.setText("Activity Status: You are currently in an Activity");
            activityId.setText("Activity ID: " + UserdetailsUtil.activityID);
        }
        else{
            inActivity.setText("Activity Status: You are currently not in any Activity");
            activityId.setText("Activity ID: N/A");
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}