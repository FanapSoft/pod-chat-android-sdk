package com.fanap.podchat.persistance;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

public class Converters {
    @Nullable
    @TypeConverter
    public static Date fromTimestamp(@Nullable Long value) {
        return value == null ? null : new Date(value);
    }

    @Nullable
    @TypeConverter
    public static Long dateToTimestamp(@Nullable Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    //   @TypeConverter
//    public static <T> ArrayList<T> fromString(String value) {
//        Type listType = new TypeToken<ArrayList<T>>() {}.getType();
//        return new Gson().fromJson(value, listType);
//    }


    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

//    @TypeConverter
//    public static <T> String fromArrayList(ArrayList<T> list) {
//        Gson gson = new Gson();
//        String json = gson.toJson(list);
//        return json;
//    }

}
