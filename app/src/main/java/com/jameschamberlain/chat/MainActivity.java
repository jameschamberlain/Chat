package com.jameschamberlain.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);

        final EditText recipientField = findViewById(R.id.recipient_field);
        recipientField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        final EditText messageField = findViewById(R.id.message_field);
        messageField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        final LinearLayout messageLayout = findViewById(R.id.message_layout);

        final Handler handler  = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setTextAppearance(R.style.Message);
                    textView.setText(String.valueOf(msg.obj));
                    messageLayout.addView(textView);
                }
                super.handleMessage(msg);
            }
        };

//        new startClientTask().execute();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                client = new Client(handler);
                client.startClientThreads();
                return null;
            }
        }.execute();

        Button sendMessageButton = findViewById(R.id.send_message_button);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipient = recipientField.getText().toString();
                String message = messageField.getText().toString();
                client.getClientSender().sendMessage(recipient, message);

                TextView textView = new TextView(getApplicationContext());
                textView.setTextAppearance(R.style.Message);
                textView.setTextColor(getResources().getColor(R.color.colorAccent));
                textView.setText("To " + recipient + ": " + message);
                messageLayout.addView(textView);
            }
        });

    }


//    private class startClientTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            client = new Client();
//            client.startClientThreads();
//            return null;
//        }
//    }

}
