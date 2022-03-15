package com.fanap.podchat.repository;

import androidx.annotation.Nullable;

import com.fanap.podchat.cachemodel.CacheFile;
import com.fanap.podchat.cachemodel.CacheMessageVO;
import com.fanap.podchat.cachemodel.queue.Failed;
import com.fanap.podchat.cachemodel.queue.Sending;
import com.fanap.podchat.cachemodel.queue.SendingQueueCache;
import com.fanap.podchat.cachemodel.queue.Uploading;
import com.fanap.podchat.cachemodel.queue.UploadingQueueCache;
import com.fanap.podchat.chat.contact.ContactManager;
import com.fanap.podchat.chat.messge.MessageManager;
import com.fanap.podchat.chat.thread.ThreadManager;
import com.fanap.podchat.mainmodel.BlockedContact;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.chat.messge.SearchSystemMetadataRequest;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.Admin;
import com.fanap.podchat.persistance.RoomIntegrityException;
import com.fanap.podchat.util.Callback;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class ChatDataSource {

    public MemoryDataSource memoryDataSource;

    public CacheDataSource cacheDataSource;


    public ChatDataSource(MemoryDataSource memoryDataSource, CacheDataSource cacheDataSource) {
        this.memoryDataSource = memoryDataSource;
        this.cacheDataSource = cacheDataSource;
    }


    /*
    THREADS
     */

    private Observable<ThreadManager.ThreadResponse> getThreadsFromMemoryDataSource(Integer count, Long offset, @Nullable ArrayList<Integer> threadIds, @Nullable String threadName, boolean isNew) {
        return memoryDataSource.getThreadsData(count, offset, threadIds, threadName, isNew);
    }

    private Observable<ThreadManager.ThreadResponse> getMutualThreadsFromCacheDataSource(Integer count, Long offset, Long userId) throws RoomIntegrityException {
        //get from disk cache and put in memory cache
        return cacheDataSource.getMutualThreadsData(count, offset, userId).doOnNext(threadResponse -> saveThreadResultFromCache(threadResponse.getThreadList()));
    }

    private Observable<ThreadManager.ThreadResponse> getThreadsFromCacheDataSource(Integer count, Long offset, @Nullable ArrayList<Integer> threadIds, @Nullable String threadName, boolean isNew) throws RoomIntegrityException {
        //get from disk cache and put in memory cache
        return cacheDataSource.getThreadsData(count, offset, threadIds, threadName, isNew).doOnNext(threadResponse -> saveThreadResultFromCache(threadResponse.getThreadList()));
    }

    public Observable<ThreadManager.ThreadResponse> getThreadData(Integer count,
                                                                  Long offset,
                                                                  ArrayList<Integer> threadIds,
                                                                  String threadName,
                                                                  boolean isNew) throws RoomIntegrityException {


        if (offset == null) {
            offset = 0L;
        }

        if (count == null || count == 0)
            count = 50;


//        return  getThreadsFromMemoryDataSource(count, offset, threadIds, threadName, isNew)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io());

        return Observable.concat(
                getThreadsFromMemoryDataSource(count, offset, threadIds, threadName, isNew),
                getThreadsFromCacheDataSource(count, offset, threadIds, threadName, isNew))
                .first()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());


    }
    public Observable<ThreadManager.ThreadResponse> getMutualThreadData(Integer count,
                                                                  Long offset,
                                                                        long  userId
                                                                  ) throws RoomIntegrityException {


        if (offset == null) {
            offset = 0L;
        }

        if (count == null || count == 0)
            count = 50;

        return getMutualThreadsFromCacheDataSource(count, offset, userId);


    }


    public void saveThreadResultFromServer(List<Thread> server) {

        memoryDataSource.cacheThreads(server);
        cacheDataSource.cacheThreads(server);

    }

    public void saveMutualThreadResultFromServer(List<Thread> server,long userId) {

        cacheDataSource.cacheMutualThreads(server,userId);

    }

    public void saveThreadResultFromServer(Thread thread) {
        cacheDataSource.cacheThread(thread);
        memoryDataSource.upsertThread(thread);
    }

    public void saveThreadResultFromCache(List<Thread> server) {
        memoryDataSource.cacheThreads(server);
    }

    public void saveThreadResultFromCache(Thread thread) {
        memoryDataSource.upsertThread(thread);
    }







    /*
    CONTACTS
     */

    public Observable<ContactManager.ContactResponse> getContactsFromMemoryDataSource(Integer count, Long offset,
                                                                                      String username) {
        return memoryDataSource.getContactsData(count, offset,username);
    }

    public Observable<ContactManager.ContactResponse> getContactsFromCacheDataSource(Integer count, Long offset,
                                                                                     String username) {
        //get from disk cache and put in memory cache
        return cacheDataSource.getContactsData(count, offset,username).doOnNext(cacheContact -> saveContactsResultFromCache(cacheContact.getContactsList()));
    }

    public Observable<ContactManager.ContactResponse> getContactData(Integer count,
                                                                     Long offset,
                                                                     String username) {

        if (offset == null) {
            offset = 0L;
        }

        if (count == null || count == 0)
            count = 50;


        return Observable.concat(
                getContactsFromMemoryDataSource(count, offset,username),
                getContactsFromCacheDataSource(count, offset,username))
                .first()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());

    }


    public void saveContactsResultFromServer(List<Contact> contacts) {
        memoryDataSource.cacheContacts(contacts);
        cacheDataSource.cacheContacts(contacts);
    }

    public void saveContactsResultFromCache(List<Contact> contacts) {
        memoryDataSource.cacheContacts(contacts);
    }

    public void saveContactResultFromServer(Contact contact) {
        cacheDataSource.cacheContact(contact);
        memoryDataSource.cacheContact(contact);
    }

    public void updateContact(Contact contact) {
        memoryDataSource.upsertContact(contact);
    }


    public void deleteContactById(long userId) {

        cacheDataSource.deleteContactById(userId);

        memoryDataSource.deleteContactById(userId);

    }

    //blocked contacts

    public void saveBlockedContactResultFromServer(BlockedContact contact) {

        cacheDataSource.saveBlockedContact(contact);

        memoryDataSource.saveBlockedContact(contact);


    }


    public void saveBlockedContactsResultFromServer(List<BlockedContact> blockedContacts) {

        cacheDataSource.saveBlockedContacts(blockedContacts);

        memoryDataSource.saveBlockedContacts(blockedContacts);

    }

    public void deleteBlockedContactById(long blockId) {

        cacheDataSource.deleteBlockedContact(blockId);

        memoryDataSource.deleteBlockedContact(blockId);


    }


    public void changeExpireAmount(int expireSecond) {
        cacheDataSource.setExpireAmount(expireSecond);
    }





     /*
    MESSAGE
     */

    public Observable<MessageManager.HistoryResponse> getMessagesFromMemoryDataSource(History request, long threadId) {
        return memoryDataSource.getMessagesData(request, threadId);
    }

    public Observable<MessageManager.HistoryResponse> getMessagesFromCacheDataSource(History request, long threadId) {
        //get from disk cache and put in memory cache
        return cacheDataSource.getMessagesData(request, threadId).doOnNext(historyResponse -> saveMessageResultFromCache(historyResponse.getResponse().getResult().getHistory()));
    }

    public Observable<MessageManager.HistoryResponse> getMessagesData(History request, long threadId) {


        return getMessagesFromCacheDataSource(request, threadId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());


    }


    public void saveMessageResultFromServer(List<MessageVO> server, long threadId) {

        memoryDataSource.cacheMessages(server);

        cacheDataSource.cacheMessages(server, threadId);

    }

    public void saveMessageResultFromServer(MessageVO server, long threadId) {

        memoryDataSource.upsertMessage(server);

        cacheDataSource.cacheMessage(server, threadId);

    }

    public void updateMessageResultFromServer(MessageVO server, long threadId) {

        memoryDataSource.upsertMessage(server);

        cacheDataSource.saveMessage(server, threadId);

    }

    public void saveMessageResultFromCache(List<MessageVO> cached) {

        memoryDataSource.cacheMessages(cached);

    }

    public void updateMessage(MessageVO message) {
        memoryDataSource.upsertMessage(message);
    }

    public void updateMessage(MessageVO lastMessage, long threadId) {

        memoryDataSource.upsertMessage(lastMessage);

        cacheDataSource.updateMessage(lastMessage, threadId);

    }

    public void deleteMessage(MessageVO msg, long threadId) {

        memoryDataSource.deleteMessage(msg, threadId);

        cacheDataSource.deleteMessage(msg.getId(), threadId);


    }

    public void updateHistoryResponse(Callback callback, List<MessageVO> messageVOS, long subjectId, List<CacheMessageVO> cMessageVOS) {

        //todo remove it!
        cacheDataSource.updateHistoryResponse(callback, messageVOS, subjectId, cMessageVOS);

    }

    public void addToSendingQueue(SendingQueueCache sendingQueue) {
        cacheDataSource.cacheSendingQueue(sendingQueue);
        memoryDataSource.insertToSendingQueue(sendingQueue);
    }

    public void cancelMessage(String uniqueId) {
        cacheDataSource.cancelMessage(uniqueId);
        memoryDataSource.cancelMessage(uniqueId);
    }

    public Observable<MessageManager.HistoryResponse> getMessagesSystemMetadataData(SearchSystemMetadataRequest request) {


        return getMessagesSystemMetadataFromCacheDataSource(request)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());


    }

    private Observable<MessageManager.HistoryResponse> getMessagesSystemMetadataFromCacheDataSource(SearchSystemMetadataRequest request) {
        //get from disk cache and put in memory cache
        return cacheDataSource.getMessagesSystemMetadataData(request).doOnNext(historyResponse -> saveMessageResultFromCache(historyResponse.getResponse().getResult().getHistory()));
    }

    //Sending Queue

    public void moveFromSendingToWaitingQueue(String uniqueId) {
        cacheDataSource.moveFromSendingToWaitingQueue(uniqueId);
        memoryDataSource.moveFromSendingToWaitingQueue(uniqueId);

    }


    public Observable<SendingQueueCache> moveFromWaitingToSendingQueue(String uniqueId) {


        return Observable.concat(
                memoryDataSource.moveFromWaitingQueueToSendingQueue(uniqueId),
                cacheDataSource.moveFromWaitingQueueToSendingQueue(uniqueId))
                .first()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());


    }

    public Observable<List<SendingQueueCache>> getAllSendingQueue() {


        return Observable.concat(
                memoryDataSource.getAllSendingQueue(),
                cacheDataSource.getAllSendingQueue())
                .first()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());


    }


    public List<Sending> getAllSendingQueueByThreadId(long subjectId) {
        return cacheDataSource.getAllSendingQueueByThreadId(subjectId);
    }


    //Waiting Queue


    public List<Failed> getAllWaitQueueCacheByThreadId(long subjectId) {
        return cacheDataSource.getAllWaitQueueCacheByThreadId(subjectId);
    }

    public Observable<List<String>> getWaitQueueUniqueIdList() {


        return cacheDataSource.getWaitQueueUniqueIdList()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public void deleteWaitQueueWithUniqueId(String uniqueId) {

        cacheDataSource.deleteWaitQueueMsgs(uniqueId);

        memoryDataSource.deleteFromWaitingQueue(uniqueId);

    }


    //Uploading Queue

    public void insertUploadingQueue(UploadingQueueCache uploadingQueue) {

        cacheDataSource.insertUploadingQueue(uploadingQueue);

        memoryDataSource.insertUploadingQueue(uploadingQueue);
    }

    public void deleteUploadingQueue(String uniqueId) {

        cacheDataSource.deleteUploadingQueue(uniqueId);

        memoryDataSource.deleteFromUploadingQueue(uniqueId);
    }

    public UploadingQueueCache getUploadingQ(String uniqueId) {

        return cacheDataSource.getUploadingQ(uniqueId);


        // TODO: 9/21/2020 handle Queue
//        return Observable.concat(memoryDataSource.getUploadingQ(uniqueId),
//                cacheDataSource.getUploadingQ(uniqueId))
//                .first()
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io());
    }

    public List<Uploading> getAllUploadingQueueByThreadId(long subjectId) {
        return cacheDataSource.getAllUploadingQueueByThreadId(subjectId);
    }


    public void updateParticipantRoles(ArrayList<Admin> admins, long threadId) {

        cacheDataSource.updateParticipantRoles(admins, threadId);
    }

    public void saveImageInCache(String localUri, String uri, String hashCode, Float quality) {

        CacheFile cacheFile = new CacheFile(localUri, uri, hashCode, quality);

        cacheDataSource.cacheImage(cacheFile);

    }


    public Observable<CacheFile> checkInCache(String hashCode, Float quality) {

        return Observable
                .create(emitter -> {
                    try {
                        CacheFile findItem = null;

                        List<CacheFile> images = cacheDataSource.getImageByHash(hashCode);

                        if (images.size() > 0)
                            if (images.get(0) != null && images.get(0).getQuality() >= quality)
                                findItem = images.get(0);

                        emitter.onNext(findItem);
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                });

    }


    public boolean checkIsAvailable(String hashCode, Float quality) {

        List<CacheFile> images = cacheDataSource.getImageByHash(hashCode);

        for (CacheFile image :
                images) {

            if (quality == null) {
                if (image.getQuality() == 1)
                    return true;
            } else {
                if (image.getQuality().equals(quality))
                    return true;
            }

        }

        return false;
    }


}
