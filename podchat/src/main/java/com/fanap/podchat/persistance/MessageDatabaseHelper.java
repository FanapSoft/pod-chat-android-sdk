package com.fanap.podchat.persistance;

import android.content.Context;

import com.fanap.podchat.cachemodel.CacheForwardInfo;
import com.fanap.podchat.cachemodel.CacheLastMessageVO;
import com.fanap.podchat.cachemodel.CacheParticipant;
import com.fanap.podchat.cachemodel.CacheReplyInfoVO;
import com.fanap.podchat.cachemodel.ThreadVo;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.LastMessageVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.mainmodel.UserInfo;
import com.fanap.podchat.model.ReplyInfoVO;
import com.fanap.podchat.persistance.dao.MessageDao;

import java.util.ArrayList;
import java.util.List;

public class MessageDatabaseHelper extends BaseDatabaseHelper {

    public MessageDao messageDao;

    public MessageDatabaseHelper(Context context) {
        super(context);
        messageDao = appDatabase.getMessageDao();
    }

    public List<Contact> getContacts() {
        return messageDao.getContact();
    }

    public void save(List<Contact> contacts) {
        messageDao.insertContact(contacts);
    }

    public void saveUserInfo(UserInfo userInfo) {
        messageDao.insertUserInfo(userInfo);
    }

    public UserInfo getUserInfo() {
        return messageDao.getUserInfo();
    }

    public int getThreadCount() {
        return messageDao.getThreadCount();
    }

    public List<Thread> getThreads(long count, long offset) {
        List<Thread> threads;
        if (messageDao.getThreads(count, offset) != null) {
            List<ThreadVo> threadVos = messageDao.getThreads(count, offset);
            threads = new ArrayList<>();
            CacheParticipant cacheParticipant;
            CacheReplyInfoVO cacheReplyInfoVO;
            Participant participant = null;
            ReplyInfoVO replyInfoVO = null;
            for (ThreadVo threadVo : threadVos) {
                LastMessageVO lastMessageVO = null;
                if (threadVo.getInviterId() != null) {
                    threadVo.setInviter(messageDao.getInviter(threadVo.getInviterId()));
                }
                if (threadVo.getLastMessageVOId() != null) {
                    threadVo.setLastMessageVO(messageDao.getLastMessageVO(threadVo.getLastMessageVOId()));
                    CacheLastMessageVO cacheLastMessageVO = threadVo.getLastMessageVO();
                    if (cacheLastMessageVO.getParticipantId() != null) {
                        cacheParticipant = messageDao.getParticipant(cacheLastMessageVO.getParticipantId());
                        participant = new Participant(
                                cacheParticipant.getId(),
                                cacheParticipant.getName(),
                                cacheParticipant.getFirstName(),
                                cacheParticipant.getLastName(),
                                cacheParticipant.getImage(),
                                cacheParticipant.getNotSeenDuration(),
                                cacheParticipant.getContactId(),
                                cacheParticipant.getContactName(),
                                cacheParticipant.getContactFirstName(),
                                cacheParticipant.getContactLastName(),
                                cacheParticipant.getSendEnable(),
                                cacheParticipant.getReceiveEnable(),
                                cacheParticipant.getCellphoneNumber(),
                                cacheParticipant.getEmail(),
                                cacheParticipant.getMyFriend(),
                                cacheParticipant.getOnline(),
                                cacheParticipant.getBlocked(),
                                cacheParticipant.getAdmin()
                        );
                    }
                    if (cacheLastMessageVO.getReplyInfoVOId() != null) {
                        cacheReplyInfoVO = messageDao.getReplyInfo(cacheLastMessageVO.getReplyInfoVOId());
                        replyInfoVO = new ReplyInfoVO(
                                cacheReplyInfoVO.getRepliedToMessageId(),
                                cacheReplyInfoVO.getMessageType(),
                                cacheReplyInfoVO.isDeleted(),
                                cacheReplyInfoVO.getRepliedToMessage(),
                                cacheReplyInfoVO.getSystemMetadata(),
                                cacheReplyInfoVO.getMetadata(),
                                cacheReplyInfoVO.getMessage()
                        );
                    }

                    if (cacheLastMessageVO.getReplyInfoVOId() != null) {

                    }

                    lastMessageVO = new LastMessageVO(
                            cacheLastMessageVO.getId(),
                            cacheLastMessageVO.getUniqueId(),
                            cacheLastMessageVO.getMessage(),
                            cacheLastMessageVO.isEdited(),
                            cacheLastMessageVO.isEditable(),
                            cacheLastMessageVO.getTime(),
                            participant,
                            replyInfoVO,
                            null
                    );
                }


                Thread thread = new Thread(
                        threadVo.getId(),
                        threadVo.getJoinDate(),
                        threadVo.getInviter(),
                        lastMessageVO,
                        threadVo.getTitle(),
                        null,
                        threadVo.getTime(),
                        threadVo.getLastMessage(),
                        threadVo.getLastParticipantName(),
                        threadVo.getLastParticipantImage(),
                        threadVo.isGroup(),
                        threadVo.getPartner(),
                        threadVo.getImage(),
                        threadVo.getDescription(),
                        threadVo.getUnreadCount(),
                        threadVo.getLastSeenMessageId(),
                        threadVo.getPartnerLastMessageId(),
                        threadVo.getPartnerLastSeenMessageId(),
                        threadVo.getPartnerLastDeliveredMessageId(),
                        threadVo.getType(),
                        threadVo.isMute(),
                        threadVo.getMetadata(),
                        threadVo.isCanEditInfo(),
                        threadVo.getParticipantCount(),
                        threadVo.isCanSpam(),
                        threadVo.isAdmin()
                );
                threads.add(thread);
            }
            return threads;
        } else {
            threads = new ArrayList<>();
        }

        return threads;
    }

