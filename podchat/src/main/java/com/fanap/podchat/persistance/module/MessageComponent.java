package com.fanap.podchat.persistance.module;

import android.content.Context;

import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.persistance.AppDatabase;
import com.fanap.podchat.persistance.MessageDatabaseHelper;
import com.fanap.podchat.persistance.PhoneContactDbHelper;
import com.fanap.podchat.persistance.dao.MessageDao;
import com.fanap.podchat.persistance.dao.MessageQueueDao;
import com.fanap.podchat.repository.CacheDataSource;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, AppDatabaseModule.class})
public interface MessageComponent {

    void inject(Chat chat);

    void injectDataSource(CacheDataSource cacheDataSource);

    MessageDao messageDao();

    MessageQueueDao MessageQueueDao();

    AppDatabase appDatabase();

    MessageDatabaseHelper messageDatabaseHelper();

    PhoneContactDbHelper PhoneContactDbHelper();

    Context context();
}
