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

public class ProfileActivity extends AppCompatActivity {

    private Spinner mySpinner;
    private EditText name = findViewById((R.id.sign_up_name_button));
    private EditText phone_number = findViewById(R.id.phone_number_button);
    private EditText school = findViewById(R.id.school_button);
    private EditText major = findViewById(R.id.major_button);
    private String inputName = name.getText().toString();
    private String inputPhone = phone_number.getText().toString();
    private String inputSchool = school.getText().toString();
    private String inputMajor = major.getText().toString();
    private boolean inputPrivate = false;
    private boolean inputInActivity = false;
    private String inputActivityID = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ListView course_list_view = findViewById((R.id.course_list));
        //drop down menu and view list
        mySpinner = findViewById(R.id.course_spinner);
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
                    default:
                        if (!course_list.contains(result)){
                            course_list.add(result);}
                        break;
                }
                course_list_adapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("ProfileActivity","Nothing Selected");
            }
        });
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        //done button setup
        Button done_btn;
        done_btn = findViewById(R.id.course_page_done_button);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("done button", "done button has been clicked");
        JSONArray jsnReq = new JSONArray();
        for(String course : course_list){
            jsnReq.put(course);
        }
                Log.d("debgg1", "username: " + UserdetailsUtil.username);

        JSONObject POSTjsnReq = new JSONObject();
        try {
           getjsnReq(POSTjsnReq, jsnReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
               helperFunction2(POSTjsnReq);
    }

    private void getjsnReq(JSONObject POSTjsnReq, JSONArray jsnReq) throws JSONException {
        POSTjsnReq.put("name", inputName);
        POSTjsnReq.put("phone", inputPhone);
        POSTjsnReq.put("school", inputSchool);
        POSTjsnReq.put("major", inputMajor);
        POSTjsnReq.put("CourseRegistered",jsnReq);
        POSTjsnReq.put("private", inputPrivate);
        POSTjsnReq.put("username", UserdetailsUtil.username);
        POSTjsnReq.put("inActivity", inputInActivity);
        POSTjsnReq.put("activityID", inputActivityID);
    }

    private void helperFunction(JSONObject response, ArrayList<String> course_list) throws JSONException {
        boolean successVal = (boolean) response.get("success");
        String stat = response.get("status").toString();
        if(successVal){
            Intent doneIntent = new Intent(ProfileActivity.this, MenuActivity.class);
            startActivity(doneIntent);

            UserdetailsUtil.name = inputName;
            UserdetailsUtil.phone = inputPhone;
            UserdetailsUtil.school = inputSchool;
            UserdetailsUtil.major = inputMajor;
            UserdetailsUtil.privatePublic = false;
            UserdetailsUtil.inactivity = false;
            UserdetailsUtil.activityID = "-1";
            UserdetailsUtil.courseRegistered = new String[course_list.size()];
            for(int i=0;i<course_list.size();i++){
                UserdetailsUtil.courseRegistered[i] = course_list.get(i);
                Log.d("courseList", "Element" + i + ": " + course_list.get(i));
                Log.d("courseRegistered", "Element" + i + ": " + UserdetailsUtil.courseRegistered[i]);
            }


            Log.d("SignUpActivity", stat);
        }
        else{
            Log.d("error1", "Error1 " + stat);
        }
    }

    private void helperFunction2(JSONObject POSTjsnReq) {
        String URL = UserdetailsUtil.getURL() + "/profiles/add";
        if (inputName.isEmpty() || inputPhone.isEmpty() || inputSchool.isEmpty() || inputMajor.isEmpty()){
                Toast.makeText(ProfileActivity.this, "ERROR: Enter all fields!", Toast.LENGTH_SHORT).show();
            }

            else if (course_list.isEmpty()){
                Toast.makeText(ProfileActivity.this, "Select your courses!", Toast.LENGTH_SHORT).show();
            }
            else {
                JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, URL, POSTjsnReq,
                        new Response.Listener<JSONObject> (){
                            @Override
                            public void onResponse(JSONObject response){
                                try {
                                    helperFunction(response, course_list);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("error2", "-------------------- " );
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
            }}
    });
        }
    }