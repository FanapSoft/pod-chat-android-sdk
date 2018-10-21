package com.fanap.podchat.persistance;

import android.content.Context;

public class BaseDatabaseHelper {

    AppDatabase appDatabase;

    BaseDatabaseHelper (Context context){
        appDatabase = AppDatabase.getInstance(context);
    }
}
