package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class ActivityPage extends AppCompatActivity {

    //Displays the courses in this activity
    private Spinner coursesSpinner;
    //Displays the users in this activity
    private Spinner usersSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_page);


        /**LOCAL VARIABLES SET UP - START*/
        final Button onMap;
        final Button leave;
        final Button back;
        leave = findViewById(R.id.LeaveButton);
        onMap = findViewById(R.id.MapButton2);
        back = findViewById(R.id.BackfromCurrentActivity);
        /**LOCAL VARIABLES SET UP - END*/


        JSONObject aid_for_search = new JSONObject();
        try{
            aid_for_search.put("aid", UserdetailsUtil.activityID);
        }catch (JSONException e) {
            Log.d("Check0","Not able to form JSON Object aid_for_search");
            e.printStackTrace();
        }

        //Get the activity Details from the backend server
        setupSortedActivityList(aid_for_search);

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveActivity();
                finish();
            }
        });

        onMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent onMapTransition = new Intent(ActivityPage.this, ActivityOnMap.class);
                startActivity(onMapTransition);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void setupSortedActivityList(JSONObject aid_for_search){

        String URL = UserdetailsUtil.getURL() + "/activities/search";
        final boolean[] success = new boolean[1];
        final RequestQueue queue = Volley.newRequestQueue(this);

        final JsonObjectRequest activity_object = new JsonObjectRequest(Request.Method.POST, URL, aid_for_search,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            success[0] = (boolean)response.get("success");
                            JSONObject activity = response.getJSONObject("value");
                            SortedlistclassUtil.aname = activity.getString("name");
                            SortedlistclassUtil.major = activity.getString("major");
                            SortedlistclassUtil.aschool = activity.getString("school");
                            setupUsers(activity);
                            SortedlistclassUtil.course = new String[activity.getJSONArray("course").length()];
                            for(int i=0;i<activity.getJSONArray("course").length();i++){
                                SortedlistclassUtil.course[i] = activity.getJSONArray("course").getString(i);
                            }



                            SortedlistclassUtil.info = activity.getString("info");
                            SortedlistclassUtil.lat = activity.getDouble("lat");
                            SortedlistclassUtil.lon = activity.getDouble("long");
                            SortedlistclassUtil.leader = activity.getString("leader");


                            if(success[0]){
                                TextView nameBox = findViewById(R.id.ActivityNameBox2);
                                TextView infoBox = findViewById(R.id.InfoBox2);
                                TextView leaderBox = findViewById(R.id.LeaderBox2);
                                TextView schoolBox = findViewById(R.id.SchoolBox2);
                                TextView majorBox = findViewById(R.id.MajorBox2);


                                nameBox.setText(SortedlistclassUtil.aname);
                                infoBox.setText(SortedlistclassUtil.info);
                                leaderBox.setText(SortedlistclassUtil.leader);
                                schoolBox.setText(SortedlistclassUtil.aschool);
                                majorBox.setText(SortedlistclassUtil.major);

                                final String [] courses = new String[SortedlistclassUtil.course.length + 1];
                                courses[0] = "Click to see Courses in Activity";
                                for(int i = 0; i< SortedlistclassUtil.course.length; i++){
                                    courses[i+1] = SortedlistclassUtil.course[i];
                                }


                                coursesSpinner = findViewById(R.id.CoursesSpinner2);
                                ArrayAdapter<String> myAdapter1 = new ArrayAdapter<String>(ActivityPage.this,
                                        android.R.layout.simple_list_item_1, courses);
                                myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                coursesSpinner.setAdapter(myAdapter1);


                            }

                        }catch (JSONException e){
                            Log.d("ActivityExtractor",":(");
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ActivityPage.this, "Connection error, try again later!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        queue.add(activity_object);

    }
    private void setupUsers(final JSONObject activity)throws JSONException{

        final RequestQueue queue2 = Volley.newRequestQueue(ActivityPage.this);

        MyJSONArrayRequest allActivities = new MyJSONArrayRequest(Request.Method.GET, UserdetailsUtil.getURL() + "/profiles/all",null,
                new Response.Listener<JSONArray> (){
                    @Override
                    public void onResponse(JSONArray response){
                        /**Get all the activities JSONArray and then parse the array to read and display the required info*/
                        try {
                            SortedlistclassUtil.allphones = new String[response.length()];
                            SortedlistclassUtil.allUsers = new String[response.length()];
                            for(int i=0; i<response.length();i++){
                                SortedlistclassUtil.allUsers[i] = response.getJSONObject(i).getString("username");
                                SortedlistclassUtil.allphones[i] = response.getJSONObject(i).getString("phone");
                            }

                            SortedlistclassUtil.users = new String[activity.getJSONArray("usernames").length()];
                            for(int i=0;i<activity.getJSONArray("usernames").length();i++){
                                for(int j=0; j<SortedlistclassUtil.allUsers.length; j++){
                                    if(activity.getJSONArray("usernames").getString(i).equals(SortedlistclassUtil.allUsers[j])){
                                        SortedlistclassUtil.users[i] = activity.getJSONArray("usernames").getString(i) + " (" + SortedlistclassUtil.allphones[j] + ")";
                                    }
                                }
                            }
                            final String [] users = new String[activity.getJSONArray("usernames").length() + 1];
                            users[0] = "Click to see Users in Activity";
                            for(int i = 0; i< SortedlistclassUtil.users.length; i++){
                                users[i+1] = SortedlistclassUtil.users[i];
                            }
                            usersSpinner = findViewById(R.id.UsersSpinner2);
                            ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(ActivityPage.this,
                                    android.R.layout.simple_list_item_1, users);
                            myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            usersSpinner.setAdapter(myAdapter2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue2.add(allActivities);
    }


    private JSONObject getLeaveObject(){
        JSONObject leaveObject = new JSONObject();
        try{
            leaveObject.put("aid", UserdetailsUtil.activityID);
            leaveObject.put("username", UserdetailsUtil.username);
            Log.d("Leave", "Created leaveObject : " + leaveObject.getString("aid") + " ... " +  leaveObject.getString("username"));
        }catch (JSONException e){
            Log.d("Leave", "Error: Could not create leaveObject");
            e.printStackTrace();
        }
        return leaveObject;
    }

    private void leaveActivity(){
        JSONObject jsonObject = getLeaveObject();
        final RequestQueue ao = Volley.newRequestQueue(this);
        JsonObjectRequest activity_object = new JsonObjectRequest(Request.Method.POST, UserdetailsUtil.getURL() + "/activities/leaveupdate", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean leaveStatus = (boolean) response.get("success");
                            if(leaveStatus){
                                UserdetailsUtil.inactivity = false;
                                UserdetailsUtil.activityID = "";
                                Toast.makeText(ActivityPage.this, response.get("status").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.d("Leave", "Error: Could not extract LeaveStatus");
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ActivityPage.this, "Connection error while leaving, try again later!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        ao.add(activity_object);
    }
}