package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SortedActivityList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorted_list);

        final String URL = "http://10.0.2.2:3000/sortactivities";
        final String[] activity_list;
        final ListView activity_list_show;
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        activity_list_show = findViewById(R.id.activity_list_show);

        JsonArrayRequest json_obj = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray> (){
                    @Override
                    public void onResponse(JSONArray response){
                        //Intent intent = new Intent(SortedActivityList.this, ActivityPage.class);
                        //startActivity(intent);
                        final ArrayList<String> activity_list = new ArrayList<String>();
                        JSONArray jsonArray = response;
                        try {
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String aid = jsonObject.getString("aid");
                                String name = jsonObject.getString("name");
                                String leader = jsonObject.getString("leader");
                                String usernames = jsonObject.getString("usernames");
                                String info = jsonObject.getString("info");
                                String major = jsonObject.getString("major");
                                String course = jsonObject.getString("course");
                                String school = jsonObject.getString("school");
                                String locLat = jsonObject.getString("lat");
                                String locLong = jsonObject.getString("long");
                                String status = jsonObject.getString("status");

                                activity_list.add(name);
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SortedActivityList.this,
                                        android.R.layout.simple_list_item_1, activity_list);
                                activity_list_show.setAdapter(arrayAdapter);

                                activity_list_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Toast.makeText(SortedActivityList.this, "Unable to get the data from the server!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(SortedActivityList.this, "Unable to get the data from the server!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        requestQueue.add(json_obj);
    }
}