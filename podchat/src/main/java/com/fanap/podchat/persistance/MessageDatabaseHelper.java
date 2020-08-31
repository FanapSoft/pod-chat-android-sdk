package com.fanap.podchat.persistance;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.content.Context;
import android.database.Observable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.commonsware.cwac.saferoom.SQLCipherUtils;
import com.commonsware.cwac.saferoom.SafeHelperFactory;
import com.fanap.podchat.cachemodel.CacheBlockedContact;
import com.fanap.podchat.cachemodel.CacheContact;
import com.fanap.podchat.cachemodel.CacheForwardInfo;
import com.fanap.podchat.cachemodel.CacheMessageVO;
import com.fanap.podchat.cachemodel.CacheParticipant;
import com.fanap.podchat.cachemodel.CacheParticipantRoles;
import com.fanap.podchat.cachemodel.CacheReplyInfoVO;
import com.fanap.podchat.cachemodel.CacheThreadParticipant;
import com.fanap.podchat.cachemodel.GapMessageVO;
import com.fanap.podchat.cachemodel.ThreadVo;
import com.fanap.podchat.cachemodel.queue.Failed;
import com.fanap.podchat.cachemodel.queue.Sending;
import com.fanap.podchat.cachemodel.queue.SendingQueueCache;
import com.fanap.podchat.cachemodel.queue.Uploading;
import com.fanap.podchat.cachemodel.queue.UploadingQueueCache;
import com.fanap.podchat.cachemodel.queue.WaitQueueCache;
import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.mention.model.GetMentionedRequest;
import com.fanap.podchat.chat.messge.GetAllUnreadMessageCountRequest;
import com.fanap.podchat.chat.thread.ThreadManager;
import com.fanap.podchat.chat.user.profile.ChatProfileVO;
import com.fanap.podchat.chat.user.profile.UpdateProfileResponse;
import com.fanap.podchat.chat.user.user_roles.model.CacheUserRoles;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.mainmodel.BlockedContact;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.ForwardInfo;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.LinkedUser;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.PinMessageVO;
import com.fanap.podchat.mainmodel.SearchContactRequest;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.mainmodel.UserInfo;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ConversationSummery;
import com.fanap.podchat.model.ReplyInfoVO;
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage;
import com.fanap.podchat.model.GetContactsResponse;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.persistance.dao.MessageDao;
import com.fanap.podchat.persistance.dao.MessageQueueDao;
import com.fanap.podchat.requestobject.GetHistoryRequest;
import com.fanap.podchat.requestobject.GetCurrentUserRolesRequest;
import com.fanap.podchat.util.Callback;
import com.fanap.podchat.util.ChatConstant;
import com.fanap.podchat.util.FunctionalListener;
import com.fanap.podchat.util.OnWorkDone;
import com.fanap.podchat.util.PodThreadManager;
import com.fanap.podchat.util.Util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.sentry.core.Sentry;


@SuppressWarnings("unchecked")
public class MessageDatabaseHelper {

    public static final String TAG = "CHAT_SDK_CACHE";
    private MessageDao messageDao;
    private MessageQueueDao messageQueueDao;
    private Context context;
    private AppDatabase appDatabase;


    @Inject
    public MessageDatabaseHelper(MessageDao messageDao, MessageQueueDao messageQueueDao, Context context, AppDatabase appDatabase) {
        this.messageQueueDao = messageQueueDao;
        this.messageDao = messageDao;
        this.context = context;
        this.appDatabase = appDatabase;
    }


    public void clearAllData(Chat.IClearMessageCache listener) {

        try {


            Runnable clearTablesTask = () -> appDatabase.clearAllTables();

            Runnable vacuumTask = () -> messageDao.vacuumDb(new SimpleSQLiteQuery("VACUUM"));

            new PodThreadManager()
                    .addNewTask(clearTablesTask)
                    .addNewTask(vacuumTask)
                    .addNewTask(listener::onCacheDatabaseCleared)
                    .runTasksSynced();


//            new java.lang.Thread(() -> {
//
//                try {
//
//                    appDatabase.clearAllTables();
//
//                    java.lang.Thread.sleep(2000);
//
//                    messageDao.vacuumDb(new SimpleSQLiteQuery("VACUUM"));
//
//                    java.lang.Thread.sleep(1000);
//
//                    listener.onCacheDatabaseCleared();
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    listener.onExceptionOccurred(e.getMessage());
//                }
//            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onExceptionOccurred(e.getMessage());
        }

    }


    public void getDatabaseState(String dbName) {
        SQLCipherUtils.getDatabaseState(context, dbName);
    }

    public void decryptDatabase(@NonNull File file, char[] passphrase) throws IOException {
        SQLCipherUtils.decrypt(context, file, passphrase);
    }

    public void reEncryptKey(char[] passphrase) {
        SupportSQLiteOpenHelper supportSQLiteOH = appDatabase.getOpenHelper();
        supportSQLiteOH.getWritableDatabase();
        SupportSQLiteDatabase supportSQLiteDatabase = supportSQLiteOH.getReadableDatabase();
        SafeHelperFactory.rekey(supportSQLiteDatabase, passphrase);
    }

    public boolean isDbOpen() {
        return appDatabase.isOpen();
    }

    /**
     * Cache history
     */
    public void saveMessageHistory(@NonNull List<MessageVO> messageVOS, long threadId) {


        worker(() -> {

            List<CacheMessageVO> cacheMessageVOList = new ArrayList<>();

            for (MessageVO messageVO : messageVOS) {

                CacheMessageVO cacheMessageVO = new CacheMessageVO(messageVO);

                cacheMessageVO.setThreadVoId(threadId);

                long time = cacheMessageVO.getTime();
                long timeNanos = cacheMessageVO.getTimeNanos();
                long pow = (long) Math.pow(10, 9);
                long timestamp = ((time / 1000) * pow) + timeNanos;
                cacheMessageVO.setTimeStamp(timestamp);

                if (cacheMessageVO.getParticipant() != null) {
                    cacheMessageVO.setParticipantId(cacheMessageVO.getParticipant().getId());
                    messageDao.insertParticipant(cacheMessageVO.getParticipant());
                }

                if (cacheMessageVO.getConversation() != null) {
                    cacheMessageVO.setConversationId(cacheMessageVO.getConversation().getId());
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
                    cacheMessageVO.setReplyInfoVOId(cacheMessageVO.getReplyInfoVO().getRepliedToMessageId());
                    if (cacheMessageVO.getReplyInfoVO().getParticipant() != null) {
                        cacheMessageVO.getReplyInfoVO().setParticipantId(cacheMessageVO.getReplyInfoVO().getParticipant().getId());
                        messageDao.insertParticipant(cacheMessageVO.getReplyInfoVO().getParticipant());
                    }

                    if (cacheMessageVO.getReplyInfoVO().getParticipant() != null) {
                        cacheMessageVO.getReplyInfoVO().setParticipantId(cacheMessageVO.getReplyInfoVO().getParticipant().getId());
                        messageDao.insertParticipant(cacheMessageVO.getReplyInfoVO().getParticipant());
                    }
                    messageDao.insertReplyInfoVO(cacheMessageVO.getReplyInfoVO());
                }

                cacheMessageVOList.add(cacheMessageVO);
            }


            messageDao.insertHistories(cacheMessageVOList);

        });


    }

    public void saveHistory(@NonNull List<CacheMessageVO> messageVOS, long threadId) {


        worker(() -> {

            for (CacheMessageVO cacheMessageVO : messageVOS) {

                cacheMessageVO.setThreadVoId(threadId);

                long time = cacheMessageVO.getTime();
                long timeNanos = cacheMessageVO.getTimeNanos();
                long pow = (long) Math.pow(10, 9);
                long timestamp = ((time / 1000) * pow) + timeNanos;
                cacheMessageVO.setTimeStamp(timestamp);

                if (cacheMessageVO.getParticipant() != null) {
                    cacheMessageVO.setParticipantId(cacheMessageVO.getParticipant().getId());
                    messageDao.insertParticipant(cacheMessageVO.getParticipant());
                }

                if (cacheMessageVO.getConversation() != null) {
                    cacheMessageVO.setConversationId(cacheMessageVO.getConversation().getId());
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
                    CacheReplyInfoVO cacheReplyInfoVO = cacheMessageVO.getReplyInfoVO();
                    cacheMessageVO.setReplyInfoVOId(cacheReplyInfoVO.getRepliedToMessageId());

                    if (cacheReplyInfoVO.getParticipant() != null) {
                        CacheParticipant cacheReplyParticipant = cacheReplyInfoVO.getParticipant();
                        cacheReplyInfoVO.setParticipantId(cacheReplyParticipant.getId());
                        messageDao.insertParticipant(cacheReplyParticipant);

                    }

                    messageDao.insertReplyInfoVO(cacheReplyInfoVO);
                }

            }


            messageDao.insertHistories(messageVOS);

        });


    }


    public void saveMessage(@NonNull CacheMessageVO cacheMessageVO, long threadId, boolean editedMessage) {

        worker(() -> {

            cacheMessageVO.setThreadVoId(threadId);

            try {
                long time = cacheMessageVO.getTime();
                long timeNanos = cacheMessageVO.getTimeNanos();
                long pow = (long) Math.pow(10, 9);
                long timestamp = ((time / 1000) * pow) + timeNanos;

                cacheMessageVO.setTimeStamp(timestamp);
            } catch (Exception e) {
                Log.e("CHAT_SDK", e.getMessage());
            }


            if (cacheMessageVO.getParticipant() != null) {
                cacheMessageVO.setParticipantId(cacheMessageVO.getParticipant().getId());
                messageDao.insertParticipant(cacheMessageVO.getParticipant());
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
                cacheMessageVO.setReplyInfoVOId(cacheMessageVO.getReplyInfoVO().getRepliedToMessageId());
                if (cacheMessageVO.getReplyInfoVO().getParticipant() != null) {
                    cacheMessageVO.getReplyInfoVO().setParticipantId(cacheMessageVO.getReplyInfoVO().getParticipant().getId());
                    messageDao.insertParticipant(cacheMessageVO.getReplyInfoVO().getParticipant());
                }
                messageDao.insertReplyInfoVO(cacheMessageVO.getReplyInfoVO());
            }


            //update thread last message id

            //check if message is new or edited message is thread last message


            boolean shouldUpdateIfEdited = false;

            if (editedMessage) {
                long lastMessageId = messageDao.getLastMessageId(threadId);
                if (lastMessageId > 0 && lastMessageId == cacheMessageVO.getId()) {
                    shouldUpdateIfEdited = true;
                }
            }


            messageDao.insertMessage(cacheMessageVO);


            boolean shouldUpdateLastMessage = !editedMessage || shouldUpdateIfEdited;

            if (shouldUpdateLastMessage)
                messageDao.updateThreadLastMessageVOId(threadId, cacheMessageVO.getId(), cacheMessageVO.getMessage());

        });

    }


    public void updateMessage(MessageVO lastMessage, long threadId) {

        worker(() -> {

            CacheMessageVO cacheMessageVO = new CacheMessageVO(lastMessage);
            cacheMessageVO.setThreadVoId(threadId);


            if (cacheMessageVO.getParticipant() != null) {
                cacheMessageVO.setParticipantId(cacheMessageVO.getParticipant().getId());
                messageDao.insertParticipant(cacheMessageVO.getParticipant());
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
                cacheMessageVO.setReplyInfoVOId(cacheMessageVO.getReplyInfoVO().getRepliedToMessageId());

                if (cacheMessageVO.getReplyInfoVO().getParticipant() != null) {
                    cacheMessageVO.getReplyInfoVO().setParticipantId(cacheMessageVO.getReplyInfoVO().getParticipant().getId());
                    messageDao.insertParticipant(cacheMessageVO.getReplyInfoVO().getParticipant());
                }
                messageDao.insertReplyInfoVO(cacheMessageVO.getReplyInfoVO());
            }

            messageDao.updateMessage(cacheMessageVO);


        });
    }

    /*
     *
     * Failed Queue
     *
     * */

    public void insertWaitMessageQueue(@NonNull SendingQueueCache sendingQueue) {

        WaitQueueCache waitMessageQueue = new WaitQueueCache();

        waitMessageQueue.setUniqueId(sendingQueue.getUniqueId());
        waitMessageQueue.setId(sendingQueue.getId());
        waitMessageQueue.setAsyncContent(sendingQueue.getAsyncContent());
        waitMessageQueue.setMessage(sendingQueue.getMessage());
        waitMessageQueue.setThreadId(sendingQueue.getThreadId());
        waitMessageQueue.setMessageType(sendingQueue.getMessageType());
        waitMessageQueue.setSystemMetadata(sendingQueue.getSystemMetadata());
        waitMessageQueue.setMetadata(sendingQueue.getMetadata());

        worker(() -> messageQueueDao.insertWaitMessageQueue(waitMessageQueue));
    }

