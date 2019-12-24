package com.fanap.podasync;

import java.io.IOException;

public class AsyncAdapter implements AsyncListener {

    @Override
    public void onReceivedMessage(String textMessage) throws IOException {

    }

    @Override
    public void onStateChanged(String state) throws IOException {

    }

    @Override
    public void onDisconnected(String textMessage)  {

    }

    @Override
    public void onError(String textMessage) {

    }

    @Override
    public void handleCallbackError(Throwable cause)  {

    }
}