    public List<Thread> getThreadsByThreadName(String threadName){
        List<Thread> threads ;
        if (messageDao.getThreadByName(threadName) !=null) {
           ThreadVo threadVo= messageDao.getThreadByName(threadName);
           threads = new ArrayList<>();
            CacheParticipant cacheParticipant;
            CacheReplyInfoVO cacheReplyInfoVO;
            Participant participant = null;
            ReplyInfoVO replyInfoVO = null;
            LastMessageVO lastMessageVO = null;
            if (threadVo.getInviterId() != null) {
                threadVo.setInviter(messageDao.getInviter(threadVo.getInviterId()));
            }
            if (threadVo.getLastMessageVOId() != null) {
                threadVo.setLastMessageVO(messageDao.getLastMessageVO(threadVo.getLastMessageVOId()));
                CacheLastMessageVO cacheLastMessageVO = threadVo.getLastMessageVO();
                if (cacheLastMessageVO.getParticipantId() != null) {
                    cacheParticipant = messageDao.getParticipant(cacheLastMessageVO.getParticipantId());
                    participant = new Participant(
                            cacheParticipant.getId(),
                            cacheParticipant.getName(),
                            cacheParticipant.getFirstName(),
                            cacheParticipant.getLastName(),
                            cacheParticipant.getImage(),
                            cacheParticipant.getNotSeenDuration(),
                            cacheParticipant.getContactId(),
                            cacheParticipant.getContactName(),
                            cacheParticipant.getContactFirstName(),
                            cacheParticipant.getContactLastName(),
                            cacheParticipant.getSendEnable(),
                            cacheParticipant.getReceiveEnable(),
                            cacheParticipant.getCellphoneNumber(),
                            cacheParticipant.getEmail(),
                            cacheParticipant.getMyFriend(),
                            cacheParticipant.getOnline(),
                            cacheParticipant.getBlocked(),
                            cacheParticipant.getAdmin()

                    );
                }
                if (cacheLastMessageVO.getReplyInfoVOId() != null) {
                    cacheReplyInfoVO = messageDao.getReplyInfo(cacheLastMessageVO.getReplyInfoVOId());
                    replyInfoVO = new ReplyInfoVO(
                            cacheReplyInfoVO.getRepliedToMessageId(),
                            cacheReplyInfoVO.getMessageType(),
                            cacheReplyInfoVO.isDeleted(),
                            cacheReplyInfoVO.getRepliedToMessage(),
                            cacheReplyInfoVO.getSystemMetadata(),
                            cacheReplyInfoVO.getMetadata(),
                            cacheReplyInfoVO.getMessage()
                    );
                }

                if (cacheLastMessageVO.getReplyInfoVOId() != null) {

                }

                lastMessageVO = new LastMessageVO(
                        cacheLastMessageVO.getId(),
                        cacheLastMessageVO.getUniqueId(),
                        cacheLastMessageVO.getMessage(),
                        cacheLastMessageVO.isEdited(),
                        cacheLastMessageVO.isEditable(),
                        cacheLastMessageVO.getTime(),
                        participant,
                        replyInfoVO,
                        null
                );
            }

            Thread thread = new Thread(
                    threadVo.getId(),
                    threadVo.getJoinDate(),
                    threadVo.getInviter(),
                    lastMessageVO,
                    threadVo.getTitle(),
                    null,
                    threadVo.getTime(),
                    threadVo.getLastMessage(),
                    threadVo.getLastParticipantName(),
                    threadVo.getLastParticipantImage(),
                    threadVo.isGroup(),
                    threadVo.getPartner(),
                    threadVo.getImage(),
                    threadVo.getDescription(),
                    threadVo.getUnreadCount(),
                    threadVo.getLastSeenMessageId(),
                    threadVo.getPartnerLastMessageId(),
                    threadVo.getPartnerLastSeenMessageId(),
                    threadVo.getPartnerLastDeliveredMessageId(),
                    threadVo.getType(),
                    threadVo.isMute(),
                    threadVo.getMetadata(),
                    threadVo.isCanEditInfo(),
                    threadVo.getParticipantCount(),
                    threadVo.isCanSpam(),
                    threadVo.isAdmin()
            );
            threads.add(thread);
        }else {
            return new ArrayList<>();
        }

        return threads;
    }

