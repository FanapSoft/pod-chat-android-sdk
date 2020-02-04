package com.fanap.chatcore.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Util {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
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


        if(uniqueIds == null) return true;
        else return uniqueIds.length == 0;

    }
}
