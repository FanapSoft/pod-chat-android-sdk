package com.fanap.podchat.persistance;

import com.fanap.podchat.cachemodel.CacheContact;
import com.fanap.podchat.cachemodel.CacheForwardInfo;
import com.fanap.podchat.cachemodel.CacheLastMessageVO;
import com.fanap.podchat.cachemodel.CacheMessageVO;
import com.fanap.podchat.cachemodel.CacheParticipant;
import com.fanap.podchat.cachemodel.CacheReplyInfoVO;
import com.fanap.podchat.cachemodel.ThreadVo;
import com.fanap.podchat.cachemodel.queue.SendingMessage;
import com.fanap.podchat.cachemodel.queue.WaitMessageQueue;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.LastMessageVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.mainmodel.UserInfo;
import com.fanap.podchat.model.ConversationSummery;
import com.fanap.podchat.model.ForwardInfo;
import com.fanap.podchat.model.MessageVO;
import com.fanap.podchat.model.ReplyInfoVO;
import com.fanap.podchat.persistance.dao.MessageDao;
import com.fanap.podchat.persistance.dao.MessageQueueDao;
import com.fanap.podchat.util.Callback;
import com.fanap.podchat.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class MessageDatabaseHelper {

    private MessageDao messageDao;
    private MessageQueueDao messageQueueDao;

    @Inject
    public MessageDatabaseHelper(MessageDao messageDao,MessageQueueDao messageQueueDao) {
        this.messageQueueDao = messageQueueDao;
        this.messageDao = messageDao;
    }

    /**
     * Cache history
     */
    public void saveHistory(List<CacheMessageVO> messageVOS, long threadId) {
        for (CacheMessageVO messageVO : messageVOS) {
            messageVO.setThreadVoId(threadId);

            long time = messageVO.getTime();
            long timeNanos = messageVO.getTimeNanos();
            long pow = (long) Math.pow(10, 9);
            long timestamp = ((time / 1000) * pow) + timeNanos;
            messageVO.setTimeStamp(timestamp);

            if (messageVO.getParticipant() != null) {
                messageVO.setParticipantId(messageVO.getParticipant().getId());
                messageDao.insertParticipant(messageVO.getParticipant());
            }

            if (messageVO.getConversation() != null) {
                messageVO.setConversationId(messageVO.getConversation().getId());
            }

            if (messageVO.getForwardInfo() != null) {
                messageVO.setForwardInfoId(messageVO.getForwardInfo().getId());
                messageDao.insertForwardInfo(messageVO.getForwardInfo());
                if (messageVO.getForwardInfo().getParticipant() != null) {
                    messageVO.getForwardInfo().setParticipantId(messageVO.getForwardInfo().getParticipant().getId());
                    messageDao.insertParticipant(messageVO.getForwardInfo().getParticipant());
                }
            }

            if (messageVO.getReplyInfoVO() != null) {
                messageVO.setReplyInfoVOId(messageVO.getReplyInfoVO().getId());
                messageDao.insertReplyInfoVO(messageVO.getReplyInfoVO());
                if (messageVO.getReplyInfoVO().getParticipant() != null) {
                    messageVO.getReplyInfoVO().setParticipantId(messageVO.getReplyInfoVO().getParticipant().getId());
                    messageDao.insertParticipant(messageVO.getReplyInfoVO().getParticipant());
                }
            }
        }
        messageDao.insertHistories(messageVOS);
    }

    public void saveMessage(CacheMessageVO cacheMessageVO, long threadId) {
        cacheMessageVO.setThreadVoId(threadId);

        if (cacheMessageVO.getParticipant() != null) {
            cacheMessageVO.setParticipantId(cacheMessageVO.getParticipant().getId());
            messageDao.insertParticipant(cacheMessageVO.getParticipant());
        }

        if (cacheMessageVO.getConversation() != null) {
//                messageDao.insertThread(messageVO.getConversation());
        }

        if (cacheMessageVO.getForwardInfo() != null) {
            cacheMessageVO.setForwardInfoId(cacheMessageVO.getForwardInfo().getId());
            messageDao.insertForwardInfo(cacheMessageVO.getForwardInfo());
            if (cacheMessageVO.getForwardInfo().getParticipant() != null) {
                cacheMessageVO.getForwardInfo().setParticipantId(cacheMessageVO.getForwardInfo().getParticipant().getId());
                messageDao.insertParticipant(cacheMessageVO.getForwardInfo().getParticipant());
            }
        }

        if (cacheMessageVO.getReplyInfoVO() != null) {
            cacheMessageVO.setReplyInfoVOId(cacheMessageVO.getReplyInfoVO().getId());
            messageDao.insertReplyInfoVO(cacheMessageVO.getReplyInfoVO());
            if (cacheMessageVO.getReplyInfoVO().getParticipant() != null) {
                cacheMessageVO.getReplyInfoVO().setParticipantId(cacheMessageVO.getReplyInfoVO().getParticipant().getId());
                messageDao.insertParticipant(cacheMessageVO.getReplyInfoVO().getParticipant());
            }
        }

        messageDao.insertMessage(cacheMessageVO);
    }

    public void insertSendingMessageQueue(String uniqueId, String textMessage, long threadId, Integer messageType, String jsonSystemMetadata){
        SendingMessage sendingMessage = new SendingMessage();

        sendingMessage.setUniqueId(uniqueId);
        sendingMessage.setMessage(textMessage);
        sendingMessage.setThreadVoId(threadId);
        sendingMessage.setMessageType(messageType);
        sendingMessage.setSystemMetadata(jsonSystemMetadata);

        messageQueueDao.insertSendingMessageQueue(sendingMessage);
    }

    public void deleteMessageQueue(String uniqueId){
        messageQueueDao.deleteSendingMessageQueue(uniqueId);
    }



    public void insertWaitMessageQueue(String uniqueId, String textMessage, long threadId, Integer messageType, String jsonSystemMetadata){
        SendingMessage sendingMessage = new SendingMessage();

        sendingMessage.setUniqueId(uniqueId);
        sendingMessage.setMessage(textMessage);
        sendingMessage.setThreadVoId(threadId);
        sendingMessage.setMessageType(messageType);
        sendingMessage.setSystemMetadata(jsonSystemMetadata);

        messageQueueDao.insertWaitMessageQueue(sendingMessage);
    }

    public void deleteWaitQueueMsgs(String uniqueId){
        messageQueueDao.deleteWaitMessageQueue(uniqueId);
    }



    public List<WaitMessageQueue> getWaitQueueMsg(long threadId){
        return messageQueueDao.getWaitQueueMsg(threadId);
    }

    public void deleteMessage(long id) {
        messageDao.deleteMessage(id);
    }

    public void updateGetHistoryResponse(Callback callback, List<MessageVO> messageVOS, long threadId, List<CacheMessageVO> cMessageVOS) {
        long count = callback.getCount();
        long offset = callback.getOffset();
        long firstMessageId = callback.getFirstMessageId();
        long lastMessageId = callback.getLastMessageId();
        long messageId = callback.getMessageId();
        long fromTime = callback.getFromTime();
        long fromTimeNanos = callback.getFromTimeNanos();
        long toTimeNanos = callback.getToTimeNanos();
        long toTime = callback.getToTime();
        String order = callback.getOrder();
        String query = callback.getQuery();

        History history = new History.Builder()
                .id(messageId)
                .count(count)
                .fromTime(fromTime)
                .fromTimeNanos(fromTimeNanos)
                .toTime(toTime)
                .toTimeNanos(toTimeNanos)
                .order(order)
                .offset(offset)
                .query(query)
                .build();

        if (!callback.isMetadataCriteria()) {
            if (order.equals("asc")) {

                whereClauseAsc(history, messageVOS, threadId, cMessageVOS);
            } else {
                whereClauseDesc(history, messageVOS, threadId, cMessageVOS);

            }
        }
    }

    private void whereClauseDesc(History history, List<MessageVO> messageVOS, long threadId, List<CacheMessageVO> cMessageVOS) {
        long messageId = history.getId();
        long fromTimeNanos = history.getFromTimeNanos();
        long fromTime = history.getFromTime() + fromTimeNanos;
        long toTimeNanos = history.getToTimeNanos();
        long toTime = history.getToTime() + toTimeNanos;
        long count = history.getCount();
        long offset = history.getOffset();
        String query = history.getQuery();
        String order = history.getOrder();

        if (!Util.isNullOrEmpty(messageId) && messageId > 0) {

            List<CacheMessageVO> messageById = getMessageById(messageId);
            if (Util.isNullOrEmpty(messageVOS) && !Util.isNullOrEmpty(messageById)) {
                messageDao.deleteMessage(messageId);
            }
            //TODO where should i put nano when there isn t any
        } else if (!Util.isNullOrEmpty(fromTime) && !Util.isNullOrEmpty(toTime)) {
            if (Util.isNullOrEmptyMessageVO(messageVOS)) {
                if (!Util.isNullOrEmptyMessageVO(getHistories(history, threadId))) {
                    messageDao.deleteMessageBetweenLastAndFirstDESC(threadId, fromTime, toTime);
                }

            } else if (messageVOS.size() == 1) {
                messageDao.deleteMessageBetweenLastAndFirstDESC(threadId, fromTime, toTime);
                saveMessage(cMessageVOS.get(0), threadId);

            } else if (messageVOS.size() > 1) {
                messageDao.deleteMessageBetweenLastAndFirstDESC(threadId, fromTime, toTime);
                saveHistory(cMessageVOS, threadId);
            }

            /**
             * From time conditions*/
        } else if (!Util.isNullOrEmpty(fromTime)) {
            if (Util.isNullOrEmptyMessageVO(messageVOS)) {
                if (!Util.isNullOrEmptyMessageVO(getHistories(history, threadId))) {
                    messageDao.deleteMessageWithFirstMessageIdDESC(count, offset, threadId, fromTime);
                }

            } else if (messageVOS.size() == 1) {
                if (getHistories(history, threadId).size() > 1) {
                    messageDao.deleteMessageWithFirstMessageIdDESC(count, offset, threadId, fromTime);
                    saveMessage(cMessageVOS.get(0), threadId);
                }

            } else if (messageVOS.size() > 1) {
                int size = getHistories(history, threadId).size();
                long firstMesssageId = getHistories(history, threadId).get(0).getId();
                long lastMesssageId = getHistories(history, threadId).get(size - 1).getId();
                messageDao.deleteMessageBetweenLastAndFirstDESC(threadId, firstMesssageId, lastMesssageId);
                saveHistory(cMessageVOS, threadId);
            }
            /**
             * To time conditions
             * */
        } else if (!Util.isNullOrEmpty(toTime)) {
            if (Util.isNullOrEmptyMessageVO(messageVOS)) {
                if (!Util.isNullOrEmptyMessageVO(getHistories(history, threadId))) {
                    messageDao.deleteMessageWithFirstMessageIdDESC(count, offset, threadId, toTime);
                }

            } else if (messageVOS.size() == 1) {
                if (getHistories(history, threadId).size() > 1) {
                    messageDao.deleteMessageWithFirstMessageIdDESC(count, offset, threadId, toTime);
                    saveMessage(cMessageVOS.get(0), threadId);
                }

            } else if (messageVOS.size() > 1) {
                int size = getHistories(history, threadId).size();
                long firstMesssageId = getHistories(history, threadId).get(0).getId();
                long lastMesssageId = getHistories(history, threadId).get(size - 1).getId();
                messageDao.deleteMessageBetweenLastAndFirstDESC(threadId, firstMesssageId, lastMesssageId);
                saveHistory(cMessageVOS, threadId);
            }
        } else if (Util.isNullOrEmpty(query)) {

            if (Util.isNullOrEmpty(messageVOS)) {
                if (!Util.isNullOrEmpty(getHistories(history, threadId))) {
                    deleteMessageWithQuery(order, threadId, count, offset, query);
                }
            } else if (messageVOS.size() == 1) {
                if (getHistories(history, threadId).size() > 1) {
                    deleteMessageWithQuery(order, threadId, count, offset, query);
                    saveMessage(cMessageVOS.get(0), threadId);
                }

            } else if (messageVOS.size() > 1) {
                int size = getHistories(history, threadId).size();
                long firstMsgId = getHistories(history, threadId).get(0).getId();
                long lastMsgId = getHistories(history, threadId).get(size - 1).getId();
                messageDao.deleteMessageBetweenLastAndFirstASC(threadId, firstMsgId, lastMsgId);
                saveHistory(cMessageVOS, threadId);
            }
        }
    }

    private void whereClauseAsc(History history, List<MessageVO> messageVOS, long threadId, List<CacheMessageVO> cMessageVOS) {

        long messageId = history.getId();
        long fromTimeNanos = history.getFromTimeNanos();
        long fromTime = history.getFromTime() + fromTimeNanos;
        long toTimeNanos = history.getToTimeNanos();
        long toTime = history.getToTime() + toTimeNanos;
        long count = history.getCount();
        long offset = history.getOffset();
        String query = history.getQuery();
        String order = history.getOrder();

        if (!Util.isNullOrEmpty(messageId) && messageId > 0) {

            List<CacheMessageVO> messageById = getMessageById(messageId);
            if (Util.isNullOrEmpty(messageVOS) && !Util.isNullOrEmpty(messageById)) {
                messageDao.deleteMessage(messageId);
            }

        } else if (!Util.isNullOrEmpty(fromTime) && !Util.isNullOrEmpty(toTime)) {
            if (Util.isNullOrEmptyMessageVO(messageVOS)) {
                if (!Util.isNullOrEmptyMessageVO(getHistories(history, threadId))) {
                    messageDao.deleteMessageBetweenLastAndFirstASC(threadId, fromTime, toTime);
                }

            } else if (messageVOS.size() == 1) {
                messageDao.deleteMessageBetweenLastAndFirstASC(threadId, fromTime, toTime);
                saveMessage(cMessageVOS.get(0), threadId);

            } else if (messageVOS.size() > 1) {
                messageDao.deleteMessageBetweenLastAndFirstASC(threadId, fromTime, toTime);
                saveHistory(cMessageVOS, threadId);
            }

            /**
             * From time conditions*/
        } else if (!Util.isNullOrEmpty(fromTime)) {
            if (Util.isNullOrEmptyMessageVO(messageVOS)) {
                if (!Util.isNullOrEmptyMessageVO(getHistories(history, threadId))) {
                    messageDao.deleteMessageWithFirstMessageIdASC(count, offset, threadId, fromTime);
                }

            } else if (messageVOS.size() == 1) {
                if (getHistories(history, threadId).size() > 1) {
                    messageDao.deleteMessageWithFirstMessageIdASC(count,offset, threadId,fromTime);
                    saveMessage(cMessageVOS.get(0), threadId);
                }

            } else if (messageVOS.size() > 1) {
                int size = getHistories(history, threadId).size();
                long firstMesssageId = getHistories(history, threadId).get(0).getId();
                long lastMesssageId = getHistories(history, threadId).get(size - 1).getId();
                messageDao.deleteMessageBetweenLastAndFirstASC(threadId, firstMesssageId, lastMesssageId);
                saveHistory(cMessageVOS, threadId);
            }
            /**
             * To time conditions
             * */
        } else if (!Util.isNullOrEmpty(toTime)) {
            if (Util.isNullOrEmptyMessageVO(messageVOS)) {
                if (!Util.isNullOrEmptyMessageVO(getHistories(history, threadId))) {
                    messageDao.deleteMessageWithFirstMessageIdASC(count, offset, threadId, fromTime);
                }

            } else if (messageVOS.size() == 1) {
                if (getHistories(history, threadId).size() > 1) {
                    messageDao.deleteMessageWithFirstMessageIdASC(count, offset, threadId, toTime);
                    saveMessage(cMessageVOS.get(0), threadId);
                }

            } else if (messageVOS.size() > 1) {
                int size = getHistories(history, threadId).size();
                long firstMesssageId = getHistories(history, threadId).get(0).getId();
                long lastMesssageId = getHistories(history, threadId).get(size - 1).getId();
                messageDao.deleteMessageBetweenLastAndFirstASC(threadId, firstMesssageId, lastMesssageId);
                saveHistory(cMessageVOS, threadId);
            }
            /**
             * Query conditions
             * */
        } else if (Util.isNullOrEmpty(query)) {

            if (Util.isNullOrEmpty(messageVOS)) {
                if (!Util.isNullOrEmpty(getHistories(history, threadId))) {
                    deleteMessageWithQuery(order, threadId, count, offset, query);
                }
            } else if (messageVOS.size() == 1) {
                if (getHistories(history, threadId).size() > 1) {
                    deleteMessageWithQuery(order, threadId, count, offset, query);
                    saveMessage(cMessageVOS.get(0), threadId);
                }

            } else if (messageVOS.size() > 1) {
                int size = getHistories(history, threadId).size();
                long firstMsgId = getHistories(history, threadId).get(0).getId();
                long lastMsgId = getHistories(history, threadId).get(size - 1).getId();
                messageDao.deleteMessageBetweenLastAndFirstASC(threadId, firstMsgId, lastMsgId);
                saveHistory(cMessageVOS, threadId);
            }
        }
    }

    private void deleteMessageWithQuery(String order, long threadId, long count, long offset, String query) {
        if (order.equals("asc")) {
            messageDao.deleteMessagesWithQueryAsc(threadId, count, offset, query);
        } else {
            messageDao.deleteMessagesWithQueryDesc(threadId, count, offset, query);
        }
    }

    private List<CacheMessageVO> getHistoriesFandLASC(String order, long count, long offset, long threadId, long firstMessageId, long lastMessageId) {
        if (order.equals("asc")) {
            return messageDao.getHistoriesFandLASC(count, offset, threadId, firstMessageId, lastMessageId);
        } else {
            return messageDao.getHistoriesFandLDESC(count, offset, threadId, firstMessageId, lastMessageId);
        }
    }

    private List<CacheMessageVO> getHistoriesMessageId(String order, long count, long offset, long threadId, long firstMessageId) {
        if (order.equals("asc")) {
            return messageDao.getHistoriesMessageIdASC(count, offset, threadId, firstMessageId);
        } else {
            return messageDao.getHistoriesMessageIdDESC(count, offset, threadId, firstMessageId);
        }
    }

    private List<CacheMessageVO> getQuery(String order, long count, long offset, long threadId, String query) {
        List<CacheMessageVO> vos;
        if (order.equals("asc")) {
            vos = messageDao.getQueryASC(count, offset, threadId, query);
            if (vos != null && vos.size() > 0) {
                return vos;
            }
        } else {
            vos = messageDao.getQueryDESC(count, offset, threadId, query);
            if (vos != null && vos.size() > 0) {
                return vos;
            }
        }
        return vos;
    }

    private List<CacheMessageVO> getHistoriesWithCountAndOffset(String order, long count, long offset, long threadId) {
        List<CacheMessageVO> vos;
        if (order.equals("asc")) {
            vos = messageDao.getHistoriesASC(count, offset, threadId);
            if (vos != null && vos.size() > 0) {
                return vos;
            }

        } else {
            vos = messageDao.getHistoriesDESC(count, offset, threadId);
            if (vos != null && vos.size() > 0) {
                return vos;
            }
        }
        return vos;
    }

    public List<MessageVO> getHistories(History history, long threadId) {
        List<MessageVO> messageVOS = new ArrayList<>();
        List<CacheMessageVO> cacheMessageVOS = new ArrayList<>();
        long lastMessageId = history.getLastMessageId();
        long firstMessageId = history.getFirstMessageId();
        long messageId = history.getId();
        long offset = history.getOffset();
        long count = history.getCount();
        String query = history.getQuery();

        String order = history.getOrder();
        if (Util.isNullOrEmpty(history.getOrder())) {
            order = "desc";
        }

        // just Message id has been set
        if (messageDao.getMessage(messageId) != null && messageId > 0) {
            cacheMessageVOS = messageDao.getMessage(messageId);
        } else {
            //just first message id and last message id has been set
            if (lastMessageId > 0 && firstMessageId > 0) {
                cacheMessageVOS = getHistoriesFandLASC(order, count, offset, threadId, firstMessageId, lastMessageId);

                // just first Message id has been set
            } else if (firstMessageId > 0) {
                cacheMessageVOS = getHistoriesMessageId(order, count, offset, threadId, firstMessageId);

                // just last Message id has been set
            } else if (lastMessageId > 0) {
                cacheMessageVOS = getHistoriesMessageId(order, count, offset, threadId, firstMessageId);

                // non of the condition of above has been set
            } else if (count > 0 && offset >= 0) {
                cacheMessageVOS = getHistoriesWithCountAndOffset(order, count, offset, threadId);

                //just query has been set
            } else if (!Util.isNullOrEmpty(query)) {
                cacheMessageVOS = getQuery(order, count, offset, threadId, query);

            }
        }

        Participant participant = null;
        ReplyInfoVO replyInfoVO = null;
        ForwardInfo forwardInfo = null;
        Thread thread = null;
        ConversationSummery conversationSummery = null;
        for (CacheMessageVO cacheMessageVO : cacheMessageVOS) {
            if (!Util.isNullOrEmpty(cacheMessageVO.getConversationId())) {
                cacheMessageVO.setConversation(messageDao.getThreadById(cacheMessageVO.getConversationId()));
                ThreadVo threadVo = cacheMessageVO.getConversation();
                thread = new Thread(
                        threadVo.getId(),
                        threadVo.getJoinDate(),
                        threadVo.getInviter(),
                        null,
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
            }
            if (cacheMessageVO.getForwardInfoId() != null) {
                cacheMessageVO.setForwardInfo(messageDao.getForwardInfo(cacheMessageVO.getForwardInfoId()));
            }
            if (cacheMessageVO.getParticipantId() != null) {
                CacheParticipant cacheParticipant = messageDao.getParticipant(cacheMessageVO.getParticipantId());
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
            if (cacheMessageVO.getReplyInfoVOId() != null) {
                CacheReplyInfoVO cacheReplyInfoVO = messageDao.getReplyInfo(cacheMessageVO.getReplyInfoVOId());
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
            if (cacheMessageVO.getForwardInfo() != null) {
                CacheForwardInfo cacheForwardInfo = messageDao.getForwardInfo(cacheMessageVO.getForwardInfoId());
                if (cacheForwardInfo.getParticipantId() != null) {
                    CacheParticipant cacheParticipant = messageDao.getParticipant(cacheForwardInfo.getParticipantId());
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
                if (Util.isNullOrEmpty(cacheForwardInfo.getConversationId())) {
                    conversationSummery = messageDao.getConversationSummery(cacheForwardInfo.getConversationId());
                }
                forwardInfo = new ForwardInfo(participant, conversationSummery);
            }

            MessageVO messageVO = new MessageVO(
                    cacheMessageVO.getId(),
                    cacheMessageVO.isEdited(),
                    cacheMessageVO.isEditable(),
                    cacheMessageVO.isDelivered(),
                    cacheMessageVO.isSeen(),
                    cacheMessageVO.isDeletable(),
                    cacheMessageVO.getUniqueId(),
                    cacheMessageVO.getMessageType(),
                    cacheMessageVO.getPreviousId(),
                    cacheMessageVO.getMessage(),
                    participant,
                    cacheMessageVO.getTime(),
                    cacheMessageVO.getTimeNanos(),
                    cacheMessageVO.getMetadata(),
                    cacheMessageVO.getSystemMetadata(),
                    thread,
                    replyInfoVO,
                    forwardInfo
            );

            messageVOS.add(messageVO);
        }
        return messageVOS;
    }


    public List<CacheMessageVO> getMessageById(long messageId) {
        return messageDao.getMessage(messageId);
    }

    public long getHistoryContentCount(long threadVoId) {
        return messageDao.getHistoryCount(threadVoId);
    }

    /**
     * //Cache contact
     */
    public List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();

        if (messageDao.getContact() != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            Date nowDate = c.getTime();

            List<CacheContact> cacheContacts = messageDao.getContact();
            for (CacheContact cacheContact : cacheContacts) {
                try {
                    Date expireDate = format.parse(cacheContact.getExpireDate());
                    if (expireDate.compareTo(nowDate) < 0) {
                        deleteContact(cacheContact);
                    } else {
                        Contact contact = new Contact(
                                cacheContact.getId(),
                                cacheContact.getFirstName(),
                                cacheContact.getUserId(),
                                cacheContact.getLastName(),
                                cacheContact.getBlocked(),
                                cacheContact.getLinkedUser(),
                                cacheContact.getCellphoneNumber(),
                                cacheContact.getEmail(),
                                cacheContact.getUniqueId(),
                                cacheContact.getNotSeenDuration(),
                                cacheContact.isHasUser()
                        );
                        contacts.add(contact);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return contacts;
    }

    public void saveContacts(List<Contact> contacts, int expireAmount) {
        List<CacheContact> cacheContacts = new ArrayList<>();
        for (Contact contact : contacts) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.SECOND, expireAmount);
            String expireDate = format.format(c.getTime());

            CacheContact cacheContact = new CacheContact(
                    expireDate,
                    contact.getId(),
                    contact.getFirstName(),
                    contact.getUserId(),
                    contact.getLastName(),
                    contact.getBlocked(),
                    contact.getLinkedUser(),
                    contact.getCellphoneNumber(),
                    contact.getEmail(),
                    contact.getUniqueId(),
                    contact.getNotSeenDuration(),
                    contact.isHasUser()
            );
            cacheContacts.add(cacheContact);
        }
        messageDao.insertContacts(cacheContacts);
    }

    public void saveContact(Contact contact, int expireSecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, expireSecond);

        String expireDate = dateFormat.format(c.getTime());
        CacheContact cacheContact = new CacheContact(
                expireDate,
                contact.getId(),
                contact.getFirstName(),
                contact.getUserId(),
                contact.getLastName(),
                contact.getBlocked(),
                contact.getLinkedUser(),
                contact.getCellphoneNumber(),
                contact.getEmail(),
                contact.getUniqueId(),
                contact.getNotSeenDuration(),
                contact.isHasUser()
        );
        messageDao.insertContact(cacheContact);
    }

    public void deleteContact(CacheContact cacheContact) {
        messageDao.deleteContact(cacheContact);
    }

    public void deleteContactById(long id) {
        messageDao.deleteContactById(id);
    }

    /**
     * Cache UserInfo
     */
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
                        if (cacheParticipant != null) {
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
                            cacheLastMessageVO.isDelivered(),
                            cacheLastMessageVO.isSeen(),
                            cacheLastMessageVO.isDeletable(),
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

    public List<Thread> getThreadsByThreadName(String threadName) {
        List<Thread> threads;
        if (messageDao.getThreadByName(threadName) != null) {
            ThreadVo threadVo = messageDao.getThreadByName(threadName);
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
                        cacheLastMessageVO.isDelivered(),
                        cacheLastMessageVO.isSeen(),
                        cacheLastMessageVO.isDeletable(),
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
        } else {
            return new ArrayList<>();
        }

        return threads;
    }

    public List<Thread> getThreadsByThreadIds(ArrayList<Integer> threadIds) {
        List<Thread> threads = new ArrayList<>();

        for (int id : threadIds) {
            if (messageDao.getThreadById(id) != null) {
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
                            cacheLastMessageVO.isDelivered(),
                            cacheLastMessageVO.isSeen(),
                            cacheLastMessageVO.isDeletable(),
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
                        CacheParticipant cacheParticipantLastMessageVO = threadVo.getLastMessageVO().getParticipant();
                        cacheParticipantLastMessageVO.setThreadId(threadVo.getId());
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

    /**
     * Cache participant
     */

    public void saveParticipants(List<CacheParticipant> participants, long threadId, int expireSecond) {
        for (CacheParticipant participant : participants) {
            participant.setThreadId(threadId);

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.SECOND, expireSecond);
            String expireDate = format.format(c.getTime());

            participant.setExpireDate(expireDate);

            messageDao.insertParticipant(participant);
        }
    }

    public void saveParticipant(CacheParticipant cacheParticipant, int expireSecond) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.SECOND, expireSecond);
        String expireDate = format.format(c.getTime());

        cacheParticipant.setExpireDate(expireDate);

        messageDao.insertParticipant(cacheParticipant);
    }

    public void deleteParticipant(long threadId, long id) {
        messageDao.deleteParticipant(threadId, id);
    }

    /**
     * Participants have expire date after expTime that you can set it manually
     */
    public List<Participant> getThreadParticipant(long offset, long count, long threadId) {
        List<Participant> participants = new ArrayList<>();
        if (messageDao.geParticipants(offset, count, threadId) == null) {
            return participants;
        } else {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            Date nowDate = c.getTime();

            List<CacheParticipant> cacheParticipants = messageDao.geParticipants(offset, count, threadId);
            for (CacheParticipant cParticipant : cacheParticipants) {

                try {
                    Date expireDate = format.parse(cParticipant.getExpireDate());
                    if (expireDate.compareTo(nowDate) < 0) {
                        deleteParticipant(threadId, cParticipant.getId());
                    } else {
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
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return participants;
    }

    public long getParticipantCount(long threadId) {
        return messageDao.getParticipantCount(threadId);
    }

    //Cache contact
    public Contact getContactById(long id) {
        CacheContact cacheContact = messageDao.getContactById(id);
        Contact contact = new Contact(
                cacheContact.getId(),
                cacheContact.getFirstName(),
                cacheContact.getUserId(),
                cacheContact.getLastName(),
                cacheContact.getBlocked(),
                cacheContact.getLinkedUser(),
                cacheContact.getCellphoneNumber(),
                cacheContact.getEmail(),
                cacheContact.getUniqueId(),
                cacheContact.getNotSeenDuration(),
                cacheContact.isHasUser()
        );
        return contact;
    }

    public List<Contact> getContactsByFirst(String firstName) {
        List<Contact> contacts = new ArrayList<>();
        List<CacheContact> cacheContacts = messageDao.getContactsByFirst(firstName);
        if (cacheContacts != null) {
            for (CacheContact cacheContact : cacheContacts) {
                Contact contact = new Contact(
                        cacheContact.getId(),
                        cacheContact.getFirstName(),
                        cacheContact.getUserId(),
                        cacheContact.getLastName(),
                        cacheContact.getBlocked(),
                        cacheContact.getLinkedUser(),
                        cacheContact.getCellphoneNumber(),
                        cacheContact.getEmail(),
                        cacheContact.getUniqueId(),
                        cacheContact.getNotSeenDuration(),
                        cacheContact.isHasUser()
                );
                contacts.add(contact);
            }
        }
        return contacts;
    }

    public List<Contact> getContactsByLast(String lastName) {
        List<Contact> contacts = new ArrayList<>();
        List<CacheContact> cacheContacts = messageDao.getContactsByLast(lastName);
        if (cacheContacts != null) {
            for (CacheContact cacheContact : cacheContacts) {
                Contact contact = new Contact(
                        cacheContact.getId(),
                        cacheContact.getFirstName(),
                        cacheContact.getUserId(),
                        cacheContact.getLastName(),
                        cacheContact.getBlocked(),
                        cacheContact.getLinkedUser(),
                        cacheContact.getCellphoneNumber(),
                        cacheContact.getEmail(),
                        cacheContact.getUniqueId(),
                        cacheContact.getNotSeenDuration(),
                        cacheContact.isHasUser()
                );
                contacts.add(contact);
            }
        }
        return contacts;
    }

    public List<Contact> getContactsByFirstAndLast(String firstName, String lastName) {
        List<Contact> contacts = new ArrayList<>();
        List<CacheContact> cacheContacts = messageDao.getContactsByFirstAndLast(firstName, lastName);
        if (cacheContacts != null) {
            for (CacheContact cacheContact : cacheContacts) {
                Contact contact = new Contact(
                        cacheContact.getId(),
                        cacheContact.getFirstName(),
                        cacheContact.getUserId(),
                        cacheContact.getLastName(),
                        cacheContact.getBlocked(),
                        cacheContact.getLinkedUser(),
                        cacheContact.getCellphoneNumber(),
                        cacheContact.getEmail(),
                        cacheContact.getUniqueId(),
                        cacheContact.getNotSeenDuration(),
                        cacheContact.isHasUser()
                );
                contacts.add(contact);
            }
        }

        return contacts;
    }

    public List<Contact> getContactByCell(String cellphoneNumber) {
        List<Contact> contacts = new ArrayList<>();
        List<CacheContact> cacheContacts = messageDao.getContactByCell(cellphoneNumber);
        if (cacheContacts != null) {
            for (CacheContact cacheContact : cacheContacts) {
                Contact contact = new Contact(
                        cacheContact.getId(),
                        cacheContact.getFirstName(),
                        cacheContact.getUserId(),
                        cacheContact.getLastName(),
                        cacheContact.getBlocked(),
                        cacheContact.getLinkedUser(),
                        cacheContact.getCellphoneNumber(),
                        cacheContact.getEmail(),
                        cacheContact.getUniqueId(),
                        cacheContact.getNotSeenDuration(),
                        cacheContact.isHasUser()
                );
                contacts.add(contact);
            }
        }
        return contacts;
    }

    public List<Contact> getContactsByEmail(String email) {
        List<Contact> contacts = new ArrayList<>();
        List<CacheContact> cacheContacts = messageDao.getContactsByEmail(email);

        if (cacheContacts != null) {
            for (CacheContact cacheContact : cacheContacts) {
                Contact contact = new Contact(
                        cacheContact.getId(),
                        cacheContact.getFirstName(),
                        cacheContact.getUserId(),
                        cacheContact.getLastName(),
                        cacheContact.getBlocked(),
                        cacheContact.getLinkedUser(),
                        cacheContact.getCellphoneNumber(),
                        cacheContact.getEmail(),
                        cacheContact.getUniqueId(),
                        cacheContact.getNotSeenDuration(),
                        cacheContact.isHasUser()
                );
                contacts.add(contact);
            }
        }
        return contacts;
    }

}
