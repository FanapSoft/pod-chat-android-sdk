package com.fanap.podchat.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fanap.podchat.cachemodel.queue.SendingMessage;
import com.fanap.podchat.cachemodel.queue.WaitMessageQueue;


import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageQueueDao {

    @Insert(onConflict = REPLACE)
    void insertSendingMessageQueue(SendingMessage sendingMessage);

    @Insert(onConflict = REPLACE)
    void insertWaitMessageQueue(SendingMessage sendingMessage);

    @Query("DELETE FROM SendingMessage WHERE uniqueId = :uniqueId")
    void deleteSendingMessageQueue(String uniqueId);

    @Query("SELECT * FROM WaitMessageQueue WHERE threadVoId = :threadId ORDER by id DESC ")
    List<WaitMessageQueue> getWaitQueueMsg(long threadId);

    @Query("DELETE FROM waitmessagequeue WHERE uniqueId = :uniqueId")
    void deleteWaitMessageQueue(String uniqueId);
}
