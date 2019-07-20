package com.fanap.podchat.persistance.dao;

import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;

import com.fanap.podchat.cachemodel.CacheContact;
import com.fanap.podchat.cachemodel.CacheForwardInfo;
import com.fanap.podchat.cachemodel.CacheLastMessageVO;
import com.fanap.podchat.cachemodel.CacheMessageVO;
import com.fanap.podchat.cachemodel.CacheParticipant;
import com.fanap.podchat.cachemodel.CacheReplyInfoVO;
import com.fanap.podchat.cachemodel.CacheThreadParticipant;
import com.fanap.podchat.cachemodel.ThreadVo;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.UserInfo;
import com.fanap.podchat.model.ConversationSummery;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageDao {

    //Cache contact
    @Insert(onConflict = REPLACE)
    void insertContacts(List<CacheContact> t);

    @Insert(onConflict = REPLACE)
    void insertContact(CacheContact contact);

    @Delete
    void deleteContact(CacheContact cacheContact);

    @Query("DELETE FROM CacheContact WHERE id =:id")
    void deleteContactById(long id);

    @Query("select * from CacheContact LIMIT :count OFFSET :offset")
    List<CacheContact> getContacts(Integer count, Long offset);


    @Query("SELECT COUNT(id) FROM CacheContact")
    int getContactCount();

    /**
     * Cache thread history
     */
    @Insert(onConflict = REPLACE)
    void insertMessage(CacheMessageVO messageVO);

    @Query("DELETE FROM CacheMessageVO WHERE id = :id ")
    void deleteMessage(long id);

    @Insert(onConflict = REPLACE)
    void insertHistories(List<CacheMessageVO> messageVOS);

    /**
     * String Query
     */
    @Query("SELECT * FROM cachemessagevo WHERE threadVoId = :threadVoId AND message LIKE '%' || :query || '%' ORDER BY timeStamp DESC LIMIT :count OFFSET :offset")
    List<CacheMessageVO> getQueryDESC(long count, long offset, long threadVoId, String query);

    @Query("SELECT * FROM cachemessagevo WHERE threadVoId = :threadVoId AND message LIKE '%' || :query || '%' ORDER BY timeStamp ASC LIMIT :count OFFSET :offset")
    List<CacheMessageVO> getQueryASC(long count, long offset, long threadVoId, String query);

    @Query("DELETE FROM CacheMessageVO WHERE threadVoId = :threadId AND timeStamp IN (SELECT timeStamp FROM CacheMessageVO WHERE message LIKE '%' || :query || '%' ORDER BY timeStamp DESC LIMIT :count OFFSET :offset) ")
    void deleteMessagesWithQueryDesc(long threadId, long count, long offset, String query);

    @Query("DELETE FROM CacheMessageVO WHERE threadVoId = :threadId AND timeStamp IN (SELECT timeStamp FROM CacheMessageVO WHERE message LIKE '%' || :query || '%' ORDER BY timeStamp ASC LIMIT :count OFFSET :offset) ")
    void deleteMessagesWithQueryAsc(long threadId, long count, long offset, String query);

    /**
     * Get history
     */

    @Query("select * from CacheMessageVO where threadVoId = :threadVoId ORDER BY timeStamp ASC LIMIT :count OFFSET :offset ")
    List<CacheMessageVO> getHistoriesASC(long count, long offset, long threadVoId);

    @Query("select * from CacheMessageVO where threadVoId = :threadVoId ORDER BY time DESC LIMIT :count OFFSET :offset ")
    List<CacheMessageVO> getHistoriesDESC(long count, long offset, long threadVoId);

    @Query("select * from CacheMessageVO where threadVoId = :threadVoId AND timeStamp >= :timeStamp ORDER BY timeStamp ASC LIMIT :count OFFSET :offset ")
    List<CacheMessageVO> getHistoriesMessageIdASC(long count, long offset, long threadVoId, long timeStamp);

    @Query("select * from CacheMessageVO where  threadVoId = :threadVoId  AND timeStamp >= :timeStamp ORDER BY timeStamp DESC LIMIT :count OFFSET :offset ")
    List<CacheMessageVO> getHistoriesMessageIdDESC(long count, long offset, long threadVoId, long timeStamp);

    @Query("select * from CacheMessageVO where threadVoId = :threadVoId AND id BETWEEN :fromTime AND :toTime ORDER BY timeStamp ASC LIMIT :count OFFSET :offset ")
    List<CacheMessageVO> getHistoriesFandLASC(long count, long offset, long threadVoId, long fromTime, long toTime);

    @Query("select * from CacheMessageVO where threadVoId = :threadVoId AND id BETWEEN :fromTime AND :toTime ORDER BY timeStamp DESC LIMIT :count OFFSET :offset ")
    List<CacheMessageVO> getHistoriesFandLDESC(long count, long offset, long threadVoId, long fromTime, long toTime);

    @RawQuery
    List<CacheMessageVO> getRawHistory(SupportSQLiteQuery query);

    /**
     * Delete message
     */

    @Query("DELETE FROM CACHEMESSAGEVO WHERE id = :threadId")
    void deleteAllMessageByThread(long threadId);

    @Query("DELETE FROM CacheMessageVo WHERE threadVoId = :threadVoId AND timeStamp IN (select timeStamp from CacheMessageVO ORDER BY timeStamp ASC LIMIT :count OFFSET :offset )")
    void deleteMessageAfterOffsetTime(long count, long offset, long threadVoId);

    @Query("DELETE FROM CacheMessageVO WHERE threadVoId = :threadVoId  AND timeStamp IN(SELECT time FROM CacheMessageVO WHERE id BETWEEN :fromTime AND :toTime ORDER BY timeStamp ASC)")
    void deleteMessageBetweenLastAndFirstASC(long threadVoId, long fromTime, long toTime);

    @Query("DELETE FROM CacheMessageVO WHERE threadVoId = :threadVoId  AND timeStamp IN(SELECT timeStamp FROM CacheMessageVO WHERE timeStamp BETWEEN :fromTime AND :toTime ORDER BY timeStamp DESC)")
    void deleteMessageBetweenLastAndFirstDESC(long threadVoId, long fromTime, long toTime);

    @Query("DELETE FROM cachemessagevo WHERE threadVoId = :threadVoId  AND timeStamp IN (SELECT timeStamp FROM CacheMessageVO WHERE timeStamp >= :fromTime ORDER BY timeStamp ASC LIMIT :count OFFSET :offset )")
    void deleteMessageWithFirstMessageIdASC(long count, long offset, long threadVoId, long fromTime);

    @Query("DELETE FROM cachemessagevo WHERE threadVoId = :threadVoId  AND timeStamp IN (SELECT timeStamp FROM CacheMessageVO WHERE timeStamp >=:fromTime ORDER BY time DESC LIMIT :count OFFSET :offset )")
    void deleteMessageWithFirstMessageIdDESC(long count, long offset, long threadVoId, long fromTime);

    @Query("SELECT COUNT(id) FROM CacheMessageVO WHERE threadVoId = :threadVoId ")
    long getHistoryCount(long threadVoId);

    @Query("SELECT * FROM cachemessagevo WHERE id = :id ")
    List<CacheMessageVO> getMessage(long id);

    @Query("SELECT COUNT(id) FROM CacheMessageVO WHERE threadVoId = :threadVoId AND id BETWEEN :fromTime AND :toTime")
    long getHistoryCountWithLastAndFirtMSGId(long threadVoId, long fromTime, long toTime);

    //Cache userInfo
    @Insert(onConflict = REPLACE)
    void insertUserInfo(UserInfo userInfo);

    @Query("select * from UserInfo")
    UserInfo getUserInfo();

    /**
     * Cache thread
     */

    @Query("SELECT lastMessageVOId FROM ThreadVo WHERE id = :threadId ")
    long getLastMessageId(long threadId);

    @RawQuery
    List<ThreadVo> getThreadRaw(SupportSQLiteQuery query);

    @Query("select COUNT(*) FROM ThreadVo ")
    int getThreadCount();

    @Query("select * from ThreadVo  ORDER BY id DESC LIMIT :count OFFSET :offset ")
    List<ThreadVo> getThreads(long count, long offset);

    @Query("select  * from ThreadVo where id = :id")
    ThreadVo getThreadById(long id);

    @Query("select  * from ThreadVo where title LIKE  '%' ||:title ||'%' ORDER BY id DESC LIMIT :count OFFSET :offset ")
    List<ThreadVo> getThreadByName(long count, long offset, String title);

    @Insert(onConflict = REPLACE)
    void insertThreads(List<ThreadVo> ThreadVo);

    @Insert(onConflict = REPLACE)
    void insertThread(ThreadVo threadVo);

    @Query("DELETE FROM threadvo WHERE id = :threadId")
    void deleteThread(long threadId);

    /**
     * cache inviter
     */

    @Query("select * from Inviter where id = :inviterId ")
    Inviter getInviter(long inviterId);

    @Insert(onConflict = REPLACE)
    void insertInviter(Inviter inviter);

    /**
     * cache LastMessageVO
     */
    @Insert(onConflict = REPLACE)
    void insertLastMessageVO(CacheLastMessageVO lastMessageVO);

    @Query("select * from CacheLastMessageVO where id = :LastMessageVOId")
    CacheLastMessageVO getLastMessageVO(long LastMessageVOId);

    @Query("DELETE FROM cachelastmessagevo WHERE id = :id ")
    void deleteLastMessage(long id);

    /**
     * cache participant
     */
    @Insert(onConflict = REPLACE)
    void insertParticipant(CacheParticipant participant);

    @Query("DELETE FROM CacheParticipant where threadId = :threadId AND id = :id")
    void deleteParticipant(long threadId, long id);

    @Query("select * from CacheParticipant where id = :participantId")
    CacheParticipant getParticipant(long participantId);

    @Query("select COUNT(id) FROM CacheParticipant WHERE threadId = :threadId")
    int getParticipantCount(long threadId);

    @Query("select * from CacheParticipant WHERE :threadId ORDER BY name LIMIT :count OFFSET :offset ")
    List<CacheParticipant> geParticipants(long offset, long count, long threadId);

    @Query("select * from CacheParticipant WHERE threadId = :threadId")
    List<CacheParticipant> geParticipantsWithThreadId(long threadId);

    /**
     * cache Thread_Participant
     */

    @Insert(onConflict = REPLACE)
    void insertThreadParticipant(CacheThreadParticipant cacheThreadParticipant);

    @Query("DELETE FROM CacheThreadParticipant WHERE participantId =:participantId")
    void deleteCacheThreadParticipant(long participantId);

    @Query("SELECT * FROM cachethreadparticipant WHERE threadId = :threadId LIMIT :count OFFSET :offset ")
    List<CacheThreadParticipant> getAllThreadParticipants(long offset, long count, long threadId);


    @Query("DELETE FROM CacheThreadParticipant WHERE threadId = :threadId")
    void deleteAllThreadParticipant(long threadId);

    /**
     * Search contact
     */
    @Query("select * from CacheContact where id = :id")
    CacheContact getContactById(long id);

    @Query("select * from CacheContact where firstName LIKE :firstName ")
    List<CacheContact> getContactsByFirst(String firstName);

    @Query("select * from CacheContact where lastName LIKE :lastName ")
    List<CacheContact> getContactsByLast(String lastName);

    @Query("select * from CacheContact where firstName LIKE :firstName AND lastName LIKE :lastName")
    List<CacheContact> getContactsByFirstAndLast(String firstName, String lastName);

    @Query("select * from CacheContact where cellphoneNumber LIKE :cellphoneNumber")
    List<CacheContact> getContactByCell(String cellphoneNumber);

    @Query("select * from CacheContact where email LIKE :email ")
    List<CacheContact> getContactsByEmail(String email);

    //cache replyInfoVO
    @Insert(onConflict = REPLACE)
    void insertReplyInfoVO(CacheReplyInfoVO replyInfoVO);

    @Query("select * from CacheReplyInfoVO where id = :replyInfoVOId")
    CacheReplyInfoVO getReplyInfo(long replyInfoVOId);

    //Cache ForwardInfo
    @Insert(onConflict = REPLACE)
    void insertForwardInfo(CacheForwardInfo forwardInfo);

    @Query("select * from CacheForwardInfo where forwardInfo_Id = :forwardInfoId ")
    CacheForwardInfo getForwardInfo(long forwardInfoId);

    //Cache ConversationSummery
    @Query("select * from ConversationSummery where id = :id")
    ConversationSummery getConversationSummery(long id);

    @Insert(onConflict = REPLACE)
    void insertConversationSummery(ConversationSummery conversationSummery);
}
