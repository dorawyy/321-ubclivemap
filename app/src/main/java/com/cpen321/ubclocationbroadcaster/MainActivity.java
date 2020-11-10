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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sign_in_btn;
        Button sign_up_btn;

        getFirebaseInstance();

        username = findViewById(R.id.username_button);
        password = findViewById(R.id.password_button);
        sign_in_btn = findViewById(R.id.sign_in_button);
        sign_up_btn = findViewById(R.id.sign_up_button);

        final RequestQueue q = Volley.newRequestQueue(this);

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("sign in button", "sign in button has been clicked");

                String URL = UserdetailsUtil.getURL() + "/users/login";
                String passwrd = password.getText().toString();
                final String usrname = username.getText().toString();


                //format request
                JSONObject jsnRequest = new JSONObject();
                try {
                    jsnRequest.put("name", usrname);
                    jsnRequest.put("password", passwrd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, URL, jsnRequest,
                        new Response.Listener<JSONObject> (){
                            @Override
                            public void onResponse(JSONObject response){
                                try {
                                    boolean successVal = (boolean) response.get("success"); // check if user signed in successfully
                                    String stat = response.get("status").toString(); // get status
                                    if(successVal) {
                                        getSuccessval();
                                    } else {
                                        Toast.makeText(MainActivity.this, "ERROR: " + stat, Toast.LENGTH_SHORT).show();
                                    }
                                    Log.d("SignInActivity", stat);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(MainActivity.this, "Unable to send the sign in data to the server!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
                q.add(json_obj);
            }
        });


        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_up_Intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(sign_up_Intent);
                Log.d("sign up button", "sign up button has been clicked");
            }
        });
    }

    private void getFirebaseInstance() {
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
    }

    private void getSuccessval() {
        RequestQueue q = Volley.newRequestQueue(this);
        final String usrname = getUsername();
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(UserdetailsUtil.getURL() + "/profiles/all",
                new Response.Listener<JSONArray> (){
                    @Override
                    public void onResponse(JSONArray response){
                        try {
                            //pos is the index of our user in userDB
                            int pos = -1;
                            for(int i=0; i<response.length();i++){
                                JSONObject jsonObject = response.getJSONObject(i);
                                if(jsonObject.getString("username").toString().equals(usrname)){
                                    pos = i;
                                    Log.d("poschange", "onResponse: " + pos);
                                    break;
                                }
                            }

                            //jsonObject is the user object
                            JSONObject jsonObject = response.getJSONObject(pos);
                            getJsonObj(jsonObject);

                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Unable to send the sign in data to the server!", Toast.LENGTH_SHORT).show();
                            Log.d("error2", "---------------------------------------");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        q.add(jsonArrayRequest);

        Intent sign_in_Intent = new Intent(MainActivity.this, MenuActivity.class);
        sign_in_Intent.putExtra("USERNAME", usrname);
        startActivity(sign_in_Intent);
        UserdetailsUtil.username = usrname;
    }

    private String getUsername() {
        String usrname = username.getText().toString();
        return usrname;
    }

    private void getJsonObj(JSONObject jsonObject) throws JSONException {
        int numOfCourses = jsonObject.getJSONArray("CourseRegistered").length();
        UserdetailsUtil.name = jsonObject.getString("name");
        UserdetailsUtil.phone = jsonObject.getString("phone");
        UserdetailsUtil.school = jsonObject.getString("school");
        UserdetailsUtil.major = jsonObject.getString("major");
        UserdetailsUtil.privatePublic = jsonObject.getBoolean("private");
        UserdetailsUtil.inactivity = jsonObject.getBoolean("inActivity");
        UserdetailsUtil.activityID = jsonObject.getString("activityID");
        UserdetailsUtil.courseRegistered = new String[numOfCourses];
        for(int i=0;i<numOfCourses;i++){
            UserdetailsUtil.courseRegistered[i] = jsonObject.getJSONArray("CourseRegistered").getString(i);
        }
    }
}