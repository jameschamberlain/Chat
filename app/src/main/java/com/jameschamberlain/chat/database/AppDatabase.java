package com.jameschamberlain.chat.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class, ChatMessage.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();
    public abstract ChatMessageDao chatMessageDao();
}
