package com.fanap.podchat.util.NetworkUtils;

public interface NetworkStateListener {

    void networkAvailable();

    void networkUnavailable();

    default void sendPingToServer(){}

    default void onConnectionLost(){}

}