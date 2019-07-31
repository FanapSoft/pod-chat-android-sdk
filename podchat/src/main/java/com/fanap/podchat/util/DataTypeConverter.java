package com.fanap.podchat.util;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanap.podchat.cachemodel.CacheParticipant;
import com.fanap.podchat.mainmodel.Participant;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataTypeConverter {
    @NonNull
    private static Gson gson = new Gson();

    @TypeConverter
    public List<Participant> stringToList(@Nullable String data) {
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


//    @TypeConverter
//    public List<CacheParticipant> stringParticipantsToList(@Nullable String data) {
//        if (data == null) {
//            return Collections.emptyList();
//        }
//
//        Type listType = new TypeToken<List<CacheParticipant>>() {}.getType();
//
//        return gson.fromJson(data, listType);
//    }
//
//    @TypeConverter
//    public String listParticipantsToString(List<CacheParticipant> t) {
//        return gson.toJson(t);
//    }




    @TypeConverter
    public List<String> dataToList(String data){



        Type t = new TypeToken<List<String>>(){}.getType();

        List<String> list = gson.fromJson(data,t);

        JsonElement element = gson.toJsonTree(data,new TypeToken<List<String>>(){}.getType());


        for (JsonElement el :
                element.getAsJsonArray()) {

            list.add(el.getAsString());


        }


        return list;


    }



    @TypeConverter
    public String convertListToString(List<String> data){



        JsonElement element = gson.toJsonTree(data,new TypeToken<List<String>>(){}.getType());



        return element.toString();


    }







}
