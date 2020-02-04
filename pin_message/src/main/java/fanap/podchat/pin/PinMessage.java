package fanap.podchat.pin;


import com.fanap.chatcore.model.Handlers.CoreConfig;
import com.fanap.chatcore.model.constants.ChatMessageType;
import com.fanap.chatcore.model.wrappers.AsyncMessage;
import com.fanap.chatcore.utils.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import fanap.podchat.pin.model.RequestPinMessage;


public class PinMessage {

    private static Gson gson;

    static {

        gson = new GsonBuilder().create();

    }


    public static String pinMessage(RequestPinMessage request, String uniqueId) {


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

        return gson.toJson(message);

    }


    public static String unPinMessage(RequestPinMessage request, String uniqueId) {


        long messageId = request.getMessageId();

        AsyncMessage message = new AsyncMessage();
        message.setSubjectId(messageId);
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.UNPIN_MESSAGE);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(!Util.isNullOrEmpty(request.getTypeCode()) ? request.getTypeCode() : CoreConfig.typeCode);


        return gson.toJson(message);

    }


}
