package com.fanap.podasync;

import java.io.IOException;

public interface AsyncListener {

    void onReceivedMessage(String textMessage) throws IOException;

    void onStateChanged(String state) throws IOException;

    void onDisconnected(String textMessage) throws IOException;

    void onError(String textMessage) throws IOException;

    void handleCallbackError( Throwable cause) throws Exception;

}
