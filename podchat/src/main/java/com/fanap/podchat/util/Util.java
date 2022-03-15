package com.fanap.podchat.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.text.TextUtils;

import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.LinkedUser;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.Contacts;
import com.fanap.podchat.model.ResultAddContact;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @NonNull
    public static ChatResponse<ResultAddContact> getReformatOutPutAddContact(Contacts contacts, String uniqueId) {
        ChatResponse<ResultAddContact> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(uniqueId);

        ResultAddContact resultAddContact = new ResultAddContact();
        resultAddContact.setContentCount(1);
        Contact contact = new Contact();

        contact.setCellphoneNumber(contacts.getResult().get(0).getCellphoneNumber());
        contact.setEmail(contacts.getResult().get(0).getEmail());
        contact.setFirstName(contacts.getResult().get(0).getFirstName());
        contact.setId(contacts.getResult().get(0).getId());
        contact.setLastName(contacts.getResult().get(0).getLastName());
        contact.setUniqueId(contacts.getResult().get(0).getUniqueId());


        //add linked user
        LinkedUser linkedUser = contacts.getResult().get(0).getLinkedUser();

        if (linkedUser != null)
            contact.setLinkedUser(linkedUser);


        resultAddContact.setContact(contact);
        chatResponse.setResult(resultAddContact);
        return chatResponse;
    }

    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isNotNullOrEmpty(@Nullable String string) {
        return string != null && !string.isEmpty();
    }

    public static <T extends Number> boolean isNullOrEmpty(@Nullable ArrayList<T> list) {
        return list == null || list.size() == 0;
    }

    public static <T extends Number> boolean isNullOrEmpty(@Nullable T number) {
        String num = String.valueOf(number);
        return number == null || num.equals("0");
    }

    public static <T extends Object> boolean isNullOrEmpty(@Nullable List<T> list) {
        return list == null || list.size() == 0;
    }

    public static <T extends Object> boolean isNotNullOrEmpty(@Nullable List<T> list) {
        return list != null && list.size() > 0;
    }

    public static boolean isNullOrEmptyMessageVO(@Nullable List<MessageVO> list) {
        return list == null || list.size() == 0;
    }

    public static <T extends Number> boolean isNullOrEmptyNumber(@Nullable List<T> list) {
        return list == null || list.size() == 0;
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static <T> String listToJson(ArrayList<T> list, Gson gson) {
        Type listType = new TypeToken<List<T>>() {
        }.getType();

        return gson.toJson(list, listType);
    }

    public static <T> JsonArray listToJsonArray(ArrayList<T> list, Gson gson) {

        JsonElement element = gson.toJsonTree(list, new TypeToken<List<T>>() {
        }.getType());

        return element.getAsJsonArray();
    }

    public static <T extends List> List<T> JsonToList(String json, Gson gson) {

        return gson.fromJson(json, new TypeToken<List<T>>() {
        }.getType());
    }


    public static String getFileName(String uriString, Uri uri, Context context) {

        String displayName = "";

        File myFile = new File(uriString);

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (uriString.startsWith("file://")) {
            displayName = myFile.getName();
        }

        return displayName;

    }

    public static JsonObject objectToJson(String jsonString, JsonParser parser) {

        return parser.parse(jsonString).getAsJsonObject();


    }


    public static <T, V> T findKeyWithUniqueValue(HashMap<T, ArrayList<V>> map, Object query) {

        for (T key : map.keySet()) {

            for (V t :
                    map.get(key)) {

                if (t.equals(query))
                    return key;

            }


        }

        return null;

    }

    public static boolean isNullOrEmpty(String[] uniqueIds) {


        if (uniqueIds == null) return true;
        else return uniqueIds.length == 0;

    }

    public static boolean parserBoolean(Boolean aBoolean) {
        return aBoolean == null ? false : aBoolean;
    }

    public static String logDivider() {
        return ".::::::::::::::::::::: LOG :::::::::::::::::::::::.";
    }


    public static void logWithDivider(String tag, String info) {
        Log.i(tag, logDivider());
        Log.i(tag, info);
        Log.i(tag, logDivider());
    }

    public static void logExceptionWithDivider(String tag, String info) {
        Log.e(tag, logDivider());
        Log.e(tag, info);
        Log.e(tag, logDivider());
    }
}
