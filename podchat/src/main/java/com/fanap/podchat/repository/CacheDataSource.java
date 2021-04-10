package com.fanap.podchat.repository;

import android.content.Context;
import android.support.annotation.Nullable;

import com.fanap.podchat.cachemodel.CacheFile;
import com.fanap.podchat.cachemodel.CacheMessageVO;
import com.fanap.podchat.cachemodel.queue.Failed;
import com.fanap.podchat.cachemodel.queue.Sending;
import com.fanap.podchat.cachemodel.queue.SendingQueueCache;
import com.fanap.podchat.cachemodel.queue.Uploading;
import com.fanap.podchat.cachemodel.queue.UploadingQueueCache;
import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.chat.contact.ContactManager;
import com.fanap.podchat.chat.messge.MessageManager;
import com.fanap.podchat.chat.thread.ThreadManager;
import com.fanap.podchat.mainmodel.BlockedContact;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.Admin;
import com.fanap.podchat.persistance.MessageDatabaseHelper;
import com.fanap.podchat.persistance.RoomIntegrityException;
import com.fanap.podchat.persistance.module.AppDatabaseModule;
import com.fanap.podchat.persistance.module.AppModule;
import com.fanap.podchat.persistance.module.DaggerMessageComponent;
import com.fanap.podchat.util.Callback;
import com.fanap.podchat.util.ChatConstant;
import com.fanap.podchat.util.OnWorkDone;
import com.fanap.podchat.util.PodChatException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class CacheDataSource {


    public static final String DISK = "DISK";
    @Inject
    MessageDatabaseHelper databaseHelper;
    private int expireAmount = 2 * 24 * 60 * 60;

    public CacheDataSource() {
    }

    public CacheDataSource(Context context, String key) {

        DaggerMessageComponent.builder()
                .appDatabaseModule(new AppDatabaseModule(context, key))
                .appModule(new AppModule(context))
                .build()
                .injectDataSource(this);

    }


    /*
    Threads
     */
    public Observable<ThreadManager.ThreadResponse> getThreadsData(Integer count, Long offset, @Nullable ArrayList<Integer> threadIds, @Nullable String threadName, boolean isNew) throws RoomIntegrityException {


        return Observable
                .create(emitter -> {
                    try {
                        databaseHelper.getThreadRaw(count, offset, threadIds, threadName, isNew, new OnWorkDone() {
                            @Override
                            public void onWorkDone(@Nullable Object o) {
//                                emitter.onNext(new ThreadManager.CacheThread((List<Thread>) o, ((List<Thread>) o).size()));
                            }

                            @Override
                            public void onWorkDone(@Nullable Object o, List z) {

                                ThreadManager.ThreadResponse threadResponse = new ThreadManager.ThreadResponse(z, (Long) o, DISK);

                                emitter.onNext(threadResponse);

                            }
                        });
                    } catch (RoomIntegrityException e) {
                        emitter.onError(e);
                    }
                });
    }

    public void cacheThreads(List<Thread> data) {
        databaseHelper.saveThreads(data);
    }

    public void cacheThread(Thread thread) {
        databaseHelper.saveNewThread(thread);
    }

    /*
    Contacts
     */

    public Observable<ContactManager.ContactResponse> getContactsData(Integer count, Long offset) {


        return Observable.create(emitter -> {


            try {

                List<Contact> contactList = databaseHelper.getContacts(count, offset);

                long contentCount = databaseHelper.getContactCount();

                ContactManager.ContactResponse contactResponse = new ContactManager.ContactResponse(contactList, contentCount, DISK);

                emitter.onNext(contactResponse);

            } catch (RoomIntegrityException e) {
                emitter.onError(e);
            }


        });


    }

    public void cacheContacts(List<Contact> contacts) {

        databaseHelper.saveContacts(contacts, getExpireAmount());

    }

    public void cacheContact(Contact contact) {
        databaseHelper.saveContact(contact, getExpireAmount());
    }

    public void saveBlockedContact(BlockedContact contact) {

        databaseHelper.saveBlockedContact(contact, getExpireAmount());

    }

    public void saveBlockedContacts(List<BlockedContact> blockedContacts) {
        databaseHelper.saveBlockedContacts(blockedContacts, getExpireAmount());
    }

    public void deleteContactById(long userId) {
        databaseHelper.deleteContactById(userId);
    }

    public void deleteBlockedContact(long blockId) {
        databaseHelper.deleteBlockedContactById(blockId);
    }

    public void setExpireAmount(int expireAmount) {
        this.expireAmount = expireAmount;
    }

    private int getExpireAmount() {
        return expireAmount;
    }


    /*
  Messages
   */
    public Observable<MessageManager.HistoryResponse> getMessagesData(History request, long threadId) {
        return databaseHelper.getThreadHistory(request,threadId)
                .map(data-> new MessageManager.HistoryResponse(data, DISK));
    }

    public void cacheMessages(List<MessageVO> data, long threadId) {

        databaseHelper.saveMessageHistory(data, threadId,getExpireAmount());
    }

    public void cacheMessage(MessageVO message, long threadId) {
        databaseHelper.saveMessage(message, threadId, false);
    }

    public void saveMessage(MessageVO messageVO, long threadId) {
        databaseHelper.saveMessage(messageVO, threadId, true);
    }

    public void updateMessage(MessageVO lastMessage, long threadId) {

        databaseHelper.updateMessage(lastMessage, threadId);
    }

    public void deleteMessage(long id, long threadId) {

        databaseHelper.deleteMessage(id, threadId);
    }

    public void updateThreadAfterChangeType(long threadId) {
        databaseHelper.changeThreadAfterChangeType(threadId);
    }

    public void cancelMessage(String uniqueId) {
        databaseHelper.deleteSendingMessageQueue(uniqueId);
        databaseHelper.deleteWaitQueueMsgs(uniqueId);
    }

    //sending queue

    public void cacheSendingQueue(SendingQueueCache sendingQueue) {
        databaseHelper.insertSendingMessageQueue(sendingQueue);
    }

    public void updateHistoryResponse(Callback callback, List<MessageVO> messageVOS, long subjectId, List<CacheMessageVO> cMessageVOS) {

        databaseHelper.updateGetHistoryResponse(callback, messageVOS, subjectId, cMessageVOS);
    }

    public void moveFromSendingToWaitingQueue(String uniqueId) {
        databaseHelper.moveFromSendQueueToWaitQueue(uniqueId);
    }

    public Observable<SendingQueueCache> moveFromWaitingQueueToSendingQueue(String uniqueId) {

        return Observable
                .create(emitter -> databaseHelper
                        .moveFromWaitQueueToSendQueue(uniqueId, sendingQueue ->
                        {
                            if (null != sendingQueue) {
                                emitter.onNext((SendingQueueCache) sendingQueue);
                            } else {
                                emitter.onCompleted();
                            }

                        }));


    }

    public Observable<List<SendingQueueCache>> getAllSendingQueue() {

        return Observable.create(em -> {
            List<SendingQueueCache> sending = databaseHelper.getAllSendingQueue();
            if (sending != null)
                em.onNext(databaseHelper.getAllSendingQueue());
            else em.onCompleted();
        });
    }

    public List<Sending> getAllSendingQueueByThreadId(long subjectId) {
        return databaseHelper.getAllSendingQueueByThreadId(subjectId);
    }

    public List<Uploading> getAllUploadingQueueByThreadId(long subjectId) {
        return databaseHelper.getAllUploadingQueueByThreadId(subjectId);
    }

    public List<Failed> getAllWaitQueueCacheByThreadId(long subjectId) {
        return databaseHelper.getAllWaitQueueCacheByThreadId(subjectId);
    }

    public Observable<List<String>> getWaitQueueUniqueIdList() {

        return Observable.create(emitter -> {

            try {
                databaseHelper.getWaitQueueUniqueIdList(o -> {
                    try {
                        if (o != null) {

                            List<String> data = (List<String>) o;


                            emitter.onNext(data);

                        } else {
                            emitter.onError(new PodChatException("No uniqueId found", ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION));
                        }
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                });
            } catch (RoomIntegrityException e) {
                emitter.onError(e);
            }
        });


    }

    public void deleteWaitQueueMsgs(String messageUniqueId) {
        databaseHelper.deleteWaitQueueMsgs(messageUniqueId);
    }

    public void insertUploadingQueue(UploadingQueueCache uploadingQueue) {

        databaseHelper.insertUploadingQueue(uploadingQueue);
    }

    public void deleteUploadingQueue(String uniqueId) {
        databaseHelper.deleteUploadingQueue(uniqueId);
    }

    public Observable<UploadingQueueCache> getUploadingMessages(String uniqueId) {


        return Observable.create(em -> {
            UploadingQueueCache uploading = databaseHelper.getUploadingQ(uniqueId);
            if (uploading != null)
                em.onNext(uploading);
            else
                em.onCompleted();
        });
    }

    public UploadingQueueCache getUploadingQ(String uniqueId) {


        return databaseHelper.getUploadingQ(uniqueId);
    }

    public void updateParticipantRoles(ArrayList<Admin> admins, long threadId) {

        for (Admin a :
                admins) {
            databaseHelper.updateParticipantRoles(a.getId(), threadId, a.getRoles());
        }
    }

    public void cacheImage(CacheFile cacheFile) {
        databaseHelper.saveImageInCache(cacheFile);
    }

    public List<CacheFile> getImageByHash(String hashCode) {

        return databaseHelper.getImagesByHash(hashCode);

    }

}
