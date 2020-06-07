package com.fanap.podchat.util.NetworkUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

import static com.fanap.podchat.util.NetworkUtils.NetworkPingSender.TAG;

public class NetworkStateReceiver extends BroadcastReceiver {

    private int timeOut = 10000;
    protected Set<NetworkStateListener> listeners;
    protected Boolean connected;
    private String hostName = "msg.pod.ir";
    private int port = 80;
    ConnectivityManager manager;

    public NetworkStateReceiver() {
        listeners = new HashSet<>();
        connected = null;
    }

    public NetworkStateReceiver(String hostName, int port) {
        listeners = new HashSet<>();
        connected = null;
        this.hostName = hostName;
        this.port = port;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }


    @SuppressWarnings("DEPRECATION")
    private boolean checkNetworkState(Context context) {

        manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (manager != null) {
                Network network = manager.getActiveNetwork();
                NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(network);

                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                }
            }

            return false;

        } else {

            if (manager != null) {
                NetworkInfo ni = manager.getActiveNetworkInfo();
                return ni != null && ni.isConnected();
            }
        }

        return false;
    }

    public void listenNetworkState(Context context) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            int transportType = getTransportType(getConnectionType(context));

            if(transportType!=-1){

                NetworkRequest networkRequest = new NetworkRequest.Builder()
                        .addTransportType(transportType)
                        .build();


                ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {

                    @Override
                    public void onLosing(@NonNull Network network, int maxMsToLive) {
                        super.onLosing(network, maxMsToLive);
                    }

                    @Override
                    public void onLost(@NonNull Network network) {
                        super.onLost(network);
                        connected = false;
                        notifyStateToAll();
                    }

                    @Override
                    public void onUnavailable() {
                        super.onUnavailable();
                        connected = false;
                        notifyStateToAll();
                    }

                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        connected = true;
                        notifyStateToAll();
                    }
                };


                try {
                    manager.registerNetworkCallback(networkRequest,networkCallback);
                } catch (Exception e) {
                    Log.wtf(TAG,e);
                }
            }
        }


    }

    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null)
            return;

        HandlerThread handlerThread = new HandlerThread("Pinger");

        handlerThread.start();

        int connectionType = getConnectionType(context);

        new Handler(handlerThread.getLooper()).post(() -> {

            if (connectionType != 0) {

                connected = false;

                try {
                    Socket socket = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(hostName, port);
                    socket.connect(socketAddress, timeOut);
                    socket.close();
                    connected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    connected = false;
                }

            } else {
                connected = false;
            }
            notifyStateToAll();
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getTransportType(int connectionType) {

        switch (connectionType){

            case 1:{
                return NetworkCapabilities.TRANSPORT_CELLULAR;
            }
            case 2: {
                return NetworkCapabilities.TRANSPORT_WIFI;
            }
            case 3:{
                return NetworkCapabilities.TRANSPORT_VPN;
            }

            default: return -1;
        }

    }

    private void notifyStateToAll() {
        for (NetworkStateListener listener : listeners)
            notifyState(listener);
    }

    private void notifyState(NetworkStateListener listener) {
        if (connected == null || listener == null)
            return;

        if (connected)
            listener.networkAvailable();
        else
            listener.networkUnavailable();
    }

    public void addListener(NetworkStateListener l) {
        listeners.add(l);
        notifyState(l);
    }

    public void removeListener(NetworkStateListener l) {
        listeners.remove(l);
    }


    @IntRange(from = 0, to = 3)
    public static int getConnectionType(Context context) {
        int result = 0; // Returns connection type. 0: none; 1: mobile data; 2: wifi
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = 2;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = 1;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        result = 3;
                    }
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        result = 2;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        result = 1;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_VPN) {
                        result = 3;
                    }
                }
            }
        }
        return result;
    }
}