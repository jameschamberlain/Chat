package com.jameschamberlain.chat.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM Contact")
    List<Contact> getAll();

    @Query("SELECT * FROM Contact WHERE username IN (:contactIds)")
    List<Contact> loadAllByIds(String[] contactIds);

    @Insert
    void insertAll(Contact... contacts);

    @Delete
    void delete(Contact contact);

}
