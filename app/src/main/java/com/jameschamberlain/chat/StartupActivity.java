package com.jameschamberlain.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jameschamberlain.chat.communication.Client;

public class StartupActivity extends AppCompatActivity {

    private String username;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("com.jameschamberlain.chat", MODE_PRIVATE);

        if (preferences.getBoolean("logged_in", false)) {
            username = preferences.getString("username", null);
            new startClientTask().execute();
        }
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

    }

    private void completeLogin() {
        Intent intent = new Intent(this, ConversationsActivity.class);
        intent.putExtra("isNewLogin", false);
        startActivity(intent);
    }

    private class startClientTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean isLoggedIn) {
            if (isLoggedIn) {
                completeLogin();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Client client = new Client();
            return client.attemptLogin(username);
        }
    }
}
