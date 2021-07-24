package com.example.chat.application.chatexample.utils;

import android.app.Activity;

import com.example.chat.application.chatexample.model.Method;
import com.fanap.podchat.chat.App;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<Method> loadJSONFromAsset(Activity activity) {
        String json = null;
        try {
            InputStream is = null;
            try {
                is = activity.getAssets().open("methods.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        List<Method> methods = App.getGson().fromJson(json, new TypeToken<ArrayList<Method>>() {
        }.getType());
        return methods;
    }
}
