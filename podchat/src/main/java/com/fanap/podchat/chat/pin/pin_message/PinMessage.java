package com.fanap.podchat.chat.pin.pin_message;


import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.pin.pin_message.model.PinUnpinMessageRequest;
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;

public class PinMessage {


    public static String pinMessage(PinUnpinMessageRequest request, String uniqueId) {

        long messageId = request.getMessageId();

        boolean notifyAll = request.isNotifyAll();

        JsonObject content = new JsonObject();

        content.addProperty("notifyAll", notifyAll);

        AsyncMessage message = new AsyncMessage();
        message.setSubjectId(messageId);
        message.setContent(content.toString());
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.PIN_MESSAGE);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(!Util.isNullOrEmpty(request.getTypeCode()) ? request.getTypeCode() : CoreConfig.typeCode);

        return App.getGson().toJson(message);

    }


    public static String unPinMessage(PinUnpinMessageRequest request, String uniqueId) {


        long messageId = request.getMessageId();

        AsyncMessage message = new AsyncMessage();
        message.setSubjectId(messageId);
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.UNPIN_MESSAGE);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(!Util.isNullOrEmpty(request.getTypeCode()) ? request.getTypeCode() : CoreConfig.typeCode);


        return App.getGson().toJson(message);

    }


    public static ChatResponse<ResultPinMessage> handleOnMessagePinned(ChatMessage message) {

        ChatResponse<ResultPinMessage> response = new ChatResponse<>();
        ResultPinMessage result = App.getGson().fromJson(message.getContent(), ResultPinMessage.class);
        response.setResult(result);
        response.setSubjectId(message.getSubjectId());
        response.setUniqueId(message.getUniqueId());

        return response;


    }


    public static ChatResponse<ResultPinMessage> handleOnMessageUnPinned(ChatMessage message){

        ChatResponse<ResultPinMessage> response = new ChatResponse<>();
        ResultPinMessage result = App.getGson().fromJson(message.getContent(), ResultPinMessage.class);
        response.setResult(result);
        response.setSubjectId(message.getSubjectId());
        response.setUniqueId(message.getUniqueId());

        return response;

    }


}
