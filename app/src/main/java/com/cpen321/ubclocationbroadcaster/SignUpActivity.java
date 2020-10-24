package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/* This activity creates a page to sign up,
   and sends the data to the database */

public class SignUpActivity extends AppCompatActivity {

    private EditText name;
    private EditText username;
    private EditText password;
    private EditText re_enter_password;
    private EditText email;
    private Button sign_up_btn;
    private ImageView profile_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        profile_pic = findViewById(R.id.sign_up_image_view);
        name = findViewById((R.id.sign_up_name_button));
        username = findViewById(R.id.sign_up_username_button);
        password = findViewById(R.id.sign_up_password_button);
        re_enter_password = findViewById(R.id.sign_up_password_button2);
        email = findViewById(R.id.sign_up_email_button);
        sign_up_btn = findViewById(R.id.sign_up_button);

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(SignUpActivity.this, ProfileActivity.class);
                startActivity(signUpIntent);
                Log.d("sign up button", "sign up button has been clicked");

                //TODO: insert the server url
                String URL = "http://";
                JsonObjectRequest json_obj = new JsonObjectRequest(Request.Method.POST, URL, null,
                        new Response.Listener<JSONObject> (){
                            @Override
                            public void onResponse(JSONObject response){
                                try {
                                    name.setText(response.getString("Name"));
                                    username.setText(response.getString("Username"));
                                    password.setText(response.getString("Password"));
                                    email.setText(response.getString("email"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(SignUpActivity.this, "Unable to send the sign up data to the server!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

                MySingleton.getInstance(SignUpActivity.this).addToRequestQueue(json_obj);

                //TODO: check if password 1 and password 2 are the same
            }
        });


    }
}
