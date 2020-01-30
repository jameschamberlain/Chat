package com.jameschamberlain.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

// Gets messages from other clients via the server (by the
// ServerSender thread).

public class ClientReceiver implements Runnable {

    /** Tag for the log messages */
    private static final String LOG_TAG = ClientReceiver.class.getSimpleName();
    /**
     * Stores the communication stream to the Server
     */
    private BufferedReader server;

    private Handler handler;


    /**
     *
     * Constructs a new client receiver
     *
     * @param server The communication stream to the server (ServerReceiver)
     */
    ClientReceiver(BufferedReader server, Handler handler) {
        this.server = server;
        this.handler = handler;
    }

    /**
     * Run the client receiver thread.
     */
    @Override
    public void run() {
        // Print to the user whatever we get from the server:
        try {

            while (true) {
                String s = server.readLine(); // Matches FFFFF in ServerSender.java
                if (s == null) {
                    throw new NullPointerException();
                }

                Log.i(LOG_TAG, s);
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = s;
                handler.sendMessage(msg);
            }
        }
        catch (SocketException e) { // Matches HHHHH in ClientModel.java
            Log.e(LOG_TAG, "ClientModel receiver ending");
        }
        catch (NullPointerException | IOException e) {
            Log.e(LOG_TAG, "Server seems to have died " + (e.getMessage() == null ? "" : e.getMessage()));
        }
    }


    private String getSender(String message) {
        int colonPosition = message.indexOf(":");
        String sender = message.substring(5, colonPosition);
        return sender;
    }


    private String getText(String message) {
        int colonPosition = message.indexOf(":");
        String text = message.substring(colonPosition + 1);
        return text;
    }

}


/*

 * The method readLine returns null at the end of the stream

 * It may throw IoException if an I/O error occurs

 * See https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html#readLine--


 */