    public List<Thread> getThreadsByThreadIds(ArrayList<Integer> threadIds){
        List<Thread> threads = new ArrayList<>() ;

        for (int id :threadIds){
            if (messageDao.getThreadById(id) !=null) {
                ThreadVo threadVo = messageDao.getThreadById(id);
                CacheParticipant cacheParticipant;
                CacheReplyInfoVO cacheReplyInfoVO;
                Participant participant = null;
                ReplyInfoVO replyInfoVO = null;
                LastMessageVO lastMessageVO = null;
                if (threadVo.getInviterId() != null) {
                    threadVo.setInviter(messageDao.getInviter(threadVo.getInviterId()));
                }
                if (threadVo.getLastMessageVOId() != null) {
                    threadVo.setLastMessageVO(messageDao.getLastMessageVO(threadVo.getLastMessageVOId()));
                    CacheLastMessageVO cacheLastMessageVO = threadVo.getLastMessageVO();
                    if (cacheLastMessageVO.getParticipantId() != null) {
                         cacheParticipant = messageDao.getParticipant(cacheLastMessageVO.getParticipantId());
                          participant = new Participant(
                                cacheParticipant.getId(),
                                cacheParticipant.getName(),
                                cacheParticipant.getFirstName(),
                                cacheParticipant.getLastName(),
                                cacheParticipant.getImage(),
                                cacheParticipant.getNotSeenDuration(),
                                cacheParticipant.getContactId(),
                                cacheParticipant.getContactName(),
                                cacheParticipant.getContactFirstName(),
                                cacheParticipant.getContactLastName(),
                                cacheParticipant.getSendEnable(),
                                cacheParticipant.getReceiveEnable(),
                                cacheParticipant.getCellphoneNumber(),
                                cacheParticipant.getEmail(),
                                cacheParticipant.getMyFriend(),
                                cacheParticipant.getOnline(),
                                cacheParticipant.getBlocked(),
                                cacheParticipant.getAdmin()
                        );
                    }
                    if (cacheLastMessageVO.getReplyInfoVOId() != null) {
                        cacheReplyInfoVO = messageDao.getReplyInfo(cacheLastMessageVO.getReplyInfoVOId());
                         replyInfoVO = new ReplyInfoVO(
                                cacheReplyInfoVO.getRepliedToMessageId(),
                                cacheReplyInfoVO.getMessageType(),
                                cacheReplyInfoVO.isDeleted(),
                                cacheReplyInfoVO.getRepliedToMessage(),
                                cacheReplyInfoVO.getSystemMetadata(),
                                cacheReplyInfoVO.getMetadata(),
                                cacheReplyInfoVO.getMessage()
                        );
                    }

                    if (cacheLastMessageVO.getReplyInfoVOId() != null) {

                    }

                    lastMessageVO = new LastMessageVO(
                            cacheLastMessageVO.getId(),
                            cacheLastMessageVO.getUniqueId(),
                            cacheLastMessageVO.getMessage(),
                            cacheLastMessageVO.isEdited(),
                            cacheLastMessageVO.isEditable(),
                            cacheLastMessageVO.getTime(),
                            participant,
                            replyInfoVO,
                            null
                    );
                }


                Thread thread = new Thread(
                        threadVo.getId(),
                        threadVo.getJoinDate(),
                        threadVo.getInviter(),
                        lastMessageVO,
                        threadVo.getTitle(),
                        null,
                        threadVo.getTime(),
                        threadVo.getLastMessage(),
                        threadVo.getLastParticipantName(),
                        threadVo.getLastParticipantImage(),
                        threadVo.isGroup(),
                        threadVo.getPartner(),
                        threadVo.getImage(),
                        threadVo.getDescription(),
                        threadVo.getUnreadCount(),
                        threadVo.getLastSeenMessageId(),
                        threadVo.getPartnerLastMessageId(),
                        threadVo.getPartnerLastSeenMessageId(),
                        threadVo.getPartnerLastDeliveredMessageId(),
                        threadVo.getType(),
                        threadVo.isMute(),
                        threadVo.getMetadata(),
                        threadVo.isCanEditInfo(),
                        threadVo.getParticipantCount(),
                        threadVo.isCanSpam(),
                        threadVo.isAdmin()
                );
                threads.add(thread);
            }
        }

        return threads;
    }

