package com.fanap.podchat.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import com.fanap.podchat.R;
import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.Error;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.Util;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.securepreferences.SecurePreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.fanap.podchat.notification.PodChatPushNotificationService.TAG;

public class PodNotificationManager {

    private static boolean chatIsReady = false;

    private static boolean shouldShowNotification = true;

    private static long DEVICE_TOKEN_HAS_ALREADY_REGISTERED = 152;

    private static final String TARGET_ACTIVITY = "TARGET_ACTIVITY";
    private static final String ICON = "ICON";
    private static final String NOTIF_IMPORTANCE = "N_IMP";
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String PACKAGE_NAME = "PACKAGE_NAME";
    private static final String KEY_FCM_TOKEN = "FCM_TOKEN";
    private static IPodNotificationManager listener;
    private static BroadcastReceiver receiver;
    private static String fcmToken;
    private static SecurePreferences mSecurePrefs;

    private static int STATE = 0;

    private static final int NEED_REGISTER_USER_DEVICE = 1;

    private static final int NEED_REFRESH_TOKEN = 2;


    private static String messageUniqueId = "";

    private static Map<String, Runnable> messagesQ = new HashMap<>();

    //    private static final ArrayList<Map<String, String>> notificationsList = new ArrayList<>();
    private static final ArrayList<PodPushMessage> notificationsList = new ArrayList<>();


    public static void setShouldShowNotification(boolean shouldShowNotification) {
        PodNotificationManager.shouldShowNotification = shouldShowNotification;
    }

    private static synchronized String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    private static void createRegisterUserDeviceRequest(Context context, long userId) {

        if (userId <= 0) {
            if (listener != null)
                listener.onNotificationEvent("Try to register device but user id was 0");
            return;
        }

        String uniqueId = generateUniqueId();
        STATE = NEED_REGISTER_USER_DEVICE;
        Map<String, String> userDeviceTokenMap = new HashMap<>();
        userDeviceTokenMap.put(fcmToken, String.valueOf(userId));

        FcmAppUsersVO fcmAppUsersVO = new FcmAppUsersVO();

        fcmAppUsersVO.setAppId(context.getApplicationInfo().packageName);
        fcmAppUsersVO.setUserDeviceTokenMap(userDeviceTokenMap);


        AsyncMessage message = new AsyncMessage();
        message.setContent(App.getGson().toJson(fcmAppUsersVO));
        message.setType(ChatMessageType.Constants.REGISTER_FCM_USER_DEVICE);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(CoreConfig.typeCode);

        messageUniqueId = uniqueId;

        listener.sendAsyncMessage(App.getGson().toJson(message), "Register notification device and user");

    }

    private static void createUpdateUserDeviceRequest(String newToken) {

        String uniqueId = generateUniqueId();

        STATE = NEED_REFRESH_TOKEN;

        Map<String, String> tokensMap = new HashMap<>();
        tokensMap.put(fcmToken, newToken);

        AsyncMessage message = new AsyncMessage();
        message.setContent(App.getGson().toJson(tokensMap));
        message.setType(ChatMessageType.Constants.UPDATE_FCM_APP_USERS_DEVICE);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(CoreConfig.typeCode);

        messageUniqueId = uniqueId;

        listener.sendAsyncMessage(App.getGson().toJson(message), "UPDATE_FCM_APP_USERS_DEVICE");


    }

    private static String getPackageName(Context context) {
        return context.getApplicationInfo().packageName;
    }

    public static void registerFCMTokenReceiver(Context context) {

        try {

            fcmToken = getSavedFCMToken(context);

            listener.onNotificationEvent("Notification Manger started");


        } catch (Exception e) {
            listener.onNotificationEvent("failed to register notification receiver");
            listener.onNotificationError(e.getMessage());
        }

    }

    public static void unRegisterReceiver(Context context) {

        try {
            listener.onNotificationEvent("Try to unregister notification receiver");

            context.unregisterReceiver(receiver);
            receiver = null;
            listener.onNotificationEvent(" unregister notification receiver done");

        } catch (Exception e) {
            listener.onNotificationEvent("failed to unregistering notification receiver");
            listener.onNotificationError(e.getMessage());
        }

    }

    public static void listenLogs(IPodNotificationManager mListener) {

        listener = mListener;

    }

    private static IntentFilter getFCMTokenIntentFilter() {
        return new IntentFilter(PodChatPushNotificationService.ACTION_REFRESH);
    }

    public static void withConfig(CustomNotificationConfig mConfig, Context context) {

        notificationsList.clear();

        ShowNotificationHelper.setupNotificationChannel(
                context,
                mConfig.getChannelId(),
                mConfig.getChannelName(),
                mConfig.getChannelDescription(),
                mConfig.getNotificationImportance());

        try {
            saveConfig(mConfig, context);
        } catch (IllegalStateException e) {
            if (listener != null) listener.onNotificationError(e.getMessage());
        }

    }

