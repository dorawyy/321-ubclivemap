package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
    private EditText name;
    private EditText phone_number;
    private AutoCompleteTextView school;
    private AutoCompleteTextView major;
    private ListView course_list_view;
    private ArrayList<String> course_list;

    private String inputName;
    private String inputPhone;
    private String inputSchool;
    private String inputMajor;

    public static final String[] SCHOOLS = new String[]{"UBC","SFU","KPU","UVIC","UoT","BCIT"};
    public static final String[] MAJORS = new String[]{"CPEN","MECH","ELEC","IGEN","CHEM","BMED","CIVL","CPSC"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        setVar();

        course_list = new ArrayList<String>();
        final ArrayAdapter<String> course_list_adapter = new ArrayAdapter<String>(ProfileActivity.this,
                android.R.layout.simple_list_item_1, course_list);
        course_list_view.setAdapter(course_list_adapter);

        spinnerSet(course_list_adapter);

        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        //done button setup
        Button done_btn;
        done_btn = findViewById(R.id.course_page_done_button);
        doneBtnSet(requestQueue, done_btn);
    }

    private void doneBtnSet(final RequestQueue requestQueue, Button done_btn) {
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = UserdetailsUtil.getURL() + "/profiles/add";
                setInputs();

                JSONArray jsnReq = new JSONArray();
                for(String course : course_list){
                    jsnReq.put(course);
                }
                Log.d("debgg1", "username: " + UserdetailsUtil.username);

                JSONObject POSTjsnReq = new JSONObject();
                try {
                    helperFunction2(jsnReq, POSTjsnReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                                        helperFunction(response, inputName, inputPhone, inputSchool, inputMajor);
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

    private void setInputs() {
        inputName = name.getText().toString();
        inputPhone = phone_number.getText().toString();
        inputSchool = school.getText().toString();
        inputMajor = major.getText().toString();
    }

    private void spinnerSet(final ArrayAdapter<String> course_list_adapter) {
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
    }

    private void helperFunction2(JSONArray jsnReq, JSONObject POSTjsnReq) throws JSONException {
        POSTjsnReq.put("name", inputName);
        POSTjsnReq.put("phone", inputPhone);
        POSTjsnReq.put("school", inputSchool);
        POSTjsnReq.put("major", inputMajor);
        POSTjsnReq.put("CourseRegistered",jsnReq);
        POSTjsnReq.put("private", false);
        POSTjsnReq.put("username", UserdetailsUtil.username);
        POSTjsnReq.put("inActivity", false);
        POSTjsnReq.put("activityID", "-1");
    }

    private void helperFunction(JSONObject response, String inputName, String inputPhone, String inputSchool, String inputMajor) throws JSONException {
        boolean successVal = (boolean) response.get("success");
        String stat = response.get("status").toString();
        if(successVal){
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
            Intent doneIntent = new Intent(ProfileActivity.this, MenuActivity.class);
            startActivity(doneIntent);
        }
        else{
            Log.d("error1", "Error1 " + stat);
        }
    }

    private void setVar() {
        name = findViewById((R.id.sign_up_name_button));
        phone_number = findViewById(R.id.phone_number_button);

        school = findViewById(R.id.school_button);
        ArrayAdapter<String> schoolsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,SCHOOLS);
        school.setAdapter(schoolsAdapter);

        major = findViewById(R.id.major_button);
        ArrayAdapter<String> majorsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,MAJORS);
        major.setAdapter(majorsAdapter);

        mySpinner = findViewById(R.id.course_spinner);
        course_list_view = findViewById((R.id.course_list));

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(ProfileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.spinner));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
    }

    @Override
    public void onBackPressed() {
        JSONObject jsnReq = new JSONObject();
        try{
            jsnReq.put("name",UserdetailsUtil.username);
        }catch (JSONException e){
            e.printStackTrace();
        }

        final JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, UserdetailsUtil.getURL() + "/users/delete", jsnReq,
                new Response.Listener<JSONObject> (){
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            boolean successVal = (boolean) response.get("success");
                            if (successVal){
                                finish();
                            }
                            else {
                                Log.d("ProfileActivity", "Could not delete username");
                            }
                            UserdetailsUtil.cleanup();
                        } catch (JSONException e) {
                            Log.d("ProfileActivity", "Error parsing the response from users/delete");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(ProfileActivity.this, "Unable to send the sign up data to the server!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        RequestQueue q = Volley.newRequestQueue(this);
        q.add(json_obj);
        super.onBackPressed();
    }
}