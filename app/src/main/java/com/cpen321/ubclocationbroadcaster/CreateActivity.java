package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.util.ArrayList;

public class CreateActivity extends AppCompatActivity {

    /***INITIALIZATION - START*/
    private Spinner mySpinner; //For the user registered courses list drop down
    private ListView reg_courses_view;
    final private ArrayList<String> activity_courses = new ArrayList<String>(); //List of courses from the registered courses of the leader to be put in the Activity
    private Double inputLat;
    private Double inputLong;
    /***INITIALIZATION - END*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_create);

        /***INITIALIZATION - START*/
        final RequestQueue s = Volley.newRequestQueue(this);
        final String username = UserdetailsUtil.username;
        final String[] user_courses = new String[10];
        mySpinner = findViewById(R.id.reg_course_spinner);
        reg_courses_view = findViewById((R.id.registered_courses));
        Button showCoursesBtn = findViewById(R.id.courseBTN);
        Button done_btn = findViewById(R.id.activity_done);
        Button addLoc = findViewById(R.id.LocButton);

        /***INITIALIZATION - START*/

        /**user_courses SET UP START*/
        user_courses[0] = "Choose from your courses";
        for (int i = 0; i < UserdetailsUtil.courseRegistered.length; i++) {

            user_courses[i + 1] = UserdetailsUtil.courseRegistered[i];
            Log.d("courses", "Display reg course " + user_courses[i]);
        }
        //Fill the remaining places in the user_course with empty values, otherwise spinner gives an error
        for (int i = (UserdetailsUtil.courseRegistered.length + 1); i < 10; i++) {
            user_courses[i] = " ";
        }
        /**user_courses SET UP END*/

        /**SHOW THE REGISTERED COURSES OF THE USER*/
        showCoursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Usercourse:", "Usercourse1 is: " + user_courses[1]);
                ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(CreateActivity.this,
                        android.R.layout.simple_list_item_1, user_courses);
                myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mySpinner.setAdapter(myAdapter);


                final ArrayAdapter<String> reg_course_list_adapter = new ArrayAdapter<String>(CreateActivity.this,
                        android.R.layout.simple_list_item_1, activity_courses);
                reg_courses_view.setAdapter(reg_course_list_adapter);

                mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String result = mySpinner.getSelectedItem().toString();
                        if ((position > 0) && (!activity_courses.contains(result)) && (!result.equals(" "))) {
                            activity_courses.add(result);
                        }
                        reg_course_list_adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        /**If the user wants to enter the location manually.
         * Open up google maps, and ask them to long press a location to add marker
         * The activity will be located on this location*/
        addLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t = new Intent(CreateActivity.this,Getlocation.class);
                startActivity(t);
            }
        });


        /**CREATE A NEW ACTIVITY
         * CREATE A NEW JSONObject WITH ACTIVITY FIELDS AND SEND A POST REQUEST*/
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = UserdetailsUtil.getURL() + "/activities/add";

                JSONObject jsnRequest = new JSONObject();
                EditText name = findViewById(R.id.activity_name);
                EditText info = findViewById(R.id.activity_desc);
                EditText aid = findViewById(R.id.activity_id);
                final String inputaid = aid.getText().toString();
                String inputInfo = info.getText().toString();

                if(UserdetailsUtil.activitylat == null){
                    inputLat = UserdetailsUtil.lat;
                    inputLong = UserdetailsUtil.lon;
                }
                else {
                    inputLat = UserdetailsUtil.activitylat;
                    inputLong = UserdetailsUtil.activitylon;
                }
                Log.d("LATLONG", " LAT : " + inputLat.toString() + " LONG : " + inputLong.toString() );

                String inputName = name.getText().toString();

                JSONArray courses = new JSONArray();
                for (String course : activity_courses) {
                    courses.put(course);
                }

                try {
                    jsnRequest.put("name", inputName);
                    jsnRequest.put("aid", inputaid);
                    jsnRequest.put("leader", username);
                    jsnRequest.put("usernames", username);
                    jsnRequest.put("info", inputInfo);
                    jsnRequest.put("major", UserdetailsUtil.major);
                    jsnRequest.put("course", courses);
                    jsnRequest.put("school", UserdetailsUtil.school);
                    jsnRequest.put("lat", inputLat);
                    jsnRequest.put("long", inputLong);
                    jsnRequest.put("status", "1");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (inputaid.isEmpty() || inputInfo.isEmpty() || inputName.isEmpty()) {
                    Toast.makeText(CreateActivity.this, "Please Enter Valid Values for all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, URL, jsnRequest,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean activityS = (boolean) response.get("success"); // check if user signed in successfully
                                    String stat = response.get("status").toString(); // get status

                                    if (activityS) {
                                        /**UPDATE THE userDB UserdetailsUtil TO REFLECT THAT THE USER IS NOW IN AN ACTIVITY*/
                                        UserdetailsUtil.inactivity = true;
                                        UserdetailsUtil.activityID = inputaid;
                                        finish();
                                    } else {
                                        Toast.makeText(CreateActivity.this, "ERROR: " + stat, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Log.d("CreateActivity", "Error Parsing the Object Returned in activities/add");
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreateActivity.this, "Unable to send the create activity data to the server!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
                s.add(json_obj);

            }


        });
    }

}