    private static void saveConfig(CustomNotificationConfig mConfig, Context context) {

        SecurePreferences s = getSecurePrefs(context);

        SharedPreferences.Editor e = s.edit();

        if (mConfig.getTargetActivity() != null) {
            e.putString(TARGET_ACTIVITY, mConfig.getTargetActivity().getClass().getName());
        } else if (!Util.isNullOrEmpty(mConfig.getTargetActivityString())) {
            e.putString(TARGET_ACTIVITY, mConfig.getTargetActivityString());
        } else throw new IllegalStateException("Target Activity Could not be null");

        e.putInt(ICON, mConfig.getIcon());

        e.putInt(NOTIF_IMPORTANCE, mConfig.getNotificationImportance());

        e.putString(CHANNEL_ID, mConfig.getChannelId());

        e.putString(PACKAGE_NAME, context.getApplicationInfo().packageName);

        e.apply();


    }

    public static void showNotification(Map<String, String> data, Context context) {

//        isGroup
//       /threadName
//       /MessageSenderUserName
//       /MessageSenderName
//       /text
//       /messageId
//       /threadId
//       /profileImage
//

        if (!shouldShowNotification) return;

        SecurePreferences securePreferences = getSecurePrefs(context);


        PodPushMessage pushMessage = new PodPushMessage().createFromMapData(data);

        notificationsList.add(pushMessage);

        PodThreadPushMessages.addNewMessage(pushMessage);


        new Thread(() -> ShowNotificationHelper.showNewMessageNotification(
                context,
                securePreferences.getString(TARGET_ACTIVITY, ""),
                securePreferences.getInt(NOTIF_IMPORTANCE, NotificationManagerCompat.IMPORTANCE_DEFAULT),
                securePreferences.getInt(ICON, R.drawable.common_google_signin_btn_icon_dark),
                securePreferences.getString(CHANNEL_ID, "")
        )).start();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            new Thread(() -> ShowNotificationHelper.showSampleNotification2(context)).start();
//        } else {
//            new Thread(() -> ShowNotificationHelper.showSampleNotification(context)).start();
//        }

    }


    public static Map<String, ArrayList<PodPushMessage>> getNotificationsGroup() {
        return PodThreadPushMessages.getNotificationsGroup();
    }

    public static void deliverThreadMessages(long threadId) {

        PodThreadPushMessages.markThreadAsRead(threadId);

    }

    public static void deliverThreadMessages(String threadId) {

        try {
            if (Util.isNotNullOrEmpty(threadId))
                PodThreadPushMessages.markThreadAsRead(Long.parseLong(threadId));
        } catch (NumberFormatException e) {
            if (listener != null) listener.onNotificationError(e.getMessage());
        }

    }

    public static ArrayList<PodPushMessage> getNotificationsList() {
        return notificationsList;
    }

    static void handleMessage(Context context, RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();

        showNotification(data, context);

    }

    private static void saveFCMToken(String token, Context context) {


        mSecurePrefs = getSecurePrefs(context);

        mSecurePrefs.edit().putString(KEY_FCM_TOKEN, token).apply();

    }

    private static SecurePreferences getSecurePrefs(Context context) {

        if (mSecurePrefs == null) {
            mSecurePrefs = new SecurePreferences(context, "", "chat_prefs.xml");
        }
        return mSecurePrefs;
    }

    private static String getSavedFCMToken(Context context) {

        SecurePreferences mSecurePrefs = getSecurePrefs(context);

        return mSecurePrefs.getString(KEY_FCM_TOKEN, null);

    }

    private static void checkForNewFCMToken(Context context, long userId) {


//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setApplicationId(context.getString(R.string.firebase_app_id))
//                .setApiKey(context.getString(R.string.firebase_api_key))
//                .setProjectId(context.getString(R.string.firebase_project_id))
//                .build();
//
//        FirebaseApp.initializeApp(context /* Context */, options, "secondary");
//
//        FirebaseApp secondary = FirebaseApp.getInstance("secondary");
//
//        FirebaseInstanceId.getInstance(secondary).getInstanceId().addOnCompleteListener(task -> {
//
//            if (task.isSuccessful()) {
//
//                listener.onNotificationEvent("Token Retrieved");
//
//                String newToken = task.getResult() != null ? task.getResult().getToken() : null;
//
//                SecurePreferences mSecurePrefs = getSecurePrefs(context);
//
//                String lastToken = mSecurePrefs.getString(KEY_FCM_TOKEN, null);
//
//                // no token saved in device register device with new token
//                if (lastToken == null && newToken != null) {
//
//                    fcmToken = newToken;
//
//                    createRegisterUserDeviceRequest(context, userId);
//
//                }
//
//                // token refreshed, should update in server
//                if (lastToken != null && newToken != null && !lastToken.equals(newToken)) {
//
//                    fcmToken = lastToken;
//
//                    createUpdateUserDeviceRequest(newToken);
//
//                }
//
//            } else {
//
//                String cause = task.getException() != null ? task.getException().getMessage() != null ? task.getException().getMessage() : "Unknown" : "Unknown";
//
//                listener.onNotificationError("Failed to retrieve fcm token: " + cause);
//                Log.w(TAG, "getInstanceId failed", task.getException());
//
//            }
//
//        });


    }

