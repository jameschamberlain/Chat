package com.jameschamberlain.chat;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;


// Repeatedly reads recipient's command and any extra text from the user in
// separate lines, sending them to the server (read by ServerReceiver
// thread).

public class ClientSender implements Runnable {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = ClientSender.class.getSimpleName();
    /**
     * Stores the communication stream to the server
     */
    private PrintStream server;
    /**
     * Stores whether the thread should stop running or not
     */
    private boolean shouldBreak = false;

    private boolean messageReady = false;
    private String recipient;
    private String message;


    /**
     * Constructs a new client sender
     *
     * @param server The communication stream to the server (ServerSender)
     */
    ClientSender(PrintStream server) {
        this.server = server;
    }

    /**
     * Start ClientSender thread.
     */
    @Override
    public void run() {
        // So that we can use the method readLine:
        BufferedReader user = new BufferedReader(new InputStreamReader(System.in));

        try {
            // Loop until the client logs out,
            // sending messages to recipients
            // and commands to the server via the server:
            while (!shouldBreak) {

                if (messageReady) {
                    server.println("send");      // Matches YYYYY in ClientSender.java
                    server.println(recipient);    // Matches CCCCC in ServerReceiver
                    server.println(message);      // Matches DDDDD in ServerReceiver
                    messageReady = false;
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Communication broke in ClientSender" + e.getMessage());
        }

        Log.e(LOG_TAG, "ClientModel sender thread ending"); // Matches GGGGG in ClientModel.java

    }

    void sendMessage(String recipient, String message) {
        this.recipient = recipient;
        this.message = message;
        this.messageReady = true;
    }

}