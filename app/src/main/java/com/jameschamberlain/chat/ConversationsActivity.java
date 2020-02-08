package com.jameschamberlain.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
    private String username;
    private Handler handler;

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
        adapter = new ConversationsAdapter(users, this);
        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup the database.
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "finishSettingUpClient-db").build();
        Log.e(LOG_TAG, "1");
        (new fetchUsersTask()).execute();


        // Setup a handler to receive messages from the server and do something.
        handler  = new Handler() {
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
//                            finishSettingUpClient(contact);
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

        // Check whether this is the first login for the user. Depending on the answer, shuffle
        // around the communication logic to the server.
        Intent intent = getIntent();
        boolean isFirstLogin = intent.getBooleanExtra("isFirstLogin", false);
        username = intent.getStringExtra("username");
        if (!isFirstLogin) {
            new connectToServerTask().execute();
        }
        else {
            new startClientThreadsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * Check if there are any contacts. If there aren't any, show a helper image to the user.
     */
    private void checkUsersExist() {
        Log.e(LOG_TAG, "3");
        LinearLayout noTeamLayout = findViewById(R.id.no_team_layout);
        if (users.isEmpty()) {
            noTeamLayout.setVisibility(View.VISIBLE);
        }
        else {
            noTeamLayout.setVisibility(View.GONE);
        }
    }


    /**
     *
     * Create a listener for the 'add user' fab. Show a dialog to enter the username.
     *
     * @param v The view that is clicked.
     */
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


    /**
     * If there are > 0 contacts then show them, otherwise show a helper image to the user.
     *
     * @param dbUsers The list of users.
     */
    private void updateUi(List<Contact> dbUsers) {
        Log.e(LOG_TAG, "2");
        if(dbUsers.size() == 0) {
            Log.e(LOG_TAG, "4");
            return;
        }
        for (Contact contact : dbUsers) {
            users.add(contact.getUsername());
        }
        checkUsersExist();
        adapter.notifyDataSetChanged();
    }


    /**
     * Finish setting up the client by starting the necessary communication threads.
     */
    private void finishSettingUpClient() {
        // Start the communication threads.
        new startClientThreadsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        // Get the connection status text view.
        final TextView connectionStatus = findViewById(R.id.connection_status);
        // Animate the color.
        int colorFrom = ContextCompat.getColor(this, R.color.notConnected);
        int colorTo = ContextCompat.getColor(this, R.color.connected);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                connectionStatus.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
        // Change the text.
        connectionStatus.setText("Connected");
        // Animate out of sight.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int y = -connectionStatus.getHeight();
                ObjectAnimator animation = ObjectAnimator.ofFloat(connectionStatus, "translationY", y);
                animation.setDuration(500);
                animation.start();
            }
        }, 500);
        // Hide it.
        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                connectionStatus.setVisibility(View.GONE);
            }
        }, 3000);
    }


    /**
     * Contact the database to get the list of contacts.
     */
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

    /**
     * Try and connect to the server.
     */
    private class connectToServerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            finishSettingUpClient();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            client = new Client(handler);
            client.attemptLogin(username);
            return null;
        }
    }

    /**
     * Start the threads necessary for sending and receiving messages from the server.
     */
    private class startClientThreadsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            client.startClientThreads();
            return null;
        }
    }
}
