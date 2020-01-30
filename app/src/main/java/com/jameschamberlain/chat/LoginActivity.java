package com.jameschamberlain.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Client client;
    private String username;
    private EditText usernameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