    public static void onChatIsReady(Context context, long userId) {

        chatIsReady = true;

        // if notification is not enabled
        if (listener == null) return;


//        if (fcmToken == null) {
//
//            //no fcmToken Saved in device
//            //check for fcm token
//            checkForNewFCMToken(context, userId);
//
//        } else {
//
//            //we have saved token
//            //update token
//            createUpdateUserDeviceRequest(getSavedFCMToken(context));
//
//        }

    }

    public static void handleOnUserAndDeviceRegistered(ChatMessage chatMessage, Context context) {

        String content = chatMessage.getContent();

        String uniqueId = chatMessage.getUniqueId();

        if (listener != null) {

            listener.onNotificationEvent("User and device registered successfully");

            listener.onNotificationEvent(content);

            messagesQ.remove(uniqueId);

            messageUniqueId = "";

            STATE = 0;

            saveFCMToken(fcmToken, context);

        }

    }

    public static void handleOnFCMTokenRefreshed(ChatMessage chatMessage, Context context) {

        String content = chatMessage.getContent();

        String uniqueId = chatMessage.getUniqueId();

        if (listener != null) {

            listener.onNotificationEvent("FCM token refreshed successfully");

            listener.onNotificationEvent(content);

            messagesQ.remove(uniqueId);

            STATE = 0;

            saveFCMToken(fcmToken, context);

        }

    }

    public static boolean isNotificationError(ChatMessage chatMessage, Error error, Context context, long userId) {


        //notification not enabled
        if (listener == null || messageUniqueId.isEmpty()) return false;

        if (messageUniqueId.equals(chatMessage.getUniqueId())) {

            Log.e(TAG, chatMessage.getContent());

            switch (STATE) {

                case NEED_REGISTER_USER_DEVICE: {

                    if (error.getCode() == DEVICE_TOKEN_HAS_ALREADY_REGISTERED) {


                        //user registered but old token not exist
                        //we send the only token we have as old token and new token
                        createUpdateUserDeviceRequest(fcmToken);

                    } else if (error.getCode() == 100) {

                        listener.onNotificationError(chatMessage.getContent());

                    }

                    break;
                }
                case NEED_REFRESH_TOKEN: {

                    //refresh failed, maybe we need register user device
                    createRegisterUserDeviceRequest(context, userId);

                    break;
                }

                default: {

                    listener.onNotificationError(chatMessage.getContent());
                }
            }

            return true;
        }

        return false;

    }

    public static void clearNotifications(Context context) {

        PodThreadPushMessages.clearMessages();
        notificationsList.clear();
        ShowNotificationHelper.dismissOtherNotifications(context);

    }

    public static void clearNotification(String messageId) {


        ArrayList<PodPushMessage> shownNotificationsList = new ArrayList<>();

        for (PodPushMessage notifData : notificationsList) {
            String sMessageId = String.valueOf(notifData.getMessageId());
            if (!Util.isNullOrEmpty(sMessageId))
                if (sMessageId.equals(messageId)) {
                    shownNotificationsList.add(notifData);
                }
        }


        notificationsList.removeAll(shownNotificationsList);

    }

    public static void dismissAllNotifications(Context context) {
        ShowNotificationHelper.dismissOtherNotifications(context);
    }

    public static void registerClickReceiver(Context context) {

//        try {
//            listener.onNotificationEvent("registering click receiver...");
//
//            IntentFilter filter = new IntentFilter(ShowNotificationHelper.ACTION_1);
//
//            ShowNotificationHelper.NotificationClickReceiver receiver = new ShowNotificationHelper.NotificationClickReceiver();
//
//            context.registerReceiver(receiver, filter);
//
//            listener.onNotificationEvent("registering click done!");
//
//            ShowNotificationHelper.isClickReceiverRegistered(true);
//
//        } catch (Exception e) {
//            ShowNotificationHelper.isClickReceiverRegistered(false);
//
//            if (listener != null) {
//                listener.onNotificationError("click receiver registration failed: " + e.getMessage());
//            }
//        }


    }

    public interface IPodNotificationManager {

        void onNotificationEvent(String log);

        void onNotificationError(String log);

        void sendAsyncMessage(String message, String info);
    }

}
