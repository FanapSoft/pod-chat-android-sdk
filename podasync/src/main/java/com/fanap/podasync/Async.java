package com.fanap.podasync;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fanap.podasync.model.AsyncConstant;
import com.fanap.podasync.model.AsyncMessageType;
import com.fanap.podasync.model.ClientMessage;
import com.fanap.podasync.model.Message;
import com.fanap.podasync.model.MessageWrapperVo;
import com.fanap.podasync.model.PeerInfo;
import com.fanap.podasync.model.RegistrationRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import static com.neovisionaries.ws.client.WebSocketState.OPEN;

/*
 * By default WebSocketFactory uses for non-secure WebSocket connections (ws:)
 * and for secure WebSocket connections (wss:).
 */
public class Async {

    private WebSocket webSocket;
    private static final int SOCKET_CLOSE_TIMEOUT = 110000;
    private WebSocket webSocketReconnect;
    private static final String TAG = "Async" + " ";
    private static volatile Async instance;
    private boolean isServerRegister;
    private boolean rawLog;
    private boolean isDeviceRegister;
    private static SharedPreferences sharedPrefs;
    private MessageWrapperVo messageWrapperVo;
    private static AsyncListenerManager asyncListenerManager = new AsyncListenerManager();

    private Gson gson = new GsonBuilder().create();

    private String errorMessage;
    private long lastSentMessageTime;
    private long lastReceiveMessageTime;
    private String message;
    private String state;
    private String appId;
    private String peerId;
    private String deviceID;
    private ArrayList<String> asyncQueue = new ArrayList<>();
    private String serverAddress;
    private static final Handler pingHandler;
    private static final Handler reconnectHandler;
    private static final Handler socketCloseHandler;
    private String token;
    private String serverName;
    private String ssoHost;
    private int retryStep = 1;
    private boolean reconnectOnClose = false;
    private boolean log;
    private long connectionCheckTimeout = 10000;
    private long JSTimeLatency = 100;


    private Async() {

    }

    public static Async getInstance(Context context) {
        if (instance == null) {
            sharedPrefs = context.getSharedPreferences(AsyncConstant.Constants.PREFERENCE, Context.MODE_PRIVATE);
            instance = new Async();
        }
        return instance;
    }


    private void onEvent(WebSocket webSocket) {
        webSocket.addListener(new WebSocketListener() {
            /**
             * Get the current state of this WebSocket.
             * <p>
             * <p>
             * The initial state is {@link WebSocketState#CREATED CREATED}.
             * When {conncet(String, String, String, String, String, String)} is called, the state is changed to
             * { CONNECTING}, and then to
             * {OPEN} after a successful opening
             * handshake. The state is changed to {CLOSING} when a closing handshake
             * is started, and then to {CLOSED}
             * when the closing handshake finished.
             * </p>
             *
             * @return The current state.
             */
            @Override
            public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
                asyncListenerManager.callOnStateChanged(newState.toString());
                setState(newState.toString());
                if (log) Log.d(TAG, "State" + " Is Now " + newState.toString());
                switch (newState) {
                    case OPEN:
                        reconnectHandler.removeCallbacksAndMessages(null);
                        retryStep = 1;
                        break;
                    case CLOSED:
                        stopSocket();
                        if (reconnectOnClose) {
                            retryReconnect();
                        } else {
                            if (log) Log.e(TAG, "Socket Closed!");
                        }
                        break;
                    case CONNECTING:

                        break;
                    case CLOSING:

                        break;
                }
            }

            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {

            }

            @Override
            public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
                if (log) Log.e("onConnected", cause.toString());

            }


            /**
             * <p>
             * Before a WebSocket is closed, a closing handshake is performed. A closing handshake
             * is started (1) when the server sends a close frame to the client or (2) when the
             * client sends a close frame to the server. You can start a closing handshake by calling
             * {disconnect} method (or by sending a close frame manually).
             * </p>
             */
            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                if (log) Log.e("Disconnected", serverCloseFrame.getCloseReason());
                asyncListenerManager.callOnDisconnected(serverCloseFrame.getCloseReason());
            }

