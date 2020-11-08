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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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

    private boolean activityS;
    private boolean userS;
    private String username = UserdetailsUtil.username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        //final RequestQueue q = Volley.newRequestQueue(this);
        final RequestQueue r = Volley.newRequestQueue(this);
        final String[] user_courses = new String[10];

        /*For courses in the activity. The user should be able to choose the courses in the activity from his/her registered courses.*/
        mySpinner = findViewById(R.id.reg_course_spinner);
        reg_courses_view = findViewById((R.id.registered_courses));

        user_courses[0] = "Choose from your courses";
        Log.d("checkpoint1","UserDetails.courseRegistered[0] : " + UserdetailsUtil.courseRegistered[0]);

        helperFunction(user_courses);

        /*SHOW THE REGISTERED COURSES OF THE USER*/
        Button showCoursesBtn = findViewById(R.id.courseBTN);
        showCoursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter(user_courses);
                setSpinner();
            }
        });

        /*CREATE A NEW ACTIVITY
        * CREATE A NEW JSONOBJECT WITH ACTIVITY FIELDS AND SEND A POST REQUEST*/
        Button done_btn;
        done_btn = findViewById(R.id.activity_done);

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String URL = "http://40.122.147.73:3030/activities/add";
                String URL = UserdetailsUtil.getURL() + "/activities/add";

                JSONObject jsnRequest = new JSONObject();
                EditText aid  = findViewById(R.id.activity_id);
                String inputaid = aid.getText().toString();
                getJsnRequest(jsnRequest);

                JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, URL, jsnRequest,
                        new Response.Listener<JSONObject> (){
                            @Override
                            public void onResponse(JSONObject response){
                                helperFunction6(response);
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
               updateUserDB(user_courses, inputaid);
            }
        });
    }

    private void updateUserDB(String[] user_courses, String inputaid) {
        final RequestQueue s = Volley.newRequestQueue(this);
        final JSONArray reg_courses = new JSONArray();
        helperFunction3(user_courses, inputaid, reg_courses);
        JSONObject userObject = new JSONObject();
        helperFunction5(inputaid, userObject);
        JsonObjectRequest userUpdateObject = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:3000/profiles/join", userObject,
                new Response.Listener<JSONObject> (){
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            helperFunction4(response);
                        } catch (JSONException e) {
                            Log.d("UserDBUpdate", "UNSuccessfully Updated User to reflect Joined Activity");
                            e.printStackTrace();
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

    private void setSpinner() {
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
                //unused
            }
        });
    }

    private void helperFunction6(JSONObject response) {
        try {
            helperFunction2(response);
        } catch (JSONException e) {
            Log.d("Error4", "Error in creating new Activity");
            e.printStackTrace();
        }
    }

    private void helperFunction4(JSONObject response) throws JSONException {
        boolean userJoinStatus = false;
        userJoinStatus = (boolean) response.get("success");
        if(userJoinStatus && activityS){
            //Update Locally
            UserdetailsUtil.inactivity = true;
            UserdetailsUtil.activityID = SortedlistclassUtil.activity_to_be_displayed;
            Intent menu_Intent = new Intent(CreateActivity.this, MenuActivity.class);
            startActivity(menu_Intent);
            Log.d("Next button", "Next button has been clicked");
        }
        else if (userJoinStatus){
            //Update Locally
            UserdetailsUtil.inactivity = true;
            UserdetailsUtil.activityID = SortedlistclassUtil.activity_to_be_displayed;
        }
        else{
            Log.d("Error5", "Error5 ");
        }
    }

    private void helperFunction3(String[] user_courses, String inputaid, JSONArray reg_courses) {
        for (String course : user_courses) {
            if ((!course.equals(" ")) && (!course.equals("Choose from your courses")))
                reg_courses.put(course);
        }
    }

    private void helperFunction5(String inputaid, JSONObject userObject){
        //Update the UserDetails locally and the UserDB backend Database to reflect that the user is in a activity now.
        try{
            userObject.put("username", UserdetailsUtil.username);
            userObject.put("aid", inputaid);
        }catch (JSONException e){
            e.printStackTrace();
            Log.d("Join", "Error: Could not create userObject");
        }
    }

    private void helperFunction2(JSONObject response) throws JSONException {
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
    }

    private void helperFunction(String[] user_courses) {
        for(int i = 0; i < UserdetailsUtil.courseRegistered.length; i++){
            user_courses[i+1] = UserdetailsUtil.courseRegistered[i];
            Log.d("courses", "Display reg course " + user_courses[i]);
        }
        //Fill the remaining places in the user_course with empty values, otherwise spinner gives an error
        for(int i = (UserdetailsUtil.courseRegistered.length+1); i<10; i++){
            user_courses[i] = " ";
        }
    }

    private void getJsnRequest(JSONObject jsnRequest) {
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
            jsnRequest.put("major", UserdetailsUtil.major);
            jsnRequest.put("course", courses);
            jsnRequest.put("school", UserdetailsUtil.school);
            jsnRequest.put("lat", inputLat);
            jsnRequest.put("long", inputLong);
            jsnRequest.put("status","1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(inputaid.isEmpty()||inputInfo.isEmpty()||inputName.isEmpty()){
            Toast.makeText(CreateActivity.this, "Please Enter Valid Values for all fields", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void setAdapter(String[] user_courses) {
        Log.d("Usercourse:", "Usercourse1 is: " + user_courses[1]);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(CreateActivity.this,
                android.R.layout.simple_list_item_1, user_courses);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
    }
}