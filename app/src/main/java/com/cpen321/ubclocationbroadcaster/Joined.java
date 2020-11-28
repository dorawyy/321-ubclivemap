package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

/*If the User presses join, first see if they are already in an activity or not.
 * If not, then do not process the joining. The user is sent a Toast message letting
 * them know they are already in an activity.
 * If the user is not in an activity, the activity's users is modified to also contain
 * the username of the user.
 * We do not need to make this change on the front end SortedListClass.users String array since it is re-initialized every time this page is opened.
 * We then change the user's 'inActivity' flag to 'true' and 'activityID' to the activityId of the
 * current activity both locally in UserDetails class as well as on the backend server.*/


public class Joined extends AppCompatActivity {


    private JSONObject getJoinObject(String aid, String username){
        JSONObject joinObject = new JSONObject();
        try{
            joinObject.put("aid", aid);
            joinObject.put("username", username);
            Log.d("Join", "Created joinObject : " + joinObject.getString("aid") + " ... " +  joinObject.getString("username"));
        }catch (JSONException e){
            Log.d("Join", "Error: Could not create joinObject");
            e.printStackTrace();
        }
        return joinObject;
    }

    private void updateActivityDB (JSONObject joinObject){
        //Update the Activity Database Entry's usernames.
        //That is: Add the username of the current user to the users of the activity

        final RequestQueue ao = Volley.newRequestQueue(this);
        JsonObjectRequest activity_object = new JsonObjectRequest(Request.Method.POST, UserdetailsUtil.getURL() + "/activities/joinupdate", joinObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("Join", "Entered activity_object");
                            boolean joinStatus = (boolean) response.get("success");
                            Log.d("Join","joinStatus: " + joinStatus);
                            Log.d("Join","response success: " + response.get("status").toString());
                            if(joinStatus){
                                UserdetailsUtil.inactivity = true;
                                UserdetailsUtil.activityID = SortedlistclassUtil.activity_to_be_displayed;
                                Toast.makeText(Joined.this, response.get("status").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.d("Join", "Error: Could not extract joinStatus");
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Joined.this, "Connection error while joining, try again later!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        ao.add(activity_object);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_joined);

        Log.d("Join", "Join has been clicked");
        //Check that the user should not already be in an activity
        if(!UserdetailsUtil.inactivity){
            Log.d("Join", "It seems inactivity was false.");

            //Object Required by Backend to update the Activity users
            JSONObject joinObject = getJoinObject(SortedlistclassUtil.activity_to_be_displayed,UserdetailsUtil.username);

            updateActivityDB(joinObject);

        }else {
            Toast.makeText(Joined.this, "Sorry, you are already in an activity!", Toast.LENGTH_SHORT).show();
        }
        Intent sign_up_Intent = new Intent(Joined.this, MenuActivity.class);
        startActivity(sign_up_Intent);
        Log.d("Done", "Going Menu");

    }
}