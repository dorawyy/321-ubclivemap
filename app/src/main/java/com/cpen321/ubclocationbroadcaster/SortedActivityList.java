package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.List;

public class SortedActivityList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorted_list);

        final TextView act1;
        final TextView act2;
        final TextView act3;

        act1 = findViewById(R.id.suggested_activity1);
        act2 = findViewById(R.id.suggested_activity2);
        act3 = findViewById(R.id.suggested_activity3);

        String URL = "https://";
        final List<String> jsonResponses = new ArrayList<>();
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String[] data = null;
        JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject> (){
                    @Override
                    public void onResponse(JSONObject response){
                        //Intent intent = new Intent(SortedActivityList.this, ActivityPage.class);
                        //startActivity(intent);
                        try {
                            JSONObject obj = response.getJSONObject("colorObject");
                            String color = obj.getString("colorName");
                            String desc = obj.getString("description");

                            // Adds strings from object to the "data" string
                            data[0] += "Color Name: " + color;

                            // Adds the data string to the TextView "results"
                            act1.setText(data[0]);
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