package com.cpen321.ubclocationbroadcaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

        
    }
}