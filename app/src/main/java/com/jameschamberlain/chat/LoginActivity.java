package com.jameschamberlain.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jameschamberlain.chat.communication.Client;

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
        Intent intent = new Intent(LoginActivity.this, ConversationsActivity.class);
        intent.putExtra("isNewLogin", true);
        startActivity(intent);
    }

    private class startClientTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean isLoggedIn) {
            if (isLoggedIn) {
                SharedPreferences preferences = getSharedPreferences("com.jameschamberlain.chat", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", username);
                editor.putBoolean("logged_in", true);
                editor.apply();
                completeLogin();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            client = new Client();
            return client.attemptLogin(username);
        }
    }


}
