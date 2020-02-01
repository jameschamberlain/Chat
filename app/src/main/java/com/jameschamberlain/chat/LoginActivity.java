package com.jameschamberlain.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LoginActivity extends AppCompatActivity {

    private Client client;
    private String username;
    private EditText usernameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        usernameField = findViewById(R.id.username_login_field);
        usernameField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameField.getText().toString();
                new startClientTask().execute();
            }
        });

        Button signUpButton = findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameField.getText().toString();
                client.attemptSignUp(username);
            }
        });
    }

    private void completeLogin() {
        Looper.prepare();
        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(LoginActivity.this, ConversationsActivity.class);
        startActivity(intent);
    }

    private class startClientTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            client = new Client();
            boolean isLoggedIn = client.attemptLogin(username);
            if (isLoggedIn) {
                completeLogin();
            }
            return null;
        }
    }


}
