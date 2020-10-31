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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        final String username = UserClass.getUsername();
        final String[] user_courses = UserClass.getCourseRegistered();
        Log.d("User:", "Username is: " + username);
        Log.d("Usercourse:", "Usercourse1 is: " + user_courses[0]);

        /*For courses in the activity. The user should be able to choose the courses in the activity from his/her registered courses.*/

        JSONObject findUser = new JSONObject();
        mySpinner = findViewById(R.id.reg_course_spinner);
        reg_courses_view = findViewById((R.id.registered_courses));
        //final String[] user_courses = new String[10];
        //final String username = getIntent().getStringExtra("USERNAME");
        //Log.d("User:", "Username is: " + username);
        String urlU = "http://10.0.2.2:4000/usersearch";

        //Put the username into the json object
        try {
            findUser.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Get the user details and the store the registered courses into
        /*JsonObjectRequest userDetails = new JsonObjectRequest(Request.Method.POST, urlU, findUser,
                new Response.Listener<JSONObject> (){
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            JSONArray jsonArray = response.getJSONArray("courses");
                            user_courses[0] = "Choose from your courses";
                            for(int i = 0; i < jsonArray.length(); i++){
                                user_courses[i+1] = jsonArray.getString(i);
                                Log.d("courses", "Display reg course " + user_courses[i]);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        requestQueue.add(userDetails);*/


        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(CreateActivity.this,
                android.R.layout.simple_list_item_1, user_courses);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        final ArrayList<String> activity_courses = new ArrayList<String>();
        final ArrayAdapter<String> reg_course_list_adapter = new ArrayAdapter<String>(CreateActivity.this,
                android.R.layout.simple_list_item_1, activity_courses);
        reg_courses_view.setAdapter(reg_course_list_adapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String result = mySpinner.getSelectedItem().toString();
                if((position>0)&&(!activity_courses.contains(result))){
                    activity_courses.add(result);
                }
                reg_course_list_adapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final JSONArray courses = new JSONArray();
        for(String course : activity_courses){
            courses.put(course);
            //courses.put("test");
        }

        Button done_btn;
        done_btn = findViewById(R.id.activity_done);



        //format request
        final JSONObject jsnRequest = new JSONObject();

        EditText name = findViewById(R.id.activity_name);
        EditText aid  = findViewById(R.id.activity_id);
        EditText info = findViewById(R.id.activity_desc);



        String inputaid = aid.getText().toString();
        String inputInfo = info.getText().toString();
        //String inputLat = lat.getText().toString();
        //String inputLong = lon.getText().toString();
        String inputLat = "23";
        String inputLong = "24";
        String inputName = name.getText().toString();

        String inputSchool = getIntent().getStringExtra("SCHOOL");
        String inputMajor = getIntent().getStringExtra("MAJOR");
        String course1 = getIntent().getStringExtra("COURSE1");
        String course2 = getIntent().getStringExtra("COURSE2");
        String course3 = getIntent().getStringExtra("COURSE3");
        String course4 = getIntent().getStringExtra("COURSE4");
        String course5 = getIntent().getStringExtra("COURSE5");

        try {
            jsnRequest.put("name", inputName);
            jsnRequest.put("aid", inputaid);
            jsnRequest.put("users", username);
            jsnRequest.put("course", courses);
            jsnRequest.put("school", inputSchool);
            jsnRequest.put("major", inputMajor);
            jsnRequest.put("info", inputInfo);

            jsnRequest.put("lat", inputLat);
            jsnRequest.put("long", inputLong);
            jsnRequest.put("lat", inputLat);
            jsnRequest.put("long", inputLong);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = "http://10.0.2.2:5000/addactivity";
                JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, URL, jsnRequest,
                        new Response.Listener<JSONObject> (){
                            @Override
                            public void onResponse(JSONObject response){
                                try {
                                    boolean successVal = (boolean) response.get("success"); // check if user signed in successfully
                                    String stat = response.get("status").toString(); // get status
                                    if(successVal) {
                                        Intent sign_in_Intent = new Intent(CreateActivity.this, MenuActivity.class);
                                        Toast.makeText(CreateActivity.this, "Activity added", Toast.LENGTH_SHORT).show();
                                        startActivity(sign_in_Intent);
                                    } else {
                                        Toast.makeText(CreateActivity.this, "ERROR: " + stat, Toast.LENGTH_SHORT).show();
                                    }
                                    Log.d("SignInActivity", stat);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(CreateActivity.this, "Unable to send the sign in data to the server!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
            }
        });

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu_Intent = new Intent(CreateActivity.this, MenuActivity.class);
                startActivity(menu_Intent);
                Log.d("sdone button", "done button has been clicked");
            }
        });
    }
}