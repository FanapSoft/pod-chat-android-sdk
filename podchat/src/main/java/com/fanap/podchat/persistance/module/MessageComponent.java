package com.fanap.podchat.persistance.module;

import android.content.Context;

import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.persistance.AppDatabase;
import com.fanap.podchat.persistance.MessageDatabaseHelper;
import com.fanap.podchat.persistance.dao.MessageDao;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, AppDatabaseModule.class})
public interface MessageComponent {

    void inject(Chat chat);

    MessageDao messageDao();

    AppDatabase appDatabase();

    MessageDatabaseHelper messageDatabaseHelper();

    Context context();
}
