package com.fanap.podchat.persistance.dao;

import android.arch.persistence.room.Insert;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

public interface BaseDao<T> {


    @Insert(onConflict = REPLACE)
    void insert(T t);

    @Insert(onConflict = REPLACE)
    void insert(List<T> t);
}
