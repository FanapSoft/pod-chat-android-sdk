package com.fanap.podchat.persistance.module;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;

import com.commonsware.cwac.saferoom.SQLCipherUtils;
import com.commonsware.cwac.saferoom.SafeHelperFactory;
import com.fanap.podchat.persistance.AppDatabase;
import com.fanap.podchat.persistance.MessageDatabaseHelper;
import com.fanap.podchat.persistance.PhoneContactDbHelper;
import com.fanap.podchat.persistance.dao.MessageDao;
import com.fanap.podchat.persistance.dao.MessageQueueDao;
import com.fanap.podchat.persistance.dao.PhoneContactDao;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.commonsware.cwac.saferoom.SQLCipherUtils.State.ENCRYPTED;
import static com.commonsware.cwac.saferoom.SQLCipherUtils.State.UNENCRYPTED;

@Module
public class AppDatabaseModule {

    private AppDatabase appDatabase;
    private static final String DATABASE_DB = "cache.db";

    public AppDatabaseModule(Context context,String secretKey) {

//        String stKey = "slkjgndsjkkdhksdfas";
        char[] passphrase = secretKey.toCharArray();
        SafeHelperFactory factory = new SafeHelperFactory(passphrase);

        File file = new File(String.valueOf(context.getDatabasePath("cache.db")));

        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DATABASE_DB)
                .openHelperFactory(factory)
                .allowMainThreadQueries()
                .build();
        SQLCipherUtils.State  state = SQLCipherUtils.getDatabaseState(file);
        if (state.equals(UNENCRYPTED)) {
            try {
                SQLCipherUtils.encrypt(context,file,passphrase);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (appDatabase.isOpen()) {
            if (state.equals(ENCRYPTED)) {
                try {
                    SQLCipherUtils.decrypt(context,file,passphrase);
//                    ;encrypt(context,file,passphrase);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public AppDatabaseModule(Context context) {

        String stKey = "slkjgndsjkkdhksdfas";
        char[] passphrase = stKey.toCharArray();
        SafeHelperFactory factory = new SafeHelperFactory(passphrase);

        File file = new File(String.valueOf(context.getDatabasePath("cache.db")));

        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DATABASE_DB)
                .openHelperFactory(factory)
                .allowMainThreadQueries()
                .build();
        SQLCipherUtils.State state = SQLCipherUtils.getDatabaseState(file);
        if (state.equals(UNENCRYPTED)) {
            try {
                SQLCipherUtils.encrypt(context,file,passphrase);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (appDatabase.isOpen()) {
            if (state.equals(ENCRYPTED)) {
                try {
                    SQLCipherUtils.decrypt(context,file,passphrase);
//                    ;encrypt(context,file,passphrase);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Singleton
    @Provides
    AppDatabase provideAppDatabase() {
        return appDatabase;
    }

    @Singleton
    @Provides
    MessageDao provideMessageDao(@NonNull AppDatabase appDatabase) {
        return appDatabase.getMessageDao();
    }

    @Singleton
    @Provides
    MessageQueueDao messageQueueDao(@NonNull AppDatabase appDatabase) {
        return appDatabase.getMessageQueueDao();
    }

    @Singleton
    @Provides
    PhoneContactDao phoneContactDao(@NonNull AppDatabase appDatabase) {
        return appDatabase.getPhoneContactDao();
    }

    @NonNull
    @Singleton
    @Provides
    MessageDatabaseHelper messageDatabaseHelper(MessageDao messageDao, MessageQueueDao messageQueueDao, Context context, AppDatabase appDatabase) {
        return new MessageDatabaseHelper(messageDao, messageQueueDao, context, appDatabase);
    }

    @NonNull
    @Singleton
    @Provides
    PhoneContactDbHelper phoneContactDbHelper(PhoneContactDao phoneContactDao) {
        return new PhoneContactDbHelper(phoneContactDao);
    }

}
