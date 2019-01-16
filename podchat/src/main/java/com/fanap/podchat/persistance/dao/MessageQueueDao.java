package com.fanap.podchat.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fanap.podchat.cachemodel.queue.SendingQueue;
import com.fanap.podchat.cachemodel.queue.UploadingQueue;
import com.fanap.podchat.cachemodel.queue.WaitQueue;


import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageQueueDao {

    /**
     *
     * Sending Queue
     *
     */
    @Insert(onConflict = REPLACE)
    void insertSendingMessageQueue(SendingQueue sendingQueue);

    @Query("DELETE FROM SendingQueue WHERE uniqueId = :uniqueId")
    void deleteSendingMessageQueue(String uniqueId);

    @Query("SELECT * FROM SendingQueue ORDER by id DESC")
    List<SendingQueue> getAllSendingQueue();

    /**
     *
     * Wait Queue
     *
     */
    @Insert(onConflict = REPLACE)
    void insertWaitMessageQueue(WaitQueue waitMessageQueue);

    @Query("SELECT * FROM WaitQueue WHERE threadVoId = :threadId ORDER by id DESC ")
    List<WaitQueue> getWaitQueueMsg(long threadId);

    @Query("SELECT * FROM WaitQueue ORDER by id DESC ")
    List<WaitQueue> getAllWaitQueueMsg();

    @Query("DELETE FROM WaitQueue WHERE uniqueId = :uniqueId")
    void deleteWaitMessageQueue(String uniqueId);

    /**
     *
     * Uploading Queue
     *
     */

    @Insert(onConflict = REPLACE)
    void insertUploadingQueue(UploadingQueue uploadingQueue);

    @Query("DELETE FROM UploadingQueue WHERE uniqueId = :uniqueId")
    void deleteUploadingQueue(String uniqueId);

    @Query("SELECT * FROM uploadingqueue ORDER by id DESC")
    List<UploadingQueue> getAllUploadingQueue();

    @Query("SELECT * FROM uploadingqueue WHERE threadVoId = :threadId ORDER by id DESC ")
    List<UploadingQueue> getAllUploadingQueueByThreadId(long threadId);

}
