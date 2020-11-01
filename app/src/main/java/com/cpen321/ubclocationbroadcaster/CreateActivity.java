package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreateActivity extends AppCompatActivity {

    //For the user registered courses list drop down
    private Spinner mySpinner;
    private ListView reg_courses_view;
    //List of courses from the registered courses of the leader to be put in the Activity
    final private ArrayList<String> activity_courses = new ArrayList<String>();
    //Number of Registered courses of the user
    private int numOfCourses =0;
    //Class that stores the user details
    //private UserDetails ud = new UserDetails();

    private boolean activityS , userS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        final RequestQueue q = Volley.newRequestQueue(this);
        final RequestQueue r = Volley.newRequestQueue(this);
        final RequestQueue s = Volley.newRequestQueue(this);

        final String username = UserDetails.username;
        final String[] user_courses = new String[10];


        /*For courses in the activity. The user should be able to choose the courses in the activity from his/her registered courses.*/

        JSONObject findUser = new JSONObject();
        mySpinner = findViewById(R.id.reg_course_spinner);
        reg_courses_view = findViewById((R.id.registered_courses));

        user_courses[0] = "Choose from your courses";
        Log.d("checkpoint1","UserDetails.courseRegistered[0] : " + UserDetails.courseRegistered[0]);
        for(int i = 0; i < UserDetails.courseRegistered.length; i++){

            user_courses[i+1] = UserDetails.courseRegistered[i];
            Log.d("courses", "Display reg course " + user_courses[i]);
        }
        //Fill the remaining places in the user_course with empty values, otherwise spinner gives an error
        for(int i=(UserDetails.courseRegistered.length+1); i<10; i++){
            user_courses[i] = " ";
        }

        /*SHOW THE REGISTERED COURSES OF THE USER*/
        Button showCoursesBtn ;
        showCoursesBtn = findViewById(R.id.courseBTN);
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
                        if((position>0) && (!activity_courses.contains(result)) && (!result.equals(" "))){
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


        /*CREATE A NEW ACTIVITY
        * CREATE A NEW JSONOBJECT WITH ACTIVITY FIELDS AND SEND A POST REQUEST*/

        Button done_btn;
        done_btn = findViewById(R.id.activity_done);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = "http://10.0.2.2:3000/addactivity";

                JSONObject jsnRequest = new JSONObject();
                EditText name = findViewById(R.id.activity_name);
                EditText info = findViewById(R.id.activity_desc);
                EditText aid  = findViewById(R.id.activity_id);
                String inputaid = aid.getText().toString();
                String inputInfo = info.getText().toString();
                String inputLat = "23";
                String inputLong = "24";
                String inputName = name.getText().toString();

                JSONArray courses = new JSONArray();
                for(String course : activity_courses){
                    courses.put(course);
                }

                try {
                    jsnRequest.put("name", inputName);
                    jsnRequest.put("aid", inputaid);
                    jsnRequest.put("leader", username);
                    jsnRequest.put("usernames", username);
                    jsnRequest.put("info", inputInfo);
                    jsnRequest.put("major", UserDetails.major);
                    jsnRequest.put("course", courses);
                    jsnRequest.put("school", UserDetails.school);
                    jsnRequest.put("lat", inputLat);
                    jsnRequest.put("long", inputLong);
                    jsnRequest.put("status","1");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, URL, jsnRequest,
                        new Response.Listener<JSONObject> (){
                            @Override
                            public void onResponse(JSONObject response){
                                try {
                                    activityS = (boolean) response.get("success"); // check if user signed in successfully
                                    String stat = response.get("status").toString(); // get status
                                    if(activityS && userS){
                                        Intent menu_Intent = new Intent(CreateActivity.this, MenuActivity.class);
                                        startActivity(menu_Intent);
                                        Log.d("Next button", "Next button has been clicked");
                                    }
                                    else if(activityS) {
                                        Log.d("UpdatingUser", stat);
                                    } else {
                                        Log.d("Error3", stat);
                                        Toast.makeText(CreateActivity.this, "ERROR: " + stat, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Log.d("Error4", "Error in creating new Activity");
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(CreateActivity.this, "Unable to send the create activity data to the server!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
                r.add(json_obj);


                /*UPDATE THE userDB USER DETAILS TO REFLECT THAT THE USER IS NOW IN AN ACTIVITY*/
                final JSONArray reg_courses = new JSONArray();
                for(String course : user_courses){
                    if((!course.equals(" ")) && (!course.equals("Choose from your courses")))
                        reg_courses.put(course);
                }

                JSONObject updateUser = new JSONObject();
                try {
                    updateUser.put("name", UserDetails.name);
                    updateUser.put("phone", UserDetails.phone);
                    updateUser.put("school", UserDetails.school);
                    updateUser.put("major", UserDetails.major);
                    updateUser.put("CourseRegistered",reg_courses);
                    updateUser.put("private", UserDetails.privatePublic);
                    updateUser.put("username", UserDetails.username);
                    updateUser.put("inActivity", "True");
                    updateUser.put("activityID", inputaid);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest userUpdateObject = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:3000/userupdate", updateUser,
                    new Response.Listener<JSONObject> (){
                        @Override
                        public void onResponse(JSONObject response){
                            try {
                                userS = (boolean) response.get("success");
                                String stat = response.get("status").toString();
                                if(userS && activityS){
                                    Log.d("UpdatingUser", stat);
                                    Intent menu_Intent = new Intent(CreateActivity.this, MenuActivity.class);
                                    startActivity(menu_Intent);
                                    Log.d("Next button", "Next button has been clicked");
                                }
                                else if (userS){
                                    Log.d("UserUpdate", stat);
                                }
                                else{
                                    Log.d("Error5", "Error5 " + stat);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("Error6", "-------could not update user------------- " );
                            }
                        }
                    }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(CreateActivity.this, "Unable to send the user info to the server!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

                s.add(userUpdateObject);
            }


        });
    }
}