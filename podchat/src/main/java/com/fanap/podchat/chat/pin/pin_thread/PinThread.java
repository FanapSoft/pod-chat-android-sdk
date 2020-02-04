package com.fanap.podchat.chat.pin.pin_thread;

import android.util.Log;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.pin.pin_thread.model.RequestPinThread;
import com.fanap.podchat.chat.pin.pin_thread.model.ResultPinThread;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.AsyncAckType;
import com.fanap.podchat.util.ChatConstant;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;

public class PinThread {


    public static String pinThread(RequestPinThread request, String uniqueId) {


        long threadId = request.getThreadId();

        AsyncMessage message = new ChatMessage();
        message.setType(ChatMessageType.Constants.PIN_THREAD);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setSubjectId(threadId);
        message.setUniqueId(uniqueId);


        return App.getGson().toJson(message);
    }


    public static String unPinThread(RequestPinThread request, String uniqueId) {


        long threadId = request.getThreadId();

        AsyncMessage message = new ChatMessage();
        message.setType(ChatMessageType.Constants.UNPIN_THREAD);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setSubjectId(threadId);
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);

    }


    public static ChatResponse<ResultPinThread> handleOnThreadPinned(ChatMessage chatMessage) {

        ResultPinThread result = new ResultPinThread();

        result.setUniqueId(chatMessage.getUniqueId());

        result.setContent(chatMessage.getContent());

        result.setTime(chatMessage.getTime());

        ChatResponse<ResultPinThread> response = new ChatResponse<>();

        response.setUniqueId(chatMessage.getUniqueId());

        response.setResult(result);

        response.setSubjectId(chatMessage.getSubjectId());

        return response;

    }


    public static ChatResponse<ResultPinThread> handleOnThreadUnPinned(ChatMessage chatMessage) {

        ResultPinThread result = new ResultPinThread();

        result.setUniqueId(chatMessage.getUniqueId());

        result.setContent(chatMessage.getContent());

        result.setTime(chatMessage.getTime());

        ChatResponse<ResultPinThread> response = new ChatResponse<>();

        response.setUniqueId(chatMessage.getUniqueId());

        response.setResult(result);

        response.setSubjectId(chatMessage.getSubjectId());

        return response;

    }


}
