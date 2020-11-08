package com.cpen321.ubclocationbroadcaster;

import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListScrollingActivity extends AppCompatActivity {
    RecyclerView activity_view;
    ArrayList<String> user_course_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_scrolling);
        activity_view = findViewById((R.id.activity_view));


        JSONObject User = new JSONObject(); // NEED TO GET CURRENT USER
        JSONArray courses = null;
        try {
            courses = User.getJSONArray("CourseRegistered");
            for(int i=0; i<courses.length(); i++){
                user_course_list.add(courses.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //final RequestQueue requestQueueMA = Volley.newRequestQueue(this);
        //String urlMA = "http://40.122.147.73:3030/activities/all";
        String urlMA = BackendURL.getURL() + "/activities/all";

        JsonArrayRequest allActivities = new JsonArrayRequest(urlMA,
                new Response.Listener<JSONArray> (){
                    @Override
                    public void onResponse(JSONArray response){
                        try {
                            HashMap<Integer, JSONObject> match_activity = new HashMap<>();
                            for(int i=0; i<response.length();i++){
                                int match_factor = 0;
                                String activity_course = response.getJSONObject(i).getString("Course");
                                match_factor += user_course_list.contains(activity_course) ? 1 : 0;
                                match_factor += 0;
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
    }
}