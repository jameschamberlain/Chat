package com.jameschamberlain.chat.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatMessageDao {


    @Query("SELECT * FROM ChatMessage")
    List<ChatMessage> getAll();

    @Query("SELECT * FROM ChatMessage WHERE sender IN (:userIds) OR receiver IN (:userIds)")
    List<ChatMessage> loadAllByIds(String[] userIds);

    @Insert
    void insertAll(ChatMessage... chatMessages);

    @Delete
    void delete(ChatMessage chatMessages);

}
