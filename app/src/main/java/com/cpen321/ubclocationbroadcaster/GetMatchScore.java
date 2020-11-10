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
    private String inputDist = getRadius.getText().toString();
    private double inputLat = 123.232;
    private double inputLong = -100.432;
    private int inputLoc = priorities[0];
    private int inputCourse = priorities[1];
    private int inputMajor = priorities[2];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button done_btn;

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        priorities = new int[3];
        getRadius = findViewById(R.id.getRadius);

        serVar();

        done_btn = findViewById(R.id.done_button_activity);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("done button", "done button has been clicked");
                String URL = UserdetailsUtil.getURL() + "/activities/sort";

                JSONObject user = new JSONObject();
                try {
                    getuserDetail(user);

                } catch (JSONException e){
                    e.printStackTrace();
                }

                JSONObject jsnReq = new JSONObject();
                try {
                    getJsnReq(jsnReq, user);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MyJSONArrayRequest json_obj = new MyJSONArrayRequest(Request.Method.POST, URL, jsnReq,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    if(response.length()>0) {
                                        helperFunction2(response);
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

    private void serVar() {
        setContentView(R.layout.activity_get_match_score);
        TextView showLocPriority = findViewById(R.id.showLocPriority);
        TextView showCoursePriority = findViewById(R.id.showCoursePriority);
        TextView showMajorPriority = findViewById(R.id.showMajorPriority);
        SeekBar locPriority = findViewById(R.id.LocationPriority);
        SeekBar coursePriority = findViewById(R.id.coursePriority);
        SeekBar majorPriority = findViewById(R.id.majorPriority);
        helperFunction(showLocPriority, locPriority, "Location Priority", 0);
        helperFunction(showCoursePriority, coursePriority, "Course Priority", 1);
        helperFunction(showMajorPriority, majorPriority, "Major Priority", 2);
    }

    private void helperFunction2(JSONArray response) throws JSONException {
        SortedlistclassUtil.aids = new String[response.length()];
        for(int i=0; i<response.length();i++){
            SortedlistclassUtil.aids[i] = response.getJSONObject(i).getString("aid");
        }
        Intent transition = new Intent(GetMatchScore.this, SortedActivityList.class);
        startActivity(transition);
    }

    private void getJsnReq(JSONObject jsnReq, JSONObject user) throws JSONException {

        jsnReq.put("maxradius", inputDist);
        jsnReq.put("user", user);
        jsnReq.put("locationweight", inputLoc);
        jsnReq.put("userlat", inputLat);
        jsnReq.put("userlong", inputLong);
        jsnReq.put("coursesweight", inputCourse);
        jsnReq.put("majorweight", inputMajor);
    }

    private void getuserDetail(JSONObject user) throws JSONException {
        user.put("name", UserdetailsUtil.name);
        user.put("username", UserdetailsUtil.username);
        user.put("major", UserdetailsUtil.major);
        user.put("CourseRegistered", UserdetailsUtil.courseRegistered);
        user.put("school", UserdetailsUtil.school);
        user.put("phone", UserdetailsUtil.phone);
        user.put("private", UserdetailsUtil.privatePublic);
        user.put("inActivity", UserdetailsUtil.inactivity);
        user.put("activityID", UserdetailsUtil.activityID);
        Log.d("Createduser:","User Details: " + user.getString("name") +
                user.getString("username") + user.getString("school") +
                user.getString("phone") + user.getString("private") +
                user.getString("inActivity") + user.getString("activityID") +
                user.getString("major") + user.getString("CourseRegistered"));
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