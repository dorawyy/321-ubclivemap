package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
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

public class UpdateProfile extends AppCompatActivity {

    EditText name,phone;
    AutoCompleteTextView school,major;
    Spinner mySpinner;
    ListView course_list_view;
    ArrayList<String> course_list;

    String inputName,inputPhone,inputSchool,inputMajor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_update_profile);

        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        setupViews();

        Button done = findViewById(R.id.updateDone);
        donePressed(requestQueue,done, this);
    }
    private void setupViews(){
        name = findViewById(R.id.updateNameValue);
        phone = findViewById(R.id.updatePhoneValue);

        school = findViewById(R.id.updateSchoolValue);
        ArrayAdapter<String> schoolsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,ProfileActivity.SCHOOLS);
        school.setAdapter(schoolsAdapter);

        major = findViewById(R.id.updateMajorValue);
        ArrayAdapter<String> majorsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,ProfileActivity.MAJORS);
        major.setAdapter(majorsAdapter);

        mySpinner = findViewById(R.id.update_course_spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.spinner));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        course_list_view = findViewById(R.id.update_course_list);
        course_list = new ArrayList<String>();
        final ArrayAdapter<String> course_list_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, course_list);
        course_list_view.setAdapter(course_list_adapter);
        setupSpinner(course_list_adapter);
    }

    private void setupSpinner(final ArrayAdapter<String> course_list_adapter){
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
                Log.d("UpdateProfile","Nothing Selected");
            }
        });
    }

    private void donePressed (final RequestQueue requestQueue, Button done, Context ct){
        final Context context = ct;
        final Activity act = (Activity)ct;
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUpdateValues();
                if (inputName.isEmpty() && inputPhone.isEmpty() && inputSchool.isEmpty() && inputMajor.isEmpty() && course_list.isEmpty()){
                    act.finish();
                    Toast.makeText(UpdateProfile.this, "Nothing to Update :)", Toast.LENGTH_SHORT).show();
                }
                else {
                    JSONObject postJSONObject = setupPostJSONObject();
                    JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, UserdetailsUtil.getURL() + "/profiles/update", postJSONObject,
                            new Response.Listener<JSONObject> (){
                                @Override
                                public void onResponse(JSONObject response){
                                    try {
                                        helperFunction(response);
                                        act.finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.d("UpdateProfile", "Unable to parse response" );
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Toast.makeText(UpdateProfile.this, "Unable to send the user info to the server!", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    });

                    requestQueue.add(json_obj);
                }

            }
        });
    }

    private void getUpdateValues(){
        inputName = name.getText().toString();
        inputPhone = phone.getText().toString();
        inputSchool = school.getText().toString();
        inputMajor = major.getText().toString();
    }

    private JSONObject setupPostJSONObject(){

        JSONObject ret = new JSONObject();
        enterJSONfield(ret,"username",UserdetailsUtil.username);

        if (inputName.isEmpty())
            enterJSONfield(ret, "name", UserdetailsUtil.name);
        else
            enterJSONfield(ret, "name", inputName);

        if(inputPhone.isEmpty())
            enterJSONfield(ret,"phone",UserdetailsUtil.phone);
        else
            enterJSONfield(ret,"phone",inputPhone);

        if(inputSchool.isEmpty())
            enterJSONfield(ret,"school",UserdetailsUtil.school);
        else
            enterJSONfield(ret,"school",inputSchool);

        if(inputMajor.isEmpty())
            enterJSONfield(ret,"major",UserdetailsUtil.major);
        else
            enterJSONfield(ret,"major",inputMajor);

        try{
            ret.put("private", UserdetailsUtil.privatePublic);
            ret.put("inActivity", UserdetailsUtil.inactivity);
            ret.put("activityID", UserdetailsUtil.activityID);
        }catch (JSONException e){
            e.printStackTrace();
        }

        JSONArray courseObj = new JSONArray();
        if(course_list.isEmpty()){
            for(String course : UserdetailsUtil.courseRegistered){
                courseObj.put(course);
            }
        }
        else{
            for(String course : course_list){
                courseObj.put(course);
            }
        }
        try{
            ret.put("CourseRegistered",courseObj);
        }catch (JSONException ee){
            ee.printStackTrace();
        }
        return ret;
    }

    private void enterJSONfield(JSONObject obj, String fieldName, String fieldValue){
        try{
            obj.put(fieldName,fieldValue);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void helperFunction(JSONObject response) throws JSONException{
        if((boolean) response.get("success")){
            UserdetailsUtil.name = response.getJSONObject("value").getString("name");
            UserdetailsUtil.phone = response.getJSONObject("value").getString("phone");
            UserdetailsUtil.school = response.getJSONObject("value").getString("school");
            UserdetailsUtil.major = response.getJSONObject("value").getString("major");
            UserdetailsUtil.privatePublic = response.getJSONObject("value").getBoolean("private");
            UserdetailsUtil.inactivity = response.getJSONObject("value").getBoolean("inActivity");
            UserdetailsUtil.activityID = response.getJSONObject("value").getString("activityID");
            UserdetailsUtil.courseRegistered = new String[response.getJSONObject("value").getJSONArray("CourseRegistered").length()];
            for(int i=0; i<response.getJSONObject("value").getJSONArray("CourseRegistered").length(); i++){
                UserdetailsUtil.courseRegistered[i] = response.getJSONObject("value").getJSONArray("CourseRegistered").getString(i);
            }

            Toast.makeText(UpdateProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
        }
        else{
            Log.d("UpdateProfile","Error: " + response.get("status").toString());
        }
    }
}