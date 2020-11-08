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

public class MainMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        final RequestQueue requestQueueMA = Volley.newRequestQueue(this);
        String urlMA = userDetails.getURL() + "/activities/all";

        MyJSONArrayRequest allActivities = new MyJSONArrayRequest(Request.Method.GET,urlMA,null,
                new Response.Listener<JSONArray> (){
                    @Override
                    public void onResponse(JSONArray response){
                        try {
                            for(int i=0; i<response.length();i++){
                                JSONObject jsonObject = response.getJSONObject(i);
                                String lat = jsonObject.getString("lat");
                                Double la = Double.parseDouble(lat);
                                String lon = jsonObject.getString("long");
                                Double lo = Double.parseDouble(lon);
                                String name = jsonObject.getString("name");
                                LatLng sydney = new LatLng(la, lo);
                                mMap.addMarker(new MarkerOptions().position(sydney).title(name));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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

        requestQueueMA.add(allActivities);

    }
}