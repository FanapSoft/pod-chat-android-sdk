package com.fanap.podchat.repository;

import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fanap.podchat.chat.contact.ContactManager;
import com.fanap.podchat.chat.thread.ThreadManager;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Thread;

import java.util.ArrayList;
import java.util.List;

import io.sentry.core.Sentry;
import rx.Observable;

public class MemoryDataSource {


    public static final String MEMORY = "MEMORY";
    private ArrayList<Thread> threadsList = new ArrayList<>();
    private long threadListContentCount = threadsList.size();


    private ArrayList<Contact> contactsList = new ArrayList<>();
    private long contactContentCount = contactsList.size();


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
    public Observable<ThreadManager.CacheThread> getThreadsData(Integer count,
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
                .map(data -> page(data, count, offset))
                .map(ThreadManager::sortThreads)
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




    private ThreadManager.CacheThread createThreadListResponse(List<Thread> data) {
        return new ThreadManager.CacheThread(data, threadListContentCount, MEMORY);
    }

    private List<Thread> updateThreadsContentCount(List<Thread> data) {
        threadListContentCount = data.size();
        return data;
    }

    public void cacheThreadsInMemory(List<Thread> data) {
        for (Thread thread : data) {
            upsertThread(thread);
        }
    }

    public void upsertThread(Thread thread) {

        if (threadsList.contains(thread))
            threadsList.set(threadsList.indexOf(thread), thread);
        else
            threadsList.add(thread);

    }

    /*
    Contact
     */

    public Observable<ContactManager.ContactResponse> getContactsData(
            Integer count,
            Long offset
    ){



        return Observable.from(new ArrayList<>(contactsList))
                .toList()
                .map(this::updateContactsContentCount)
                .map(data -> page(data, count, offset))
                .map(ContactManager::sortContacts)
                .map(this::createContactListResponse)
                .filter(response -> !response.getContactsList().isEmpty());



    }


    private ContactManager.ContactResponse createContactListResponse(List<Contact> contacts) {
        return new ContactManager.ContactResponse(contactsList,contactContentCount, MEMORY);
    }

    private List<Contact> updateContactsContentCount(List<Contact> contacts) {
        contactContentCount = contacts.size();
        return contacts;
    }



    public void cacheContactsInMemory(List<Contact> contactsList) {
        for (Contact contact :
                contactsList) {
            upsertContact(contact);
        }
    }

    private void upsertContact(Contact contact) {


        if (contactsList.contains(contact))
            contactsList.set(contactsList.indexOf(contact), contact);
        else
            contactsList.add(contact);
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
            Sentry.captureException(e);
            return new ArrayList<>();
        }

    }



}
