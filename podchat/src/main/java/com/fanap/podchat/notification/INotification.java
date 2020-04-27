package com.fanap.podchat.notification;

@FunctionalInterface
public interface INotification {

    void onUserIdUpdated(String userId);

    default void onPushMessageReceived(String message) {
    }

}
