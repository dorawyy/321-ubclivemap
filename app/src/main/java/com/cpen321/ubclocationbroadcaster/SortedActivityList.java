package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class SortedActivityList extends AppCompatActivity {

    private Spinner activitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorted_list);
        activitySpinner = findViewById(R.id.activitiesSpinner);

        final String [] activities = new String[SortedlistclassUtil.aids.length + 1];
        activities[0] = "Suggested Ranked Activities";
        for(int i = 0; i< SortedlistclassUtil.aids.length; i++){
            activities[i+1] = SortedlistclassUtil.aids[i];
            Log.d("SortedList aids","Number: " + i + " : " + SortedlistclassUtil.aids[i] + "\n");
        }

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(SortedActivityList.this,
                android.R.layout.simple_list_item_1, activities);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(myAdapter);

        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                if(position>0){
                    String result = activitySpinner.getSelectedItem().toString();
                    //Sortedlistclass.activity_to_be_displayed = new String();
                    SortedlistclassUtil.activity_to_be_displayed = result;
                    Log.d("SortedActivityList","Selected activity aid: " + SortedlistclassUtil.activity_to_be_displayed);
                    Intent transition = new Intent(SortedActivityList.this, DisplayActivityDetails.class);
                    startActivity(transition);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("SortedActivityList","Nothing selected");
            }
        });

    }
}