package com.fanap.podchat.persistance.module;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.fanap.podchat.persistance.AppDatabase;
import com.fanap.podchat.persistance.MessageDatabaseHelper;
import com.fanap.podchat.persistance.dao.MessageDao;
import com.fanap.podchat.persistance.dao.MessageQueueDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppDatabaseModule {

    private AppDatabase appDatabase;
    private static final String DATABASE_DB = "cache.db";

    public AppDatabaseModule(Context context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DATABASE_DB).allowMainThreadQueries().build();
    }

    @Singleton
    @Provides
    AppDatabase provideAppDatabase() {
        return appDatabase;
    }

    @Singleton
    @Provides
    MessageDao provideMessageDao(AppDatabase appDatabase) {
        return appDatabase.getMessageDao();
    }

    @Singleton
    @Provides
    MessageDatabaseHelper messageDatabaseHelper(MessageDao messageDao, MessageQueueDao messageQueueDao) {
        return new MessageDatabaseHelper(messageDao, messageQueueDao);
    }
}
