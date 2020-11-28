package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Getlocation extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,GoogleMap.OnMarkerDragListener {
    private GoogleMap mMap;
    private String TAG = "Getlocation";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_getlocation);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d(TAG, "Drag Started");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d(TAG, "Dragging");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        UserdetailsUtil.activitylat = marker.getPosition().latitude;
        UserdetailsUtil.activitylon = marker.getPosition().longitude;
        Log.d(TAG, "Drag Over at: Lat: " + UserdetailsUtil.activitylat + "Long: " + UserdetailsUtil.activitylon);

        new AlertDialog.Builder(Getlocation.this)
                .setTitle("DONE?")
                .setMessage("If you are satisfied with the location, press YES Button\nIf you would like to Drag the Marker to another location, press NO Button")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "No Pressed on Alert");
                        return;
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .create()
                .show();

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        String name = "Press - Hold - Drag to move";
        mMap.addMarker(new MarkerOptions().position(latLng).title(name).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f));

        UserdetailsUtil.activitylat = latLng.latitude;
        UserdetailsUtil.activitylon = latLng.longitude;
        Log.d(TAG, "Mark Set at: Lat: " + UserdetailsUtil.activitylat + "Long: " + UserdetailsUtil.activitylon);

        new AlertDialog.Builder(Getlocation.this)
                .setTitle("DONE?")
                .setMessage("If you are satisfied with the location, press YES Button\nIf you would like to Drag the Marker to another location, press NO Button")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "No Pressed on Alert");
                        return;
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .create()
                .show();
    }
}