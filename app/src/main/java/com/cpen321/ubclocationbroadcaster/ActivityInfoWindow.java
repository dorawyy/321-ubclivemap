package com.cpen321.ubclocationbroadcaster;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityInfoWindow extends AppCompatActivity {

    private TextView name_btn;
    private TextView info_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        Button back_btn = findViewById(R.id.back_to_map);
        Button details_btn = findViewById((R.id.ActDetail));
        name_btn = findViewById(R.id.actName);
        info_btn = findViewById((R.id.ActDetails));

        getWindow().setLayout((int)(width*.6), (int)(height*.6));

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Log.d("ActivityInfoWindow", "Back button has been clicked");
            }
        });

        details_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortedlistclassUtil.activity_to_be_displayed = MainMapsActivity.markerID;
                Intent myIntent = new Intent(ActivityInfoWindow.this, DisplayActivityDetails.class);
                startActivity(myIntent);
                Log.d("ActivityInfoWindow", "See Details button has been clicked");
            }
        });

        getVolleyReq();
    }

    private void getVolleyReq() {

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject aid_for_search = new JSONObject();
        try{
            aid_for_search.put("aid", MainMapsActivity.markerID);
        }catch (JSONException e) {
            Log.d("Check0","Not able to form JSON Object aid_for_search");
            e.printStackTrace();
        }

        //Get the activity Details from the backend server
        final JsonObjectRequest activity_object = new JsonObjectRequest(Request.Method.POST, UserdetailsUtil.getURL() + "/activities/search", aid_for_search,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject activity = response.getJSONObject("value");
                            String name = activity.getString("name");
                            String info = activity.getString("info");

                            name_btn.setText(name);
                            info_btn.setText(info);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ActivityInfoWindow.this, "Connection error, try again later!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        requestQueue.add(activity_object);
    }
}
