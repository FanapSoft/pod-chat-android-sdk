package com.fanap.podchat.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.fanap.podchat.cachemodel.CacheForwardInfo;
import com.fanap.podchat.cachemodel.CacheLastMessageVO;
import com.fanap.podchat.cachemodel.CacheParticipant;
import com.fanap.podchat.cachemodel.CacheReplyInfoVO;
import com.fanap.podchat.cachemodel.ThreadVo;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.LastMessageVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.mainmodel.UserInfo;
import com.fanap.podchat.model.ConversationSummery;
import com.fanap.podchat.model.ReplyInfoVO;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageDao {

    //Cache contact
    @Insert(onConflict = REPLACE)
    void insertContact(List<Contact> t);

    @Query("select * from Contact")
    List<Contact> getContact();

    //Cache userInfo
    @Insert(onConflict = REPLACE)
    void insertUserInfo(UserInfo userInfo);

    @Query("select * from UserInfo")
    UserInfo getUserInfo();

    //Cache thread

    @Query("select COUNT(id) FROM THREADVO")
    int getThreadCount();

    @Query("select * from ThreadVo LIMIT :count OFFSET :offset")
    List<ThreadVo> getThreads(long count, long offset);

    @Query("select  * from ThreadVo where id = :id")
    ThreadVo getThread(long id);

    @Query("select  * from ThreadVo where id = :id")
    ThreadVo getThreadByName(long id);

    @Insert(onConflict = REPLACE)
    void insertThreads(List<ThreadVo> ThreadVo);

    @Insert(onConflict = REPLACE)
    void insertThread(ThreadVo threadVo);

    //cache inviter

    @Query("select * from Inviter where id = :inviterId ")
    Inviter getInviter(long inviterId);

    @Insert(onConflict = REPLACE)
    void insertInviter(Inviter inviter);

    //cache LastMessageVO
    @Insert(onConflict = REPLACE)
    void insertLastMessageVO(CacheLastMessageVO lastMessageVO);

    @Query("select * from CacheLastMessageVO where id = :LastMessageVOId")
    CacheLastMessageVO getLastMessageVO(long LastMessageVOId);

    //cache participant
    @Insert(onConflict = REPLACE)
    void insertParticipant(CacheParticipant participant);

    @Query("select * from CacheParticipant where id = :participantId")
    CacheParticipant getParticipant(long participantId);

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
