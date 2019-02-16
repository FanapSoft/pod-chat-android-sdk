package com.example.chat.application.chatexample;

import com.fanap.podnotify.model.Notification;
import com.fanap.podnotify.service.PodMessagingService;

public class NotificationAdapter  extends PodMessagingService {

    private ChatContract.view view;


    public  NotificationAdapter(ChatContract.view view){
        this.view = view;
    }
    public NotificationAdapter(){}

    @Override
    public void onMessageReceived(Notification notification) {
        super.onMessageReceived(notification);
        view.onRecivedNotification(notification);
    }
}
