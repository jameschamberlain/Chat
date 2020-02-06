package com.jameschamberlain.chat.communication;

import android.util.Log;

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
    private String code;
    private String recipient;
    private String contents;


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

                    if (code.equals("add_user_request")) {
                        server.println(code);      // Matches YYYYY in ClientSender.java
                        server.println(contents);      // Matches DDDDD in ServerReceiver
                    }
                    else {
                        server.println(code);      // Matches YYYYY in ClientSender.java
                        server.println(recipient);    // Matches CCCCC in ServerReceiver
                        server.println(contents);      // Matches DDDDD in ServerReceiver
                    }

                    messageReady = false;
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Communication broke in ClientSender" + e.getMessage());
        }

        Log.e(LOG_TAG, "ClientModel sender thread ending"); // Matches GGGGG in ClientModel.java

    }

    public void sendMessage(String recipient, String contents) {
        this.code = "send";
        this.recipient = recipient;
        this.contents = contents;
        this.messageReady = true;
    }

    public void addUser(String contents) {
        this.code = "add_user_request";
        this.recipient = "";
        this.contents = contents;
        this.messageReady = true;
    }

}