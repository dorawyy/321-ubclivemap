package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GetMatchScore extends AppCompatActivity {

    private EditText getRadius;
    private TextView showLocPriority;
    private TextView showCoursePriority;
    private TextView showMajorPriority;
    private SeekBar locPriority;
    private SeekBar coursePriority;
    private SeekBar majorPriority;

    private int[] priorities = new int[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_get_match_score);

        final Button done_btn;
        //final SharedPreferences userSettings = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        setUp();
        getLocPriorities();
        getCoursePriorities();
        getMajorPriorities();


        done_btn = findViewById(R.id.done_button_activity);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String inputDist = getRadius.getText().toString();
                final double inputLat = UserdetailsUtil.lat;
                final double inputLong = UserdetailsUtil.lon;
                final int inputLoc = priorities[0];
                final int inputCourse = priorities[1];
                final int inputMajor = priorities[2];
                JSONObject user = getUserObject();
                JSONObject jsnReq = getReqObject(inputDist,user,inputLoc,inputLat,inputLong,inputCourse,inputMajor);

                MyJSONArrayRequest json_obj = new MyJSONArrayRequest(Request.Method.POST, UserdetailsUtil.getURL() + "/activities/sort", jsnReq,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    if(response.length()>0) {
                                        SortedlistclassUtil.aids = new String[response.length()];
                                        SortedlistclassUtil.anames = new String[response.length()];
                                        for(int i=0; i<response.length();i++){
                                            SortedlistclassUtil.aids[i] = response.getJSONObject(i).getString("aid");
                                            SortedlistclassUtil.anames[i] = response.getJSONObject(i).getString("name");
                                        }
                                        Intent transition = new Intent(GetMatchScore.this, DisplaySortedList.class);
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

    private void setUp(){
        getRadius = findViewById(R.id.getRadius);
        locPriority = findViewById(R.id.LocationPriority);
        showLocPriority = findViewById(R.id.showLocPriority);
        showCoursePriority = findViewById(R.id.showCoursePriority);
        showMajorPriority = findViewById(R.id.showMajorPriority);
        coursePriority = findViewById(R.id.coursePriority);
        majorPriority = findViewById(R.id.majorPriority);
    }

    private void getLocPriorities() {
        showLocPriority.setText("Location Priority: " + locPriority.getProgress() + "/" + locPriority.getMax());
        locPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showLocPriority.setText("Location Priority: " + progress + "/" + locPriority.getMax());
                priorities[0] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("GetMatchScore", "onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("GetMatchScore", "onStopTrackingTouch");
            }
        });
    }

    private void getCoursePriorities() {
        showCoursePriority.setText("Course Priority: " + coursePriority.getProgress() + "/" + coursePriority.getMax());
        coursePriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showCoursePriority.setText("Course Priority: " + progress + "/" + coursePriority.getMax());
                priorities[1] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("GetMatchScore", "onStartTrackingTouch2");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("GetMatchScore", "onStopTrackingTouch2");
            }
        });
    }

    private void getMajorPriorities(){
        showMajorPriority.setText("Major Priority: "+majorPriority.getProgress()+"/"+majorPriority.getMax());
        majorPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showMajorPriority.setText("Major Priority: "+progress+"/"+majorPriority.getMax());
                priorities[2] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("GetMatchScore", "onStartTrackingTouch2");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("GetMatchScore", "onStartTrackingTouch2");
            }
        });
    }

    private JSONObject getUserObject(){
        JSONObject user = new JSONObject();
        try {
            user.put("name", UserdetailsUtil.name);
            user.put("username", UserdetailsUtil.username);
            user.put("major", UserdetailsUtil.major);
            user.put("CourseRegistered", UserdetailsUtil.courseRegistered);
            user.put("school", UserdetailsUtil.school);
            user.put("phone", UserdetailsUtil.phone);
            user.put("private", UserdetailsUtil.privatePublic);
            user.put("inActivity", UserdetailsUtil.inactivity);
            user.put("activityID", UserdetailsUtil.activityID);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return user;
    }

    private JSONObject getReqObject(String inputDist, JSONObject user, int inputLoc, Double inputLat, Double inputLong, int inputCourse, int inputMajor){
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
        return jsnReq;
    }
}