    public void saveThreads(List<ThreadVo> threadVos) {
        try {
            CacheLastMessageVO cacheLastMessageVO = null;
            CacheReplyInfoVO cacheReplyInfoVO;
            CacheForwardInfo cacheForwardInfo;
            for (ThreadVo threadVo : threadVos) {
                if (threadVo.getInviter() != null) {

                    threadVo.setInviterId(threadVo.getInviter().getId());
                    messageDao.insertInviter(threadVo.getInviter());
                }
                if (threadVo.getLastMessageVO() != null) {

                    threadVo.setLastMessageVOId(threadVo.getLastMessageVO().getId());
                    cacheLastMessageVO = threadVo.getLastMessageVO();
                    messageDao.insertLastMessageVO(cacheLastMessageVO);
                    if (threadVo.getLastMessageVO().getParticipant() != null) {

                        cacheLastMessageVO.setParticipantId(threadVo.getLastMessageVO().getParticipant().getId());
                        messageDao.insertLastMessageVO(cacheLastMessageVO);
                        messageDao.insertParticipant(threadVo.getLastMessageVO().getParticipant());
                    }
                    if (threadVo.getLastMessageVO().getReplyInfoVO() != null) {
                        cacheReplyInfoVO = threadVo.getLastMessageVO().getReplyInfoVO();
                        cacheLastMessageVO.setReplyInfoVOId(threadVo.getLastMessageVO().getReplyInfoVO().getId());
                        messageDao.insertLastMessageVO(cacheLastMessageVO);
                        messageDao.insertReplyInfoVO(cacheReplyInfoVO);

                        if (threadVo.getLastMessageVO().getReplyInfoVO().getParticipant() != null) {

                            cacheReplyInfoVO.setParticipantId(threadVo.getLastMessageVO().getReplyInfoVO().getParticipant().getId());
                            messageDao.insertReplyInfoVO(cacheReplyInfoVO);
                            messageDao.insertParticipant(threadVo.getLastMessageVO().getReplyInfoVO().getParticipant());
                        }
                    }
                    if (threadVo.getLastMessageVO().getForwardInfo() != null) {

                        cacheForwardInfo = threadVo.getLastMessageVO().getForwardInfo();
                        cacheForwardInfo.setId(threadVo.getLastMessageVO().getId());
                        messageDao.insertForwardInfo(cacheForwardInfo);
                        cacheLastMessageVO.setForwardInfoId(threadVo.getLastMessageVO().getId());
                        messageDao.insertLastMessageVO(cacheLastMessageVO);
                        if (threadVo.getLastMessageVO().getForwardInfo().getParticipant() != null) {

                            cacheForwardInfo.setParticipantId(threadVo.getLastMessageVO().getForwardInfo().getParticipant().getId());
                            messageDao.insertParticipant(threadVo.getLastMessageVO().getForwardInfo().getParticipant());
                        }
                        if (threadVo.getLastMessageVO().getForwardInfo().getConversation() != null) {
                            cacheForwardInfo.setConversationId(threadVo.getLastMessageVO().getForwardInfo().getConversation().getId());
                            messageDao.insertConversationSummery(threadVo.getLastMessageVO().getForwardInfo().getConversation());
                        }
                    }
                }
                messageDao.insertThread(threadVo);
            }
        } catch (Exception e) {

        }

    }

