package com.cpen321.ubclocationbroadcaster;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.view.View;


/**This Activity Class displays the location of an Acivity as a Marker on the Google Maps.
 * The only place where this is called from is from DisplayActivityDetails and AcitivityPage classes respectively, when the user clicks onMapButton.
 * The activity to be displayed is the Activity the user chose during the suggested list
 * Inorder to go back, the user can either press the back button on their device or click on the Button named : "BACK" on the device screen.*/
public class ActivityOnMap extends AppCompatActivity implements OnMapReadyCallback {

    private Button backButton;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_map);

        /**The following code snippet is obtained from https://developers.google.com/maps/documentation/android-sdk/marker
         * This tutorial explains how to display markers on Google Maps*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        backButton = findViewById(R.id.BackButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng marker = new LatLng(SortedlistclassUtil.lat, SortedlistclassUtil.lon);
        mMap.addMarker(new MarkerOptions().position(marker).title(SortedlistclassUtil.aname).snippet(SortedlistclassUtil.info));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,14.0f));
    }
}