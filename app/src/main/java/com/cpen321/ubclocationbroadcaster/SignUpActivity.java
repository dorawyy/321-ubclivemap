
package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private TextView t1;
    private TextView t2;
    private String URL = UserdetailsUtil.getURL() + "/users/register";
    private Animation topAnim;
    private Animation bottomAnim;
    private Button sign_up_btn;
    /**INITIALIZATION - END*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);


        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anime_another);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anime_another);
        username = findViewById(R.id.sign_up_username_button);
        password = findViewById(R.id.sign_up_password_button);
        sign_up_btn = findViewById(R.id.sign_up_button);
        t1 = findViewById(R.id.textView5);
        t2 = findViewById(R.id.textView6);

        /**ANIMATION START*/
        t1.setAnimation(topAnim);
        t2.setAnimation(topAnim);
        username.setAnimation(topAnim);
        password.setAnimation(topAnim);
        sign_up_btn.setAnimation(bottomAnim);
        /**ANIMATION END*/

        final RequestQueue q = Volley.newRequestQueue(this);

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // format request
                final String inputUsername = username.getText().toString();
                final String inputPassword = password.getText().toString();


                final JSONObject jsnReq = new JSONObject();
                try {
                    jsnReq.put("name", inputUsername);
                    jsnReq.put("password", inputPassword);
                    jsnReq.put("token", UserdetailsUtil.token);

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
                                        //signUpIntent.putExtra("object", (Parcelable) jsnReq);
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