    public void saveParticipants(List<CacheParticipant> participants, long threadId) {
        for (CacheParticipant participant : participants) {
            participant.setThreadId(threadId);
            messageDao.insertParticipant(participant);
        }
    }

    public List<Participant> getThreadParticipant(long offset, long count, long threadId) {
        List<Participant> participants = new ArrayList<>();
        if (messageDao.geParticipants(offset, count, threadId) == null) {
            return participants;
        } else {
            List<CacheParticipant> cacheParticipants = messageDao.geParticipants(offset, count, threadId);

            for (CacheParticipant cParticipant : cacheParticipants) {
                Participant participant = new Participant(
                        cParticipant.getId(),
                        cParticipant.getName(),
                        cParticipant.getFirstName(),
                        cParticipant.getLastName(),
                        cParticipant.getImage(),
                        cParticipant.getNotSeenDuration(),
                        cParticipant.getContactId(),
                        cParticipant.getContactName(),
                        cParticipant.getContactFirstName(),
                        cParticipant.getContactLastName(),
                        cParticipant.getSendEnable(),
                        cParticipant.getReceiveEnable(),
                        cParticipant.getCellphoneNumber(),
                        cParticipant.getEmail(),
                        cParticipant.getMyFriend(),
                        cParticipant.getOnline(),
                        cParticipant.getBlocked(),
                        cParticipant.getAdmin()
                );
                participants.add(participant);
            }
        }
        return participants;
    }

    public long getParticipantCount(long threadId) {
        return messageDao.getParticipantCount(threadId);
    }

    //Cache contact
    public Contact getContactById(long id){
        return messageDao.getContactById(id);
    }

    public List<Contact> getContactsByFirst(String firstName){
        return messageDao.getContactsByFirst(firstName);
    }

    public List<Contact> getContactsByLast(String lastName){
        return messageDao.getContactsByLast(lastName);
    }

    public List<Contact> getContactsByFirstAndLast(String firstName, String lastName){
        return messageDao.getContactsByFirstAndLast(firstName, lastName);
    }

    public List<Contact> getContactByCell(String cellphoneNumber){
        return messageDao.getContactByCell(cellphoneNumber);
    }

    public List<Contact> getContactsByEmail(String email){
        return messageDao.getContactsByEmail(email);
    }

}