    public void deleteWaitQueueMsgs(String uniqueId) {
        worker(() -> messageQueueDao.deleteWaitMessageQueue(uniqueId));
    }

    public List<WaitQueueCache> getAllWaitQueueMsg() {
        return messageQueueDao.getAllWaitQueueMsg();
    }

    public void getWaitQueueUniqueIdList(OnWorkDone listener) throws RoomIntegrityException {

        if (!canUseDatabase()) throw new RoomIntegrityException();

        List<String> items = new ArrayList<>();

        new PodThreadManager()
                .doThisSafe(
                        () -> items.addAll(messageQueueDao.getAllWaitQueueMsgUniqueId()),
                        new PodThreadManager.IComplete() {
                            @Override
                            public void onComplete() {

                                listener.onWorkDone(items);
                            }

                            @Override
                            public void onError(String error) {

                                listener.onWorkDone(null);
                            }
                        });

    }

    private boolean canUseDatabase() {

        try {
            appDatabase.beginTransaction();
            appDatabase.setTransactionSuccessful();
            appDatabase.endTransaction();
            return appDatabase != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Nullable
    public SendingQueueCache getWaitMessageQueue(String uniqueId) {
        SendingQueueCache sendingQueueCache = new SendingQueueCache();

//        messageQueueDao.getWaitQueueMsgByUniqueId(uniqueId);
//
//        sendingQueueCache.
//
        return null;
    }


    @NonNull
    public List<Failed> getAllWaitQueueCacheByThreadId(long threadId) {
        List<Failed> listQueues = new ArrayList<>();
        List<WaitQueueCache> listCaches = messageQueueDao.getWaitQueueMsgByThreadId(threadId);
        for (WaitQueueCache queueCache : listCaches) {
            Failed failed = new Failed();

            MessageVO messageVO = new MessageVO();

            messageVO.setId(queueCache.getId());
            messageVO.setMessage(queueCache.getMessage());
            messageVO.setMessageType(queueCache.getMessageType());
            messageVO.setMetadata(queueCache.getMetadata());
            messageVO.setSystemMetadata(queueCache.getSystemMetadata());

            failed.setMessageVo(messageVO);
            failed.setThreadId(queueCache.getThreadId());
            failed.setUniqueId(queueCache.getUniqueId());

            listQueues.add(failed);
        }
        return listQueues;
    }

    public void getWaitQueueAsyncContent(String uniqueId) {
        messageQueueDao.getWaitQueueAsyncContent(uniqueId);
    }


    /*
     *
     * Sending Queue
     *
     * */
    public void insertSendingMessageQueue(SendingQueueCache sendingQueue) {
        worker(() -> messageQueueDao.insertSendingMessageQueue(sendingQueue));
    }

    public void deleteSendingMessageQueue(String uniqueId) {
        worker(() -> messageQueueDao.deleteSendingMessageQueue(uniqueId));
    }


    private SendingQueueCache getSendingQueueCache(String uniqueId) {
        return messageQueueDao.getSendingQueue(uniqueId);
    }

    public void moveFromSendQueueToWaitQueue(String uniqueId) {

        new PodThreadManager().doThisSafe(() -> {
            SendingQueueCache sendingQueue = getSendingQueueCache(uniqueId);

            deleteSendingMessageQueue(uniqueId);

            insertWaitMessageQueue(sendingQueue);
        }, new PodThreadManager.IComplete() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onError(String error) {

            }
        });

    }

    public void moveFromWaitQueueToSendQueue(String uniqueId, OnWorkDone listener) {

        new PodThreadManager().doThisAndGo(() -> {
            SendingQueueCache sendingQueue = getWaitQueueMsgByUnique(uniqueId);
            deleteWaitQueueMsgs(uniqueId);
            insertSendingMessageQueue(sendingQueue);
            listener.onWorkDone(sendingQueue);
        });

    }

    public List<SendingQueueCache> getAllSendingQueue() {
        return messageQueueDao.getAllSendingQueue();
    }

    public List<WaitQueueCache> getListWaitQueueMsg(long threadId) {
        return messageQueueDao.getWaitQueueMsgByThreadId(threadId);
    }


    @NonNull
    private SendingQueueCache getWaitQueueMsgByUnique(String uniqueId) {
        SendingQueueCache sendingQueueCache = new SendingQueueCache();
        WaitQueueCache waitQueueCache = messageQueueDao.getWaitQueueMsgByUniqueId(uniqueId);

        sendingQueueCache.setAsyncContent(waitQueueCache.getAsyncContent());
        sendingQueueCache.setId(waitQueueCache.getId());
        sendingQueueCache.setMessage(waitQueueCache.getMessage());
        sendingQueueCache.setMetadata(waitQueueCache.getMetadata());
        sendingQueueCache.setThreadId(waitQueueCache.getThreadId());
        sendingQueueCache.setUniqueId(waitQueueCache.getUniqueId());
        sendingQueueCache.setSystemMetadata(waitQueueCache.getSystemMetadata());


        return sendingQueueCache;
    }


    public List<WaitQueueCache> getWaitQueueMsg(long threadId) {
        return messageQueueDao.getWaitQueueMsgByThreadId(threadId);
    }

    public List<SendingQueueCache> getAllSendingQueueCByThreadId(long threadId) {
        return messageQueueDao.getAllSendingQueue();
    }


    @NonNull
    public List<Sending> getAllSendingQueueByThreadId(long threadId) {

        List<Sending> listQueues = new ArrayList<>();
        List<SendingQueueCache> listCaches = messageQueueDao.getAllSendingQueueByThredId(threadId);

        for (SendingQueueCache queueCache : listCaches) {
            Sending sending = new Sending();
            sending.setThreadId(queueCache.getThreadId());

            MessageVO messageVO = new MessageVO();
            messageVO.setId(queueCache.getId());
            messageVO.setMessage(queueCache.getMessage());
            messageVO.setMessageType(queueCache.getMessageType());
            messageVO.setMetadata(queueCache.getMetadata());
            messageVO.setSystemMetadata(queueCache.getSystemMetadata());

            sending.setMessageVo(messageVO);

            sending.setUniqueId(queueCache.getUniqueId());

            listQueues.add(sending);
        }
        return listQueues;
    }

    /*
     * Uploading Queue
     * */

    public void insertUploadingQueue(UploadingQueueCache uploadingQueue) {
        worker(() -> messageQueueDao.insertUploadingQueue(uploadingQueue));
    }

    @NonNull
    public List<Uploading> getAllUploadingQueueByThreadId(long threadId) {
        List<Uploading> uploadingQueues = new ArrayList<>();
        List<UploadingQueueCache> uploadingQueueCaches = messageQueueDao.getAllUploadingQueueByThreadId(threadId);

        for (UploadingQueueCache queueCache : uploadingQueueCaches) {
            Uploading uploadingQueue = new Uploading();

            MessageVO messageVO = new MessageVO();

            messageVO.setId(queueCache.getId());
            messageVO.setMessage(queueCache.getMessage());
            messageVO.setMessageType(queueCache.getMessageType());
            messageVO.setMetadata(queueCache.getMetadata());
            messageVO.setSystemMetadata(queueCache.getSystemMetadata());

            uploadingQueue.setMessageVo(messageVO);
            uploadingQueue.setThreadId(queueCache.getThreadId());
            uploadingQueue.setUniqueId(queueCache.getUniqueId());
            uploadingQueues.add(uploadingQueue);
        }

        return uploadingQueues;
    }

    public void deleteUploadingQueue(String uniqueId) {
        worker(() -> messageQueueDao.deleteUploadingQueue(uniqueId));
    }

    public UploadingQueueCache getUploadingQ(String uniqueId) {
        return messageQueueDao.getUploadingQ(uniqueId);
    }

    /*
     * MessageVo Cache
     * */


    public void deleteMessage(long id, long subjectId) {


        //if this message is thread last message
        //get previous message and then delete this
        //then set previous message as last message

        worker(() -> {

            if (subjectId > 0) {

                ThreadVo threadVo = messageDao.getThreadById(subjectId);

                if (threadVo != null) {

                    long threadLastMessageId = threadVo.getLastMessageVOId();

                    if (threadLastMessageId == id && threadLastMessageId > 0) {

                        //this is last message
                        List<CacheMessageVO> cacheMessage = messageDao.getMessage(id);

                        if (!Util.isNullOrEmpty(cacheMessage)) {

                            long previousMessageId = cacheMessage.get(0).getPreviousId();

                            //Get previous message
                            List<CacheMessageVO> previousMessage = messageDao.getMessage(previousMessageId);

                            if (!Util.isNullOrEmpty(previousMessage)) {

                                String message = previousMessage.get(0).getMessage();
                                messageDao.updateThreadLastMessageVOId(subjectId, previousMessageId, message);

                            } else {

                                // this thread has only one message
                                messageDao.removeThreadLastMessageVO(subjectId);
                            }
                        }
                    }
                }

                //delete from pinned message
                PinMessageVO pinnedMessage = messageDao.getThreadPinnedMessage(subjectId);

                if (pinnedMessage != null && pinnedMessage.getMessageId() == id) {

                    messageDao.deletePinnedMessageById(id);

                }

            }

            messageDao.deleteMessage(id);

        });
    }

    public void updateGetHistoryResponse(@NonNull Callback callback, @NonNull List<MessageVO> messageVOS, long threadId, @NonNull List<CacheMessageVO> cMessageVOS) {

        worker(() -> {

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


        });

    }

    private void whereClauseDesc(History history, @NonNull List<MessageVO> messageVOS, long threadId, @NonNull List<CacheMessageVO> cMessageVOS) {
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
                saveMessage(cMessageVOS.get(0), threadId, false);

            } else if (messageVOS.size() > 1) {
                messageDao.deleteMessageBetweenLastAndFirstDESC(threadId, fromTime, toTime);
                saveHistory(cMessageVOS, threadId);
            }

            /*
             * From time conditions*/
        } else if (!Util.isNullOrEmpty(fromTime)) {
            if (Util.isNullOrEmptyMessageVO(messageVOS)) {
                if (!Util.isNullOrEmptyMessageVO(getHistories(history, threadId))) {
                    messageDao.deleteMessageWithFirstMessageIdDESC(count, offset, threadId, fromTime);
                }

            } else if (messageVOS.size() == 1) {
                if (getHistories(history, threadId).size() > 1) {
                    messageDao.deleteMessageWithFirstMessageIdDESC(count, offset, threadId, fromTime);
                    saveMessage(cMessageVOS.get(0), threadId, false);
                }

            } else if (messageVOS.size() > 1) {
                int size = getHistories(history, threadId).size();
                long firstMesssageId = getHistories(history, threadId).get(0).getId();
                long lastMesssageId = getHistories(history, threadId).get(size - 1).getId();
                messageDao.deleteMessageBetweenLastAndFirstDESC(threadId, firstMesssageId, lastMesssageId);
                saveHistory(cMessageVOS, threadId);
            }
            /*
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
                    saveMessage(cMessageVOS.get(0), threadId, false);
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
                    saveMessage(cMessageVOS.get(0), threadId, false);
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

    private void whereClauseAsc(History history, @NonNull List<MessageVO> messageVOS, long threadId, @NonNull List<CacheMessageVO> cMessageVOS) {

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
                saveMessage(cMessageVOS.get(0), threadId, false);

            } else if (messageVOS.size() > 1) {
                messageDao.deleteMessageBetweenLastAndFirstASC(threadId, fromTime, toTime);
                saveHistory(cMessageVOS, threadId);
            }

            /*
             * From time conditions
             * */
        } else if (!Util.isNullOrEmpty(fromTime)) {
            if (Util.isNullOrEmptyMessageVO(messageVOS)) {
                if (!Util.isNullOrEmptyMessageVO(getHistories(history, threadId))) {
                    messageDao.deleteMessageWithFirstMessageIdASC(count, offset, threadId, fromTime);
                }

            } else if (messageVOS.size() == 1) {
                if (getHistories(history, threadId).size() > 1) {
                    messageDao.deleteMessageWithFirstMessageIdASC(count, offset, threadId, fromTime);
                    saveMessage(cMessageVOS.get(0), threadId, false);
                }

            } else if (messageVOS.size() > 1) {
                int size = getHistories(history, threadId).size();
                long firstMesssageId = getHistories(history, threadId).get(0).getId();
                long lastMesssageId = getHistories(history, threadId).get(size - 1).getId();
                messageDao.deleteMessageBetweenLastAndFirstASC(threadId, firstMesssageId, lastMesssageId);
                saveHistory(cMessageVOS, threadId);
            }
            /*
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
                    saveMessage(cMessageVOS.get(0), threadId, false);
                }

            } else if (messageVOS.size() > 1) {
                int size = getHistories(history, threadId).size();
                long firstMesssageId = getHistories(history, threadId).get(0).getId();
                long lastMesssageId = getHistories(history, threadId).get(size - 1).getId();
                messageDao.deleteMessageBetweenLastAndFirstASC(threadId, firstMesssageId, lastMesssageId);
                saveHistory(cMessageVOS, threadId);
            }
            /*
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
                    saveMessage(cMessageVOS.get(0), threadId, false);
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

        worker(() -> {

            if (order.equals("asc")) {
                messageDao.deleteMessagesWithQueryAsc(threadId, count, offset, query);
            } else {
                messageDao.deleteMessagesWithQueryDesc(threadId, count, offset, query);
            }

        });
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


    public void deleteMessages(@NonNull History history, long threadId) {

        long fromTime = history.getFromTime();
        long fromTimeNanos = history.getFromTimeNanos();
        long toTime = history.getToTime();
        long toTimeNanos = history.getToTimeNanos();
        long messageId = history.getId();
        long offset = history.getOffset();
        long count = history.getCount();
        String query = history.getQuery();
        String order = history.getOrder();

        offset = offset >= 0 ? offset : 0;
        count = count > 0 ? count : 50;

        if (Util.isNullOrEmpty(order)) {
            order = "desc";
        }

        String rawQuery = "DELETE FROM CacheMessageVO WHERE threadVoId =" + threadId + " ORDER BY timeStamp desc LIMIT 50 OFFSET 0";

//        rawQuery = rawQuery + " LIMIT " + count + " OFFSET " + offset;

        SupportSQLiteQuery sqLiteQuery = new SimpleSQLiteQuery(rawQuery);

        messageDao.deleteMessages(sqLiteQuery);

    }

    public void deleteMessages(List<CacheMessageVO> cacheMessages) {

        messageDao.deleteMessages(cacheMessages);

    }


    @NonNull
    public List<MessageVO> getHistories(@NonNull History history, long threadId) {


        List<MessageVO> messageVOS = new ArrayList<>();
        List<CacheMessageVO> cacheMessageVOS;
        long fromTime = history.getFromTime();
        long fromTimeNanos = history.getFromTimeNanos();
        long toTime = history.getToTime();
        long toTimeNanos = history.getToTimeNanos();
        long messageId = history.getId();
        long offset = history.getOffset();
        long count = history.getCount();
        String query = history.getQuery();
        String order = history.getOrder();
        offset = offset >= 0 ? offset : 0;
        count = count > 0 ? count : 50;
        if (Util.isNullOrEmpty(order)) {
            order = "desc";
        }

        String rawQuery = "SELECT * FROM CacheMessageVO WHERE threadVoId =" + threadId;

        rawQuery = addMessageIdIfExist(messageId, rawQuery);

        rawQuery = addFromTimeIfExist(fromTime, fromTimeNanos, rawQuery);

        rawQuery = addToTimeIfExist(toTime, toTimeNanos, rawQuery);

        rawQuery = addQueryIfExist(query, rawQuery);

        rawQuery = addOrderAndLimitAndOffset(offset, count, order, rawQuery);

        SupportSQLiteQuery sqLiteQuery = new SimpleSQLiteQuery(rawQuery);

        cacheMessageVOS = messageDao.getRawHistory(sqLiteQuery);


        prepareMessageVOs(messageVOS, cacheMessageVOS);


        return messageVOS;
    }

    public void getHistories(@NonNull History history, long threadId, OnWorkDone listener) {


        List<MessageVO> messageVOS = new ArrayList<>();
        List<CacheMessageVO> cacheMessageVOS;
        long fromTime = history.getFromTime();
        long fromTimeNanos = history.getFromTimeNanos();
        long toTime = history.getToTime();
        long toTimeNanos = history.getToTimeNanos();
        long messageId = history.getId();
        long offset = history.getOffset();
        long count = history.getCount();
        int messageType = history.getMessageType();
        String query = history.getQuery();
        String order = history.getOrder();
        offset = offset >= 0 ? offset : 0;
        count = count > 0 ? count : 50;
        if (Util.isNullOrEmpty(order)) {
            order = "desc";
        }

        String rawQuery = "SELECT * FROM CacheMessageVO WHERE threadVoId =" + threadId;

        rawQuery = addMessageIdIfExist(messageId, rawQuery);

        rawQuery = addFromTimeIfExist(fromTime, fromTimeNanos, rawQuery);

        rawQuery = addToTimeIfExist(toTime, toTimeNanos, rawQuery);

        rawQuery = addQueryIfExist(query, rawQuery);

        rawQuery = addMessageTypeIfExist(messageType, rawQuery);


        long contentCount = messageDao.getHistoryContentCount(new SimpleSQLiteQuery(rawQuery.replaceFirst("SELECT \\* ", "SELECT COUNT(ID) ")));

        rawQuery = addOrderAndLimitAndOffset(offset, count, order, rawQuery);

        SupportSQLiteQuery sqLiteQuery = new SimpleSQLiteQuery(rawQuery);

        cacheMessageVOS = messageDao.getRawHistory(sqLiteQuery);

        prepareMessageVOs(messageVOS, cacheMessageVOS);

        List<Sending> sendingList = getAllSendingQueueByThreadId(threadId);

        List<Uploading> uploadingList = getAllUploadingQueueByThreadId(threadId);

        List<Failed> failedList = getAllWaitQueueCacheByThreadId(threadId);

        ChatResponse<ResultHistory> chatResponse = new ChatResponse<>();
        chatResponse.setCache(true);

        ResultHistory resultHistory = new ResultHistory();
        resultHistory.setHistory(messageVOS);

        resultHistory.setNextOffset(history.getOffset() + messageVOS.size());
        resultHistory.setContentCount(contentCount);
        if (messageVOS.size() + history.getOffset() < contentCount) {
            resultHistory.setHasNext(true);
        } else {
            resultHistory.setHasNext(false);
        }

        resultHistory.setHistory(messageVOS);


        resultHistory.setSending(sendingList);
        resultHistory.setUploadingQueue(uploadingList);
        resultHistory.setFailed(failedList);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setErrorMessage("");
        chatResponse.setResult(resultHistory);
        chatResponse.setCache(true);
        chatResponse.setSubjectId(threadId);


        listener.onWorkDone(chatResponse);
    }


    private void prepareMessageVOs(List<MessageVO> messageVOS, List<CacheMessageVO> cacheMessageVOS) {

        for (CacheMessageVO cacheMessageVO : cacheMessageVOS) {

            Participant participant = null;
            ReplyInfoVO replyInfoVO = null;
            ForwardInfo forwardInfo = null;
            ConversationSummery conversationSummery = null;

            if (cacheMessageVO.getForwardInfoId() != null) {
                cacheMessageVO.setForwardInfo(messageDao.getForwardInfo(cacheMessageVO.getForwardInfoId()));
            }
            if (cacheMessageVO.getParticipantId() != null) {
                CacheParticipant cacheParticipant = messageDao.getParticipant(cacheMessageVO.getParticipantId());
                participant = cacheToParticipantMapper(cacheParticipant, null, null);

            }
            if (cacheMessageVO.getReplyInfoVOId() != null) {

                CacheReplyInfoVO cacheReplyInfoVO = messageDao.getReplyInfo(cacheMessageVO.getReplyInfoVOId());


                if (cacheReplyInfoVO != null) {

                    replyInfoVO = new ReplyInfoVO(
                            cacheReplyInfoVO.getRepliedToMessageId(),
                            cacheReplyInfoVO.getMessageType(),
                            cacheReplyInfoVO.isDeleted(),
                            cacheReplyInfoVO.getRepliedToMessage(),
                            cacheReplyInfoVO.getSystemMetadata(),
                            cacheReplyInfoVO.getMetadata(),
                            cacheReplyInfoVO.getMessage(),
                            cacheReplyInfoVO.getRepliedToMessageTime(),
                            cacheReplyInfoVO.getRepliedToMessageNanos()
                    );

                    if (cacheReplyInfoVO.getParticipantId() > 0) {

                        CacheParticipant cacheParticipant = messageDao.getParticipant(cacheReplyInfoVO.getParticipantId());

                        Participant replyParticipant = cacheToParticipantMapper(cacheParticipant, false, null);

                        replyInfoVO.setParticipant(replyParticipant);
                    }

                }

            }
            if (cacheMessageVO.getForwardInfo() != null) {
                CacheForwardInfo cacheForwardInfo = messageDao.getForwardInfo(cacheMessageVO.getForwardInfoId());

                if (cacheForwardInfo != null) {

                    if (cacheForwardInfo.getParticipantId() != null) {
                        CacheParticipant cacheParticipant = messageDao.getParticipant(cacheForwardInfo.getParticipantId());
                        participant = cacheToParticipantMapper(cacheParticipant, null, null);
                    }
                    if (Util.isNullOrEmpty(cacheForwardInfo.getConversationId())) {
                        //todo check it again
                        conversationSummery = messageDao.getConversationSummery(cacheForwardInfo.getConversationId());
                    }
                    forwardInfo = new ForwardInfo(participant, conversationSummery);


                }


            }

            MessageVO messageVO = cacheMessageVoToMessageVoMapper(participant, replyInfoVO, forwardInfo, null, cacheMessageVO);

            messageVOS.add(messageVO);
        }
    }

    private void addPinnedMessageOfThread(ThreadVo threadVo) {
        worker(() -> {

            PinMessageVO pinnedMessage = messageDao.getThreadPinnedMessage(threadVo.getId());

            if (pinnedMessage != null) {


                //get cached participant
                if (pinnedMessage.getParticipantId() > 0) {

                    CacheParticipant cacheParticipant = messageDao.getParticipant(pinnedMessage.getParticipantId());


                    if (cacheParticipant != null) {

                        //convert cached participant to participant

                        Participant participant = cacheToParticipantMapper(cacheParticipant, false, null);

                        pinnedMessage.setParticipant(participant);


                    }

                }

                threadVo.setPinMessageVO(pinnedMessage);
            }

        });
    }

    private String addOrderAndLimitAndOffset(long offset, long count, String order, String rawQuery) {
        rawQuery = rawQuery + " ORDER BY timeStamp " + order + " LIMIT " + count + " OFFSET " + offset;
        return rawQuery;
    }

    private String addQueryIfExist(String query, String rawQuery) {
        if (!Util.isNullOrEmpty(query)) {
            rawQuery = rawQuery + " AND message LIKE '%" + query + "%'";
        }
        return rawQuery;
    }

    private String addToTimeIfExist(long toTime, long toTimeNanos, String rawQuery) {
        if (!Util.isNullOrEmpty(toTime)) {
            long pow = (long) Math.pow(10, 9);
            if (!Util.isNullOrEmpty(toTimeNanos)) {
                long timestamp = ((toTime / 1000) * pow) + toTimeNanos;
                rawQuery = rawQuery + " AND timeStamp <=" + timestamp;
            } else {
                long timestamp = ((toTime / 1000) * pow);
                rawQuery = rawQuery + " AND timeStamp <=" + timestamp;
            }
        }
        return rawQuery;
    }

    private String addFromTimeIfExist(long fromTime, long fromTimeNanos, String rawQuery) {
        if (!Util.isNullOrEmpty(fromTime)) {
            long pow = (long) Math.pow(10, 9);
            if (!Util.isNullOrEmpty(fromTimeNanos)) {
                long timestamp = ((fromTime / 1000) * pow) + fromTimeNanos;
                rawQuery = rawQuery + " AND timeStamp >=" + timestamp;
            } else {
                long timestamp = ((fromTime / 1000) * pow);
                rawQuery = rawQuery + " AND timeStamp >=" + timestamp;
            }
        }
        return rawQuery;
    }

    private String addMessageIdIfExist(long messageId, String rawQuery) {
        if (messageDao.getMessage(messageId) != null && messageId > 0) {
            rawQuery = rawQuery + " AND id=" + messageId;
        }
        return rawQuery;
    }

    private String addMessageTypeIfExist(int messageType, String rawQuery) {

        if (messageType > 0) {
            rawQuery = rawQuery + " AND messageType=" + messageType;
        }

        return rawQuery;
    }

    private MessageVO cacheMessageVoToMessageVoMapper(Participant participant, ReplyInfoVO replyInfoVO, ForwardInfo forwardInfo, Thread thread, CacheMessageVO cacheMessageVO) {
        return new MessageVO(
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
                forwardInfo,
                false,
                cacheMessageVO.hasGap(),
                cacheMessageVO.isPinned()
        );
    }

    public List<CacheMessageVO> getCacheHistories(@NonNull History history, long threadId) {
        List<CacheMessageVO> cacheMessageVOS;
        long fromTime = history.getFromTime();
        long fromTimeNanos = history.getFromTimeNanos();
        long toTime = history.getToTime();
        long toTimeNanos = history.getToTimeNanos();
        long messageId = history.getId();
        long offset = history.getOffset();
        long count = history.getCount();
        String query = history.getQuery();
        String order = history.getOrder();

        offset = offset >= 0 ? offset : 0;
        count = count > 0 ? count : 50;

        if (Util.isNullOrEmpty(order)) {
            order = "desc";
        }


        String rawQuery = "SELECT * FROM CacheMessageVO WHERE threadVoId =" + threadId;

        rawQuery = addMessageIdIfExist(messageId, rawQuery);

        rawQuery = addFromTimeIfExist(fromTime, fromTimeNanos, rawQuery);

        rawQuery = addToTimeIfExist(toTime, toTimeNanos, rawQuery);

        rawQuery = addQueryIfExist(query, rawQuery);

        rawQuery = addOrderAndLimitAndOffset(offset, count, order, rawQuery);

        SupportSQLiteQuery sqLiteQuery = new SimpleSQLiteQuery(rawQuery);

        cacheMessageVOS = messageDao.getRawHistory(sqLiteQuery);

        return cacheMessageVOS;
    }


    private History getHistoryModelFromRequestGetHistory(GetHistoryRequest request) {
        return new History.Builder()
                .count(request.getCount())
                .firstMessageId(request.getFirstMessageId())
                .lastMessageId(request.getLastMessageId())
                .metadataCriteria(request.getMetadataCriteria())
                .offset(request.getOffset())
                .fromTime(request.getFromTime())
                .fromTimeNanos(request.getFromTimeNanos())
                .toTime(request.getToTime())
                .toTimeNanos(request.getToTimeNanos())
                .uniqueIds(request.getUniqueIds())
                .id(request.getId())
                .order(request.getOrder() != null ? request.getOrder() : "desc").build();
    }


    public void getMentionList(GetMentionedRequest request, FunctionalListener listener) {


        worker(() -> {

            List<MessageVO> messageVOS = new ArrayList<>();


            List<CacheMessageVO> cacheMessageVOS = new ArrayList<>();

            String condition;

            condition = request.getUnreadMentioned() != null &&
                    request.getUnreadMentioned() ? " and seen = false " : " ";

            String rawQuery = "SELECT * FROM CacheMessageVO WHERE threadVoId = " + request.getThreadId() +
                    " and mentioned = true" + condition + "ORDER BY timeStamp ASC LIMIT " + request.getCount() + " OFFSET " + request.getOffset();

            SupportSQLiteQuery sqLiteQuery = new SimpleSQLiteQuery(rawQuery);

            cacheMessageVOS = messageDao.getRawHistory(sqLiteQuery);


            String contentCountQuery = "SELECT count(*) FROM CacheMessageVO WHERE threadVoId = " + request.getThreadId() +
                    " and mentioned = true" + condition;

            long contentCount = messageDao.getHistoryContentCount(new SimpleSQLiteQuery(contentCountQuery));

            prepareMessageVOs(messageVOS, cacheMessageVOS);

            if (messageVOS.size() > 0)
                listener.onWorkDone(messageVOS, contentCount);

        });


    }


    public List<CacheMessageVO> getMessageById(long messageId) {
        return messageDao.getMessage(messageId);
    }


    public long getHistoryContentCount(long threadVoId) {
        return messageDao.getHistoryCount(threadVoId);
    }


    @NonNull
    public List<Contact> getContacts(Integer count, Long offset) throws RoomIntegrityException {

        if (!canUseDatabase()) throw new RoomIntegrityException();

        List<Contact> contacts = new ArrayList<>();

        List<CacheContact> cacheContacts = messageDao.getContacts(count, offset);


        if (cacheContacts != null && cacheContacts.size() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            Date nowDate = c.getTime();

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
                                cacheContact.getCreationDate(),
                                cacheContact.getLinkedUser(),
                                cacheContact.getCellphoneNumber(),
                                cacheContact.getEmail(),
                                cacheContact.getUniqueId(),
                                cacheContact.getNotSeenDuration(),
                                cacheContact.isHasUser()
                        );

                        contact.setCache(true);

                        contacts.add(contact);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return contacts;
    }

    public int getContactCount() {
        return messageDao.getContactCount();
    }

    public void saveContacts(@NonNull List<Contact> contacts, int expireAmount) {

        worker(() -> {

            List<CacheContact> cacheContacts = new ArrayList<>();
            for (Contact contact : contacts) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.SECOND, expireAmount);
                String expireDate = format.format(c.getTime());

                CacheContact cacheContact = null;

                try {
                    cacheContact = getCacheContact(expireDate, contact, contact.getBlocked(), contact.getLinkedUser());
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    continue;
                }

                cacheContacts.add(cacheContact);
            }
            messageDao.insertContacts(cacheContacts);


        });
    }

    public void saveContact(@NonNull Contact contact, int expireSecond) {
        worker(() -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, expireSecond);

            String expireDate = dateFormat.format(c.getTime());
            CacheContact cacheContact = getCacheContact(expireDate, contact, contact.getBlocked(), contact.getLinkedUser());
            messageDao.insertContact(cacheContact);

        });
    }

    private void deleteContact(CacheContact cacheContact) {
        worker(() -> messageDao.deleteContact(cacheContact));
    }

    public void deleteContactById(long id) {
        worker(() -> messageDao.deleteContactById(id));
    }


    /**
     * Blocked Contacts in Cache
     *
     * @param blockedContact blocked contact
     * @param expireSecond   validation second
     */

    public void saveBlockedContact(@NonNull BlockedContact blockedContact, int expireSecond) {

        worker(() -> {

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, expireSecond);

            String expireDate = dateFormat.format(c.getTime());


            Contact contact = blockedContact.getContactVO();


            CacheBlockedContact cacheBlockedContact;


            if (contact != null) {

                CacheContact cacheContact = getCacheContact(expireDate, contact, true, null);

                cacheBlockedContact = getCacheBlockedContact(blockedContact, expireDate, cacheContact);

                saveContactVoInBlockedContact(cacheContact);

            } else {

                cacheBlockedContact = getCacheBlockedContact(blockedContact, expireDate, null);

            }


            messageDao.insertBlockedContact(cacheBlockedContact);


        });
    }

