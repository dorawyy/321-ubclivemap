package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

                String result = mySpinner.getOnItemSelectedListener().toString();
                //for M6 we only have 10 cpen courses
                switch (position){
                    case 0:
                        break;
                    case 1:
                        course_list.add(result);
                        break;
                    case 2:
                        course_list.add(result);
                        break;
                    case 3:
                        course_list.add(result);
                        break;
                    case 4:
                        course_list.add(result);
                        break;
                    case 5:
                        course_list.add(result);
                        break;
                    case 6:
                        course_list.add(result);
                        break;
                    case 7:
                        course_list.add(result);
                        break;
                    case 8:
                        course_list.add(result);
                        break;
                    case 9:
                        course_list.add(result);
                        break;
                    case 10:
                        course_list.add(result);
                        break;
                }
                course_list_adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Gson g = new Gson();
        String course_list_j = g.toJson(course_list);

        //TODO:url here
        String URL = "http://";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest json_obj = new JsonArrayRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONArray> (){
                    @Override
                    public void onResponse(JSONArray response){
                        for (int i = 0; i < response.length(); i++){
                        try {
                            JSONObject jsonObj = response.getJSONObject(i);
                            //for M6 it only sends 5 courses to the DB
                            String course1 = jsonObj.getString("Course 1");
                            String course2 = jsonObj.getString("Course 2");
                            String course3 = jsonObj.getString("Course 3");
                            String course4 = jsonObj.getString("Course 4");
                            String course5 = jsonObj.getString("Course 5");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(ProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        requestQueue.add(json_obj);

    }
    
}