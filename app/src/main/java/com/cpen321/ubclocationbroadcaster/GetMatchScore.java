package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;


public class GetMatchScore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_match_score);

        final EditText getRadius;
        final TextView showLocPriority;
        final TextView showCoursePriority;
        final TextView showMajorPriority;
        final SeekBar locPriority;
        final SeekBar coursePriority;
        final SeekBar majorPriority;
        final Button done_btn;
        final int[] priorities = new int[3];
        final SharedPreferences userSettings = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        getRadius = findViewById(R.id.getRadius);
        locPriority = findViewById(R.id.LocationPriority);
        showLocPriority = findViewById(R.id.showLocPriority);
        showCoursePriority = findViewById(R.id.showCoursePriority);
        showMajorPriority = findViewById(R.id.showMajorPriority);
        coursePriority = findViewById(R.id.coursePriority);
        majorPriority = findViewById(R.id.majorPriority);

        showLocPriority.setText("Location Priority: "+locPriority.getProgress()+"/"+locPriority.getMax());
        locPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showLocPriority.setText("Location Priority: "+progress+"/"+locPriority.getMax());
                priorities[0] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        showCoursePriority.setText("Course Priority: "+coursePriority.getProgress()+"/"+coursePriority.getMax());
        coursePriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showCoursePriority.setText("Course Priority: "+progress+"/"+coursePriority.getMax());
                priorities[1] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        showMajorPriority.setText("Major Priority: "+majorPriority.getProgress()+"/"+majorPriority.getMax());
        majorPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showMajorPriority.setText("Major Priority: "+progress+"/"+majorPriority.getMax());
                priorities[2] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        done_btn = findViewById(R.id.done_button_activity);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("done button", "done button has been clicked");
                //TODO: set the url here
                String URL = "http://40.122.147.73:3030/activities/sort";

                final String inputDist = getRadius.getText().toString();
                final double inputLat = 123.232;
                final double inputLong = -100.432;
                final int inputLoc = priorities[0];
                final int inputCourse = priorities[1];
                final int inputMajor = priorities[2];

                JSONObject user = new JSONObject();
                try {
                    user.put("name", UserDetails.name);
                    user.put("username", UserDetails.username);
                    user.put("major", UserDetails.major);
                    user.put("CourseRegistered", UserDetails.courseRegistered);
                    user.put("school", UserDetails.school);
                    user.put("phone", UserDetails.phone);
                    user.put("private", UserDetails.privatePublic);
                    user.put("inActivity", UserDetails.inactivity);
                    user.put("activityID", UserDetails.activityID);
                } catch (JSONException e){
                    e.printStackTrace();
                }

                JSONObject jsnReq = new JSONObject();
                try {
                    jsnReq.put("maxradius", inputDist);
                    jsnReq.put("user", user);
                    jsnReq.put("locationweight", inputLoc);
                    jsnReq.put("userlat", inputLat);
                    jsnReq.put("userlong", inputLong);
                    jsnReq.put("coursesweight", inputCourse);
                    jsnReq.put("majorweight", inputMajor);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MyJSONArrayRequest json_obj = new MyJSONArrayRequest(Request.Method.POST, URL, jsnReq,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    if(response.length()>0) {
                                        SortedListClass.aids = new String[response.length()];
                                        for(int i=0; i<response.length();i++){
                                            SortedListClass.aids[i] = response.getJSONObject(i).getString("aid");
                                        }
                                        Intent transition = new Intent(GetMatchScore.this, SortedActivityList.class);
                                        Toast.makeText(GetMatchScore.this, "Activity added", Toast.LENGTH_SHORT).show();
                                        startActivity(transition);
                                    } else {
                                        Toast.makeText(GetMatchScore.this, "No activities in this range" , Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GetMatchScore.this, "Unable to send weighting data to the server!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
                requestQueue.add(json_obj);
            }
        });

    }
}