            @Override
            public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                if (log) Log.i(TAG, "onCloseFrame");
                if (log) Log.i(TAG, frame.getCloseReason());
            }

            @Override
            public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }


            /**
             * @param textMessage that received when socket send message to Async
             */
            @Override
            public void onTextMessage(WebSocket websocket, String textMessage) throws Exception {
                if (rawLog) Log.d(TAG, textMessage);
                int type = 0;
                lastReceiveMessageTime = new Date().getTime();

                ClientMessage clientMessage = gson.fromJson(textMessage, ClientMessage.class);
                if (clientMessage != null) {
                    type = clientMessage.getType();
                }
                scheduleCloseSocket();

                @AsyncMessageType.MessageType int currentMessageType = type;
                switch (currentMessageType) {
                    case AsyncMessageType.MessageType.ACK:
                        handleOnAck(clientMessage);
                        break;
                    case AsyncMessageType.MessageType.DEVICE_REGISTER:
                        handleOnDeviceRegister(websocket, clientMessage);
                        break;
                    case AsyncMessageType.MessageType.ERROR_MESSAGE:
                        handleOnErrorMessage(clientMessage);
                        break;
                    case AsyncMessageType.MessageType.MESSAGE_ACK_NEEDED:

                        handleOnMessageAckNeeded(websocket, clientMessage);
                        break;
                    case AsyncMessageType.MessageType.MESSAGE_SENDER_ACK_NEEDED:
                        handleOnMessageAckNeeded(websocket, clientMessage);
                        break;
                    case AsyncMessageType.MessageType.MESSAGE:
                        handleOnMessage(clientMessage);
                        break;
                    case AsyncMessageType.MessageType.PEER_REMOVED:
                        break;
                    case AsyncMessageType.MessageType.PING:
                        handleOnPing(websocket, clientMessage);
                        break;
                    case AsyncMessageType.MessageType.SERVER_REGISTER:
                        handleOnServerRegister(textMessage);
                        break;
                }
            }

            @Override
            public void onTextMessage(WebSocket websocket, byte[] data) throws Exception {

            }

            @Override
            public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {

            }

            @Override
            public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {

            }

            @Override
            public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {

            }

            @Override
            public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {

            }

            @Override
            public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                if (log) Log.e(TAG, "onError");
                if (log) Log.e(TAG, cause.getCause().getMessage());
                asyncListenerManager.callOnError(cause.toString());
            }

            @Override
            public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {

            }

            /**
             * After error event its start reconnecting again.
             * Note that you should not trigger reconnection in onError() method because onError()
             * may be called multiple times due to one error.
             * Instead, onDisconnected() is the right place to trigger reconnection.
             */

            @Override
            public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
                if (log) Log.e("onMessageError", cause.toString());
            }

            @Override
            public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {

            }

            @Override
            public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {

            }

            @Override
            public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {

            }

            @Override
            public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {

            }

            @Override
            public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {

            }

            @Override
            public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {

            }
        });
    }

    /*
     * Its showed
     * */
    public void rawLog(boolean rawLog) {
        this.rawLog = rawLog;
    }

    private void retryReconnect() {
        runOnUiThreadRecconect(new Runnable() {
            @Override
            public void run() {
                try {
                    reConnect();
                } catch (WebSocketException e) {
                    asyncListenerManager.callOnError(e.getMessage());
                }
                if (log)
                    Log.e(TAG, "Async: reConnect in " + " retryStep " + retryStep + " s ");
            }
        }, retryStep * 1000);
        if (retryStep < 60) retryStep *= 2;
    }

    public void isLoggable(boolean log) {
        this.log = log;
    }

    public void connect(String socketServerAddress, final String appId, String serverName,
                        String token, String ssoHost, String deviceID) {

        try {
            WebSocketFactory webSocketFactory = new WebSocketFactory();
            SSLSocketFactory.getDefault();
            setAppId(appId);
            setServerAddress(socketServerAddress);
            setToken(token);
            setServerName(serverName);
            setSsoHost(ssoHost);
            webSocket = webSocketFactory
                    .createSocket(socketServerAddress);
            onEvent(webSocket);

            webSocket.setMaxPayloadSize(100);
            webSocket.addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);
            webSocket.connectAsynchronously();

            if (deviceID != null && !deviceID.isEmpty()) {
//                saveDeviceId(deviceID);
                setDeviceID(deviceID);
            }

        } catch (IOException e) {
            if (log) Log.e("Async: connect", e.getMessage());
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());

        }
    }

    /**
     * @Param textContent
     * @Param messageType it could be 3, 4, 5
     * @Param []receiversId the Id's that we want to send
     */
    public void sendMessage(String textContent, int messageType, long[] receiversId) {
        try {
            Message message = new Message();
            message.setContent(textContent);
            message.setReceivers(receiversId);

            String jsonMessage = gson.toJson(message);
            String wrapperJsonString = getMessageWrapper(jsonMessage, messageType);
            sendData(webSocket, wrapperJsonString);
        } catch (Exception e) {
            asyncListenerManager.callOnError(e.getCause().getMessage());
            if (log) Log.e("Async: connect", e.getCause().getMessage());
        }
    }

    /**
     * First we checking the state of the socket then we send the message
     */
    public void sendMessage(String textContent, int messageType) {
        try {
            long ttl = new Date().getTime();
            Message message = new Message();
            message.setContent(textContent);
            message.setPriority(1);
            message.setPeerName(getServerName());
            message.setTtl(ttl);

            String json = gson.toJson(message);

            messageWrapperVo = new MessageWrapperVo();
            messageWrapperVo.setContent(json);
            messageWrapperVo.setType(messageType);

            String json1 = gson.toJson(messageWrapperVo);
            sendData(webSocket, json1);
            if (log) Log.i(TAG, "Send message");

        } catch (Exception e) {
            asyncListenerManager.callOnError(e.getCause().getMessage());
            if (log) Log.e("Async: connect", e.getCause().getMessage());
        }

    }

    public void closeSocket() {
        webSocket.sendClose();
    }

    public void logOut() {
        removePeerId(AsyncConstant.Constants.PEER_ID, null);
        isServerRegister = false;
        isDeviceRegister = false;
        webSocket.sendClose();
    }

    public void setReconnectOnClose(boolean reconnectOnClosed) {
        reconnectOnClose = reconnectOnClosed;
    }

    /**
     * Add a listener to receive events on this Async.
     *
     * @param listener A listener to add.
     * @return {@code this} object.
     */
    public Async addListener(AsyncListener listener) {
        asyncListenerManager.addListener(listener, log);
        return this;
    }

    public Async setListener(AsyncListener listener) {
        asyncListenerManager.clearListeners();
        asyncListenerManager.addListener(listener, log);
        return this;
    }

    public Async addListeners(List<AsyncListener> listeners) {
        asyncListenerManager.addListeners(listeners);
        return this;
    }

    public Async removeListener(AsyncListener listener) {
        asyncListenerManager.removeListener(listener);
        return this;
    }

    public List<AsyncListener> getSynchronizedListeners() {
        return asyncListenerManager.getSynchronizedListeners();
    }

    public Async clearListeners() {
        asyncListenerManager.clearListeners();
        return this;
    }

    /**
     * Connect webSocket to the Async
     *
     * @Param socketServerAddress
     * @Param appId
     */
    private void handleOnAck(ClientMessage clientMessage) throws IOException {
        setMessage(clientMessage.getContent());
        asyncListenerManager.callOnTextMessage(clientMessage.getContent());
    }

    /**
     * When socket closes by any reason
     * , server is still registered and we sent a lot of message but
     * they are still in the queue
     */
    private void handleOnDeviceRegister(WebSocket websocket, ClientMessage clientMessage) {
        try {
            isDeviceRegister = true;
            if (!peerIdExistence()) {
                String peerId = clientMessage.getContent();
                savePeerId(peerId);
            }

            if (isServerRegister && peerId.equals(getPeerId())) {
                if (websocket.getState() == OPEN) {
                    if (websocket.getFrameQueueSize() > 0) {

                    }
                }

            } else {
                serverRegister(websocket);
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }

    }

    private void serverRegister(WebSocket websocket) {
        if (websocket != null) {
            try {
                RegistrationRequest registrationRequest = new RegistrationRequest();
                registrationRequest.setName(getServerName());

                String jsonRegistrationRequestVo = gson.toJson(registrationRequest);
                String jsonMessageWrapperVo = getMessageWrapper(jsonRegistrationRequestVo, AsyncMessageType.MessageType.SERVER_REGISTER);
                sendData(websocket, jsonMessageWrapperVo);
            } catch (Exception e) {
                if (log) Log.e(TAG, e.getCause().getMessage());
            }
        } else {
            if (log) Log.e(TAG, "WebSocket Is Null");
        }
    }

    private void sendData(WebSocket websocket, String jsonMessageWrapperVo) {

        try {
            lastSentMessageTime = new Date().getTime();
            if (jsonMessageWrapperVo != null) {
                if (getState().equals("OPEN")) {
                    if (websocket != null) {
                        websocket.sendText(jsonMessageWrapperVo);
                    } else {
                        if (log) Log.e(TAG, "webSocket instance is Null");
                    }
                } else {
                    asyncListenerManager.callOnError("Socket is close");
                    asyncQueue.add(jsonMessageWrapperVo);
                }

            } else {
                if (log) Log.e(TAG, "message is Null");
            }
            ping();
        } catch (Exception e) {
            if (log) Log.e("Async: connect", e.getCause().getMessage());
        }

    }

    private void handleOnErrorMessage(ClientMessage clientMessage) {
        if (log) Log.e(TAG + "OnErrorMessage", clientMessage.getContent());
        setErrorMessage(clientMessage.getContent());
    }

    private void handleOnMessage(ClientMessage clientMessage) {
        if (clientMessage != null) {
            try {
                setMessage(clientMessage.getContent());
                asyncListenerManager.callOnTextMessage(clientMessage.getContent());
            } catch (Exception e) {
                if (log) Log.e(TAG, e.getCause().getMessage());
            }
        } else {
            if (log) Log.e(TAG, " clientMessage Is Null");
        }
    }

    private void handleOnPing(WebSocket webSocket, ClientMessage clientMessage) {
        try {
            if (clientMessage.getContent() != null) {
                if (getDeviceId() == null || getDeviceId().isEmpty()) {
//                    saveDeviceId(clientMessage.getContent());
                    setDeviceID(clientMessage.getContent());
                }
                deviceRegister(webSocket);
            } else {
                if (log) Log.i(TAG, "ASYNC_PING_RECEIVED");
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }

    /*
     * After registered on the server its sent messages from queue
     * */
    private void handleOnServerRegister(String textMessage) {
        try {
            if (log) Log.i(TAG, "SERVER_REGISTERED");
            if (log) Log.i("ASYNC_IS_READY", textMessage);

            asyncListenerManager.callOnStateChanged("ASYNC_READY");
            for (String message : asyncQueue) {
                sendData(webSocket, message);
            }
            isServerRegister = true;
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }


    public boolean isServerRegister() {
        return isServerRegister;
    }

    private void handleOnMessageAckNeeded(WebSocket websocket, ClientMessage clientMessage) {

        try {
            if (websocket != null) {
                handleOnMessage(clientMessage);

                Message messageSenderAckNeeded = new Message();
                messageSenderAckNeeded.setMessageId(clientMessage.getId());

                String jsonSenderAckNeeded = gson.toJson(messageSenderAckNeeded);
                String jsonSenderAckNeededWrapper = getMessageWrapper(jsonSenderAckNeeded, AsyncMessageType.MessageType.ACK);
                sendData(websocket, jsonSenderAckNeededWrapper);
            } else {
                if (log) Log.e(TAG, "WebSocket Is Null ");
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }

    private void deviceRegister(WebSocket websocket) {
        try {
            if (websocket != null) {
                PeerInfo peerInfo = new PeerInfo();
                if (getPeerId() != null) {
                    peerInfo.setRefresh(true);
                } else {
                    peerInfo.setRenew(true);
                }
                peerInfo.setAppId(getAppId());
                peerInfo.setDeviceId(getDeviceId());

//                JsonAdapter<PeerInfo> jsonPeerMessageAdapter = moshi.adapter(PeerInfo.class);
                String peerMessageJson = gson.toJson(peerInfo);
                String jsonPeerInfoWrapper = getMessageWrapper(peerMessageJson, AsyncMessageType.MessageType.DEVICE_REGISTER);
                sendData(websocket, jsonPeerInfoWrapper);
                if (log) Log.i(TAG, "SEND_SERVER_REGISTER");
                if (log) Log.d(TAG, jsonPeerInfoWrapper);

            } else {
                if (log) Log.e(TAG, "WebSocket Is Null ");
            }

        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }

    @NonNull
    private String getMessageWrapper(String json, int messageType) {
        messageWrapperVo = new MessageWrapperVo();
        messageWrapperVo.setContent(json);
        messageWrapperVo.setType(messageType);

        return gson.toJson(messageWrapperVo);
    }

    /**
     * If peerIdExistence we set {@param refresh = true} to the
     * Async else we set {@param renew = true}  to the Async to
     * get the new PeerId
     */
    private void reConnect() throws WebSocketException {
        connect(getServerAddress(), getAppId(), getServerName(), getToken(), getSsoHost(), null);
    }

    /**
     * Remove the peerId and send ping again but this time
     * peerId that was set in the server was removed
     */

    private void removePeerId(String peerId, String nul) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(peerId, nul);
        editor.apply();
    }

    private void ping() {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                long currentTime = new Date().getTime();
                if (currentTime - lastSentMessageTime >= connectionCheckTimeout - JSTimeLatency) {
                    if (!getState().equals("CLOSING") || !getState().equals("CLOSED")) {
                        message = getMessageWrapper("", AsyncMessageType.MessageType.PING);
                        try {
                            sendData(webSocket, message);
                        } catch (Exception e) {
                            if (log) Log.e(TAG, e.getMessage());
                        }
                        if (log) Log.i(TAG, "SEND_ASYNC_PING");
                    } else {
                        if (log) Log.e(TAG + "Socket Is", "Closed");
                    }
                }
            }
        }, connectionCheckTimeout);
    }

    static {
        reconnectHandler = new Handler(Looper.getMainLooper());
    }

    protected static void runOnUiThreadRecconect(Runnable runnable, long delayedTime) {
        if (reconnectHandler != null) {
            reconnectHandler.postDelayed(runnable, delayedTime);
        } else {
            runnable.run();
        }
    }


    static {
        pingHandler = new Handler(Looper.getMainLooper());
    }

    protected static void runOnUIThread(Runnable runnable, long delayedTime) {
        if (pingHandler != null) {
            pingHandler.postDelayed(runnable, delayedTime);
        } else {
            runnable.run();
        }
    }

    static {
        socketCloseHandler = new Handler(Looper.getMainLooper());
    }

    protected static void runOnUIThreadCloseSocket(Runnable runnable, long delayedTime) {
        if (socketCloseHandler != null) {
            socketCloseHandler.postDelayed(runnable, delayedTime);
        } else {
            runnable.run();
        }
    }

    private void ScheduleCloseSocket() {
        runOnUIThreadCloseSocket(new Runnable() {
            @Override
            public void run() {
                if (lastSentMessageTime - lastReceiveMessageTime >= SOCKET_CLOSE_TIMEOUT) {
                    closeSocket();
                }
            }
        }, SOCKET_CLOSE_TIMEOUT);
    }

    /**
     * After a delay Time it calls the method in the Run
     */
    private void scheduleCloseSocket() {
        long currentTime = new Date().getTime();
        socketCloseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentTime - lastReceiveMessageTime > 10000) {
                    closeSocket();
                }
            }
        }, 10000);
    }

    /**
     * When its send message the lastSentMessageTime gets updated.
     * if the {@param currentTime} - {@param lastSentMessageTime} was bigger than 10 second
     * it means we need to send ping to keep socket alive.
     * we don't need to set ping interval because its send ping automatically by itself
     * with the {@param type}type that not 0.
     * We set {@param type = 0} with empty content.
     */

    public void stopSocket() {
        try {
            if (webSocket != null) {
                isServerRegister = false;
                webSocket.disconnect();
                webSocket = null;
                pingHandler.removeCallbacksAndMessages(null);
                if (log) Log.i(TAG, "Socket Stopped");
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Checking if the peerId exist or not. if user logout Peer id is set to null
     */
    private boolean peerIdExistence() {
        boolean isPeerIdExistence;
        String peerId = sharedPrefs.getString(AsyncConstant.Constants.PEER_ID, null);
        setPeerId(peerId);
        isPeerIdExistence = peerId != null;
        return isPeerIdExistence;
    }

    /**
     * Save peerId in the SharedPreferences
     */
    private void savePeerId(String peerId) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(AsyncConstant.Constants.PEER_ID, peerId);
        editor.apply();
    }

    //Save deviceId in the SharedPreferences
    private static void saveDeviceId(String deviceId) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(AsyncConstant.Constants.DEVICE_ID, deviceId);
        editor.apply();
    }

    private void setServerName(String serverName) {
        this.serverName = serverName;
    }

    private String getServerName() {
        return serverName;
    }

    private String getDeviceId() {
//        return sharedPrefs.getString(AsyncConstant.Constants.DEVICE_ID, null);
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getPeerId() {
        return sharedPrefs.getString(AsyncConstant.Constants.PEER_ID, null);
    }

    private void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    private String getAppId() {
        return appId;
    }

    private void setAppId(String appId) {
        this.appId = appId;
    }

    public String getState() {
        return state;
    }

    private void setState(String state) {
        this.state = state;
    }

    private void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private String getServerAddress() {
        return serverAddress;
    }

    private void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    private void setToken(String token) {
        this.token = token;
    }

    private String getToken() {
        return token;
    }

    /**
     * Get the manager that manages registered listeners.
     */
    AsyncListenerManager getListenerManager() {
        return asyncListenerManager;
    }

    private void setSsoHost(String ssoHost) {
        this.ssoHost = ssoHost;
    }

    private String getSsoHost() {
        return ssoHost;
    }
}

