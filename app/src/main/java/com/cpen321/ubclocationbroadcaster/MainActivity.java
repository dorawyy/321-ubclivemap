package com.cpen321.ubclocationbroadcaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            return;
                        }

                        String token = task.getResult().getToken();
                        String msg = getString(R.string.fcm_token, token);
                        Log.d("MainActivity", msg);
                    }
                });
        /**INITIALIZATION : END*/

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String URL = UserdetailsUtil.getURL() + "/users/login";
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, URL, jsnRequest,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {

                                        boolean successVal = (boolean) response.get("success"); // check if user signed in successfully
                                        String stat = response.get("status").toString(); // get status
                                        if (successVal) {

                                            final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(UserdetailsUtil.getURL() + "/profiles/all",
                                                    new Response.Listener<JSONArray>() {
                                                        @Override
                                                        public void onResponse(JSONArray response) {
                                                            try {
                                                                //pos is the index of our user in userDB
                                                                int pos = -1;
                                                                for (int i = 0; i < response.length(); i++) {
                                                                    JSONObject jsonObject = response.getJSONObject(i);
                                                                    if (jsonObject.getString("username").toString().equals(usrname)) {
                                                                        pos = i;
                                                                        Log.d("poschange", "onResponse: " + pos);
                                                                        break;
                                                                    }
                                                                }
                                                                //jsonObject is the user object
                                                                JSONObject jsonObject = response.getJSONObject(pos);
                                                                UserdetailsUtil.username = usrname;
                                                                int numOfCourses = jsonObject.getJSONArray("CourseRegistered").length();
                                                                UserdetailsUtil.name = jsonObject.getString("name");
                                                                UserdetailsUtil.phone = jsonObject.getString("phone");
                                                                UserdetailsUtil.school = jsonObject.getString("school");
                                                                UserdetailsUtil.major = jsonObject.getString("major");
                                                                UserdetailsUtil.privatePublic = jsonObject.getBoolean("private");
                                                                UserdetailsUtil.inactivity = jsonObject.getBoolean("inActivity");
                                                                UserdetailsUtil.activityID = jsonObject.getString("activityID");
                                                                UserdetailsUtil.courseRegistered = new String[numOfCourses];

                                                                for (int i = 0; i < numOfCourses; i++) {
                                                                    UserdetailsUtil.courseRegistered[i] = jsonObject.getJSONArray("CourseRegistered").getString(i);
                                                                }

                                                            } catch (JSONException e) {
                                                                Toast.makeText(MainActivity.this, "PLEASE UPDATE YOUR PROFILE", Toast.LENGTH_SHORT).show();
                                                                Log.d("MainActivity", "Unable to parse the User Details");
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Toast.makeText(MainActivity.this, "Server Error. Try Again", Toast.LENGTH_SHORT).show();
                                                    Log.d("MainActivity", "profiles/all Server Error");
                                                    error.printStackTrace();
                                                }
                                            });
                                            q.add(jsonArrayRequest);

                                            Intent sign_in_Intent = new Intent(MainActivity.this, MenuActivity.class);
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
                            if (error.networkResponse.statusCode == 401)
                                Toast.makeText(MainActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                            else if (error.networkResponse.statusCode == 402)
                                Toast.makeText(MainActivity.this, "Username does not exist. ", Toast.LENGTH_SHORT).show();
                            else if (error.networkResponse.statusCode == 403)
                                Toast.makeText(MainActivity.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
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

}