package edu.uga.cs.finalproject;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private Button registerButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        registerButton = findViewById(R.id.buttonRegister);
        loginButton = findViewById(R.id.buttonLogin);

        registerButton.setOnClickListener(view -> {
            startActivity(new Intent(SplashScreenActivity.this, RegisterActivity.class));
            finish();
        });

        loginButton.setOnClickListener(view -> {
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            finish();
        });
    }
}
