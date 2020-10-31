package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {


    private Spinner mySpinner;
    private ListView course_list_view;
    private EditText name;
    private EditText phone_number;
    private EditText school;
    private EditText major;
    private Button done_btn;

    //TODO: don't go to the next page if data is invalid
    //TODO: use mySkeleton if it works
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById((R.id.sign_up_name_button));
        phone_number = findViewById(R.id.phone_number_button);
        school = findViewById(R.id.school_button);
        major = findViewById(R.id.major_button);
        final SharedPreferences userSettings = getSharedPreferences("UserPreferences", MODE_PRIVATE);


        //drop down menu and view list
        mySpinner = findViewById(R.id.course_spinner);
        course_list_view = findViewById((R.id.course_list));

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(ProfileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.spinner));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        final ArrayList<String> course_list = new ArrayList<String>();
        final ArrayAdapter<String> course_list_adapter = new ArrayAdapter<String>(ProfileActivity.this,
                android.R.layout.simple_list_item_1, course_list);
        course_list_view.setAdapter(course_list_adapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String result = mySpinner.getSelectedItem().toString();
                //all cpen courses
                switch (position){
                    case 0:
                        break;
                    case 1:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 2:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 3:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 4:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 5:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 6:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 7:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 8:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 9:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 10:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 11:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 12:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 13:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 14:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 15:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 16:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 17:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 18:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 19:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 20:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 21:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 22:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 23:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 24:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 25:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 26:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                    case 27:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;

                }
                course_list_adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        //done button setup
        done_btn = findViewById(R.id.course_page_done_button);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent doneIntent = new Intent(ProfileActivity.this, MenuActivity.class);
                startActivity(doneIntent);
                Log.d("done button", "done button has been clicked");

        String URL = "http://10.0.2.2:4000/userprofile";


        final String inputName = name.getText().toString();
        final String inputPhone = phone_number.getText().toString();
        final String inputSchool = school.getText().toString();
        final String inputMajor = major.getText().toString();
        final String userID =  userSettings.getString("USERNAME", "");
        final boolean inputPrivate = false;
        final boolean inputInActivity = false;
        final int inputActivityID = -1;

        doneIntent.putExtra("SCHOOL", inputSchool);
        doneIntent.putExtra("MAJOR", inputMajor);
        //doneIntent.putExtra("COURSE1", course1);
        //doneIntent.putExtra("COURSE2", course2);
        //doneIntent.putExtra("COURSE3", course3);
        //doneIntent.putExtra("COURSE4", course4);
        //doneIntent.putExtra("COURSE5", course5);

        JSONArray jsnReq = new JSONArray();
        for(String course : course_list){
            jsnReq.put(course);
        }

        JSONObject POSTjsnReq = new JSONObject();
        try {
            POSTjsnReq.put("name", inputName);
            POSTjsnReq.put("phone", inputPhone);
            POSTjsnReq.put("school", inputSchool);
            POSTjsnReq.put("major", inputMajor);
            POSTjsnReq.put("CourseRegistered",jsnReq);
            POSTjsnReq.put("private", inputPrivate);
            POSTjsnReq.put("userid", userID);
            POSTjsnReq.put("inActivity", inputInActivity);
            POSTjsnReq.put("activityID", inputActivityID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

                JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, URL, POSTjsnReq,
                        new Response.Listener<JSONObject> (){
                            @Override
                            public void onResponse(JSONObject response){
                                //Intent signUpIntent = new Intent(SignUpActivity.this, ProfileActivity.class);
                                //startActivity(signUpIntent);
                                try {
                                    boolean successVal = (boolean) response.get("success");
                                    String stat = response.get("status").toString();
                                    Log.d("SignUpActivity", stat);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(ProfileActivity.this, "Unable to send the user info to the server!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        requestQueue.add(json_obj);
            }
        });
    }

}