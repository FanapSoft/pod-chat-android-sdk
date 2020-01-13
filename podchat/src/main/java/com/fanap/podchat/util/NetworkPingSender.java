package com.fanap.podchat.util;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class NetworkPingSender {

    private int connectTimeout = 10000;

    private String hostName = "msg.pod.ir";

    private int port = 80;

    private HandlerThread handlerThread;

    private NetworkStateListener listener;

    private boolean connected = false;

    private int interval = 7000;

    private int disConnectionThreshold = 2;

    private int numberOfDisConnection = 0;

    private boolean isConnecting = false;


    private NetworkStateConfig config;


    public void setDisConnectionThreshold(int disConnectionThreshold) {

        this.disConnectionThreshold = disConnectionThreshold;
    }

    public void setHostName(String hostName) {

        if (!Util.isNullOrEmpty(hostName))
            this.hostName = hostName;
    }

    public void setPort(int port) {

        if (port != -1)
            this.port = port;
    }


    public void setInterval(int interval) {
        this.interval = interval;
    }


    public NetworkPingSender(NetworkStateListener listener, boolean connected) {
        this.listener = listener;
        this.connected = connected;
    }


    public NetworkStateConfig getConfig() {
        return config;
    }

    public void setConfig(NetworkStateConfig config) {


        if(config != null){

            this.disConnectionThreshold = config.disConnectionThreshold != null ? config.disConnectionThreshold : disConnectionThreshold;

            this.port = config.port != null ? config.port : port ;

            this.hostName = config.hostName != null ? config.hostName : hostName;

            this.interval = config.interval != null ? config.interval : interval;

            this.connectTimeout = config.connectTimeout != null ? config.connectTimeout : connectTimeout;

        }


    }

    public NetworkPingSender(NetworkStateListener listener) {
        this.listener = listener;
    }

    public synchronized void startPing() {


        if (handlerThread != null) {

            handlerThread.quit();

            handlerThread = null;
        }

        handlerThread = new HandlerThread("Network-Ping-Thread");

        handlerThread.start();


        Runnable job = new Runnable() {
            @Override
            public void run() {

                ping();
                new Handler(handlerThread.getLooper()).postDelayed(this, interval);

            }
        };

        new Handler(handlerThread.getLooper()).postDelayed(job, interval);


    }

    public void stopPing() {

        if (handlerThread != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    handlerThread.quitSafely();
                } else {
                    handlerThread.quit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void ping() {


        try {

            if(isConnecting)
                return;
            isConnecting = true;
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(hostName, port);

            socket.connect(socketAddress, connectTimeout);
            socket.close();
            isConnecting = false;


            notifyNetworkAvailable();
        } catch (IOException e) {
            e.printStackTrace();


            notifyConnectionIsLost();


        }

    }

    private void notifyConnectionIsLost() {


        connected = false;
        isConnecting = false;


        if(numberOfDisConnection < disConnectionThreshold){
            numberOfDisConnection++;
            return;
        }

        numberOfDisConnection = 0;
        listener.networkUnavailable();

    }

    private void notifyNetworkAvailable() {
        if (!connected)
            listener.networkAvailable();

        connected = true;
    }

    //todo test it
    public void asyncIsClosedOrClosing() {

        connected = false;

    }


    public static class NetworkStateConfig{


        private String hostName;

        private Integer port;

        private Integer interval;

        private Integer disConnectionThreshold;

        private Integer connectTimeout;


        public NetworkStateConfig() {

        }

        public NetworkStateConfig setHostName(String hostName) {
            this.hostName = hostName;
            return this;
        }

        public NetworkStateConfig setPort(Integer port) {
            this.port = port;
            return this;
        }


        public NetworkStateConfig setInterval(Integer interval) {
            this.interval = interval;
            return this;
        }

        public NetworkStateConfig setDisConnectionThreshold(Integer disConnectionThreshold) {
            this.disConnectionThreshold = disConnectionThreshold;
            return this;
        }

        public NetworkStateConfig setConnectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public NetworkStateConfig build(){
            return this;
        }
    }




}
