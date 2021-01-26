package com.fanap.podchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chat.application.chatexample.BaseApplication;
import com.fanap.podchat.call.CallConfig;
import com.fanap.podchat.call.audio_call.ICallState;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatListener;
import com.fanap.podchat.example.R;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.requestobject.RequestConnect;

public class KafkaTestActivity extends AppCompatActivity implements ICallState, ChatListener {

    TextView status;

    private String TOKEN = BaseApplication.getInstance().getString(R.string.Ahmad_Sajadi);
    private static String serverName = BaseApplication.getInstance().getString(R.string.integration_serverName);
    private static String platformHost = BaseApplication.getInstance().getString(R.string.integration_platformHost);
    private static String fileServer = BaseApplication.getInstance().getString(R.string.integration_platformHost);

    private static String appId = BaseApplication.getInstance().getString(R.string.integration_appId);
    private static String ssoHost = BaseApplication.getInstance().getString(R.string.integration_ssoHost);
    private static String socketAddress = BaseApplication.getInstance().getString(R.string.integration_socketAddress);
    private static String podspaceServer = BaseApplication.getInstance().getString(R.string.podspace_file_server_main);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kafka_test);

        Chat chat = Chat.init(this);

        EditText br = findViewById(R.id.etBroker);
        EditText s = findViewById(R.id.etSend);
        EditText r = findViewById(R.id.etReceive);
        EditText sk = findViewById(R.id.etSendKey);

        status = findViewById(R.id.tvKafkaSt);

        Button d = findViewById(R.id.buttonConnectKafka);


        RequestConnect connect = new RequestConnect.Builder(
                socketAddress,
                appId,
                serverName,
                TOKEN,
                ssoHost,
                platformHost, fileServer,
                podspaceServer
        ).build();

        chat.addListener(this);

        CallConfig callConfig = new CallConfig(this.getClass().getName());
        chat.setAudioCallConfig(callConfig);

//        chat.connect(connect);

        d.setOnClickListener(v -> new Thread(() -> chat.testCall(
                br.getText().toString(),
                s.getText().toString(),
                r.getText().toString(),
               "",
                sk.getText().toString(),
                this
        )).start());


    }

    @Override
    public void onInfoEvent(String info) {
//        status.setText(info);
        updataStatus(info);
    }

    private void updataStatus(String info) {
        runOnUiThread(() -> {
            status.setText(info);
        });
    }

    @Override
    public void onErrorEvent(String cause) {
//        status.setText(cause);
        updataStatus(cause);
    }

    @Override
    public void onEndCallRequested() {

    }

    @Override
    public void onError(String content, ErrorOutPut error) {
        updataStatus(content);
    }

    @Override
    public void onChatState(String state) {

    }
}