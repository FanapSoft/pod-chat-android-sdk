package com.fanap.podchat.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Thread;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageDao {

    @Insert(onConflict = REPLACE)
    void insertContact(List<Contact> t);

    @Query("select * from Contact")
    List<Contact> getContact();

//    @Query("select * from Thread")
//    List<Thread> getThread();
//
//    @Insert(onConflict = REPLACE)
//    void insertThread(List<Thread> threads);
}
