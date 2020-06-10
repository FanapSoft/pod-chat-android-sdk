package com.fanap.podchat.podcall;

import android.os.Handler;
import android.util.Log;

import com.example.kafkassl.kafkaclient.ConsumerClient;
import com.example.kafkassl.kafkaclient.ProducerClient;
import com.fanap.podchat.chat.call.ResultStartCall;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class PodAudioCallManager {


    private static final String TAG = "CHAT_SDK_CALL";
    private static final String SENDING_TOPIC = "testconnection2";
    private static final String RECEIVING_TOPIC = "testconnection1";
    private ProducerClient producerClient;
    private ConsumerClient consumerClient;

    public void startCallStream(ResultStartCall result) {


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


    public void testStream() {

        final Properties propertiesProducer = new Properties();

        propertiesProducer.setProperty("bootstrap.servers", "172.16.106.158:9092");

        producerClient = new ProducerClient(propertiesProducer);
        producerClient.connect();

        propertiesProducer.setProperty("group.id", "0");
        propertiesProducer.setProperty("auto.offset.reset", "end");
        ConsumerClient consumerClient = new ConsumerClient(propertiesProducer, RECEIVING_TOPIC);
        consumerClient.connect();


        try {

          new Thread(()->{

              AtomicBoolean r = new AtomicBoolean(true);

              while (r.get()) {

                  new Handler().postDelayed(()-> r.set(false),5000);


                  byte[] endBuffer = "salam".getBytes();

                  String x = producerClient.produceMessege(endBuffer, "masoud", SENDING_TOPIC);

                  String result = null;

                  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                      result = new String(consumerClient.consumingTopic(), StandardCharsets.UTF_8);
                      if (!"salam".equals(result) && result.length() > 0)
                          Log.e(TAG, "Consume: " + result);
                  }

                  try {
                      Thread.sleep(1000);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }

              }

          }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

//        new PodAudioStreamManager()
//                .recordAudio(new PodAudioStreamManager.IPodAudioListener() {
//                    @Override
//                    public void onByteRecorded(byte[] bytes) {
//
//                        producerClient.produceMessege(bytes,"masoud","masoud200");
//
//                    }
//
//                    @Override
//                    public void onRecordStopped() {
//
//                        Log.e(TAG,"STOPPED");
//                    }
//
//                    @Override
//                    public void onErrorOccurred(String cause) {
//                        Log.e(TAG,"ERROR " + cause);
//
//                    }
//                });


    }
}
