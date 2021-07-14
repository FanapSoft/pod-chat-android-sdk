package com.fanap.podchat.persistance.dao;

import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import com.fanap.podchat.cachemodel.CacheAssistantHistoryVo;
import com.fanap.podchat.cachemodel.CacheAssistantVo;
import com.fanap.podchat.cachemodel.CacheBlockedContact;
import com.fanap.podchat.cachemodel.CacheContact;
import com.fanap.podchat.cachemodel.CacheFile;
import com.fanap.podchat.cachemodel.CacheForwardInfo;
import com.fanap.podchat.cachemodel.CacheMessageVO;
import com.fanap.podchat.cachemodel.CacheParticipant;
import com.fanap.podchat.cachemodel.CacheParticipantRoles;
import com.fanap.podchat.cachemodel.CacheReplyInfoVO;
import com.fanap.podchat.cachemodel.CacheTagParticipantVO;
import com.fanap.podchat.cachemodel.CacheTagVo;
import com.fanap.podchat.cachemodel.CacheThreadParticipant;
import com.fanap.podchat.cachemodel.GapMessageVO;
import com.fanap.podchat.cachemodel.ThreadVo;
import com.fanap.podchat.call.persist.CacheCall;
import com.fanap.podchat.call.persist.CacheCallParticipant;
import com.fanap.podchat.chat.user.profile.ChatProfileVO;
import com.fanap.podchat.chat.user.user_roles.model.CacheUserRoles;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.PinMessageVO;
import com.fanap.podchat.mainmodel.UserInfo;
import com.fanap.podchat.model.ConversationSummery;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageDao {


    @RawQuery
    int vacuumDb(SupportSQLiteQuery query);


    //Cache User Roles

    @Insert(onConflict = REPLACE)
    void insertCurrentUserRoles(CacheUserRoles role);

    @Insert(onConflict = REPLACE)
    void insertCurrentUserRoles(List<CacheUserRoles> roles);

    @Delete
    void deleteUserRole(CacheUserRoles role);

    @Delete
    void deleteUserRoles(List<CacheUserRoles> roles);

    @Query("select * from cacheuserroles where threadId = :threadId")
    CacheUserRoles getUserRoles(long threadId);

    @Query("select * from cacheuserroles where threadId = :threadId limit :count offset :offset")
    CacheUserRoles getUserRoles(long threadId, Integer count, Long offset);


    //Cache contact
    @Insert(onConflict = REPLACE)
    void insertContacts(List<CacheContact> t);

    @Insert(onConflict = REPLACE)
    void insertContact(CacheContact contact);

    @Delete
    void deleteContact(CacheContact cacheContact);

    @Query("DELETE FROM CacheContact WHERE userId =:userId")
    void deleteContactById(long userId);

    @Query("select * from CacheContact order by hasUser desc, lastName is null or lastName='', lastName, firstName is null or firstName='', firstName LIMIT :count OFFSET :offset")
    List<CacheContact> getContacts(Integer count, Long offset);

    @RawQuery
    List<CacheContact> getRawContacts(SupportSQLiteQuery sqLiteQuery);

    @RawQuery
    long getRawContactsCount(SupportSQLiteQuery sqLiteQuery);


    @Query("SELECT COUNT(id) FROM CacheContact")
    int getContactCount();

    @Query("update CacheContact set blocked = :blocked where id = :contactId")
    void updateContactBlockedState(boolean blocked, long contactId);


    //Cache Blocked Contact

    @Insert(onConflict = REPLACE)
    void insertBlockedContacts(List<CacheBlockedContact> t);

    @Insert(onConflict = REPLACE)
    void insertBlockedContact(CacheBlockedContact contact);

    @Delete
    void deleteBlockedContact(CacheBlockedContact cacheBlockContacts);

    @Query("DELETE FROM CacheBlockedContact WHERE blockId =:id")
    void deleteBlockedContactById(long id);


    @Query("select * from CacheBlockedContact LIMIT :count OFFSET :offset")
    List<CacheBlockedContact> getBlockedContacts(Long count, Long offset);

    @Query("select * from CacheBlockedContact where blockId = :id")
    CacheBlockedContact getBlockedContactByBlockId(long id);


    @Query("SELECT COUNT(blockId) FROM CacheBlockedContact")
    int getBlockContactsCount();


    /**
     * Cache thread history
     */
    @Insert(onConflict = REPLACE)
    void insertMessage(CacheMessageVO messageVO);

    @Update
    void updateMessage(CacheMessageVO messageVO);

    @Query("DELETE FROM CacheMessageVO WHERE id = :id ")
    void deleteMessage(long id);

    @Insert(onConflict = REPLACE)
    void insertHistories(List<CacheMessageVO> messageVOS);

    /**
     * Unread Messags
     */

    @Query("SELECT sum(unreadCount) from threadvo where unreadCount > 0")
    long getAllUnreadMessagesCount();

    @Query("SELECT sum(unreadCount) from threadvo where unreadCount > 0 and mute = :isMute")
    long getAllUnreadMessagesCountNoMutes(boolean isMute);


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

    @RawQuery
    long getHistoryContentCount(SupportSQLiteQuery query);


    /**
     * Delete message
     */

    @Query("DELETE FROM CACHEMESSAGEVO WHERE threadVoId = :threadId")
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

    @RawQuery
    long getThreadContentCount(SupportSQLiteQuery query);

    @RawQuery
    List<Long> getThreadIds(SupportSQLiteQuery query);

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

    @Query("update ThreadVo set pin = :isPinned where id = :threadId")
    void updateThreadPinState(long threadId, boolean isPinned);

    @Query("update ThreadVo set lastMessageVOId = :lastMessageId, lastMessage = :lastMessage where id = :threadId")
    void updateThreadLastMessageVOId(long threadId, long lastMessageId, String lastMessage);

    @Query("update ThreadVo set lastMessageVOId = 0, lastMessage = null where id = :threadId")
    void removeThreadLastMessageVO(long threadId);


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
    void insertLastMessageVO(CacheMessageVO lastMessageVO);

    @Query("select * from CacheMessageVO where id = :LastMessageVOId")
    CacheMessageVO getLastMessageVO(long LastMessageVOId);

    @Query("DELETE FROM cachemessagevo WHERE id = :id ")
    void deleteLastMessage(long id);

    /**
     * cache participant
     */
    @Insert(onConflict = REPLACE)
    void insertParticipant(CacheParticipant participant);

    @Insert(onConflict = REPLACE)
    void insertRoles(CacheParticipantRoles roles);

    @Query("select * from CacheParticipantRoles where id = :id AND threadId = :threadId")
    CacheParticipantRoles getParticipantRoles(long id, long threadId);


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
     *
     * cache admins
     *
     */


    /**
     * Search contact
     */
    @Query("select * from CacheContact where id = :id")
    CacheContact getContactById(long id);

    @Query("select * from CacheContact where firstName LIKE '%'||:firstName||'%'")
    List<CacheContact> getContactsByFirst(String firstName);

    @Query("select * from CacheContact where lastName LIKE '%'||:lastName||'%' ")
    List<CacheContact> getContactsByLast(String lastName);

    @Query("select * from CacheContact where firstName LIKE '%'||:firstName||'%' AND lastName LIKE '%'||:lastName||'%'")
    List<CacheContact> getContactsByFirstAndLast(String firstName, String lastName);

    @Query("select * from CacheContact where cellphoneNumber LIKE '%'||:cellphoneNumber||'%'")
    List<CacheContact> getContactByCell(String cellphoneNumber);

    @Query("select * from CacheContact where email LIKE '%'||:email||'%'")
    List<CacheContact> getContactsByEmail(String email);

    //cache replyInfoVO
    @Insert(onConflict = REPLACE)
    void insertReplyInfoVO(CacheReplyInfoVO replyInfoVO);

    @Query("select * from CacheReplyInfoVO where repliedToMessageId = :replyToId")
    CacheReplyInfoVO getReplyInfo(long replyToId);

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

    @RawQuery
    String deleteMessages(SupportSQLiteQuery sqLiteQuery);

    @Delete
    void deleteMessages(List<CacheMessageVO> cacheMessageVOS);


    //GapMessages Queries

    @Insert(onConflict = REPLACE)
    void insertGap(GapMessageVO gap);

    @Query("select * from gapmessagevo where threadId = :threadId")
    List<GapMessageVO> getGapMessages(long threadId);

    @Delete
    void deleteGapMessages(GapMessageVO gapMessageVO);

    @Query("Delete from gapmessagevo where id = :id")
    void deleteGap(long id);

    @Query("Delete from gapmessagevo where threadId = :threadId")
    void deleteAllGapMessagesFrom(long threadId);

    @Insert(onConflict = REPLACE)
    void insertAllGapMessages(List<GapMessageVO> gapMessageVOS);

    @Query("Select * from gapmessagevo where id = :id")
    GapMessageVO getGap(long id);


    //pin message queries
    @Query("select * from pinmessagevo where threadId = :id")
    PinMessageVO getThreadPinnedMessage(long id);

    @Insert(onConflict = REPLACE)
    void insertPinnedMessage(PinMessageVO pinMessageVO);

    @Delete
    void deletePinnedmessage(PinMessageVO pinMessageVO);

    @Query("delete from pinmessagevo where messageId = :messageId")
    void deletePinnedMessageById(long messageId);

    @Query("delete from pinmessagevo where threadId = :threadId")
    void deletePinnedMessageByThreadId(long threadId);


    //Chat Profile


    @Insert(onConflict = REPLACE)
    void insertChatProfile(ChatProfileVO chatProfileVO);

    @Query("Select * from chatprofilevo where id = :id")
    ChatProfileVO getChatProfileVOById(long id);

    @Delete
    void deleteChatProfile(ChatProfileVO chatProfileVO);

    @Query("delete from chatprofilevo where id = :id")
    void deleteChatProfileBtId(long id);


    //Call

    @Insert(onConflict = REPLACE)
    void insertCacheCalls(ArrayList<CacheCall> cacheCalls);

    @Query("SELECT * FROM CACHECALL where type = :type order by createTime desc LIMIT :count OFFSET :offset")
    List<CacheCall> getCachedCalls(long count, long offset, long type);

    @Query("SELECT * FROM CACHECALL where creatorId = :creatorUserId and type = :type order by createTime desc LIMIT :count OFFSET :offset")
    List<CacheCall> getCachedCallByUserId(long count, long offset, long creatorUserId, long type);

    @Query("SELECT COUNT(*) FROM CACHECALL where creatorId = :creatorUserId and type = :type")
    long getCountOfCachedCallByUserId(long creatorUserId, long type);

    @Query("SELECT * FROM CACHECALL where type = :type order by createTime desc LIMIT :count OFFSET :offset")
    List<CacheCall> getCachedCallByType(long count, long offset, long type);

    @Query("SELECT COUNT(*) FROM CACHECALL where type = :type")
    long getCountOfCachedCallByType(long type);

    @Query("delete from CacheCall where id = :id")
    void deleteCallFromHistory(long id);

    @Query("SELECT * FROM CACHECALL where id IN (:callIds) order by createTime desc LIMIT :count OFFSET :offset")
    List<CacheCall> getCachedCallByIds(long count, long offset, String callIds);

    @Query("SELECT COUNT(*) FROM CACHECALL where id IN (:callIds)")
    long getCountOfCachedCallByIds(String callIds);

    @Query("SELECT * FROM CACHECALL where id = :callId")
    CacheCall getCachedCallById(long callId);


    @Query("SELECT COUNT(*) FROM CACHECALL")
    long getCachedCallsCount();

    @Insert(onConflict = REPLACE)
    void insertCallParticipant(CacheCallParticipant cacheCallParticipant);

    @Insert(onConflict = REPLACE)
    void insertCallParticipants(ArrayList<CacheCallParticipant> cacheCalls);

    @Query("SELECT * FROM CacheCallParticipant where callId = :callId LIMIT :count OFFSET :offset")
    List<CacheCallParticipant> getCachedCallParticipants(long count, long offset, long callId);

    @Query("SELECT * FROM CacheCallParticipant where callId = :callId")
    List<CacheCallParticipant> getCachedCallParticipants(long callId);

    @Query("SELECT * FROM CacheCallParticipant where id = :participantId")
    CacheCallParticipant getCachedCallParticipant(long participantId);

    @Insert(onConflict = REPLACE)
    void insertImage(CacheFile image);

    @Query("SELECT * FROM CacheFile")
    List<CacheFile> getAllImageCaches();

    @Query("SELECT * FROM CacheFile WHERE hashCode = :hachCode order by quality desc limit 1")
    List<CacheFile> getImageCachesByHash(String hachCode);

    @Delete
    void deleteImage(CacheFile file);



    //cache assistant
    @Insert(onConflict = REPLACE)
    void insertCacheAssistantVo(CacheAssistantVo assistantVo);

    @Insert(onConflict = REPLACE)
    void insertCacheAssistantVos(List<CacheAssistantVo> assistantVo);


    @Query("SELECT * FROM CacheAssistantVo")
    List<CacheAssistantVo> getCacheAssistantVos();

    @Query("delete from CacheAssistantVo where inviteeId = :inviteeId")
    void deleteCacheAssistantVo(long inviteeId);

    @Query("DELETE FROM CacheAssistantVo")
    void deleteAllCacheAssistantVo();


    @Query("SELECT * FROM CacheAssistantHistoryVo")
    List<CacheAssistantHistoryVo> getCacheAssistantHistory();

    @Insert(onConflict = REPLACE)
    void insertCacheAssistantHistoryVo(List<CacheAssistantHistoryVo> assistantVo);

    @Query("DELETE FROM CacheAssistantHistoryVo")
    void deleteAllCacheAssistantHistoryVo();


    @Insert(onConflict = REPLACE)
    void insertCacheTagVo(CacheTagVo tagVo);

    @Query("SELECT * FROM CacheTagVo")
    List<CacheTagVo> getCacheTagVos();

    @Query("DELETE FROM CacheTagVo")
    void deleteAllCacheTagVo();


    @Insert(onConflict = REPLACE)
    void insertCacheTagParticipantVos(List<CacheTagParticipantVO> tagVos);

    @Query("SELECT * FROM CacheTagVo")
    List<CacheTagParticipantVO> getAllCacheTagParticipantVOs();

    @Query("SELECT * FROM CacheTagParticipantVO WHERE tagId=:tagId")
    List<CacheTagParticipantVO> getCacheTagParticipantVosByTagId(long tagId);

    @Query("DELETE FROM CacheTagParticipantVO")
    void deleteAllCacheTagParticipantVO();



}
