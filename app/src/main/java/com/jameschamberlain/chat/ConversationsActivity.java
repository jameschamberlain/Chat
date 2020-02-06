package com.jameschamberlain.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.jameschamberlain.chat.communication.Client;
import com.jameschamberlain.chat.database.*;

import java.util.ArrayList;
import java.util.List;

public class ConversationsActivity extends AppCompatActivity implements View.OnClickListener {

    /** Tag for the log messages */
    private static final String LOG_TAG = ConversationsActivity.class.getSimpleName();
    private Client client;
    private AppDatabase db;
    private ConversationsAdapter adapter;
    private ArrayList<String> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        // Set the status bar and navigation bar to be the same colour as the background.
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        users = new ArrayList<>();

        // Setup UI components.
        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        adapter = new ConversationsAdapter(users);
        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup the database.
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "test-db").build();
        (new fetchUsersTask()).execute();


        // Setup a handler to receive messages from the server and do something.
        final Handler handler  = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    List<String> serverResponse = (List<String>) msg.obj;
                    String code = serverResponse.get(0);
                    Log.i(LOG_TAG, code);
                    if (code.equals("add_user_request")) {
                        String contents = serverResponse.get(1);
                        Log.i(LOG_TAG, contents);
                        if (contents.equals("valid")) {
                            final Contact contact = new Contact();
                            contact.setUsername(serverResponse.get(2));
                            Log.i(LOG_TAG, serverResponse.get(2));
//                            test(contact);
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    db.contactDao().insertAll(contact);
                                    return null;
                                }
                            }.execute();
                            users.add(serverResponse.get(2));
                            adapter.notifyDataSetChanged();
                            checkUsersExist();
                        }
                    }

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
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void checkUsersExist() {
        LinearLayout noTeamLayout = findViewById(R.id.no_team_layout);
        if (users.isEmpty()) {
            noTeamLayout.setVisibility(View.VISIBLE);
        }
        else {
            noTeamLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        final EditText editText = new EditText(v.getContext());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editText.setHint("Username");
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (20*scale + 0.5f);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, 0);
        editText.setLayoutParams(lp);
        container.addView(editText);
        alert.setTitle("Add a user:")
                .setView(container)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        client.getClientSender().addUser(editText.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog dialog = alert.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.show();
    }



    private void updateUi(List<Contact> dbUsers) {
        if(dbUsers.size() == 0) {
            return;
        }
        for (Contact contact : dbUsers) {
            users.add(contact.getUsername());
        }
        checkUsersExist();
        adapter.notifyDataSetChanged();
    }



    private class fetchUsersTask extends AsyncTask<AppDatabase, Void, List<Contact>> {

        @Override
        protected void onPostExecute(List<Contact> users) {
            updateUi(users);
        }

        @Override
        protected List<Contact> doInBackground(AppDatabase... appDatabases) {
            return db.contactDao().getAll();
        }
    }
}
