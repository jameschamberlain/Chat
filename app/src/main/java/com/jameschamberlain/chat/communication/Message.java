package com.jameschamberlain.chat.communication;

public final class Message {

    /**
     * Stores the sender of the message
     */
    private final String sender;
    /**
     * Stores the contents of the message
     */
    private final String text;


    /**
     *
     * Constructs a new message
     *
     * @param _sender The sender of the message
     * @param _text The content of the message
     */
    Message(String _sender, String _text) {
        this.sender = _sender;
        this.text = _text;
    }

    /**
     *
     * Gets the sender of the message
     *
     * @return A string containing the sender of the message
     */
    String getSender() {
        return sender;
    }

    /**
     *
     * Gets the contents of the message
     *
     * @return A string containing the contents of the message
     */
    String getText() {
        return text;
    }

    /**
     *
     * Converts a message to a string
     *
     * @return A string containing the message
     */
    public String toString() {
        return "From " + sender + ": " + text;
    }

}
