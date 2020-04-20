package com.fanap.podchat.notification;

import android.app.Activity;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.arissystem.touca.tmtp.TmtpPushReceiver;
import com.arissystem.touca.tmtp.TmtpServiceInitialization;
import com.arissystem.touca.tmtp.sdk.receivers.TmtpBootReceiver;

import static com.fanap.podchat.chat.Chat.TAG;

public class NotificationManager {


    public static void enableNotification(String applicationId, AppCompatActivity activity, INotification listener) {


        registerNotificationReceiver(activity,false);


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

            registerNotificationReceiver((AppCompatActivity) activity,false);
            TmtpServiceInitialization tmtpServiceInitialization =
                    new TmtpServiceInitialization(
                            (AppCompatActivity) activity, activity.getApplicationContext(), applicationId, s -> {
                        listener.onUserIdUpdated(s);
                        Log.i(TAG, "Notification Username changed: " + s);
                    });


            ((AppCompatActivity) activity).getLifecycle().addObserver(tmtpServiceInitialization);
        } catch (Exception e) {
            Log.e(TAG, "Enabling Notification Failed");
            Log.e(TAG, e.getMessage());
        }

    }


    private static void registerNotificationReceiver(AppCompatActivity activity, boolean custom) {


        //add boot receiver
        TmtpBootReceiver tmtpBootReceiver = new TmtpBootReceiver();
        IntentFilter bootReceiverFilter = new IntentFilter();
        bootReceiverFilter.addAction("android.intent.action.BOOT_COMPLETED");
        bootReceiverFilter.addAction("android.intent.action.QUICKBOOT_POWERON");
        activity.getApplicationContext().registerReceiver(tmtpBootReceiver, bootReceiverFilter);


        IntentFilter pushReceiverFilter = new IntentFilter();
        pushReceiverFilter.addAction("com.arissystem.touca.app");


        if (custom) {

            //add custom notification receiver

            ShowNotificationHelper.setupNotificationChannel(activity.getApplicationContext());
            PodPushNotificationReceiver podNotificationReceiver = new PodPushNotificationReceiver();
            podNotificationReceiver.setListener(message -> {

                Log.e(TAG, "We got notification");

                ShowNotificationHelper.showNotification((String) message, activity.getApplicationContext(), activity.getClass());
            });
            activity.getApplicationContext().registerReceiver(podNotificationReceiver, pushReceiverFilter);

        } else {


            TmtpPushReceiver tmtpPushReceiver = new TmtpPushReceiver();
            activity.getApplicationContext().registerReceiver(tmtpPushReceiver, pushReceiverFilter);


        }


    }

}
