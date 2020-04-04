package com.fanap.podchat.notification;

import android.app.Activity;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.arissystem.touca.tmtp.TmtpPushReceiver;
import com.arissystem.touca.tmtp.TmtpServiceInitialization;
import com.arissystem.touca.tmtp.sdk.receivers.TmtpBootReceiver;

import static com.fanap.podchat.chat.Chat.TAG;

public class NotificationHelper {


    public static void enableNotification(String applicationId, AppCompatActivity activity, INotification listener) {


        registerNotificationReceiver(activity);


        TmtpServiceInitialization tmtpServiceInitialization =
                new TmtpServiceInitialization(
                        activity, activity.getApplicationContext(), applicationId,
                        s -> {
                            listener.onUserIdUpdated(s);
                            Log.i(TAG, "Notification Username changed: " + s);

                        });


        activity.getLifecycle().addObserver(tmtpServiceInitialization);


    }

    public static void enableNotification(String applicationId, Activity activity, INotification listener) {

        try {

            registerNotificationReceiver((AppCompatActivity) activity);

            TmtpServiceInitialization tmtpServiceInitialization =
                    new TmtpServiceInitialization(
                            (AppCompatActivity) activity, activity.getApplicationContext(), applicationId,
                            s -> {
                                listener.onUserIdUpdated(s);
                                Log.i(TAG, "Notification Username changed: " + s);
                            });


            ((AppCompatActivity) activity).getLifecycle().addObserver(tmtpServiceInitialization);
        } catch (Exception e) {
            Log.e(TAG, "Enabling Notification Failed");
            Log.e(TAG, e.getMessage());
        }

    }


    private static void registerNotificationReceiver(AppCompatActivity activity) {


        TmtpPushReceiver tmtpPushReceiver = new TmtpPushReceiver();
        TmtpBootReceiver tmtpBootReceiver = new TmtpBootReceiver();
        IntentFilter pushReceiverFilter = new IntentFilter();
        pushReceiverFilter.addAction("com.arissystem.touca.app");


        IntentFilter bootReceiverFilter = new IntentFilter();
        bootReceiverFilter.addAction("android.intent.action.BOOT_COMPLETED");
        bootReceiverFilter.addAction("android.intent.action.QUICKBOOT_POWERON");


        activity.getApplicationContext().registerReceiver(tmtpPushReceiver, pushReceiverFilter);
        activity.getApplicationContext().registerReceiver(tmtpBootReceiver, bootReceiverFilter);
    }

}
