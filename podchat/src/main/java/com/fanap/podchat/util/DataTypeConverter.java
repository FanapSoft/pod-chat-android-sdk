package com.fanap.podchat.util;

import android.arch.persistence.room.TypeConverter;

import com.fanap.podchat.mainmodel.Participant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class DataTypeConverter {
    private static Gson gson = new Gson();
    @TypeConverter
    public List<Participant> stringToList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Participant>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public  String ListToString(List<Participant> t) {
        return gson.toJson(t);
    }
}
