package com.fanap.podchat.repository;

import android.content.Context;
import android.support.annotation.Nullable;

import com.fanap.podchat.chat.contact.ContactManager;
import com.fanap.podchat.chat.thread.ThreadManager;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.persistance.MessageDatabaseHelper;
import com.fanap.podchat.persistance.RoomIntegrityException;
import com.fanap.podchat.persistance.module.AppDatabaseModule;
import com.fanap.podchat.persistance.module.AppModule;
import com.fanap.podchat.persistance.module.DaggerMessageComponent;
import com.fanap.podchat.util.OnWorkDone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.sentry.core.Sentry;
import rx.Observable;

public class CacheDataSource {


    public static final String DISK = "DISK";
    @Inject
    MessageDatabaseHelper databaseHelper;

    public CacheDataSource() {
    }

    public CacheDataSource(Context context, String key) {

        DaggerMessageComponent.builder()
                .appDatabaseModule(new AppDatabaseModule(context, key))
                .appModule(new AppModule(context))
                .build()
                .injectDataSource(this);

    }


    public Observable<ThreadManager.CacheThread> getThreadsData(Integer count, Long offset, @Nullable ArrayList<Integer> threadIds, @Nullable String threadName, boolean isNew) throws RoomIntegrityException {


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

                                ThreadManager.CacheThread threadResponse = new ThreadManager.CacheThread(z, (Long) o, DISK);

                                emitter.onNext(threadResponse);

                            }
                        });
                    } catch (RoomIntegrityException e) {
                        emitter.onError(e);
                    }
                });
    }

    public void cacheThreadsInDB(List<Thread> data) {
        databaseHelper.saveThreads(data);
    }


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

}
