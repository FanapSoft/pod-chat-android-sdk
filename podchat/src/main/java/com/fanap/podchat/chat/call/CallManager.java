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
        callVO.setPartnerId(Long.parseLong(request.getInvitees().get(0).getId()));
        callVO.setInvitees(request.getInvitees());
        callVO.setType(request.getCallType());

        JsonObject j = (JsonObject) App.getGson().toJsonTree(callVO);

        j.remove("partnerId");

        AsyncMessage message = new AsyncMessage();
        message.setContent(j.toString());
        message.setType(ChatMessageType.Constants.CALL_REQUEST);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);

        JsonObject a = (JsonObject) App.getGson().toJsonTree(message);
        a.remove("subjectId");

        return a.toString();


    }

    public static String createAcceptCallRequest(AcceptCallRequest request, String uniqueId) {

        CallVO callVO = new CallVO();
        callVO.setCreatorId(request.getCreatorId());
        callVO.setPartnerId(request.getPartnerId());
        callVO.setInvitees(request.getInvitees());
        callVO.setType(request.getCallType());

        JsonObject j = (JsonObject) App.getGson().toJsonTree(callVO);

        AsyncMessage message = new AsyncMessage();
        message.setContent(j.toString());
        message.setType(ChatMessageType.Constants.ACCEPT_CALL);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setSubjectId(request.getCallId());
        message.setUniqueId(uniqueId);

        JsonObject a = (JsonObject) App.getGson().toJsonTree(message);



        return a.toString();


    }

    public static String createRejectCallRequest(RejectCallRequest request, String uniqueId) {

        CallVO callVO = new CallVO();
        callVO.setCreatorId(request.getCreatorId());
        callVO.setPartnerId(request.getPartnerId());
        callVO.setInvitees(request.getInvitees());
        callVO.setType(request.getCallType());

        JsonObject j = (JsonObject) App.getGson().toJsonTree(callVO);

        AsyncMessage message = new AsyncMessage();
        message.setContent(j.toString());
        message.setType(ChatMessageType.Constants.REJECT_CALL);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setSubjectId(request.getCallId());
        message.setUniqueId(uniqueId);

        JsonObject a = (JsonObject) App.getGson().toJsonTree(message);



        return a.toString();


    }

    public static ChatResponse<ResultCallRequest> handleOnCallRequest(ChatMessage chatMessage) {

        CallVO callVO = App.getGson().fromJson(chatMessage.getContent(), CallVO.class);

        ResultCallRequest resultCallRequest = new ResultCallRequest(callVO);

        ChatResponse<ResultCallRequest> response = new ChatResponse<>();
        response.setResult(resultCallRequest);
        response.setUniqueId(chatMessage.getUniqueId());
        response.setSubjectId(chatMessage.getSubjectId());

        return response;


    }

    public static ChatResponse<ResultCallRequest> handleOnRejectCallRequest(ChatMessage chatMessage) {

        CallVO callVO = App.getGson().fromJson(chatMessage.getContent(), CallVO.class);

        ResultCallRequest resultCallRequest = new ResultCallRequest(callVO);

        ChatResponse<ResultCallRequest> response = new ChatResponse<>();
        response.setResult(resultCallRequest);
        response.setUniqueId(chatMessage.getUniqueId());
        response.setSubjectId(chatMessage.getSubjectId());

        return response;


    }

    public static ChatResponse<ResultCallRequest> handleOnCallStarted(ChatMessage chatMessage) {

        String content = chatMessage.getContent();


        return null;
    }
}
