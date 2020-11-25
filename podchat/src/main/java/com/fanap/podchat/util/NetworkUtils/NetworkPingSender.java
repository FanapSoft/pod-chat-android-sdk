package com.fanap.podchat.util.NetworkUtils;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatListener;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.util.ChatStateType;
import com.fanap.podchat.util.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import io.sentry.core.Sentry;

public class NetworkPingSender {

    private static final int VPN_CHECK_DELAY_MILLIS = 2000;
    private Context context;

    public static final String TAG = "CHAT_SDK_NET";

    private int connectTimeout = 10000;

    private String hostName = "msg.pod.ir";

    private int port = 443;

    private HandlerThread handlerThread;

    private HandlerThread handlerVPNThread;
    private Handler handler;

    private NetworkStateListener listener;

    private boolean connected = false;

    private int interval = 7000;

    private int disConnectionThreshold = 2;

    private int numberOfDisConnection = 0;

    private boolean isConnecting = false;

    private NetworkStateConfig config;

    private int numberOfPingsWithoutPong = 0;


    private static int VPN_STATE = 0;

    private static final int WITH_VPN = 2;

    private static final int WITHOUT_VPN = 1;

    private boolean hasPing = false;

    private boolean requestToClose = false;


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


        if (config != null) {

            this.disConnectionThreshold = config.disConnectionThreshold != null ? config.disConnectionThreshold : disConnectionThreshold;

            this.port = config.port != null ? config.port : port;

            this.hostName = config.hostName != null ? config.hostName : hostName;

            this.interval = config.interval != null ? config.interval : interval;

            this.connectTimeout = config.connectTimeout != null ? config.connectTimeout : connectTimeout;

        }


    }

    public NetworkPingSender(Context context, NetworkStateListener listener) {
        this.context = context;
        this.listener = listener;
        setVPNState();


        runVPNConnectionChecker();


    }

    private void runVPNConnectionChecker() {


        if (handlerVPNThread != null) {

            handlerVPNThread.quit();
            handlerVPNThread = null;

        }
        handlerVPNThread = new HandlerThread("VPN-Checker-Thread");

        handlerVPNThread.start();

        handler = new Handler(handlerVPNThread.getLooper());

        Runnable vpnConnectionChecker = new Runnable() {
            @Override
            public void run() {

                checkVPNState();

                handler.postDelayed(this, VPN_CHECK_DELAY_MILLIS);

            }
        };

        handler.postDelayed(vpnConnectionChecker, VPN_CHECK_DELAY_MILLIS);
    }

    public synchronized void startPing() {


        if (handlerThread != null) {

            handlerThread.quit();

            handlerThread = null;
        }

        handlerThread = new HandlerThread("Network-Ping-Thread");

        handlerThread.start();

        Handler pingHandler = new Handler(handlerThread.getLooper());


        Runnable job = new Runnable() {
            @Override
            public void run() {

                if(!requestToClose){
                    ping();
                }
                pingHandler.postDelayed(this, interval);

            }
        };
        pingHandler.postDelayed(job, interval);
    }

    public boolean isRequestToClose() {
        return requestToClose;
    }

    public void setRequestToClose(boolean requestToClose) {
        this.requestToClose = requestToClose;
    }

    public void stopPing() {

        if (handlerThread != null) {
            try {
                handlerThread.quit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void ping() {


        try {

            if (isConnecting)
                return;

            long startTime = System.currentTimeMillis();
            isConnecting = true;
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(hostName, port);


            socket.connect(socketAddress, connectTimeout);
            socket.close();
            isConnecting = false;

            long endTime = System.currentTimeMillis();
            notifyNetworkAvailable();
            logInfo("Ping delay: " + (endTime - startTime) + " milliseconds");
        } catch (IOException e) {
            logError("Timeout Exception host: " + hostName + " port: " + port);
            notifyConnectionIsLost();
        }

    }

    public void onPong(ChatMessage chatMessage) {

        numberOfPingsWithoutPong--;
        hasPing = numberOfPingsWithoutPong == 0;

    }

    private void checkVPNState() {

        boolean hasVPN = VPNChecker.hasVPN(context);

        // if vpn state has changed
        // we should check if connection
        // to sever is alive or not.

        if (hasVPNStateChanged(hasVPN)) {

            logInfo("VPN connection change detected!");

            numberOfPingsWithoutPong++;
            hasPing = false;
            listener.sendPingToServer();

            handler.postDelayed(() -> {

                //connection is not alive. we should reconnect.
                if (!hasPing) {

                    logInfo(this.toString());
                    logError("Connection lost!");
                    connected = false;
                    isConnecting = false;
                    numberOfDisConnection = 0;
                    numberOfPingsWithoutPong = 0;
                    listener.onConnectionLost();
                }
            }, connectTimeout);
            setVPNState();
        }
    }

    private void logError(String error) {
        Log.e(TAG, error);
        if (Sentry.isEnabled())
            Sentry.captureMessage(error);
    }

    private void logInfo(String info) {
        Log.i(TAG, info);
        if (Sentry.isEnabled())
            Sentry.addBreadcrumb(info);
    }

    private boolean hasVPNStateChanged(boolean hasVPN) {
        return hasVPN && VPN_STATE == WITHOUT_VPN
                || !hasVPN && VPN_STATE == WITH_VPN;
    }

    private void sendServerPing() {

        numberOfPingsWithoutPong++;

        if (numberOfPingsWithoutPong > 1) {

            connected = false;
            isConnecting = false;
            numberOfDisConnection = 0;
            numberOfPingsWithoutPong = 0;
            listener.networkUnavailable();


        } else listener.sendPingToServer();

    }


    //check if vpn connected or not.
    // so we can detect further changes on
    // network because of vpn connection.

    private void setVPNState() {

        boolean hasVPN = VPNChecker.hasVPN(context);

        if (hasVPN) {

            VPN_STATE = WITH_VPN;

        } else {

            VPN_STATE = WITHOUT_VPN;
        }

    }

    private void notifyConnectionIsLost() {


        connected = false;
        isConnecting = false;


        if (numberOfDisConnection < disConnectionThreshold) {
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

        if (numberOfDisConnection > 0) numberOfDisConnection = 0;

    }

    //todo test it
    public void asyncIsClosedOrClosing() {

        connected = false;

    }


    public void setStateListener(Chat chat) {

        chat.addListener(new ChatListener() {
            @Override
            public void onChatState(String state) {

                Log.d(TAG, "CHAT STATE CHANGED: " + state);

                switch (state) {

                    case ChatStateType.ChatSateConstant.CLOSING:
                    case ChatStateType.ChatSateConstant.CLOSED: {


                        connected = false;
                        isConnecting = false;

                        break;
                    }
                }

            }


        });


    }


    public static class NetworkStateConfig {


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

        public String getHostName() {
            return hostName;
        }

        public Integer getPort() {
            return port;
        }

        public Integer getConnectTimeout() {
            return connectTimeout;
        }

        public NetworkStateConfig build() {
            return this;
        }


        @Override
        public String toString() {
            return "NetworkStateConfig{" +
                    "hostName='" + hostName + '\'' +
                    ", port=" + port +
                    ", interval=" + interval +
                    ", disConnectionThreshold=" + disConnectionThreshold +
                    ", connectTimeout=" + connectTimeout +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "NetworkPingSender{" +
                "connectTimeout=" + connectTimeout +
                ", hostName='" + hostName + '\'' +
                ", port=" + port +
                ", connected=" + connected +
                ", interval=" + interval +
                ", disConnectionThreshold=" + disConnectionThreshold +
                ", numberOfDisConnection=" + numberOfDisConnection +
                ", isConnecting=" + isConnecting +
                ", config=" + config +
                ", numberOfPingsWithoutPong=" + numberOfPingsWithoutPong +
                ", hasPing=" + hasPing +
                '}';
    }
}
