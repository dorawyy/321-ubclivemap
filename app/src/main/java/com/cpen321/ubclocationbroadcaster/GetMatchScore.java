package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class GetMatchScore extends AppCompatActivity {
    private EditText getRadius;
    private int[] priorities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_match_score);
        TextView showLocPriority;
        TextView showCoursePriority;
        TextView showMajorPriority;
        SeekBar locPriority;
        SeekBar coursePriority;
        SeekBar majorPriority;
        Button done_btn;

        //final SharedPreferences userSettings = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        priorities = new int[3];

        getRadius = findViewById(R.id.getRadius);
        locPriority = findViewById(R.id.LocationPriority);
        showLocPriority = findViewById(R.id.showLocPriority);
        showCoursePriority = findViewById(R.id.showCoursePriority);
        showMajorPriority = findViewById(R.id.showMajorPriority);
        coursePriority = findViewById(R.id.coursePriority);
        majorPriority = findViewById(R.id.majorPriority);

        helperFunction(showLocPriority, locPriority, "Location Priority", 0);
        helperFunction(showCoursePriority, coursePriority, "Course Priority", 1);
        helperFunction(showMajorPriority, majorPriority, "Major Priority", 2);

        done_btn = findViewById(R.id.done_button_activity);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("done button", "done button has been clicked");
                //TODO: set the url here
                //String URL = "http://40.122.147.73:3030/activities/sort";
                String URL = UserDetails.getURL() + "/activities/sort";

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
                                        startActivity(transition);
                                    } else {
                                        Toast.makeText(GetMatchScore.this, "No activities in this range, Please try another Combination." , Toast.LENGTH_SHORT).show();
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

    private void helperFunction(final TextView t, final SeekBar s, final String priorityName, final int i) {
        t.setText(priorityName + ": " + s.getProgress() + "/" + s.getMax());
        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                t.setText(priorityName + ": " + progress + "/" + s.getMax());
                priorities[i] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //unused
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //unused
            }
        });
    }
}