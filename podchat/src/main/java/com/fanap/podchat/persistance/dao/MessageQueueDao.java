package com.fanap.podchat.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fanap.podchat.cachemodel.queue.SendingQueueCache;
import com.fanap.podchat.cachemodel.queue.UploadingQueueCache;
import com.fanap.podchat.cachemodel.queue.WaitQueueCache;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageQueueDao {

    /**
     * Sending Queue
     */
    @Insert(onConflict = REPLACE)
    void insertSendingMessageQueue(SendingQueueCache sendingQueueCache);

    @Query("DELETE FROM SendingQueueCache WHERE uniqueId = :uniqueId")
    void deleteSendingMessageQueue(String uniqueId);

    @Query("SELECT * FROM SendingQueueCache ORDER by QueueId DESC")
    List<SendingQueueCache> getAllSendingQueue();

    @Query("SELECT * FROM SendingQueueCache Where uniqueId = :uniqueId")
    SendingQueueCache getSendingQueue(String uniqueId);


    @Query("SELECT * FROM SendingQueueCache WHERE threadId = :threadId ORDER by QueueId DESC")
    List<SendingQueueCache> getAllSendingQueueByThredId(long threadId);

    /**
     * Failed Queue
     */
    @Insert(onConflict = REPLACE)
    void insertWaitMessageQueue(WaitQueueCache waitQueueCache);

    @Query("SELECT * FROM WaitQueueCache WHERE threadId = :threadId ORDER by QueueId DESC ")
    List<WaitQueueCache> getWaitQueueMsgByThreadId(long threadId);

    @Query("SELECT * FROM WaitQueueCache WHERE uniqueId = :uniqueId  ")
    WaitQueueCache getWaitQueueMsgByUniqueId(String uniqueId);

    @Query("SELECT asyncContent FROM WaitQueueCache WHERE uniqueId = :uniqueId")
    String getWaitQueueAsyncContent(String uniqueId);


    @Query("SELECT * FROM WaitQueueCache ORDER by QueueId DESC ")
    List<WaitQueueCache> getAllWaitQueueMsg();

    @Query("DELETE FROM WaitQueueCache WHERE uniqueId = :uniqueId")
    void deleteWaitMessageQueue(String uniqueId);

    /**
     * Uploading Queue
     */

    @Insert(onConflict = REPLACE)
    void insertUploadingQueue(UploadingQueueCache uploadingQueueCache);

    @Query("DELETE FROM UploadingQueueCache WHERE uniqueId = :uniqueId")
    void deleteUploadingQueue(String uniqueId);

    @Query("SELECT * FROM UploadingQueueCache ORDER by QueueId DESC")
    List<UploadingQueueCache> getAllUploadingQueue();

    @Query("SELECT * FROM UploadingQueueCache WHERE  threadId= :threadId ORDER by QueueId DESC ")
    List<UploadingQueueCache> getAllUploadingQueueByThreadId(long threadId);

    @Query("SELECT * FROM uploadingqueuecache WHERE uniqueId = :uniqueId")
    UploadingQueueCache getUploadingQ(String uniqueId);
}
