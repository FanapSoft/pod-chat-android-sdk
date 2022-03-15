package com.fanap.podchat.repository;

import android.os.Build;
import androidx.annotation.Nullable;

import com.fanap.podchat.cachemodel.queue.Failed;
import com.fanap.podchat.cachemodel.queue.Sending;
import com.fanap.podchat.cachemodel.queue.SendingQueueCache;
import com.fanap.podchat.cachemodel.queue.Uploading;
import com.fanap.podchat.cachemodel.queue.UploadingQueueCache;
import com.fanap.podchat.cachemodel.queue.WaitQueueCache;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.contact.ContactManager;
import com.fanap.podchat.chat.messge.MessageManager;
import com.fanap.podchat.chat.thread.ThreadManager;
import com.fanap.podchat.mainmodel.BlockedContact;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.sentry.core.Sentry;
import rx.Observable;

public class MemoryDataSource {


    public static final String MEMORY = "MEMORY";



    private ArrayList<Thread> threadsList = new ArrayList<>();


    private long threadListContentCount = threadsList.size();



    private ArrayList<Contact> contactsList = new ArrayList<>();
    private long contactContentCount = contactsList.size();


    private ArrayList<MessageVO> messagesList = new ArrayList<>();
    private long messagesContentCount = messagesList.size();
    private ArrayList<SendingQueueCache> sendingQueue = new ArrayList<>();
    private ArrayList<WaitQueueCache> waitQueue = new ArrayList<>();
    private ArrayList<UploadingQueueCache> uploadingQueue = new ArrayList<>();


    public MemoryDataSource() {
    }


     /*
    Thread
     */

    /**
     * @param count      count of list
     * @param offset     offset
     * @param threadIds  only threads with these ids will return
     * @param threadName return thread that contains these characters
     * @param isNew      thread with unread messages
     * @return list of threads that meets conditions and null if no thread found
     */
    public Observable<ThreadManager.ThreadResponse> getThreadsData(Integer count,
                                                                   Long offset,
                                                                   @Nullable ArrayList<Integer> threadIds,
                                                                   @Nullable String threadName,
                                                                   boolean isNew) {


        // TODO: 8/24/2020 handle if disk has data that memory doesn't


        return Observable.from(new ArrayList<>(threadsList))
                .toList()
                .flatMap(data -> ThreadManager.filterIsNew(isNew, data))
                .flatMap(data -> ThreadManager.getByIds(threadIds, data))
                .flatMap(data -> ThreadManager.getByName(threadName, data))
                .map(this::updateThreadsContentCount)
                .map(ThreadManager::sortThreads)
                .map(data -> page(data, count, offset))
                .map(this::createThreadListResponse)
                .filter(response -> !response.getThreadList().isEmpty());

//        return Observable.from(new ArrayList<>(threadsList))
//                .toList()
//                .flatMap(data -> ThreadManager.filterIsNew(isNew, data))
//                .map(data -> {
//                    Log.e("MTAG", "data1-> " + data);
//                    return data;
//                })
//                .flatMap(data -> ThreadManager.getByIds(threadIds, data))
//                .map(data -> {
//                    Log.e("MTAG", "data2-> " + data);
//                    return data;
//                })
//                .flatMap(data -> ThreadManager.getByName(threadName, data))
//                .map(data -> {
//                    Log.e("MTAG", "data3-> " + data);
//                    return data;
//                })
//                .map(this::updateThreadsContentCount)
//                .map(data -> {
//                    Log.e("MTAG", "data4-> " + data);
//                    return data;
//                })
//                .map(data -> page(data, count, offset))
//                .map(data -> {
//                    Log.e("MTAG", "data5-> " + data);
//                    return data;
//                })
//                .map(ThreadManager::sortThreads)
//                .map(data -> {
//                    Log.e("MTAG", "data6-> " + data);
//                    return data;
//                })
//                .map(this::createThreadListResponse)
//                .map(data -> {
//                    Log.e("MTAG", "data7-> " + data);
//                    return data;
//                })
//                .filter(response -> !response.getThreadList().isEmpty())
//                .map(data -> {
//                    Log.e("MTAG", "data8-> " + data);
//                    return data;
//                })
//                .filter(response -> response.getThreadList().size() == count)
//                .map(data -> {
//                    Log.e("MTAG", "data9-> " + data);
//                    return data;
//                });


    }

    private ThreadManager.ThreadResponse createThreadListResponse(List<Thread> data) {
        return new ThreadManager.ThreadResponse(data, threadListContentCount, MEMORY);
    }


    private List<Thread> updateThreadsContentCount(List<Thread> data) {
        threadListContentCount = data.size();
        return data;
    }


    public void cacheThreads(List<Thread> data) {
        for (Thread thread : data) {
            upsertThread(thread);
        }
    }

