package com.fanap.podchat.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Permission {

    //Request Permission
    public static void Request_STORAGE(@NonNull Activity act, int code) {

        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
    }

    public static void Request_CAMERA(@NonNull Activity act, int code) {
        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.CAMERA}, code);
    }

    public static void Request_FINE_LOCATION(@NonNull Activity act, int code) {
        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.ACCESS_FINE_LOCATION}, code);
    }

    public static void Request_READ_SMS(@NonNull Activity act, int code) {
        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.READ_SMS}, code);
    }

    public static void Request_READ_CONTACTS(@NonNull Activity act, int code) {
        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.READ_CONTACTS}, code);
    }
    public static void Request_ACCESS_NETWORK_STATE(@NonNull Activity act, int code) {
        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.ACCESS_NETWORK_STATE}, code);
    }

    public static void Request_READ_CALENDAR(@NonNull Activity act, int code) {
        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.READ_CALENDAR}, code);
    }

    public static void Request_RECORD_AUDIO(@NonNull Activity act, int code) {
        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.RECORD_AUDIO}, code);
    }

    //Check Permisson
    public static boolean Check_READ_STORAGE(@NonNull Activity act) {
        int result = ContextCompat.checkSelfPermission(act, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    public static boolean Check_INTERNET(@NonNull Activity act) {
        int result = ContextCompat.checkSelfPermission(act, Manifest.permission.INTERNET);
        return result == PackageManager.PERMISSION_GRANTED;

    } public static boolean Check_ACCESS_NETWORK_STATE(@NonNull Activity act) {
        int result = ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_NETWORK_STATE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean Check_CAMERA(@NonNull Activity act) {
        int result = ContextCompat.checkSelfPermission(act, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean Check_FINE_LOCATION(@NonNull Activity act) {
        int result = ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean Check_READ_SMS(@NonNull Activity act) {
        int result = ContextCompat.checkSelfPermission(act, Manifest.permission.READ_SMS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean Check_READ_CONTACTS(@NonNull Activity act) {
        int result = ContextCompat.checkSelfPermission(act, Manifest.permission.READ_CONTACTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean Check_READ_CALENDAR(@NonNull Activity act) {
        int result = ContextCompat.checkSelfPermission(act, Manifest.permission.READ_CALENDAR);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean Check_RECORD_AUDIO(@NonNull Activity act) {
        int result = ContextCompat.checkSelfPermission(act, Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }
}