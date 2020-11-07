package com.cpen321.ubclocationbroadcaster;

import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListScrollingActivity extends AppCompatActivity {
    private RecyclerView activity_view;
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

        final RequestQueue requestQueueMA = Volley.newRequestQueue(this);
        //String urlMA = "http://40.122.147.73:3030/activities/all";
        String urlMA = "http://10.0.2.2:3000/activities/all";

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