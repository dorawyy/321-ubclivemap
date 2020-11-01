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
                String URL = "http://10.0.2.2:3000/sortactivities";

                final String inputDist = getRadius.getText().toString();
                final double inputLat = 123.232;
                final double inputLong = -100.432;
                final int inputLoc = priorities[0];
                final int inputCourse = priorities[1];
                final int inputMajor = priorities[2];
                final double[] inputLatLong = new double[]{inputLat, inputLong};
                JSONObject jsnReq = new JSONObject();
                try {
                    jsnReq.put("radius", inputDist);
                    jsnReq.put("user", UserDetails.name);
                    jsnReq.put("userid", UserDetails.username);
                    jsnReq.put("major", UserDetails.major);
                    jsnReq.put("school", UserDetails.school);
                    jsnReq.put("courseRegistered", UserDetails.courseRegistered);
                    jsnReq.put("phone", UserDetails.phone);
                    jsnReq.put("private", UserDetails.privatePublic);
                    jsnReq.put("inActivity", UserDetails.inactivity);
                    jsnReq.put("activityID", UserDetails.activityID);
                    jsnReq.put("locationweight", inputLoc);
                    jsnReq.put("userlocation", inputLatLong);
                    jsnReq.put("coursesweight", inputCourse);
                    jsnReq.put("majorweight", inputMajor);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, URL, jsnReq,
                        new Response.Listener<JSONObject> (){
                            @Override
                            public void onResponse(JSONObject response){
                                try {
                                    boolean successVal = (boolean) response.get("success");
                                    String stat = response.get("status").toString();
                                    if(successVal) {
                                        Intent sign_in_Intent = new Intent(GetMatchScore.this, SortedActivityList.class);
                                        Toast.makeText(GetMatchScore.this, "Activity added", Toast.LENGTH_SHORT).show();
                                        startActivity(sign_in_Intent);
                                    } else {
                                        Toast.makeText(GetMatchScore.this, "ERROR: " + stat, Toast.LENGTH_SHORT).show();
                                    }
                                    Log.d("GetMatchScore", stat);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(GetMatchScore.this, "Unable to send weighting data to the server!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

                requestQueue.add(json_obj);
            }
        });

    }
}