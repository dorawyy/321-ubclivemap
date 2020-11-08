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

/*This Activity is opened when a user clicks on an activity either
* while viewing the suggested activities list, or while viewing
* all the activities on the map.
* Through this activity, the user can see the details of the activity
* the user has clicked. The user can also join the activity from in here.*/
public class DisplayActivityDetails extends AppCompatActivity {

    //Displays the courses in this activity
    private Spinner coursesSpinner;
    //Displays the users in this activity
    private Spinner usersSpinner;
    //Boolean value becomes true if successfully received activity details from server
    private boolean success = false;

    private int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details);

        String URL = BackendURL.getURL() + "/activities/search";

        final RequestQueue queue = Volley.newRequestQueue(this);

        Log.d("DisplayActivity",SortedListClass.activity_to_be_displayed);

        JSONObject aid_for_search = new JSONObject();
        try{
            aid_for_search.put("aid",SortedListClass.activity_to_be_displayed);
        }catch (JSONException e) {
            Log.d("Check0","Not able to form JSON Object aid_for_search");
            e.printStackTrace();
        }

        //Get the activity Details from the backend server
        final JsonObjectRequest activity_object = new JsonObjectRequest(Request.Method.POST, URL, aid_for_search,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            success = (boolean)response.get("success");
                            JSONObject activity = response.getJSONObject("value");
                            SortedListClass.aname = activity.getString("name");
                            SortedListClass.major = activity.getString("major");
                            SortedListClass.aschool = activity.getString("school");
                            SortedListClass.course = new String[activity.getJSONArray("course").length()];
                            for(int i=0;i<activity.getJSONArray("course").length();i++){
                                SortedListClass.course[i] = activity.getJSONArray("course").getString(i);
                            }

                            SortedListClass.users = new String[10];
                            for(int i=0;i<activity.getJSONArray("usernames").length();i++){
                                SortedListClass.users[i] = activity.getJSONArray("usernames").getString(i);
                            }
                            for(int i=activity.getJSONArray("usernames").length();i<10;i++){
                                SortedListClass.users[i] = " ";
                            }

                            SortedListClass.info = activity.getString("info");
                            SortedListClass.lat = activity.getString("lat");
                            SortedListClass.lon = activity.getString("long");
                            SortedListClass.leader = activity.getString("leader");


                            if(success){
                                TextView nameBox = findViewById(R.id.ActivityNameBox);
                                TextView infoBox = findViewById(R.id.InfoBox);
                                TextView leaderBox = findViewById(R.id.LeaderBox);
                                TextView schoolBox = findViewById(R.id.SchoolBox);
                                TextView majorBox = findViewById(R.id.MajorBox);


                                nameBox.setText(SortedListClass.aname);
                                infoBox.setText(SortedListClass.info);
                                leaderBox.setText(SortedListClass.leader);
                                schoolBox.setText(SortedListClass.aschool);
                                majorBox.setText(SortedListClass.major);

                                final String [] courses = new String[SortedListClass.course.length + 1];
                                courses[0] = "Click to see Courses in Activity";
                                for(int i=0;i<SortedListClass.course.length;i++){
                                    courses[i+1] = SortedListClass.course[i];
                                }

                                //numOfusers = SortedListClass.users.length;
                                for(int i=0;i<SortedListClass.users.length;i++){
                                    if(!SortedListClass.users[i].equals(" "))
                                        counter++;
                                }
                                final String [] users = new String[counter + 1];
                                users[0] = "Click to see Users in Activity";
                                for(int i=0;i<SortedListClass.users.length;i++){
                                    if(!SortedListClass.users[i].equals(" "))
                                        users[i+1] = SortedListClass.users[i];
                                }

                                coursesSpinner = findViewById(R.id.CoursesSpinner);
                                ArrayAdapter<String> myAdapter1 = new ArrayAdapter<String>(DisplayActivityDetails.this,
                                        android.R.layout.simple_list_item_1, courses);
                                myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                coursesSpinner.setAdapter(myAdapter1);

                                usersSpinner = findViewById(R.id.UsersSpinner);
                                ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(DisplayActivityDetails.this,
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
                Toast.makeText(DisplayActivityDetails.this, "Connection error, try again later!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        queue.add(activity_object);

        //Button onMap = findViewById(R.id.MapButton);
        final Button join = findViewById(R.id.JoinButton);

        

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_up_Intent = new Intent(DisplayActivityDetails.this, Joined.class);
                startActivity(sign_up_Intent);
                Log.d("Done", "Going Joined");
            }
        });

    }
}