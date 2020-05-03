package com.fanap.podchat.notification;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Objects;

import ir.fanap.ntf_sdk_test.NTFPush;
import ir.fanap.ntf_sdk_test.ResponseListener;
import ir.fanap.ntf_sdk_test.api.ResponseModel;


public class PodNotificationManager {

    private NTFPush ntfPush;
    private static final String TAG = "CHAT_SDK_NOTIFICATION";
    private CustomNotificationConfig config;

    public PodNotificationManager(@NonNull CustomNotificationConfig config) {
        this.config = config;
        ShowNotificationHelper.setupNotificationChannel(
                config.getActivity().getApplicationContext(),
                config.getChannelId(),
                config.getChannelName(),
                config.getChannelDescription(),
                config.getNotificationImportance());
    }


    public void enableNotification(INotification listener) {
        createNotification(config.getApplicationId(), config.getActivity(), config.getToken(), listener);
    }


    public boolean disableNotification() {
        if (ntfPush != null) {
            return ntfPush.unregisterNotificationReceiver();
        } else return false;

    }


    private void createNotification(String applicationId, Activity activity, String token, INotification listener) {

        try {

            ntfPush = new NTFPush.PushBuilder()
                    .setActivity((AppCompatActivity) activity)
                    .setAppId(applicationId)
                    .setContext(activity.getApplicationContext())
                    .setApiToken(token)
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(ResponseModel responseModel) {
                            try {
                                Log.i(TAG, "Push Response: " + responseModel.isContent());
                            } catch (Exception e) {
                                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e(TAG, Objects.requireNonNull(t.getMessage()));
                        }
                    })
                    .build();


            ntfPush.setINotificationCallback(listener::onPushMessageReceived);

        } catch (Exception e) {
            Log.e(TAG, "Enabling Notification Failed");
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }

    }


}
