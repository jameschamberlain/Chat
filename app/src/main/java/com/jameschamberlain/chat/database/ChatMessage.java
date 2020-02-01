package com.jameschamberlain.chat.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity
public class ChatMessage {

    @PrimaryKey
    private int mid;

    @ForeignKey(entity = Contact.class, parentColumns = "username", childColumns = "sender", onDelete = ForeignKey.CASCADE)
    private String sender;

    @ForeignKey(entity = Contact.class, parentColumns = "username", childColumns = "receiver", onDelete = ForeignKey.CASCADE)
    private String receiver;

    private String contents;


    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
