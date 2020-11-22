package com.cpen321.ubclocationbroadcaster;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**Activity Name: MainMapsActivity
 * This Activity Displays a Google Maps screen on the user device.
 * This screen contains several markers. These Markers correspond to all of active activities
 * The user can click on the marker and view the activity information.
 * The user can also join an activity by clicking on a marker and following the prompts*/
public class MainMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps);

        /**The following code snippet is obtained from https://developers.google.com/maps/documentation/android-sdk/marker
         * This tutorial explains how to display markers on Google Maps*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**This function is called when the Map has been created.
     * Most of our code is implemented in this function*/
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        MyJSONArrayRequest allActivities = new MyJSONArrayRequest(Request.Method.GET, UserdetailsUtil.getURL() + "/activities/all",null,
                new Response.Listener<JSONArray> (){
                    @Override
                    public void onResponse(JSONArray response){

                        /**Get all the activities JSONArray and then parse the array to read and display the required info*/
                        try {
                            for(int i=0; i<response.length();i++){
                                JSONObject jsonObject = response.getJSONObject(i);

                                String name = jsonObject.getString("name");

                                LatLng marker = new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("long"));

                                mMap.addMarker(new MarkerOptions().position(marker).title(name).draggable(true));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,14.0f));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(allActivities);

    }
}