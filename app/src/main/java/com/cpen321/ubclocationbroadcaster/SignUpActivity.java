
package com.cpen321.ubclocationbroadcaster;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


/** This activity creates a page to sign up,
   and sends the data to the database */

public class SignUpActivity extends AppCompatActivity {

    /**INITIALIZATION - START*/
    private EditText username;
    private EditText password;

    private String URL = UserdetailsUtil.getURL() + "/users/register";
    /**INITIALIZATION - END*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button sign_up_btn;

        username = findViewById(R.id.sign_up_username_button);
        password = findViewById(R.id.sign_up_password_button);
        sign_up_btn = findViewById(R.id.sign_up_button);

        final RequestQueue q = Volley.newRequestQueue(this);

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // format request
                final String inputUsername = username.getText().toString();
                final String inputPassword = password.getText().toString();


                JSONObject jsnReq = new JSONObject();
                try {
                    jsnReq.put("name", inputUsername);
                    jsnReq.put("password", inputPassword);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(inputUsername.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Enter a username", Toast.LENGTH_SHORT).show();
                }

                else if(inputPassword.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Enter a password", Toast.LENGTH_SHORT).show();
                }
                else {
                final JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, URL, jsnReq,
                        new Response.Listener<JSONObject> (){
                            @Override
                            public void onResponse(JSONObject response){
                                try {
                                    boolean successVal = (boolean) response.get("success");
                                    if (successVal){
                                        Intent signUpIntent = new Intent(SignUpActivity.this, ProfileActivity.class);
                                        startActivity(signUpIntent);
                                    }
                                    else {
                                            Toast.makeText(SignUpActivity.this, "ERROR: Username Already Exists", Toast.LENGTH_SHORT).show();
                                    }
                                    String stat = response.get("status").toString();
                                    UserdetailsUtil.username = inputUsername;
                                    Log.d("SignUpActivity", stat);
                                } catch (JSONException e) {
                                    Log.d("SignUpActivity", "Error parsing the response from users/register");
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        if(error.networkResponse.statusCode == 401)
                            Toast.makeText(SignUpActivity.this, "Please Enter all the details", Toast.LENGTH_SHORT).show();
                        else if(error.networkResponse.statusCode == 402)
                            Toast.makeText(SignUpActivity.this, "Username Already Exists", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(SignUpActivity.this, "Unable to send the sign up data to the server!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });


               q.add(json_obj);

            }}
        });

    }
}