    private CacheContact getCacheContact(String expireDate, Contact contact, boolean blocked, LinkedUser linkedUser) {

        return new CacheContact(
                expireDate,
                contact.getId(),
                contact.getFirstName(),
                contact.getUserId(),
                contact.getLastName(),
                blocked,
                contact.getCreationDate(),
                linkedUser,
                contact.getCellphoneNumber(),
                contact.getEmail(),
                contact.getUniqueId(),
                contact.getNotSeenDuration(),
                contact.isHasUser(),
                contact.getProfileImage());


    }

    private CacheBlockedContact getCacheBlockedContact(@NonNull BlockedContact blockedContact, String expireDate, @Nullable CacheContact cacheContact) {
        return new CacheBlockedContact(
                expireDate,
                blockedContact.getBlockId(),
                blockedContact.getFirstName(),
                blockedContact.getLastName(),
                blockedContact.getNickName(),
                blockedContact.getProfileImage(),
                blockedContact.getCoreUserId(),
                cacheContact != null ? cacheContact.getId() : 0,
                cacheContact
        );
    }

    public void saveBlockedContacts(@NonNull List<BlockedContact> contacts, int expireAmount) {

        worker(() -> {

            List<CacheBlockedContact> cacheBlockedContacts = new ArrayList<>();

            for (BlockedContact blockedContact : contacts) {

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.SECOND, expireAmount);
                String expireDate = format.format(c.getTime());

                @Nullable Contact contact = blockedContact.getContactVO();

                //BlockedContact to CacheBlockedContact
                CacheBlockedContact cacheBlockedContact;

                if (contact != null) {

                    CacheContact cacheContact = getCacheContact(expireDate, contact, true, null);

                    cacheBlockedContact = getCacheBlockedContact(blockedContact, expireDate, cacheContact);

                    saveContactVoInBlockedContact(cacheContact);

                } else {
                    cacheBlockedContact = getCacheBlockedContact(blockedContact, expireDate, null);
                }

                cacheBlockedContacts.add(cacheBlockedContact);

            }

            if (cacheBlockedContacts.size() > 0)
                messageDao.insertBlockedContacts(cacheBlockedContacts);

        });

    }

    private void saveContactVoInBlockedContact(CacheContact cacheContact) {


        if (cacheContact != null) {

            //if blocked contact wasn't in contacts cache, insert it.
            CacheContact contactInCache = messageDao.getContactById(cacheContact.getId());

            if (contactInCache == null) {

                messageDao.insertContact(cacheContact);

            }

        }


    }


    @NonNull
    public List<BlockedContact> getBlockedContacts(Long count, Long offset) {

        List<BlockedContact> contacts = new ArrayList<>();

        List<CacheBlockedContact> cacheBlockedContacts = messageDao.getBlockedContacts(count, offset);

        if (cacheBlockedContacts != null && cacheBlockedContacts.size() > 0) {

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            Date nowDate = c.getTime();

            for (CacheBlockedContact blockedContact : cacheBlockedContacts) {

                try {
                    Date expireDate = format.parse(blockedContact.getExpireDate());
                    if (expireDate.compareTo(nowDate) < 0) {
                        deleteBlockedContact(blockedContact);
                    } else {


                        Contact contactVO = null;

                        if (blockedContact.getContactId() > 0) {

                            CacheContact cacheContact = messageDao.getContactById(blockedContact.getContactId());

                            blockedContact.setCacheContact(cacheContact);

                        }

                        if (blockedContact.getCacheContact() != null) {

                            try {
                                contactVO = new Contact(
                                        blockedContact.getCacheContact().getId(),
                                        blockedContact.getCacheContact().getFirstName(),
                                        blockedContact.getCacheContact().getUserId(),
                                        blockedContact.getCacheContact().getLastName(),
                                        blockedContact.getCacheContact().getBlocked(),
                                        blockedContact.getCacheContact().getCreationDate(),
                                        blockedContact.getCacheContact().getLinkedUser(),
                                        blockedContact.getCacheContact().getCellphoneNumber(),
                                        blockedContact.getCacheContact().getEmail(),
                                        blockedContact.getCacheContact().getUniqueId(),
                                        blockedContact.getCacheContact().getNotSeenDuration(),
                                        blockedContact.getCacheContact().isHasUser(),
                                        true,
                                        blockedContact.getCacheContact().getProfileImage()
                                );
                            } catch (Exception e) {
                                Log.d(TAG, e.getMessage());
                            }


                        }


                        BlockedContact blocked = new BlockedContact(
                                blockedContact.getBlockId(),
                                blockedContact.getFirstName(),
                                blockedContact.getLastName(),
                                blockedContact.getNickName(),
                                blockedContact.getProfileImage(),
                                blockedContact.getCoreUserId(),
                                contactVO
                        );


                        contacts.add(blocked);
                    }
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return contacts;
    }


    private void deleteBlockedContact(CacheBlockedContact cacheContact) {
        worker(() -> messageDao.deleteBlockedContact(cacheContact));
    }

    public void deleteBlockedContactById(long id) {


        //updated blocked field in CacheContact

        worker(() -> {

            try {
                CacheBlockedContact cacheBlockedContact = messageDao.getBlockedContactByBlockId(id);

                long contactId = cacheBlockedContact.getContactId();

                if (contactId > 0) {
                    messageDao.updateContactBlockedState(false, contactId);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            messageDao.deleteBlockedContactById(id);

        });


    }

    public void deleteBlockedContactByCoreUserId(long coreUserId) {
        messageDao.deleteBlockedContactByCoreUserId(coreUserId);
    }

    public void deleteBlockedContactByBlockId(long blockId) {
        messageDao.deleteBlockedContactByBlockId(blockId);
    }

    public void retrieveAndUpdateThreadOnLastMessageEdited(Thread thread, ThreadManager.ILastMessageChanged callback) {

        worker(() -> {

            long threadId = thread.getId();

            ArrayList<Integer> tIds = new ArrayList<>();

            tIds.add((int) threadId);

            try {
                getThreadRaw(1, (long) 0, tIds, null, false, threads -> {

                    List<Thread> threadList = (List<Thread>) threads;

                    if (!Util.isNullOrEmpty(threadList) && threadList.get(0).getId() > 0) {

                        Thread threadFromCache = threadList.get(0);

                        threadFromCache.setLastMessage(thread.getLastMessage());

                        threadFromCache.setLastMessageVO(thread.getLastMessageVO());

                        callback.onThreadExistInCache(threadFromCache);

                        saveNewThread(threadFromCache);

                    } else {
                        callback.threadNotFoundInCache();
                    }

                });
            } catch (RoomIntegrityException e) {
                e.printStackTrace();
                callback.threadNotFoundInCache();
            }

        });

    }

    public void retrieveAndUpdateThreadOnLastMessageDeleted(Thread thread, ThreadManager.ILastMessageChanged callback) {

        worker(() -> {

            long threadId = thread.getId();

            ArrayList<Integer> tIds = new ArrayList<>();

            tIds.add((int) threadId);

            try {
                getThreadRaw(1, (long) 0, tIds, null, false, threads -> {

                    List<Thread> threadList = (List<Thread>) threads;

                    if (!Util.isNullOrEmpty(threadList) && threadList.get(0).getId() > 0) {

                        Thread threadFromCache = threadList.get(0);

                        threadFromCache.setLastMessage(thread.getLastMessage());

                        threadFromCache.setLastMessageVO(thread.getLastMessageVO());

                        threadFromCache.setTime(thread.getTime());

                        threadFromCache.setUnreadCount(thread.getUnreadCount());

                        callback.onThreadExistInCache(threadFromCache);

                        saveNewThread(threadFromCache);

                    } else {
                        callback.threadNotFoundInCache();
                    }

                });
            } catch (RoomIntegrityException e) {
                e.printStackTrace();
                callback.threadNotFoundInCache();
            }

        });

    }

    public void retrieveAndUpdateThreadOnLastSeenUpdated(Thread thread, ThreadManager.ILastMessageChanged callback) {

        worker(() -> {

            long threadId = thread.getId();

            ArrayList<Integer> tIds = new ArrayList<>();

            tIds.add((int) threadId);

            try {
                getThreadRaw(1, (long) 0, tIds, null, false, threads -> {

                    List<Thread> threadList = (List<Thread>) threads;

                    if (!Util.isNullOrEmpty(threadList) && threadList.get(0).getId() > 0) {

                        Thread threadFromCache = threadList.get(0);
                        threadFromCache.setUnreadCount(thread.getUnreadCount());
                        callback.onThreadExistInCache(threadFromCache);
                        saveNewThread(threadFromCache);
                    } else {
                        callback.threadNotFoundInCache();
                    }

                });
            } catch (RoomIntegrityException e) {
                e.printStackTrace();
                callback.threadNotFoundInCache();
            }

        });

    }

    public void retrieveAndUpdateThreadOnNewMessageAdded(Thread thread, ThreadManager.ILastMessageChanged callback) {

        worker(() -> {

            long threadId = thread.getId();

            ArrayList<Integer> tIds = new ArrayList<>();

            tIds.add((int) threadId);

            try {
                getThreadRaw(1, (long) 0, tIds, null, false, threads -> {

                    List<Thread> threadList = (List<Thread>) threads;

                    if (!Util.isNullOrEmpty(threadList) && threadList.get(0).getId() > 0) {

                        Thread threadFromCache = threadList.get(0);

                        if (Util.isNotNullOrEmpty(thread.getTitle()))
                            threadFromCache.setTitle(thread.getTitle());

                        threadFromCache.setImage(thread.getImage());
                        threadFromCache.setDescription(thread.getDescription());
                        threadFromCache.setMetadata(thread.getMetadata());
                        threadFromCache.setTime(thread.getTime());
                        threadFromCache.setUserGroupHash(thread.getUserGroupHash());
                        threadFromCache.setUnreadCount(thread.getUnreadCount());

                        callback.onThreadExistInCache(threadFromCache);

                        saveNewThread(threadFromCache);

                    } else {
                        callback.threadNotFoundInCache();
                    }

                });
            } catch (RoomIntegrityException e) {
                e.printStackTrace();
                callback.threadNotFoundInCache();
            }

        });

    }

    public void retrieveAndUpdateThreadOnThreadInfoUpdated(Thread thread, ThreadManager.ILastMessageChanged callback) {

        worker(() -> {

            long threadId = thread.getId();

            ArrayList<Integer> tIds = new ArrayList<>();

            tIds.add((int) threadId);

            try {
                getThreadRaw(1, (long) 0, tIds, null, false, threads -> {

                    List<Thread> threadList = (List<Thread>) threads;

                    if (!Util.isNullOrEmpty(threadList) && threadList.get(0).getId() > 0) {

                        Thread threadFromCache = threadList.get(0);

                        if (!Util.isNullOrEmpty(thread.getTitle()))
                            threadFromCache.setTitle(thread.getTitle());

                        if (!Util.isNullOrEmpty(thread.getImage()))
                            threadFromCache.setImage(thread.getImage());

                        if (!Util.isNullOrEmpty(thread.getDescription()))
                            threadFromCache.setDescription(thread.getDescription());

                        if (!Util.isNullOrEmpty(thread.getMetadata()))
                            threadFromCache.setMetadata(thread.getMetadata());


                        threadFromCache.setTime(thread.getTime());
                        threadFromCache.setUserGroupHash(thread.getUserGroupHash());

                        callback.onThreadExistInCache(threadFromCache);

                        saveNewThread(threadFromCache);

                    } else {
                        callback.threadNotFoundInCache();
                    }

                });
            } catch (RoomIntegrityException e) {
                e.printStackTrace();
                callback.threadNotFoundInCache();
            }

        });

    }


    public interface IRoomIntegrity {

        void onDatabaseNeedReset();

        void onResetFailed();

        void onRoomIntegrityError();

        void onDatabaseDown();

    }

    /**
     * Cache UserInfo
     */
    public void saveUserInfo(UserInfo userInfo, IRoomIntegrity listener) {

        worker(() -> {

            try {
                messageDao.insertUserInfo(userInfo);

                //set user id for profile table
                if (userInfo.getChatProfileVO() != null) {

                    userInfo.getChatProfileVO().setId(userInfo.getId());

                    messageDao.insertChatProfile(userInfo.getChatProfileVO());
                }
            } catch (Exception e) {

                handleDatabaseException(listener, e);
            }


        });


    }

    private void handleDatabaseException(IRoomIntegrity listener, Exception e) {
        listener.onDatabaseDown();

        if (e instanceof IllegalStateException
                || e instanceof net.sqlcipher.database.SQLiteException) {
            Log.i(TAG, "Reset DB");
            listener.onRoomIntegrityError();

            File file = new File(String.valueOf(context.getDatabasePath("cache.db")));
            try {
                boolean del = file.delete();
                if (del) {
                    File db = new File(String.valueOf(context.getDatabasePath("cache.db")));
                    boolean res = db.createNewFile();
                    if (res) {
                        listener.onDatabaseNeedReset();
                    }
                }
            } catch (Exception err) {
                listener.onResetFailed();
            }

        }
    }


    public boolean resetDatabase() {

        File file = new File(String.valueOf(context.getDatabasePath("cache.db")));
        try {
            boolean del = file.delete();
            if (del) {
                File db = new File(String.valueOf(context.getDatabasePath("cache.db")));
                boolean res = db.createNewFile();
                if (res) {
                    return true;
                }
            }
        } catch (Exception err) {
            return false;
        }

        return false;
    }

    public UserInfo getUserInfo() {

        UserInfo userInfo = new UserInfo();
        if (messageDao.getUserInfo() != null) {
            userInfo = messageDao.getUserInfo();

            if (userInfo != null && userInfo.getId() > 0) {

                ChatProfileVO chatProfileVO = getChatProfile(userInfo.getId());

                userInfo.setChatProfileVO(chatProfileVO);
            }

        }
        return userInfo;
    }

    public int getThreadCount() {
        return messageDao.getThreadCount();
    }

    @NonNull
    public void getThreadRaw(Integer count,
                             Long offset,
                             @Nullable ArrayList<Integer> threadIds,
                             @Nullable String threadName,
                             boolean isNew,
                             OnWorkDone listener) throws RoomIntegrityException {


        if (!canUseDatabase()) throw new RoomIntegrityException();

        worker(() -> {

            String sQuery;

            final String ORDER = "order by pin desc,time desc";

            sQuery = "select * from ThreadVo " + ORDER;

            if (threadName != null && !isNew) {
                sQuery = "select * from ThreadVo where title LIKE '%" + threadName + "%' " + ORDER;
            }
            if (threadIds != null && threadIds.size() > 0 && !isNew) {

                StringBuilder stringBuilder = new StringBuilder();

                for (int id : threadIds) {
                    stringBuilder.append(id).append(",");
                }

                String stringIds = stringBuilder.toString();
                String lastString = stringIds.replaceAll(",$", "");

                if (threadName != null) {
                    sQuery = "select * from ThreadVo where id IN " + "(" + lastString + ")" + "AND title LIKE  '%" + threadName + "%' " + ORDER;
                } else {
                    sQuery = "select * from ThreadVo where id IN " + "(" + lastString + ")" + " " + ORDER;
                }
            }


            //only threads with unreadCount > 0 if isNew == true
            if (isNew) {
                sQuery = "select * from ThreadVo where unreadCount > 0 " + ORDER;
            }

            long contentCount = 0;

            SimpleSQLiteQuery countQuery = new SimpleSQLiteQuery(sQuery.replaceFirst("select \\* ", "select count(id) "));

            contentCount = messageDao.getThreadContentCount(countQuery);

            sQuery += getPaging(count, offset);

            SimpleSQLiteQuery query = new SimpleSQLiteQuery(sQuery);

            List<ThreadVo> threadVos = messageDao.getThreadRaw(query);

            List<Thread> threads = new ArrayList<>();

            if (threadVos != null) {

                for (ThreadVo threadVo : threadVos) {

                    if (threadVo.getId() == 0)
                        continue;

                    CacheParticipant cacheParticipant;
                    CacheReplyInfoVO cacheReplyInfoVO;
                    Participant participant = null;
                    ReplyInfoVO replyInfoVO = null;
                    @Nullable MessageVO lastMessageVO = null;
                    if (threadVo.getInviterId() > 0) {
                        threadVo.setInviter(messageDao.getInviter(threadVo.getInviterId()));
                    }

                    if (threadVo.getLastMessageVOId() > 0) {
                        threadVo.setLastMessageVO(messageDao.getLastMessageVO(threadVo.getLastMessageVOId()));
                        CacheMessageVO cacheLastMessageVO = threadVo.getLastMessageVO();

                        if (cacheLastMessageVO != null) {
                            if (cacheLastMessageVO.getParticipantId() != null) {
                                cacheParticipant = messageDao.getParticipant(cacheLastMessageVO.getParticipantId());
                                if (cacheParticipant != null) {
                                    participant = cacheToParticipantMapper(cacheParticipant, null, null);
                                }
                            }

                            if (cacheLastMessageVO.getReplyInfoVOId() != null) {

                                cacheReplyInfoVO = messageDao.getReplyInfo(cacheLastMessageVO.getReplyInfoVOId());
                                if (cacheReplyInfoVO != null)
                                    replyInfoVO = new ReplyInfoVO(
                                            cacheReplyInfoVO.getRepliedToMessageId(),
                                            cacheReplyInfoVO.getMessageType(),
                                            cacheReplyInfoVO.isDeleted(),
                                            cacheReplyInfoVO.getRepliedToMessage(),
                                            cacheReplyInfoVO.getSystemMetadata(),
                                            cacheReplyInfoVO.getMetadata(),
                                            cacheReplyInfoVO.getMessage(),
                                            cacheReplyInfoVO.getRepliedToMessageTime(),
                                            cacheReplyInfoVO.getRepliedToMessageNanos()
                                    );
                            }

                            lastMessageVO = cacheMessageVoToMessageVoMapper(participant, replyInfoVO, null, null, cacheLastMessageVO);
                        }
                    }

                    //adding pinned message of thread if exist
                    addPinnedMessageOfThread(threadVo);

                    Thread thread = threadVoToThreadMapper(threadVo, lastMessageVO);

                    threads.add(thread);
                }
            }

            listener.onWorkDone(threads);

            listener.onWorkDone(contentCount,threads);


        });


    }

    private String getPaging(Integer count, Long offset) {
        return " LIMIT " + count + " OFFSET " + offset;
    }

    @NonNull
//    public Observable<Thread> getThreadRaw(Integer count,
//                                   Long offset,
//                                   @Nullable ArrayList<Integer> threadIds,
//                                   @Nullable String threadName,
//                                   boolean isNew) throws RoomIntegrityException {
//
//
//        if (!canUseDatabase()) throw new RoomIntegrityException();
//
//        worker(() -> {
//
//            String sQuery;
//
//            final String ORDER = "order by pin desc,time desc";
//
//            sQuery = "select * from ThreadVo " + ORDER + " LIMIT " + count + " OFFSET " + offset;
//
//            if (threadName != null && !isNew) {
//                sQuery = "select  * from ThreadVo where title LIKE  '%" + threadName + "%' " + ORDER + " LIMIT " + count + " OFFSET " + offset;
//            }
//            if (threadIds != null && threadIds.size() > 0 && !isNew) {
//
//                StringBuilder stringBuilder = new StringBuilder();
//                for (int id : threadIds) {
//                    stringBuilder.append(id).append(",");
//                }
//
//                String stringIds = stringBuilder.toString();
//                String lastString = stringIds.replaceAll(",$", "");
//
//                if (threadName != null) {
//                    sQuery = "select  * from ThreadVo where id IN " + "(" + lastString + ")" + "AND title LIKE  '%" + threadName + "%' " + ORDER + " LIMIT " + count + " OFFSET " + offset;
//                } else {
//                    sQuery = "select  * from ThreadVo where id IN " + "(" + lastString + ")" + " " + ORDER + " LIMIT " + count + " OFFSET " + offset;
//                }
//            }
//
//
//            //only threads with unreadCount > 0 if isNew == true
//            if (isNew) {
//                sQuery = "select * from ThreadVo where unreadCount > 0 LIMIT " + count + " OFFSET " + offset;
//            }
//
//            SimpleSQLiteQuery query = new SimpleSQLiteQuery(sQuery);
//
//            List<ThreadVo> threadVos = messageDao.getThreadRaw(query);
//
//            List<Thread> threads = new ArrayList<>();
//
//            if (threadVos != null) {
//
//                for (ThreadVo threadVo : threadVos) {
//
//                    if (threadVo.getId() == 0)
//                        continue;
//
//                    CacheParticipant cacheParticipant;
//                    CacheReplyInfoVO cacheReplyInfoVO;
//                    Participant participant = null;
//                    ReplyInfoVO replyInfoVO = null;
//                    @Nullable MessageVO lastMessageVO = null;
//                    if (threadVo.getInviterId() > 0) {
//                        threadVo.setInviter(messageDao.getInviter(threadVo.getInviterId()));
//                    }
//
//                    if (threadVo.getLastMessageVOId() > 0) {
//                        threadVo.setLastMessageVO(messageDao.getLastMessageVO(threadVo.getLastMessageVOId()));
//                        CacheMessageVO cacheLastMessageVO = threadVo.getLastMessageVO();
//
//                        if (cacheLastMessageVO != null) {
//                            if (cacheLastMessageVO.getParticipantId() != null) {
//                                cacheParticipant = messageDao.getParticipant(cacheLastMessageVO.getParticipantId());
//                                if (cacheParticipant != null) {
//                                    participant = cacheToParticipantMapper(cacheParticipant, null, null);
//                                }
//                            }
//
//                            if (cacheLastMessageVO.getReplyInfoVOId() != null) {
//
//                                cacheReplyInfoVO = messageDao.getReplyInfo(cacheLastMessageVO.getReplyInfoVOId());
//                                if (cacheReplyInfoVO != null)
//                                    replyInfoVO = new ReplyInfoVO(
//                                            cacheReplyInfoVO.getRepliedToMessageId(),
//                                            cacheReplyInfoVO.getMessageType(),
//                                            cacheReplyInfoVO.isDeleted(),
//                                            cacheReplyInfoVO.getRepliedToMessage(),
//                                            cacheReplyInfoVO.getSystemMetadata(),
//                                            cacheReplyInfoVO.getMetadata(),
//                                            cacheReplyInfoVO.getMessage(),
//                                            cacheReplyInfoVO.getRepliedToMessageTime(),
//                                            cacheReplyInfoVO.getRepliedToMessageNanos()
//                                    );
//                            }
//
//                            lastMessageVO = cacheMessageVoToMessageVoMapper(participant, replyInfoVO, null, null, cacheLastMessageVO);
//                        }
//                    }
//
//                    //adding pinned message of thread if exist
//                    addPinnedMessageOfThread(threadVo);
//
//                    Thread thread = threadVoToThreadMapper(threadVo, lastMessageVO);
//
//                    threads.add(thread);
//                }
//            }
//
//
//
//            listener.onWorkDone(threads);
//
//        });
//
//
//    }

    private Thread threadVoToThreadMapper(ThreadVo threadVo, MessageVO lastMessageVO) {

        return new Thread(
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
                threadVo.getPartnerLastSeenMessageId(),
                threadVo.getPartnerLastDeliveredMessageId(),
                threadVo.getLastSeenMessageNanos(),
                threadVo.getLastSeenMessageTime(),
                threadVo.getPartnerLastSeenMessageTime(),
                threadVo.getPartnerLastSeenMessageNanos(),
                threadVo.getPartnerLastDeliveredMessageTime(),
                threadVo.getPartnerLastDeliveredMessageNanos(),
                threadVo.getType(),
                threadVo.isMute(),
                threadVo.getMetadata(),
                threadVo.isCanEditInfo(),
                threadVo.getParticipantCount(),
                threadVo.isCanSpam(),
                threadVo.isAdmin(),
                threadVo.isPin(),
                threadVo.isMentioned(),
                threadVo.getPinMessageVO(),
                threadVo.getUniqueName(),
                threadVo.getUserGroupHash());


    }

    private ThreadVo threadToThreadVoMapper(Thread thread) {

        return new ThreadVo(
                thread.getId(),
                thread.getJoinDate(),
                null,
                0,
                null,
                0,
                thread.getTitle(),
                null,
                thread.getTime(),
                null,
                thread.getLastParticipantName(),
                thread.getLastParticipantImage(),
                thread.isGroup(),
                thread.getPartner(),
                thread.getImage(),
                thread.getDescription(),
                thread.getUnreadCount(),
                0,
                0,
                thread.getPartnerLastSeenMessageId(),
                thread.getPartnerLastDeliveredMessageId(),
                thread.getLastSeenMessageNanos(),
                thread.getLastSeenMessageTime(),
                thread.getPartnerLastSeenMessageTime(),
                thread.getPartnerLastSeenMessageNanos(),
                thread.getPartnerLastDeliveredMessageTime(),
                thread.getPartnerLastDeliveredMessageNanos(),
                thread.getType(),
                thread.isMute()
                , thread.getMetadata(),
                thread.isCanEditInfo(),
                thread.getParticipantCount(),
                thread.getCanSpam() != null ? thread.getCanSpam() : false,
                thread.getAdmin() != null ? thread.getAdmin() : false,
                thread.isPin() != null && thread.isPin(),
                thread.isMentioned(),
                null,
                thread.getUniqueName(),
                thread.getUserGroupHash());
    }

    @NonNull
    private Participant cacheToParticipantMapper(CacheParticipant cacheParticipant, Boolean getAdmin, List<String> roles) {


        return new Participant(
                cacheParticipant.getId(),
                cacheParticipant.getName(),
                cacheParticipant.getFirstName(),
                cacheParticipant.getLastName(),
                cacheParticipant.getImage(),
                cacheParticipant.getNotSeenDuration(),
                cacheParticipant.getContactId(),
                cacheParticipant.getCoreUserId(),
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
                cacheParticipant.getAdmin(),
                cacheParticipant.isAuditor(),
                getAdmin != null ? getAdmin ? roles : null : null,
                cacheParticipant.getKeyId(),
                cacheParticipant.getUsername(),
                cacheParticipant.getChatProfileVO()
        );
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
                MessageVO lastMessageVO = null;
                if (threadVo.getInviterId() > 0) {
                    threadVo.setInviter(messageDao.getInviter(threadVo.getInviterId()));
                }
                if (threadVo.getLastMessageVOId() > 0) {
                    threadVo.setLastMessageVO(messageDao.getLastMessageVO(threadVo.getLastMessageVOId()));
                    CacheMessageVO cacheLastMessageVO = threadVo.getLastMessageVO();

                    if (cacheLastMessageVO != null) {
                        if (cacheLastMessageVO.getParticipantId() != null) {
                            cacheParticipant = messageDao.getParticipant(cacheLastMessageVO.getParticipantId());
                            if (cacheParticipant != null) {
                                participant = cacheToParticipantMapper(cacheParticipant, null, null);
                            }

                        }
                        if (cacheLastMessageVO.getReplyInfoVOId() != null) {
                            cacheReplyInfoVO = messageDao.getReplyInfo(cacheLastMessageVO.getReplyInfoVOId());
                            if (cacheReplyInfoVO != null)
                                replyInfoVO = new ReplyInfoVO(
                                        cacheReplyInfoVO.getRepliedToMessageId(),
                                        cacheReplyInfoVO.getMessageType(),
                                        cacheReplyInfoVO.isDeleted(),
                                        cacheReplyInfoVO.getRepliedToMessage(),
                                        cacheReplyInfoVO.getSystemMetadata(),
                                        cacheReplyInfoVO.getMetadata(),
                                        cacheReplyInfoVO.getMessage(),
                                        cacheReplyInfoVO.getRepliedToMessageTime(),
                                        cacheReplyInfoVO.getRepliedToMessageNanos()
                                );
                        }
                        lastMessageVO = cacheMessageVoToMessageVoMapper(participant, replyInfoVO, null, null, cacheLastMessageVO);

                    }

                }

                //adding pinned message of thread if exist
                addPinnedMessageOfThread(threadVo);

                Thread thread = threadVoToThreadMapper(threadVo, lastMessageVO);
                threads.add(thread);
            }


            //sort threads by last message time

            Collections.sort(new ArrayList<>(threads), (o1, o2) -> Long.compare(o1.getLastMessageVO().getTime(), o2.getLastMessageVO().getTime()));

            return threads;
        } else {
            threads = new ArrayList<>();
        }

        return threads;
    }

    @NonNull
    public void getThreadList(OnWorkDone listener) {

        new java.lang.Thread(() -> {

            String rawQuery = "select * from ThreadVo";

            SupportSQLiteQuery query = new SimpleSQLiteQuery(rawQuery);

            List<Thread> threads = new ArrayList<>();

            List<ThreadVo> tvo = messageDao.getThreadRaw(query);

            for (ThreadVo threadInCache :
                    tvo) {

                //adding pinned message of thread if exist
                addPinnedMessageOfThread(threadInCache);


                threads.add(threadVoToThreadMapper(threadInCache, null));

            }


            listener.onWorkDone(threads);

        }).start();


    }

    @NonNull
    public void getThreadIdsList(OnWorkDone listener) {

        worker(() -> {

            String rawQuery = "select id from ThreadVo";

            SupportSQLiteQuery query = new SimpleSQLiteQuery(rawQuery);

            List<Long> tvo = messageDao.getThreadIds(query);

            listener.onWorkDone(tvo);

        });


    }

    public List<Thread> getThreadsByThreadName(String threadName) {
        List<Thread> threads;
        List<ThreadVo> listThreadVo = messageDao.getThreadByName(50, 0, threadName);
        if (listThreadVo != null) {
            threads = new ArrayList<>();
            for (ThreadVo threadVo : listThreadVo) {
                CacheParticipant cacheParticipant;
                CacheReplyInfoVO cacheReplyInfoVO;
                Participant participant = null;
                ReplyInfoVO replyInfoVO = null;
                MessageVO lastMessageVO = null;
                if (threadVo.getInviterId() > 0) {
                    threadVo.setInviter(messageDao.getInviter(threadVo.getInviterId()));
                }
                if (threadVo.getLastMessageVOId() > 0) {
                    threadVo.setLastMessageVO(messageDao.getLastMessageVO(threadVo.getLastMessageVOId()));
                    CacheMessageVO cacheLastMessageVO = threadVo.getLastMessageVO();

                    if (cacheLastMessageVO != null) {
                        if (cacheLastMessageVO.getParticipantId() != null) {
                            cacheParticipant = messageDao.getParticipant(cacheLastMessageVO.getParticipantId());
                            participant = cacheToParticipantMapper(cacheParticipant, null, null);
                        }
                        if (cacheLastMessageVO.getReplyInfoVOId() != null) {
                            cacheReplyInfoVO = messageDao.getReplyInfo(cacheLastMessageVO.getReplyInfoVOId());

                            if (cacheReplyInfoVO != null)
                                replyInfoVO = new ReplyInfoVO(
                                        cacheReplyInfoVO.getRepliedToMessageId(),
                                        cacheReplyInfoVO.getMessageType(),
                                        cacheReplyInfoVO.isDeleted(),
                                        cacheReplyInfoVO.getRepliedToMessage(),
                                        cacheReplyInfoVO.getSystemMetadata(),
                                        cacheReplyInfoVO.getMetadata(),
                                        cacheReplyInfoVO.getMessage(),
                                        cacheReplyInfoVO.getRepliedToMessageTime(),
                                        cacheReplyInfoVO.getRepliedToMessageNanos()
                                );
                        }

                        lastMessageVO = cacheMessageVoToMessageVoMapper(participant, replyInfoVO, null, null, cacheLastMessageVO);

                    }

                }

                //adding pinned message of thread if exist
                addPinnedMessageOfThread(threadVo);

                Thread thread = threadVoToThreadMapper(threadVo, lastMessageVO);
                threads.add(thread);
            }


        } else {
            return new ArrayList<>();
        }

        return threads;
    }

    @NonNull
    public List<Thread> getThreadsByThreadIds(@NonNull ArrayList<Integer> threadIds) {
        List<Thread> threads = new ArrayList<>();

        for (int id : threadIds) {
            if (messageDao.getThreadById(id) != null) {
                ThreadVo threadVo = messageDao.getThreadById(id);
                CacheParticipant cacheParticipant;
                CacheReplyInfoVO cacheReplyInfoVO;
                Participant participant = null;
                ReplyInfoVO replyInfoVO = null;
                MessageVO lastMessageVO = null;
                if (threadVo.getInviterId() > 0) {
                    threadVo.setInviter(messageDao.getInviter(threadVo.getInviterId()));
                }
                if (threadVo.getLastMessageVOId() > 0) {

                    threadVo.setLastMessageVO(messageDao.getLastMessageVO(threadVo.getLastMessageVOId()));

                    CacheMessageVO cacheLastMessageVO = threadVo.getLastMessageVO();

                    if (cacheLastMessageVO != null) {

                        if (cacheLastMessageVO.getParticipantId() != null) {
                            cacheParticipant = messageDao.getParticipant(cacheLastMessageVO.getParticipantId());
                            participant = cacheToParticipantMapper(cacheParticipant, null, null);
                        }
                        if (cacheLastMessageVO.getReplyInfoVOId() != null) {
                            cacheReplyInfoVO = messageDao.getReplyInfo(cacheLastMessageVO.getReplyInfoVOId());

                            if (cacheReplyInfoVO != null)
                                replyInfoVO = new ReplyInfoVO(
                                        cacheReplyInfoVO.getRepliedToMessageId(),
                                        cacheReplyInfoVO.getMessageType(),
                                        cacheReplyInfoVO.isDeleted(),
                                        cacheReplyInfoVO.getRepliedToMessage(),
                                        cacheReplyInfoVO.getSystemMetadata(),
                                        cacheReplyInfoVO.getMetadata(),
                                        cacheReplyInfoVO.getMessage(),
                                        cacheReplyInfoVO.getRepliedToMessageTime(),
                                        cacheReplyInfoVO.getRepliedToMessageNanos()
                                );
                        }
                        lastMessageVO = cacheMessageVoToMessageVoMapper(participant, replyInfoVO, null, null, cacheLastMessageVO);
                    }

                }

                //adding pinned message of thread if exist
                addPinnedMessageOfThread(threadVo);


                Thread thread = threadVoToThreadMapper(threadVo, lastMessageVO);
                threads.add(thread);
            }
        }

        return threads;
    }


    public void saveNewThread(Thread thread) {

        if (thread != null && thread.getId() > 0)
            worker(() -> prepareThreadVOAndSaveIt(thread));

    }


    public void saveThreads(@NonNull List<Thread> threads) {


        worker(() -> {

            for (Thread thread : threads) {
                if (thread.getId() > 0)
                    prepareThreadVOAndSaveIt(thread);
            }

        });

    }

    private void prepareThreadVOAndSaveIt(Thread thread) {

        String threadJson = App.getGson().toJson(thread);

        ThreadVo threadVo = App.getGson().fromJson(threadJson, ThreadVo.class);

        if (threadVo.getInviter() != null) {

            insertInviter(threadVo);
        }

        if (thread.getPinMessageVO() != null) {

            insertPinnedMessage(thread);

        }

        if (thread.getLastMessageVO() != null) {


            updateThreadLastMessage(thread, threadVo);


        }

        messageDao.insertThread(threadVo);
    }

    private void updateThreadLastMessage(Thread thread, ThreadVo threadVo) {
        CacheMessageVO cacheMessageVO;
        CacheReplyInfoVO cacheReplyInfoVO;
        CacheForwardInfo cacheForwardInfo;

        cacheMessageVO = insertLastMessage(thread, threadVo);
        if (cacheMessageVO == null) return;


        if (threadVo.getLastMessageVO().getParticipant() != null) {

            insertParticipant(cacheMessageVO, threadVo);

        }
        if (threadVo.getLastMessageVO().getReplyInfoVO() != null) {

            cacheReplyInfoVO = insertReplyInfo(cacheMessageVO, threadVo);

            if (threadVo.getLastMessageVO().getReplyInfoVO().getParticipant() != null) {

                insertReplyParticipant(cacheReplyInfoVO, threadVo);
            }
        }
        if (threadVo.getLastMessageVO().getForwardInfo() != null) {

            cacheForwardInfo = insertForwardInfo(cacheMessageVO, threadVo);
            if (threadVo.getLastMessageVO().getForwardInfo().getParticipant() != null) {

                insertForwardInfo(cacheForwardInfo, threadVo);
            }
            if (threadVo.getLastMessageVO().getForwardInfo().getConversation() != null) {
                insertConversationSummary(cacheForwardInfo, threadVo);
            }
        }
    }

    private void insertConversationSummary(CacheForwardInfo cacheForwardInfo, ThreadVo threadVo) {

        cacheForwardInfo.setConversationId(threadVo.getLastMessageVO().getForwardInfo().getConversation().getId());
        messageDao.insertConversationSummery(threadVo.getLastMessageVO().getForwardInfo().getConversation());
    }

    private void insertForwardInfo(CacheForwardInfo cacheForwardInfo, ThreadVo threadVo) {
        cacheForwardInfo.setParticipantId(threadVo.getLastMessageVO().getForwardInfo().getParticipant().getId());
        messageDao.insertParticipant(threadVo.getLastMessageVO().getForwardInfo().getParticipant());
    }

    private CacheForwardInfo insertForwardInfo(CacheMessageVO cacheMessageVO, ThreadVo threadVo) {
        CacheForwardInfo cacheForwardInfo;
        cacheForwardInfo = threadVo.getLastMessageVO().getForwardInfo();
        cacheForwardInfo.setId(threadVo.getLastMessageVO().getId());
        messageDao.insertForwardInfo(cacheForwardInfo);
        cacheMessageVO.setForwardInfoId(threadVo.getLastMessageVO().getId());
        messageDao.insertLastMessageVO(cacheMessageVO);
        return cacheForwardInfo;
    }

    private void insertReplyParticipant(CacheReplyInfoVO cacheReplyInfoVO, ThreadVo threadVo) {
        cacheReplyInfoVO.setParticipantId(threadVo.getLastMessageVO().getReplyInfoVO().getParticipant().getId());
        messageDao.insertReplyInfoVO(cacheReplyInfoVO);
        messageDao.insertParticipant(threadVo.getLastMessageVO().getReplyInfoVO().getParticipant());
    }

    private CacheReplyInfoVO insertReplyInfo(CacheMessageVO cacheMessageVO, ThreadVo threadVo) {
        CacheReplyInfoVO cacheReplyInfoVO;
        cacheReplyInfoVO = threadVo.getLastMessageVO().getReplyInfoVO();
        cacheMessageVO.setReplyInfoVOId(threadVo.getLastMessageVO().getReplyInfoVO().getRepliedToMessageId());
        messageDao.insertLastMessageVO(cacheMessageVO);
        if (cacheReplyInfoVO.getParticipant() != null) {
            cacheReplyInfoVO.setParticipantId(cacheReplyInfoVO.getParticipant().getId());
            messageDao.insertParticipant(cacheMessageVO.getReplyInfoVO().getParticipant());
        }

        messageDao.insertReplyInfoVO(cacheReplyInfoVO);
        return cacheReplyInfoVO;
    }

    private void insertParticipant(CacheMessageVO cacheMessageVO, ThreadVo threadVo) {
        cacheMessageVO.setParticipantId(threadVo.getLastMessageVO().getParticipant().getId());
        messageDao.insertLastMessageVO(cacheMessageVO);
        CacheParticipant cacheParticipantLastMessageVO = threadVo.getLastMessageVO().getParticipant();
        cacheParticipantLastMessageVO.setThreadId(threadVo.getId());
        messageDao.insertParticipant(threadVo.getLastMessageVO().getParticipant());
    }

    @Nullable
    private CacheMessageVO insertLastMessage(Thread thread, ThreadVo threadVo) {

        CacheMessageVO cacheMessageVO;
        try {

            MessageVO messageVO = thread.getLastMessageVO();

            messageVO.setConversation(thread);

            cacheMessageVO = new CacheMessageVO(messageVO);

            threadVo.setLastMessageVO(cacheMessageVO);

            threadVo.setLastMessageVOId(cacheMessageVO.getId());

            messageDao.insertLastMessageVO(cacheMessageVO);

            messageVO.setConversation(null);

        } catch (Exception e) {
            Sentry.captureException(e);
            return null;
        }

        return cacheMessageVO;
    }

    private void insertPinnedMessage(Thread thread) {

        PinMessageVO pinMessageVO = thread.getPinMessageVO();
        pinMessageVO.setThreadId(thread.getId());


        try {
            Participant participant = pinMessageVO.getParticipant();

            if (participant != null) {
                String participantJson = App.getGson().toJson(participant);
                CacheParticipant cacheParticipant = App.getGson().fromJson(participantJson, CacheParticipant.class);
                messageDao.insertParticipant(cacheParticipant);
                pinMessageVO.setParticipantId(cacheParticipant.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        messageDao.insertPinnedMessage(pinMessageVO);

    }

    private void insertInviter(ThreadVo threadVo) {
        threadVo.setInviterId(threadVo.getInviter().getId());
        messageDao.insertInviter(threadVo.getInviter());
    }

    public void leaveThread(long threadId) {

        worker(() -> {
            deleteLastMessageVo(threadId);
            messageDao.deleteThread(threadId);
            messageDao.deleteAllThreadParticipant(threadId);
            messageDao.deleteAllMessageByThread(threadId);
        });

    }

    private void deleteLastMessageVo(long threadId) {
        long lastMsgId = messageDao.getLastMessageId(threadId);
        messageDao.deleteLastMessage(lastMsgId);
    }

    /**
     * Cache participant
     */

    public void saveParticipants(@NonNull List<CacheParticipant> participants, long threadId, int expireSecond) {

        worker(() -> {
            for (CacheParticipant participant : participants) {

                participant.setThreadId(threadId);

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.SECOND, expireSecond);
                String expireDate = format.format(c.getTime());

                messageDao.insertParticipant(participant);

                CacheThreadParticipant ctp = new CacheThreadParticipant();
                ctp.setExpireDate(expireDate);
                ctp.setParticipantId(participant.getId());
                ctp.setThreadId(threadId);

                messageDao.insertThreadParticipant(ctp);

                if (!Util.isNullOrEmpty(participant.getRoles())) {

                    CacheParticipantRoles cpr = new CacheParticipantRoles();

                    cpr.setId(participant.getId());

                    cpr.setThreadId(threadId);

                    cpr.setRoles(participant.getRoles());

                    Log.d("MTAG", "SAVE CPR: " + cpr);

                    messageDao.insertRoles(cpr);

                }

                if (participant.getChatProfileVO() != null) {

                    ChatProfileVO chatProfileVO = participant.getChatProfileVO();
                    chatProfileVO.setId(participant.getId());
                    messageDao.insertChatProfile(chatProfileVO);

                }
            }
        });
    }

    public void deleteParticipant(long threadId, long id) {
        messageDao.deleteParticipant(threadId, id);
    }

    /**
     * Participants have expire date after expTime that you can set it manually
     */


    public void updateParticipantRoles(long participantId, long threadId, List<String> roles) {

        worker(() -> {
            CacheParticipantRoles cpr = new CacheParticipantRoles();

            cpr.setId(participantId);

            cpr.setThreadId(threadId);

            cpr.setRoles(roles);

            messageDao.insertRoles(cpr);
        });

    }


    @NonNull
    public void getThreadParticipant(long offset, long count, long threadId, FunctionalListener listener)
            throws RoomIntegrityException {

        if (!canUseDatabase()) throw new RoomIntegrityException();

        worker(() -> {

            List<Participant> participants = new ArrayList<>();

            List<CacheThreadParticipant> listCtp = messageDao.getAllThreadParticipants(offset, count, threadId);

            long participantCount = messageDao.getParticipantCount(threadId);

            if (listCtp == null) {

                listener.onWorkDone(participantCount, participants);

            } else {

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                Date nowDate = c.getTime();

                for (CacheThreadParticipant threadParticipant : listCtp) {
                    try {
                        Date expireDate = format.parse(threadParticipant.getExpireDate());
                        long participantId = threadParticipant.getParticipantId();

                        if (expireDate != null) {
                            if (expireDate.compareTo(nowDate) < 0) {
                                messageDao.deleteCacheThreadParticipant(participantId);
                            } else {

                                CacheParticipant cParticipant = messageDao.getParticipant(participantId);

                                ChatProfileVO chatProfileVO = messageDao.getChatProfileVOById(cParticipant.getId());
                                if (chatProfileVO != null) {
                                    cParticipant.setChatProfileVO(chatProfileVO);
                                }

                                List<String> roles = new ArrayList<>();

                                Participant participant = cacheToParticipantMapper(cParticipant, false, roles);
                                participants.add(participant);


                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }


            listener.onWorkDone(participantCount, participants);

        });

    }


    public void getThreadAdmins(long offset, long count, long threadId, FunctionalListener listener)

            throws RoomIntegrityException {

        if (!canUseDatabase()) throw new RoomIntegrityException();

        worker(() -> {

            List<Participant> participants = new ArrayList<>();

            List<CacheThreadParticipant> listCtp = messageDao.getAllThreadParticipants(offset, count, threadId);

            long participantCount = messageDao.getParticipantCount(threadId);

            if (listCtp == null) {

                listener.onWorkDone(participantCount, participants);

            } else {

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                Date nowDate = c.getTime();

                for (CacheThreadParticipant threadParticipant : listCtp) {
                    try {
                        Date expireDate = format.parse(threadParticipant.getExpireDate());
                        long participantId = threadParticipant.getParticipantId();

                        if (expireDate != null) {
                            if (expireDate.compareTo(nowDate) < 0) {
                                messageDao.deleteCacheThreadParticipant(participantId);
                            } else {

                                CacheParticipant cParticipant = messageDao.getParticipant(participantId);

                                ChatProfileVO chatProfileVO = messageDao.getChatProfileVOById(cParticipant.getId());
                                if (chatProfileVO != null) {
                                    cParticipant.setChatProfileVO(chatProfileVO);
                                }

                                List<String> roles = new ArrayList<>();


                                CacheParticipantRoles cachedRoles = messageDao.getParticipantRoles(participantId, threadId);

                                if (cachedRoles != null) {
                                    if (cachedRoles.getRoles().size() > 0)
                                        roles = cachedRoles.getRoles();
                                }


                                if (roles.size() > 0) {
                                    Participant participant = cacheToParticipantMapper(cParticipant, true, roles);
                                    participants.add(participant);
                                }


                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            listener.onWorkDone(participantCount, participants);

        });

    }

    private Date createNewDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.getTime();
    }

    public long getParticipantCount(long threadId) {
        return messageDao.getParticipantCount(threadId);
    }


    //Cache contact
    @NonNull
    public Contact getContactById(long id) {
        CacheContact cacheContact = messageDao.getContactById(id);
        return new Contact(
                cacheContact.getId(),
                cacheContact.getFirstName(),
                cacheContact.getUserId(),
                cacheContact.getLastName(),
                cacheContact.getBlocked(),
                cacheContact.getCreationDate(),
                cacheContact.getLinkedUser(),
                cacheContact.getCellphoneNumber(),
                cacheContact.getEmail(),
                cacheContact.getUniqueId(),
                cacheContact.getNotSeenDuration(),
                cacheContact.isHasUser()
        );
    }

    @NonNull
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
                        cacheContact.getCreationDate(),
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


    @NonNull
    public ChatResponse<GetContactsResponse> searchContacts(SearchContactRequest requestSearchContact, String size, String offset) {

        List<Contact> contacts = new ArrayList<>();

        ChatResponse<GetContactsResponse> chatResponse = new ChatResponse<>();
        chatResponse.setCache(true);
        GetContactsResponse resultContact = new GetContactsResponse();
        resultContact.setContacts(new ArrayList<>(contacts));
        chatResponse.setHasError(false);

        long nextOffset = Long.parseLong(offset) + Long.parseLong(size);

        resultContact.setHasNext(false);
        resultContact.setNextOffset(nextOffset);


        if (requestSearchContact.getId() != null) {
            try {
                CacheContact cacheContact = messageDao.getContactById(Long.parseLong(requestSearchContact.getId()));
                contacts.add(cacheContactToContactMapper(cacheContact));
                resultContact.setContacts(new ArrayList<>(contacts));
                resultContact.setContentCount(1);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid Id");
                chatResponse.setHasError(true);
                chatResponse.setErrorMessage("Invalid Id");
                chatResponse.setErrorCode(ChatConstant.ERROR_CODE_INVALID_CONTACT_ID);
                resultContact.setContentCount(0);
            }
            chatResponse.setResult(resultContact);
            return chatResponse;
        }

        String order = Util.isNullOrEmpty(requestSearchContact.getOrder()) ? "desc" : requestSearchContact.getOrder();

        String orderBy = " order by hasUser " + order + ", lastName is null or lastName='', lastName, firstName is null or firstName='', firstName";

        String query = "select * from CacheContact where";

        if (!Util.isNullOrEmpty(requestSearchContact.getQuery())) {

            query += " (firstName LIKE '%" + requestSearchContact.getQuery() + "%' OR lastName LIKE '%" + requestSearchContact.getQuery() + "%') AND";

        } else if (!Util.isNullOrEmpty(requestSearchContact.getFirstName()) && !Util.isNullOrEmpty(requestSearchContact.getLastName()))
            query += " (firstName LIKE '%" + requestSearchContact.getFirstName() + "%' AND lastName LIKE '%" + requestSearchContact.getLastName() + "%') AND";
        else if (!Util.isNullOrEmpty(requestSearchContact.getFirstName()))
            query += " firstName LIKE '%" + requestSearchContact.getFirstName() + "%' AND";
        else if (!Util.isNullOrEmpty(requestSearchContact.getLastName()))
            query += " lastName LIKE '%" + requestSearchContact.getLastName() + "%' AND";


        if (!Util.isNullOrEmpty(requestSearchContact.getEmail()))
            query += " email LIKE '%" + requestSearchContact.getEmail() + "%' AND";

        if (!Util.isNullOrEmpty(requestSearchContact.getCellphoneNumber()))
            query += " cellphoneNumber LIKE '%" + requestSearchContact.getCellphoneNumber() + "%'";


        if (query.endsWith("AND")) {

            query = query.substring(0, query.lastIndexOf("AND") - 1);

        }

        long contentCount = messageDao.getRawContactsCount(new SimpleSQLiteQuery(query.replaceFirst("select \\* ", "select count(id) ")));


        query += orderBy + " LIMIT " + size + " OFFSET " + offset;

        List<CacheContact> cachedContacts = messageDao.getRawContacts(new SimpleSQLiteQuery(query));

        if (!Util.isNullOrEmpty(cachedContacts)) {
            for (CacheContact cachedContact :
                    cachedContacts) {
                contacts.add(cacheContactToContactMapper(cachedContact));
            }
        }


        resultContact.setContacts(new ArrayList<>(contacts));
        resultContact.setHasNext(Long.parseLong(offset) + contacts.size() < contentCount);
        resultContact.setNextOffset(Long.parseLong(offset) + contacts.size());
        resultContact.setContentCount(contentCount);

        chatResponse.setResult(resultContact);


        return chatResponse;
    }

    private Contact cacheContactToContactMapper(CacheContact cacheContact) {
        Contact contact = new Contact(
                cacheContact.getId(),
                cacheContact.getFirstName(),
                cacheContact.getUserId(),
                cacheContact.getLastName(),
                cacheContact.getBlocked(),
                cacheContact.getCreationDate(),
                cacheContact.getLinkedUser(),
                cacheContact.getCellphoneNumber(),
                cacheContact.getEmail(),
                cacheContact.getUniqueId(),
                cacheContact.getNotSeenDuration(),
                cacheContact.isHasUser()
        );
        contact.setCache(true);
        return contact;
    }


    @NonNull
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
                        cacheContact.getCreationDate(),
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

    @NonNull
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
                        cacheContact.getCreationDate(),
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

    @NonNull
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
                        cacheContact.getCreationDate(),
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

    @NonNull
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
                        cacheContact.getCreationDate(),
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


    public void insertGap(GapMessageVO gapMessageVO) {


        worker(() -> messageDao.insertGap(gapMessageVO));


    }

    public void getAllGaps(long threadId, OnWorkDone listener) {


        worker(() -> {


            List<GapMessageVO> data = messageDao.getGapMessages(threadId);

            listener.onWorkDone(data);


        });


    }


    public List<GapMessageVO> getAllGaps(long threadId) {


        return messageDao.getGapMessages(threadId);


    }

    public void deleteAllGapsFrom(long threadId) {

        worker(() -> messageDao.deleteAllGapMessagesFrom(threadId));

    }


    private void worker(Runnable work) {

        new PodThreadManager()
                .doThisSafe(work);

    }


    public void getGap(long id, OnWorkDone listener) {


        worker(() -> listener.onWorkDone(messageDao.getGap(id)));


    }

    public void deleteGapForMessageId(Long msgId) {


        worker(() -> messageDao.deleteGap(msgId));


    }

    public void updateMessageGapState(Long msgId, boolean hasGap) {

        worker(() -> {

            List<CacheMessageVO> cacheMessageVO = messageDao.getMessage(msgId);

            if (!Util.isNullOrEmpty(cacheMessageVO)) {

                for (CacheMessageVO msg :
                        cacheMessageVO) {
                    msg.setHasGap(hasGap);

                    messageDao.updateMessage(msg);
                }

            }

        });


    }

    public void deleteThread(long threadId) {

        worker(() -> {

            messageDao.deletePinnedMessageByThreadId(threadId);

            messageDao.deleteAllGapMessagesFrom(threadId);

            messageDao.deleteAllMessageByThread(threadId);

            messageDao.deleteThread(threadId);

        });

    }

    public void deleteThreads(ArrayList<Long> deletedThreadsIds) {

        worker(() -> {

            for (Long id :
                    deletedThreadsIds) {

                deleteLastMessageVo(id);
                messageDao.deleteThread(id);
                messageDao.deleteAllThreadParticipant(id);
                messageDao.deleteAllMessageByThread(id);

            }

        });

    }


    public void savePinMessage(ChatResponse<ResultPinMessage> response, long subjectId) {


        worker(() -> {
            ResultPinMessage result = response.getResult();

            PinMessageVO pinMessageVO = new PinMessageVO();

            pinMessageVO.setThreadId(subjectId);

            pinMessageVO.setMessageId(result.getMessageId());

            pinMessageVO.setNotifyAll(result.isNotifyAll());

            pinMessageVO.setText(result.getText());

            pinMessageVO.setTime(result.getTime());

            if (result.getParticipant() != null) {

                Participant participant = result.getParticipant();

                String participantJson = App.getGson().toJson(participant);
                CacheParticipant cacheParticipant = App.getGson().fromJson(participantJson, CacheParticipant.class);
                messageDao.insertParticipant(cacheParticipant);
                pinMessageVO.setParticipantId(cacheParticipant.getId());

            }

            messageDao.insertPinnedMessage(pinMessageVO);
        });


    }

    public void deletePinnedMessageById(int messageId) {

        messageDao.deletePinnedMessageById(messageId);


    }

    public void deletePinnedMessageByThreadId(long threadId) {

        worker(() -> messageDao.deletePinnedMessageByThreadId(threadId));


    }

    public void setThreadPinned(ChatMessage chatMessage) {

        worker(() -> {
            long threadId = chatMessage.getSubjectId();
            messageDao.updateThreadPinState(threadId, true);
        });

    }

    public void setThreadUnPinned(ChatMessage chatMessage) {

        worker(() -> {
            long threadId = chatMessage.getSubjectId();
            messageDao.updateThreadPinState(threadId, false);
        });

    }


    public void updateChatProfile(UpdateProfileResponse result) {

        worker(() -> {
            UserInfo userInfo = getUserInfo();

            ChatProfileVO chatProfileVO = new ChatProfileVO();

            chatProfileVO.setId(userInfo.getId());

            chatProfileVO.setBio(result.getBio());

            userInfo.setChatProfileVO(chatProfileVO);

            messageDao.insertChatProfile(chatProfileVO);

            messageDao.insertUserInfo(userInfo);


        });

    }


    private ChatProfileVO getChatProfile(long id) {

        return messageDao.getChatProfileVOById(id);

    }


    public void loadAllUnreadMessagesCount(GetAllUnreadMessageCountRequest req, OnWorkDone listener)

            throws RoomIntegrityException {

        if (!canUseDatabase()) throw new RoomIntegrityException();

        long count;

        if (req.withMuteThreads()) {
            count = messageDao.getAllUnreadMessagesCount();
        } else {
            count = messageDao.getAllUnreadMessagesCountNoMutes(false);

        }

        listener.onWorkDone(count);

    }

    public void getCurrentUserRoles(GetCurrentUserRolesRequest request, OnWorkDone listener) {

        worker(() -> {

            CacheUserRoles role = messageDao.getUserRoles(request.getThreadId());

            listener.onWorkDone(role);

        });

    }

    public void saveCurrentUserRoles(ChatResponse<ResultCurrentUserRoles> response) {

        worker(() -> {

            long threadId = response.getSubjectId();

            List<String> roles = response.getResult().getRoles();

            CacheUserRoles cacheUserRoles = new CacheUserRoles();

            cacheUserRoles.setThreadId(threadId);

            cacheUserRoles.setRole(roles);

            messageDao.insertCurrentUserRoles(cacheUserRoles);

        });


    }

    public void deleteMessagesOfThread(long subjectId) {

        worker(() -> messageDao.deleteAllMessageByThread(subjectId));
    }
}
