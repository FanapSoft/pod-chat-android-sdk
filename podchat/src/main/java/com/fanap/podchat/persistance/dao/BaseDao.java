package com.fanap.podchat.persistance.dao;

import androidx.room.Insert;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

public interface BaseDao<T> {


    @Insert(onConflict = REPLACE)
    void insert(T t);

    @Insert(onConflict = REPLACE)
    void insert(List<T> t);
}
