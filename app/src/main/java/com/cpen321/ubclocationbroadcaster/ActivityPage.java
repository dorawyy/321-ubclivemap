package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityPage extends AppCompatActivity {

    //Displays the courses in this activity
    private Spinner coursesSpinner;
    //Displays the users in this activity
    private Spinner usersSpinner;
    //Boolean value becomes true if successfully received activity details from server
    //private boolean success = false;

    private int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);



        /**LOCAL VARIABLES SET UP - START*/
        final Button leave = findViewById(R.id.LeaveButton);
        /**LOCAL VARIABLES SET UP - END*/


        //Log.d("PageActivity", SortedlistclassUtil.activity_to_be_displayed);

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
                Intent intent = new Intent(ActivityPage.this, MenuActivity.class);
                startActivity(intent);
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
                            SortedlistclassUtil.course = new String[activity.getJSONArray("course").length()];
                            for(int i=0;i<activity.getJSONArray("course").length();i++){
                                SortedlistclassUtil.course[i] = activity.getJSONArray("course").getString(i);
                            }

                            SortedlistclassUtil.users = new String[10];
                            for(int i=0;i<activity.getJSONArray("usernames").length();i++){
                                SortedlistclassUtil.users[i] = activity.getJSONArray("usernames").getString(i);
                            }
                            for(int i=activity.getJSONArray("usernames").length();i<10;i++){
                                SortedlistclassUtil.users[i] = " ";
                            }

                            SortedlistclassUtil.info = activity.getString("info");
                            SortedlistclassUtil.lat = activity.getString("lat");
                            SortedlistclassUtil.lon = activity.getString("long");
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

                                //numOfusers = SortedListClass.users.length;
                                for(int i = 0; i< SortedlistclassUtil.users.length; i++){
                                    if(!SortedlistclassUtil.users[i].equals(" "))
                                        counter++;
                                }
                                final String [] users = new String[counter + 1];
                                users[0] = "Click to see Users in Activity";
                                for(int i = 0; i< SortedlistclassUtil.users.length; i++){
                                    if(!SortedlistclassUtil.users[i].equals(" "))
                                        users[i+1] = SortedlistclassUtil.users[i];
                                }

                                coursesSpinner = findViewById(R.id.CoursesSpinner2);
                                ArrayAdapter<String> myAdapter1 = new ArrayAdapter<String>(ActivityPage.this,
                                        android.R.layout.simple_list_item_1, courses);
                                myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                coursesSpinner.setAdapter(myAdapter1);

                                usersSpinner = findViewById(R.id.UsersSpinner2);
                                ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(ActivityPage.this,
                                        android.R.layout.simple_list_item_1, users);
                                myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                usersSpinner.setAdapter(myAdapter2);

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