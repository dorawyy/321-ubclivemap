package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    private boolean joinStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined);

        //TextView joined = findViewById(R.id.textView14);
        Button mb = findViewById(R.id.menubutton10);

        final RequestQueue ao = Volley.newRequestQueue(this);
        final RequestQueue uj = Volley.newRequestQueue(this);

        Log.d("Join", "Join has been clicked");
        //Check that the user should not already be in an activity
        if(UserDetails.inactivity.equals("false") || UserDetails.inactivity.equals("False")){
            Log.d("Join", "It seems inactivity was false.");

            //Object Required by Backend to update the Activity users
            JSONObject joinObject = new JSONObject();
            try{
                joinObject.put("aid",SortedListClass.activity_to_be_displayed);
                joinObject.put("user",UserDetails.username);
                Log.d("Join", "Created joinObject" + joinObject.getString("aid") + joinObject.getString("user"));
            }catch (JSONException e){
                Log.d("Join", "Error: Could not create joinObject");
                e.printStackTrace();
            }


            //Update the Activity Database Entry's usernames.
            //That is: Add the username of the current user to the users of the activity
            JsonObjectRequest activity_object = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:3000/activities/join", joinObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("Join", "Entered activity_object");
                                joinStatus = (boolean) response.get("success");
                                Log.d("Join","joinStatus: " + joinStatus);
                                Log.d("Join","response success: " + response.get("success").toString());
                                if(joinStatus){
                                    Toast.makeText(Joined.this, "Successfully Joined Activity", Toast.LENGTH_SHORT).show();
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
        }else {
            Toast.makeText(Joined.this, "Sorry, you are already in an activity!", Toast.LENGTH_SHORT).show();
        }

        //Update the UserDetails locally and the UserDB backend Database to reflect that the user is in a activity now.
        //if(joinStatus){
            Log.d("Join", "Passed if(joinStatus)");
            JSONObject userObject = new JSONObject();
            try{
                userObject.put("username",UserDetails.username);
                userObject.put("aid",SortedListClass.activity_to_be_displayed);
                Log.d("Join", "Created joinObject" + userObject.getString("aid") + userObject.getString("username"));
            }catch (JSONException e){
                e.printStackTrace();
                Log.d("Join", "Error: Could not create userObject");
            }
            JsonObjectRequest user_join = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:3000/profiles/join", userObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean userJoinStatus = false;
                                Log.d("userJoin", "Entered user_join");
                                userJoinStatus = (boolean) response.get("success");
                                Log.d("userJoin", "userJoinStatus: " + userJoinStatus);
                                if(userJoinStatus){
                                    //Update Locally
                                    UserDetails.inactivity = "true";
                                    UserDetails.activityID = SortedListClass.activity_to_be_displayed;

                                    //For Debugging
                                    Log.d("UserDBUpdate", "Successfully Updated User to reflect Joined Activity");
                                    Log.d("UserDBUpdate", UserDetails.inactivity);
                                    Log.d("UserDBUpdate", UserDetails.activityID);

                                }
                            } catch (JSONException e) {
                                Log.d("UserDBUpdate", "UNSuccessfully Updated User to reflect Joined Activity");
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

            uj.add(user_join);
       // }

        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_up_Intent = new Intent(Joined.this, MenuActivity.class);
                startActivity(sign_up_Intent);
                Log.d("Done", "Going Menu");
            }
        });
    }
}