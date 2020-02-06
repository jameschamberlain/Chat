package com.jameschamberlain.chat.communication;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    /** Tag for the log messages */
    private static final String LOG_TAG = Client.class.getSimpleName();
    private Handler handler;
    private static PrintStream toServer;
    private static BufferedReader fromServer;
    private static Socket server = null;
    private final int PORT_NUMBER = 4444;
    private static ClientSender clientSender;
    private static ClientReceiver clientReceiver;


    /**
     * Constructs a new client
     */
    public Client(Handler handler) {
        this.handler = handler;
    }

    /**
     * Constructs a new client
     */
    public Client() {
    }

    /**
     * Run the client receiver thread.
     */
    private void setupInitialConnection() {
        String hostname = "192.168.0.2";

        // Open sockets:
        toServer = null;
        fromServer = null;
        server = null;

        try {
            server = new Socket(hostname, PORT_NUMBER); // Matches AAAAA in Server.java
            toServer = new PrintStream(server.getOutputStream());
            fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
        } catch (UnknownHostException e) {
            Log.e(LOG_TAG, "Unknown host: " + hostname);
        } catch (IOException e) {
            Log.e(LOG_TAG, "The server doesn't seem to be running " + e.getMessage());
        }
    }


    /**
     * Attempt to log the client in to an account
     *
     * @param username  The username of the client
     */
    public boolean attemptLogin(String username) {

        setupInitialConnection();

        // Tell the server what my nickname is:
        toServer.println(username + "\n" + false); // Matches BBBBB and ZZZZZ in Server.java

        try {

            String resultFromServer = fromServer.readLine(); // Matches ZZZZZ in Client

            if (resultFromServer.equals("Login successful")) {
                return true;
            }

        } catch (IOException | NullPointerException e) {
            Log.e(LOG_TAG, "IO error " + e.getMessage());
        }
        return false;
    }


    /**
     * Attempt to log the client in to an account
     *
     * @param username  The username of the client
     */
    public void attemptSignUp(String username) {

//        setupInitialConnection();
//
//        // Tell the server what my nickname is:
//        toServer.println(username + "\n" + true); // Matches BBBBB and ZZZZZ in Server.java
//
//        try {
//
//            String resultFromServer = fromServer.readLine(); // Matches ZZZZZ in Client
//
//            switch (resultFromServer) {
//                case "Username already taken, please select another":
//                    showToast(resultFromServer);
//                    break;
//                case "Register and login successful":
//                    showToast(resultFromServer);
//                    startClientThreads();
//                    break;
//                default:
//                    showToast("Error contacting server");
//                    break;
//            }
//
//        } catch (IOException | NullPointerException e) {
//            Log.e(LOG_TAG, "IO error " + e.getMessage());
//        }
    }


    public void startClientThreads() {
        // Create and start the two client threads.
        clientSender = new ClientSender(toServer);
        Thread sender = new Thread(clientSender);
        sender.start();
        clientReceiver = new ClientReceiver(fromServer, handler);
        Thread receiver = new Thread(clientReceiver);
        receiver.start();

        // Wait for them to end and close sockets.
        try {
            sender.join();         // Waits for ClientSender.java to end. Matches GGGGG.
            Log.i(LOG_TAG, "ClientModel sender ended");
            toServer.close();      // Will trigger SocketException
            fromServer.close();    // (matches HHHHH in ClientServer.java).
            server.close();        // https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html#close()
            receiver.join();
            Log.i(LOG_TAG, "ClientModel receiver ended");
        }
        catch (IOException | NullPointerException e) {
            Log.e(LOG_TAG, "Something went wrong " + e.getMessage());
        }
        catch (InterruptedException e) {
            Log.e(LOG_TAG, "Unexpected interruption " + e.getMessage());
        }
        Log.i(LOG_TAG, "ClientModel ended. Goodbye.");
    }


    public ClientSender getClientSender() {
        return clientSender;
    }
}