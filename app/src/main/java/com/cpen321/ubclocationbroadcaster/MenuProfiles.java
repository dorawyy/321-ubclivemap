package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MenuProfiles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu_profiles);

        Button updateProfileButton;
        Button viewProfileButton;
        Button deleteAccountButton;

        Animation topAnim, bottomAnim, topAnim2;

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        topAnim2 = AnimationUtils.loadAnimation(this,R.anim.top_anime_another);

        updateProfileButton = findViewById(R.id.UpdateProfile);
        updateProfileButton.setAnimation(topAnim);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuProfiles.this, UpdateProfile.class);
                startActivity(intent);
            }
        });

        viewProfileButton = findViewById(R.id.ViewProfile);
        viewProfileButton.setAnimation(bottomAnim);
        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuProfiles.this, ViewProfile.class);
                startActivity(intent);

            }
        });

        deleteAccountButton = findViewById(R.id.DeleteAccount);
        deleteAccountButton.setAnimation(topAnim2);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MenuProfiles.this)
                        .setTitle("Are You Sure you want to Delete your profile?")
                        .setMessage("You will not be able to retrieve your data.")
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) { return;
                                }
                            })
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    leaveActivity();
                                    deleteProfileAndAccount();
                                    UserdetailsUtil.cleanup();
                                    SortedlistclassUtil.cleanup();
                                    Intent newIntent = new Intent(MenuProfiles.this,MainActivity.class);
                                    finish();
                                    startActivity(newIntent);
                                }
                        }).create().show();

            }
        });

    }




    private void leaveActivity(){
        if(UserdetailsUtil.inactivity){
            JSONObject deleteObject = new JSONObject();
            try{
                deleteObject.put("username",UserdetailsUtil.username);
                deleteObject.put("aid", UserdetailsUtil.activityID);
            }catch(JSONException e){
                e.printStackTrace();
            }
            RequestQueue deleteRequest = Volley.newRequestQueue(MenuProfiles.this);
            JsonObjectRequest activity_object = new JsonObjectRequest(Request.Method.POST, UserdetailsUtil.getURL() + "/activities/leaveupdate", deleteObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean leaveStatus = (boolean) response.get("success");
                                if(leaveStatus){
                                    UserdetailsUtil.inactivity = false;
                                    UserdetailsUtil.activityID = "";
                                    Log.d("MenuProfiles","Left Activity");
                                }
                            } catch (JSONException e) {
                                Log.d("Leave", "Error: Could not extract LeaveStatus");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MenuProfiles.this, "Connection error while leaving, try again later!", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            });
            deleteRequest.add(activity_object);
        }
    }

    private void deleteProfileAndAccount(){
        JSONObject jsnReq1 = new JSONObject();
        JSONObject jsnReq2 = new JSONObject();
        try{
            jsnReq1.put("name",UserdetailsUtil.username);
            jsnReq2.put("username",UserdetailsUtil.username);
        }catch (JSONException e){
            e.printStackTrace();
        }

        sendDeleteRequests(jsnReq2,UserdetailsUtil.getURL()+"/profiles/delete");
        sendDeleteRequests(jsnReq1,UserdetailsUtil.getURL()+"/users/delete");
    }

    private void sendDeleteRequests(JSONObject obj, String URL){
        JSONObject req_obj = obj;
        final String url = URL;
        final JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, url, req_obj,
                new Response.Listener<JSONObject> (){
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            boolean successVal = (boolean) response.get("success");
                            if (successVal){
                                Log.d("ProfileActivity", "Deleted user at " + url);
                            }
                            else {
                                Log.d("ProfileActivity", "Could not delete user from " + url);
                            }
                            UserdetailsUtil.cleanup();
                        } catch (JSONException e) {
                            Log.d("ProfileActivity", "Error parsing the response from " + url);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(MenuProfiles.this, "Unable to send the sign up data to the server!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        RequestQueue q = Volley.newRequestQueue(this);
        q.add(json_obj);
    }


}