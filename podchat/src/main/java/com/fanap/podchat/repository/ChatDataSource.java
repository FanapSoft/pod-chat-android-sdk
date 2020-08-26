package com.fanap.podchat.repository;

import android.support.annotation.Nullable;
import android.util.Log;

import com.fanap.podchat.chat.contact.ContactManager;
import com.fanap.podchat.chat.thread.ThreadManager;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.persistance.RoomIntegrityException;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChatDataSource {

    public MemoryDataSource memoryDataSource;

    public CacheDataSource cacheDataSource;


    public ChatDataSource(MemoryDataSource memoryDataSource, CacheDataSource cacheDataSource) {
        this.memoryDataSource = memoryDataSource;
        this.cacheDataSource = cacheDataSource;
    }

    public Observable<ThreadManager.CacheThread> getThreadsFromMemoryDataSource(Integer count, Long offset, @Nullable ArrayList<Integer> threadIds, @Nullable String threadName, boolean isNew) {
        return memoryDataSource.getThreadsData(count, offset, threadIds, threadName, isNew);
    }

    public Observable<ThreadManager.CacheThread> getThreadsFromCacheDataSource(Integer count, Long offset, @Nullable ArrayList<Integer> threadIds, @Nullable String threadName, boolean isNew) throws RoomIntegrityException {
        //get from disk cache and put in memory cache
        return cacheDataSource.getThreadsData(count, offset, threadIds, threadName, isNew).doOnNext(cacheThread -> memoryDataSource.cacheThreadsInMemory(cacheThread.getThreadList()));
    }


    /*



     */

    public Observable<ContactManager.ContactResponse> getContactsFromMemoryDataSource(Integer count, Long offset) {
        return memoryDataSource.getContactsData(count, offset);
    }

    public Observable<ContactManager.ContactResponse> getContactsFromCacheDataSource(Integer count, Long offset) throws RoomIntegrityException {
        //get from disk cache and put in memory cache
        return cacheDataSource.getContactsData(count, offset).doOnNext(cacheContact -> memoryDataSource.cacheContactsInMemory(cacheContact.getContactsList()));
    }





    public void saveThreadResultInServer(List<Thread> server) {

        memoryDataSource.cacheThreadsInMemory(server);

        cacheDataSource.cacheThreadsInDB(server);

    }

    public void saveThreadResultInCache(List<Thread> server) {

        memoryDataSource.cacheThreadsInMemory(server);

    }

    public void updateThread(Thread thread) {
        memoryDataSource.upsertThread(thread);
    }

    public Observable<ThreadManager.CacheThread> getThreadData(Integer count,
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


    public Observable<ContactManager.ContactResponse> getContactData(Integer count,
                                                               Long offset) throws RoomIntegrityException {


        if (offset == null) {
            offset = 0L;
        }

        if (count == null || count == 0)
            count = 50;


        return Observable.concat(
                getContactsFromMemoryDataSource(count, offset),
                getContactsFromCacheDataSource(count, offset))
                .first()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());

    }











}
