package com.jameschamberlain.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jameschamberlain.chat.database.*;

import java.util.List;
import java.util.Random;

public class ConversationsActivity extends AppCompatActivity {


    private Client client;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "test-db").build();

        (new fetchUsersTask()).execute();


        // Receive messages from the server and do something.
        final Handler handler  = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    final Contact contact = new Contact();
                    contact.setUsername("George");
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            db.contactDao().insertAll(contact);
                            return null;
                        }
                    }.execute();
                    final ChatMessage message = new ChatMessage();
                    Random rand = new Random();
                    message.setMid(rand.nextInt(1000));
                    String msgString = String.valueOf(msg.obj);
                    int seperator = msgString.indexOf(":");
                    message.setSender("George");
                    message.setReceiver("James");
                    message.setContents(msgString.substring(seperator+2));

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            db.chatMessageDao().insertAll(message);
                            updateUi(db.chatMessageDao().getAll());
                            return null;
                        }
                    }.execute();
                }
                super.handleMessage(msg);
            }
        };


        // Start the threads for the client.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                client = new Client(handler);
                client.startClientThreads();
                return null;
            }
        }.execute();

    }


    private void updateUi(List<ChatMessage> messages) {
        if(messages.size() == 0) {
            return;
        }
        ChatMessage message = messages.get(0);

        TextView textView = new TextView(getApplicationContext());
        textView.setTextAppearance(R.style.Message);
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
        textView.setText("To " + message.getSender() + ": " + message.getContents());
        LinearLayout noTeamLayout = findViewById(R.id.no_team_layout);
        noTeamLayout.addView(textView);
    }

    private class fetchUsersTask extends AsyncTask<AppDatabase, Void, List<ChatMessage>> {

        @Override
        protected void onPostExecute(List<ChatMessage> messages) {
            updateUi(messages);
        }

        @Override
        protected List<ChatMessage> doInBackground(AppDatabase... appDatabases) {
            List<ChatMessage> messages = db.chatMessageDao().getAll();
            return messages;
        }
    }
}
