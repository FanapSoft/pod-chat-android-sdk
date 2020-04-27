package com.fanap.podchat.notification;

import android.app.Activity;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.arissystem.touca.tmtp.TmtpPushReceiver;
import com.arissystem.touca.tmtp.TmtpServiceInitialization;
import com.arissystem.touca.tmtp.sdk.receivers.TmtpBootReceiver;

public class PodNotificationManager {
    private static final String TAG = "CHAT_SDK_NOTIFICATION";

    /**
     * @param applicationId registered application id in tmtp server
     * @param activity      activity
     * @param listener      notification callback
     */

    public static void enableNotification(String applicationId, AppCompatActivity activity, INotification listener) {


        createNotification(applicationId, activity, true, listener);

    }

    public static void enableNotification(String applicationId, Activity activity, INotification listener) {

        createNotification(applicationId, activity, true, listener);
    }

    public static void enableNotification(String applicationId, Activity activity, boolean custom, INotification listener) {

        createNotification(applicationId, activity, custom, listener);
    }

    public static void enableNotification(CustomNotificationConfig config, INotification listener) {

        ShowNotificationHelper.setupNotificationChannel(
                config.getActivity().getApplicationContext(),
                config.getChannelId(),
                config.getChannelName(),
                config.getChannelDescription(),
                config.getNotificationImportance());

        createNotification(config.getApplicationId(), config.getActivity(), true, listener);

    }

    public static boolean disableNotification(Activity activity) {

        return unregisterNotificationReceiver((AppCompatActivity) activity);

    }


    private static void createNotification(String applicationId, Activity activity, boolean custom, INotification listener) {

        try {

            registerNotificationReceiver((AppCompatActivity) activity,
                    custom,
                    listener);

            TmtpServiceInitialization tmtpServiceInitialization =
                    new TmtpServiceInitialization(
                            (AppCompatActivity) activity,
                            activity.getApplicationContext(),
                            applicationId,
                            newUserId -> {
                                listener.onUserIdUpdated(newUserId);
                                Log.i(TAG, "Notification Username changed: " + newUserId);
                            });


            ((AppCompatActivity) activity).getLifecycle().addObserver(tmtpServiceInitialization);
        } catch (Exception e) {
            Log.e(TAG, "Enabling Notification Failed");
            Log.e(TAG, e.getMessage());
        }

    }


    private static void registerNotificationReceiver(AppCompatActivity activity, boolean custom, INotification listener) {


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

            PodPushNotificationReceiver podNotificationReceiver = new PodPushNotificationReceiver();

            podNotificationReceiver.setListener(message -> {

                Log.i(TAG, "We got notification");

                listener.onPushMessageReceived((String) message);

//                ShowNotificationHelper.showNotification((String) message, activity.getApplicationContext(), activity.getClass());
            });
            try {
                activity.getApplicationContext().registerReceiver(podNotificationReceiver, pushReceiverFilter);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to enable notification");

            }

        } else {


            TmtpPushReceiver tmtpPushReceiver = new TmtpPushReceiver();
            try {
                activity.getApplicationContext().registerReceiver(tmtpPushReceiver, pushReceiverFilter);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to enable notification");

            }


        }


    }


    private static boolean unregisterNotificationReceiver(AppCompatActivity activity) {


        try {
            //add boot receiver
            TmtpBootReceiver tmtpBootReceiver = new TmtpBootReceiver();
            activity.getApplicationContext().unregisterReceiver(tmtpBootReceiver);
            PodPushNotificationReceiver podNotificationReceiver = new PodPushNotificationReceiver();
            activity.getApplicationContext().unregisterReceiver(podNotificationReceiver);
            TmtpPushReceiver tmtpPushReceiver = new TmtpPushReceiver();
            activity.getApplicationContext().unregisterReceiver(tmtpPushReceiver);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to disable notification");
            return false;
        }


    }


}