    public void upsertThread(Thread thread) {
        insert(threadsList, thread);
    }


    /*
    Contact
     */

    public Observable<ContactManager.ContactResponse> getContactsData(
            Integer count,
            Long offset,
            String username
    ) {


        return Observable.from(new ArrayList<>(contactsList))
                .toList()
                .flatMap(data -> ContactManager.getByUsername(username, data))
                .map(this::updateContactsContentCount)
                .map(ContactManager::sortContacts)
                .map(data -> page(data, count, offset))
                .map(this::createContactListResponse)
                .filter(response -> !response.getContactsList().isEmpty());


    }


    private ContactManager.ContactResponse createContactListResponse(List<Contact> contacts) {
        return new ContactManager.ContactResponse(contacts, contactContentCount, MEMORY);
    }

    private List<Contact> updateContactsContentCount(List<Contact> contacts) {
        contactContentCount = contacts.size();
        return contacts;
    }


    public void cacheContacts(List<Contact> contactsList) {
        for (Contact contact :
                contactsList) {
            upsertContact(contact);
        }
    }

    public void cacheContact(Contact contact) {
        upsertContact(contact);
    }

    void upsertContact(Contact contact) {
        insert(contactsList, contact);
    }

    public void saveBlockedContact(BlockedContact contact) {

        if (contact.getContactVO() != null)
            upsertContact(contact.getContactVO());

        //todo save blocked contacts list


    }

    public void saveBlockedContacts(List<BlockedContact> blockedContacts) {

        for (BlockedContact blocked :
                blockedContacts) {
            saveBlockedContact(blocked);
        }

    }


    public void deleteContactById(long userId) {

        for (Contact contact :
                contactsList) {
            if (contact.getUserId() == userId) {
                contactsList.remove(contact);
                break;
            }
        }

    }

    public void deleteBlockedContact(long blockId) {

        // TODO: 9/20/2020 delete from block list

        //todo update contact list and change blocked to false

    }

    /*
    Message
     */

    public Observable<MessageManager.HistoryResponse> getMessagesData(
            History request, long threadId
    ) {

        return Observable.from(new ArrayList<>(messagesList))
                .filter(MessageManager.filterByThread(threadId))
                .filter(messageVO -> request.getId() <= 0 || messageVO.getId() == request.getId())
                .filter(MessageManager.filterByFromTime(request))
                .filter(MessageManager.filterByToTime(request))
                .filter(MessageManager.filterByQuery(request))
                .filter(MessageManager.filterByMessageType(request))
                .toList()
                .map(this::updateMessagesContentCount)
                .map(MessageManager::sortMessages)
                .map(data -> page(data, request.getCount() > 0 ? (int) request.getCount() : 25, request.getOffset()))
                .map(data -> createHistoryResponse(data, threadId))
                .filter(response -> !response.getResponse().getResult().getHistory().isEmpty());

    }


    private MessageManager.HistoryResponse createHistoryResponse(List<MessageVO> messageVOS, long threadId) {

        ChatResponse<ResultHistory> response = new ChatResponse<>();
        ResultHistory result = new ResultHistory();
        result.setHistory(messageVOS);
        result.setContentCount(contactContentCount);
        // TODO: 9/14/2020 fix here
        result.setHasNext(messageVOS.size() < contactContentCount);
        result.setNextOffset(messageVOS.size());
        result.setFailed(getThreadFailedMessages(threadId));
        result.setSending(getThreadSendingMessages(threadId));
        result.setUploadingQueue(getThreadUploadingMessage(threadId));
        response.setResult(result);

        return new MessageManager.HistoryResponse(response, MEMORY);
    }


    private List<Failed> getThreadFailedMessages(long threadId) {

        List<WaitQueueCache> waitingList = getThreadWaitingMessages(threadId);

        return MessageManager.getFailedFromWaiting(waitingList);
    }

    private List<Sending> getThreadSendingMessages(long threadId) {

        List<SendingQueueCache> sendingList = new ArrayList<>();

        for (SendingQueueCache sendingMessage :
                sendingQueue) {
            if (sendingMessage.getThreadId() == threadId)
                sendingList.add(sendingMessage);
        }

        return MessageManager.getSendingFromSendingCache(sendingList);
    }

    public Observable<List<SendingQueueCache>> getAllSendingQueue() {

        return Observable.create(em -> {

            if (Util.isNotNullOrEmpty(sendingQueue))
                em.onNext(sendingQueue);
            else em.onCompleted();
        });
    }


    private List<WaitQueueCache> getThreadWaitingMessages(long threadId) {

        List<WaitQueueCache> waitingMessages = new ArrayList<>();

        for (WaitQueueCache waitingMessage :
                waitQueue) {
            if (waitingMessage.getThreadId() == threadId)
                waitingMessages.add(waitingMessage);
        }

        return waitingMessages;
    }

