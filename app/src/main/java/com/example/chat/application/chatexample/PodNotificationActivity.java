package com.example.chat.application.chatexample;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fanap.podchat.example.R;
import com.fanap.podnotify.PodNotify;

public class PodNotificationActivity extends AppCompatActivity implements ChatContract.view {

    @NonNull
    ChatContract.view view = new ChatContract.view() {
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        NotificationAdapter notificationAdapter = new NotificationAdapter(view);

        final PodNotify podNotify = new PodNotify.builder()
                .setAppId("com.fanap.podnotification")
                .setServerName(getString(R.string.server_name))
                .setSocketServerAddress(getString(R.string.socket_server_address))
                .setSsoHost(getString(R.string.sso_host))
                .setToken(getString(R.string.server_token))
                .build(getApplicationContext());

        podNotify.start(getApplicationContext());

        podNotify.getState(getApplicationContext()).observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.d("state: ", s);
//                deviceId.setText("deviceId: " + podNotify.getDeviceId(MainActivity.this));
                Log.d("notification", podNotify.getDeviceId(PodNotificationActivity.this));

                if (podNotify.getPeerId(getApplicationContext()) != null) {
                    Log.d("peerId: ", podNotify.getPeerId(getApplicationContext()));
                    Log.d("appId: ", podNotify.getAppId());
                }
            }
        });
    }

    @Override
    public void onRecivedNotification(@NonNull com.fanap.podnotify.model.Notification notification) {
        Log.d("notification", notification.getText());
    }
}
