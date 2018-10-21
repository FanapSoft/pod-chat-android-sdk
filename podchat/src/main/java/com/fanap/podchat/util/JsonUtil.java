package com.fanap.podchat.util;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JsonUtil {
   static final ObjectMapper mapper = new ObjectMapper();

   public static String getJson(Object obj) {
       try {
           ByteArrayOutputStream bout = new ByteArrayOutputStream();
           mapper.writeValue(bout, obj);
           byte[] objectBytes = bout.toByteArray();
           return new String(objectBytes);
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
   }

   public static <T> T fromJSON(byte[] json, Class<T> classOfT) {
       try {
           return mapper.readValue(new String(json, "utf-8"), classOfT);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }

   public static <T> T fromJSON(String json, Class<T> classOfT) {
       try {
           return mapper.readValue(json, classOfT);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }

   public static <T> T fromJSON(String json, TypeReference<T> typeReference) {
       try {
           return mapper.readValue(json, typeReference);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }

   public static JSONObject getJsonObject(String json) {
       try {
           return new JSONObject(json);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }

   public static JSONArray getJsonArray(String json) {
       try {
           return new JSONArray(json);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }
}
