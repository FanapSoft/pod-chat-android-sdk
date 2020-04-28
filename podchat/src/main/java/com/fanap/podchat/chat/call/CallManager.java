package com.fanap.podchat.chat.call;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.ChatMessageType;
import com.google.gson.JsonObject;

public class CallManager {


    public static String createCallRequestMessage(CallRequest request, String uniqueId) {


        CallVO callVO = new CallVO();
        callVO.setCreatorId(CoreConfig.userId);
        callVO.setInvitees(request.getInvitees());
        callVO.setType(request.getCallType());


        AsyncMessage message = new AsyncMessage();
        message.setContent(App.getGson().toJson(callVO));
        message.setType(ChatMessageType.Constants.CALL_REQUEST);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);


        return App.getGson().toJson(message);


    }


    public static ChatResponse<ResultCallRequest> handleOnCallRequest(ChatMessage chatMessage) {

        CallVO callVO = App.getGson().fromJson(chatMessage.getContent(), CallVO.class);

        ResultCallRequest resultCallRequest = (ResultCallRequest) callVO;

        ChatResponse<ResultCallRequest> response = new ChatResponse<>();
        response.setResult(resultCallRequest);
        response.setUniqueId(chatMessage.getUniqueId());
        response.setSubjectId(chatMessage.getSubjectId());

        return response;


    }


}
