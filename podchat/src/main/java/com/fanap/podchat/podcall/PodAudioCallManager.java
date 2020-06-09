package com.fanap.podchat.podcall;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import com.example.kafkassl.kafkaclient.ConsumerClient;
import com.example.kafkassl.kafkaclient.ProducerClient;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.call.ResultStartCall;

import java.util.Arrays;
import java.util.Properties;

public class PodAudioCallManager {


    private static final String TAG = "CHAT_SDK_CALL";

    public void startCallStream(ResultStartCall result) {

        ProducerClient producerClient;
        ConsumerClient consumerClient;

        final Properties propertiesProducer = new Properties();
        propertiesProducer.setProperty("bootstrap.servers", "172.16.106.158:9097");

        producerClient = new ProducerClient(propertiesProducer);

        producerClient.connect();


        propertiesProducer.setProperty("group.id", "0");

        propertiesProducer.setProperty("auto.offset.reset", "end");


        consumerClient = new ConsumerClient(propertiesProducer, result.getTopicReceive());

        consumerClient.connect();

        PodAudioStreamManager a = new PodAudioStreamManager();

        a.recordAudio(new PodAudioStreamManager.IPodAudioListener() {
            @Override
            public void onByteRecorded(byte[] bytes) {

                producerClient.connect();

                producerClient.produceMessege(bytes, result.getClientId(), result.getTopicSend());

            }

            @Override
            public void onRecordStopped() {

                Log.e(TAG, "STOPPED");

            }

            @Override
            public void onErrorOccurred(String cause) {

                Log.e(TAG, "ERROR bc: " + cause);


            }
        });


        while (true) {

            byte[] bytes = consumerClient.consumingTopic();

            Log.i(TAG, "bytes consumed: " + Arrays.toString(bytes));

            if (bytes == null) break;

        }

    }


}
