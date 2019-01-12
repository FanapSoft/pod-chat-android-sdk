package com.fanap.podchat.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fanap.podchat.cachemodel.queue.SendingMessage;


import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageQueueDao {

    @Insert(onConflict = REPLACE)
    void insertMessageQueue(SendingMessage sendingMessage);

    @Insert(onConflict = REPLACE)
    void insertWaitMessageQueue(SendingMessage sendingMessage);

    @Query("DELETE FROM SendingMessage WHERE uniqueId = :uniqueId")
    void deleteMessageQueue(String uniqueId);

    @Query("SELECT uniqueId FROM WaitMessageQueue WHERE threadVoId = :threadId")
    List<String> getMsgQueueUniqueIds(long threadId);

    @Query("DELETE FROM waitmessagequeue WHERE uniqueId = :uniqueId")
    void deleteWaitMessageQueue(String uniqueId);
}
