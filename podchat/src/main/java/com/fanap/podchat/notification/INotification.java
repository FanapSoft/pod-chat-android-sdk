package com.fanap.podchat.notification;

@FunctionalInterface
public interface INotification {

     void onPushMessageReceived(String message);

}