    private void insertToWaitingQueue(SendingQueueCache sendingMessage) {

        WaitQueueCache waitingMessage = MessageManager.getWaitingFromSendingMessage(sendingMessage);

        insert(waitQueue, waitingMessage);

    }

    private List<MessageVO> updateMessagesContentCount(List<MessageVO> messages) {
        messagesContentCount = messages.size();
        return messages;
    }

    public void cacheMessages(List<MessageVO> messagesList) {
        for (MessageVO message :
                messagesList) {
            upsertMessage(message);
        }
    }

    void upsertMessage(MessageVO message) {
        insert(messagesList, message);
    }

    public void deleteMessage(MessageVO msg, long threadId) {

        // TODO: 9/21/2020 Update thread last message
        for (MessageVO message :
                messagesList) {
            if (message.equals(msg)) {
                messagesList.remove(message);
                break;
            }
        }

    }

    public void insertToSendingQueue(SendingQueueCache sendingMessage) {
        insert(sendingQueue, sendingMessage);
    }

    public void moveFromSendingToWaitingQueue(String uniqueId) {

        SendingQueueCache sendingMessage = deleteFromSendingQueue(uniqueId);

        if (sendingMessage != null)
            insertToWaitingQueue(sendingMessage);

    }

    public Observable<SendingQueueCache> moveFromWaitingQueueToSendingQueue(String uniqueId) {

        return Observable.create(em -> {

            WaitQueueCache waiting = deleteFromWaitingQueue(uniqueId);

            if (null != waiting) {

                SendingQueueCache sending = MessageManager.getSendingFromWaitingMessage(waiting);

                insertToSendingQueue(sending);

                em.onNext(sending);

            } else em.onCompleted();


        });


    }

    // Upload Queue

    public void insertUploadingQueue(UploadingQueueCache uploadingMessage) {
        insert(uploadingQueue, uploadingMessage);
    }

    public void deleteFromUploadingQueue(String uniqueId) {

        for (UploadingQueueCache uploading :
                uploadingQueue) {
            if (uploading.getUniqueId().equals(uniqueId)) {
                uploadingQueue.remove(uploading);
                break;
            }
        }

    }

    public void cancelMessage(String uniqueId) {

        deleteFromSendingQueue(uniqueId);

        deleteFromWaitingQueue(uniqueId);


    }

    public WaitQueueCache deleteFromWaitingQueue(String uniqueId) {
        for (WaitQueueCache waitingMessage :
                waitQueue) {

            if (waitingMessage.getUniqueId().equals(uniqueId)) {
                waitQueue.remove(waitingMessage);
                return waitingMessage;
            }
        }
        return null;
    }

    private SendingQueueCache deleteFromSendingQueue(String uniqueId) {

        for (SendingQueueCache sendingMessage :
                sendingQueue) {

            if (sendingMessage.getUniqueId().equals(uniqueId)) {
                sendingQueue.remove(sendingMessage);
                return sendingMessage;
            }
        }

        return null;
    }

    private List<Uploading> getThreadUploadingMessage(long threadId) {

        List<UploadingQueueCache> uploadingMessageList = new ArrayList<>();

        for (UploadingQueueCache uploadingMessage :
                uploadingQueue) {
            if (uploadingMessage.getThreadId() == threadId)
                uploadingMessageList.add(uploadingMessage);
        }

        return MessageManager.getUploadingFromUploadCache(uploadingMessageList);

    }


    public Observable<UploadingQueueCache> getUploadingQ(String uniqueId) {

        return Observable.create(em -> {

            for (UploadingQueueCache uploading : uploadingQueue) {
                if (uploading.getUniqueId().equals(uniqueId))
                    em.onNext(uploading);
            }

            em.onCompleted();


        });


    }

    public <T> List<T> page(List<T> items, int count, long offset) {

        if (items.size() == 0 || count == 0) {
            return new ArrayList<>();
        }

        if (count + offset > items.size()) {

            if (offset > items.size())
                return new ArrayList<>();

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return items.subList(Math.toIntExact(offset), items.size());
                } else {
                    return items.subList((int) offset, items.size());
                }
            } catch (Exception e) {
                if (Sentry.isEnabled())
                    Sentry.captureException(e);
                return new ArrayList<>();
            }

        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return items.subList(Math.toIntExact(offset), (Math.toIntExact(offset) + count));
            } else {
                return items.subList((int) offset, (int) (offset + count));
            }
        } catch (Exception e) {
            if (Sentry.isEnabled())
                Sentry.captureException(e);
            return new ArrayList<>();
        }

    }

    public <T> void insert(List<T> items, T item) {

        if (items.contains(item))
            items.set(items.indexOf(item), item);
        else
            items.add(item);
    }


}
