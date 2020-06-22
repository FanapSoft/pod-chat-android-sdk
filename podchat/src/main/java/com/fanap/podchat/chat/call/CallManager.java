package com.fanap.podchat.chat.call;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.call.request_model.AcceptCallRequest;
import com.fanap.podchat.chat.call.request_model.CallRequest;
import com.fanap.podchat.chat.call.request_model.EndCallRequest;
import com.fanap.podchat.chat.call.request_model.GetCallHistoryRequest;
import com.fanap.podchat.chat.call.request_model.RejectCallRequest;
import com.fanap.podchat.chat.call.result_model.CallRequestResult;
import com.fanap.podchat.chat.call.result_model.EndCallResult;
import com.fanap.podchat.chat.call.result_model.StartCallResult;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class CallManager {

    public static String createGetCallHistoryRequest(GetCallHistoryRequest request, String uniqueId) {


        request.setCount(request.getCount() > 0 ? request.getCount() : 50);

        JsonObject content = (JsonObject) App.getGson().toJsonTree(request);

        if (request.getCreatorSsoId() == 0)
            content.remove("creatorSsoId");
        if (request.getCreatorCoreUserId() == 0)
            content.remove("creatorCoreUserId");

        content.remove("useCache");

        AsyncMessage message = new AsyncMessage();
        message.setContent(content.toString());
        message.setType(ChatMessageType.Constants.GET_CALLS);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(Util.isNullOrEmpty(request.getTypeCode()) ? CoreConfig.typeCode : request.getTypeCode());

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(message);

        jsonObject.remove("subjectId");

        return jsonObject.toString();

    }

    public static String createCallRequestMessage(CallRequest request, String uniqueId) {


        CreateCallVO createCallVO = new CreateCallVO();
        createCallVO.setCreatorId(CoreConfig.userId);
        createCallVO.setInvitees(request.getInvitees());
        createCallVO.setType(request.getCallType());

        JsonObject j = (JsonObject) App.getGson().toJsonTree(createCallVO);

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

        CreateCallVO createCallVO = new CreateCallVO();
        createCallVO.setCreatorId(request.getCreatorId());
//        callVO.setPartnerId(request.getPartnerId());
//        if(Util.isNullOrEmpty())
        createCallVO.setInvitees(request.getInvitees());
        createCallVO.setType(request.getCallType());

        JsonObject j = (JsonObject) App.getGson().toJsonTree(createCallVO);

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

        CreateCallVO createCallVO = new CreateCallVO();
        createCallVO.setCreatorId(request.getCreatorId());
//        callVO.setPartnerId(request.getPartnerId());
        createCallVO.setInvitees(request.getInvitees());
        createCallVO.setType(request.getCallType());

        JsonObject j = (JsonObject) App.getGson().toJsonTree(createCallVO);

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

    public static String createEndCallRequestMessage(EndCallRequest request, String uniqueId) {


        AsyncMessage message = new AsyncMessage();
        message.setType(ChatMessageType.Constants.END_CALL_REQUEST);
        message.setToken(CoreConfig.token);
        message.setSubjectId(request.getSubjectId());
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(Util.isNullOrEmpty(request.getTypeCode()) ? request.getTypeCode() : CoreConfig.typeCode);
        JsonObject a = (JsonObject) App.getGson().toJsonTree(message);
        return a.toString();


    }


    public static ChatResponse<CallRequestResult> handleOnCallRequest(ChatMessage chatMessage) {

        CreateCallVO createCallVO = App.getGson().fromJson(chatMessage.getContent(), CreateCallVO.class);

        CallRequestResult callRequestResult = new CallRequestResult(createCallVO);

        ChatResponse<CallRequestResult> response = new ChatResponse<>();
        response.setResult(callRequestResult);
        response.setUniqueId(chatMessage.getUniqueId());
        response.setSubjectId(chatMessage.getSubjectId());

        return response;


    }

    public static ChatResponse<CallRequestResult> handleOnRejectCallRequest(ChatMessage chatMessage) {

        CreateCallVO createCallVO = App.getGson().fromJson(chatMessage.getContent(), CreateCallVO.class);

        CallRequestResult callRequestResult = new CallRequestResult(createCallVO);

        ChatResponse<CallRequestResult> response = new ChatResponse<>();
        response.setResult(callRequestResult);
        response.setUniqueId(chatMessage.getUniqueId());
        response.setSubjectId(chatMessage.getSubjectId());

        return response;


    }

    public static ChatResponse<StartCallResult> handleOnCallStarted(ChatMessage chatMessage) {

        ChatResponse<StartCallResult> response = new ChatResponse<>();

        String content = chatMessage.getContent();
        StartCallResult result = App.getGson().fromJson(content, StartCallResult.class);
        response.setResult(result);
        response.setUniqueId(chatMessage.getUniqueId());
        response.setSubjectId(chatMessage.getSubjectId());


        return response;
    }

    public static ChatResponse<EndCallResult> handleOnCallEnded(ChatMessage chatMessage) {

        ChatResponse<EndCallResult> response = new ChatResponse<>();

        EndCallResult result = new EndCallResult();

        result.setTypeCode(chatMessage.getTypeCode());

        result.setSubjectId(chatMessage.getSubjectId());

        response.setResult(result);
        response.setSubjectId(chatMessage.getSubjectId());
        response.setUniqueId(chatMessage.getUniqueId());

        return response;

    }

    public static ChatResponse<GetCallHistoryResult> handleOnGetCallHistory(ChatMessage chatMessage) {

        ChatResponse<GetCallHistoryResult> response = new ChatResponse<>();

        response.setUniqueId(chatMessage.getUniqueId());

        ArrayList<CallVO> calls = new ArrayList<>();

        try {
            calls = App.getGson().fromJson(chatMessage.getContent(), new TypeToken<ArrayList<CallVO>>() {
            }.getType());
        } catch (JsonSyntaxException ignored) {
        }

        response.setResult(new GetCallHistoryResult(calls, chatMessage.getContentCount()));

        return response;

    }
}
