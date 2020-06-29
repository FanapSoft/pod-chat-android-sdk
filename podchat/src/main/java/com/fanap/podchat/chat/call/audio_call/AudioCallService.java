package com.fanap.podchat.chat.call.audio_call;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.fanap.podchat.R;
import com.fanap.podchat.notification.PodNotificationManager;

public class AudioCallService extends Service {


    private String CHANNEL_ID = "PODCHAT_CALL";


    public AudioCallService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



//            Class<?> c = null;
//            try {
//                c = Class.forName("com.example.chat.application.chatexample.CallActivity");
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
            Intent notificationIntent = new Intent(this, this.getClass());

            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Example Service")
                    .setContentText("Runnuing")
                    .setSmallIcon(R.drawable.ic_message)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }
}
