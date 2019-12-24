package com.fanap.podchat.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class NetworkStateReceiver extends BroadcastReceiver {

    public static final int TIME_OUT = 10000;
    protected Set<NetworkStateListener> listeners;
    protected Boolean connected;
    private String hostName = "8.8.8.8";
    private int port = 53;

    public NetworkStateReceiver() {
        listeners = new HashSet<NetworkStateListener>();
        connected = null;
    }

    public NetworkStateReceiver(String hostName, int port) {
        listeners = new HashSet<NetworkStateListener>();
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

    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null)
            return;




        HandlerThread handlerThread = new HandlerThread("Pinger");

        handlerThread.start();

        new Handler(handlerThread.getLooper()).post(() -> {


            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();


            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {

                connected = false;


                try {
                    Socket socket = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(hostName,port);
                    socket.connect(socketAddress, TIME_OUT);
                    socket.close();
                    connected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    connected = false;
                }


            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                connected = false;
            }

            notifyStateToAll();


        });


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
}