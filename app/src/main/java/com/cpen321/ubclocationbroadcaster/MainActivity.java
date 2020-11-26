package com.cpen321.ubclocationbroadcaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private static LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!UserdetailsUtil.tokenGenerated){
            generateFirebaseToken();
        }

        /** ENABLE A SWITCH FOR DARK MODE **/
        Switch sw = (Switch) findViewById(R.id.switch1);
        if (sw != null) {
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // The toggle is enabled
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        // The toggle is disabled
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            });
        }

        checkLocationPermission();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,900000,0,this);

        /**INITIALIZATION : START*/
        final EditText username;
        username = findViewById(R.id.username_button);

        final EditText password;
        password = findViewById(R.id.password_button);

        Button sign_in_btn;
        sign_in_btn = findViewById(R.id.sign_in_button);

        Button sign_up_btn;
        sign_up_btn = findViewById(R.id.sign_up_button);

        final RequestQueue q = Volley.newRequestQueue(this);
        /**INITIALIZATION : END*/

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usrname = username.getText().toString();
                String passwrd = password.getText().toString();

                if(usrname.isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter a username", Toast.LENGTH_SHORT).show();
                }
                else if(passwrd.isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter a password", Toast.LENGTH_SHORT).show();
                }
                else {
                    //format request
                    JSONObject jsnRequest = new JSONObject();
                    try {
                        jsnRequest.put("name", usrname);
                        jsnRequest.put("password", passwrd);
                        if(UserdetailsUtil.tokenGenerated){
                            jsnRequest.put("token", UserdetailsUtil.token);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, UserdetailsUtil.getURL() + "/users/login", jsnRequest,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        boolean successVal = (boolean) response.get("success"); // check if user signed in successfully
                                        String stat = response.get("status").toString(); // get status
                                        if (successVal) {
                                            JSONObject userProfile = response.getJSONObject("value");
                                            setProfileCache(userProfile);

                                            Intent sign_in_Intent = new Intent(MainActivity.this, MenuActivity.class);
                                            UserdetailsUtil.signedIn = true;
                                            startActivity(sign_in_Intent);
                                        } else {
                                            Log.d("MainActivity", "Error: " + stat);
                                            Toast.makeText(MainActivity.this, "ERROR: " + stat, Toast.LENGTH_SHORT).show();
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
                    q.add(json_obj);
                }
            }
        });

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_up_Intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(sign_up_Intent);
                Log.d("MainActivity", "Sign up button has been clicked");
            }
        });
    }

    private void setProfileCache(JSONObject userProfile) {
        try {
            UserdetailsUtil.username = userProfile.getString("username");
            int numOfCourses = userProfile.getJSONArray("CourseRegistered").length();
            UserdetailsUtil.name = userProfile.getString("name");
            UserdetailsUtil.phone = userProfile.getString("phone");
            UserdetailsUtil.school = userProfile.getString("school");
            UserdetailsUtil.major = userProfile.getString("major");
            UserdetailsUtil.privatePublic = userProfile.getBoolean("private");
            UserdetailsUtil.inactivity = userProfile.getBoolean("inActivity");
            UserdetailsUtil.activityID = userProfile.getString("activityID");
            UserdetailsUtil.courseRegistered = new String[numOfCourses];
            for (int i = 0; i < numOfCourses; i++) {
                UserdetailsUtil.courseRegistered[i] = userProfile.getJSONArray("CourseRegistered").getString(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateFirebaseToken() {
        FirebaseInstallations.getInstance().delete();
        FirebaseMessaging.getInstance().deleteToken()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if(!task.isSuccessful()){
                                        return;
                                    }
                                    UserdetailsUtil.tokenGenerated = true;
                                    UserdetailsUtil.token = task.getResult();
                                    String msg = getString(R.string.fcm_token, UserdetailsUtil.token);
                                    Log.d("MainActivity", task.getResult());
                                    //Log.d("MainActivity", "whats this " + FirebaseInstanceId.getInstance().getId());
                                }
                            });
                }
            });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(MainActivity.this, "Permissions has already been granted!", Toast.LENGTH_LONG).show();
            return;
        }
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Need Location Permissions")
                        .setMessage("Location Permissions are needed to run the app")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this, "We need your permissions to run the app!", Toast.LENGTH_LONG).show();
                                checkLocationPermission();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        return;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("LOCATIONTEST","Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
        UserdetailsUtil.lat =  location.getLatitude();
        UserdetailsUtil.lon = location.getLongitude();
    }
}