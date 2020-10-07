package com.fanap.podchat.call;

import android.util.Log;

import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.call.model.CallVO;
import com.fanap.podchat.call.model.ClientDTO;
import com.fanap.podchat.call.model.CreateCallVO;
import com.fanap.podchat.call.request_model.AcceptCallRequest;
import com.fanap.podchat.call.request_model.CallRequest;
import com.fanap.podchat.call.request_model.EndCallRequest;
import com.fanap.podchat.call.request_model.GetCallHistoryRequest;
import com.fanap.podchat.call.request_model.RejectCallRequest;
import com.fanap.podchat.call.request_model.TerminateCallRequest;
import com.fanap.podchat.call.result_model.CallDeliverResult;
import com.fanap.podchat.call.result_model.CallReconnectResult;
import com.fanap.podchat.call.result_model.CallRequestResult;
import com.fanap.podchat.call.result_model.CallStartResult;
import com.fanap.podchat.call.result_model.EndCallResult;
import com.fanap.podchat.call.result_model.GetCallHistoryResult;
import com.fanap.podchat.call.result_model.JoinCallParticipantResult;
import com.fanap.podchat.call.result_model.LeaveCallResult;
import com.fanap.podchat.call.result_model.RemoveFromCallResult;
import com.fanap.podchat.call.result_model.StartedCallModel;
import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RequestRemoveParticipants;
import com.fanap.podchat.util.Callback;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.InviteType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.fanap.podchat.chat.Chat.TAG;

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

        if (request.getSubjectId() <= 0)
            createCallVO.setInvitees(request.getInvitees());

        createCallVO.setType(request.getCallType());

        JsonObject contentObj = (JsonObject) App.getGson().toJsonTree(createCallVO);

        AsyncMessage message = new AsyncMessage();
        message.setContent(contentObj.toString());
        message.setType(ChatMessageType.Constants.CALL_REQUEST);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);


        JsonObject messageObj;

        if (Util.isNullOrEmpty(request.getInvitees())) {
            message.setSubjectId(request.getSubjectId());
            messageObj = (JsonObject) App.getGson().toJsonTree(message);
        } else {
            messageObj = (JsonObject) App.getGson().toJsonTree(message);
            messageObj.remove("subjectId");
        }


        return messageObj.toString();


    }

    public static String createGroupCallRequestMessage(CallRequest request, String uniqueId) {

        CreateCallVO createCallVO = new CreateCallVO();

        createCallVO.setGroup(true);
        createCallVO.setCreatorId(CoreConfig.userId);

        if (request.getSubjectId() <= 0)
            createCallVO.setInvitees(request.getInvitees());
        else
            createCallVO.setThreadId(request.getSubjectId());

        createCallVO.setType(request.getCallType());

        JsonObject contentObj = (JsonObject) App.getGson().toJsonTree(createCallVO);

        AsyncMessage message = new AsyncMessage();
        message.setContent(contentObj.toString());
        message.setType(ChatMessageType.Constants.GROUP_CALL_REQUEST);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);


        JsonObject messageObj = (JsonObject) App.getGson().toJsonTree(message);
        messageObj.remove("subjectId");

        return messageObj.toString();


    }

    public static String createAddCallParticipantMessage(RequestAddParticipants request, String uniqueId) {

        JsonArray participantsJsonArray = new JsonArray();


        if (request.getContactIds() != null) {
            for (Long p : request.getContactIds()) {
                participantsJsonArray.add(p);
            }
        } else if (request.getUserNames() != null) {

            for (String username :
                    request.getUserNames()) {

                Invitee invitee = new Invitee();
                invitee.setId(username);
                invitee.setIdType(InviteType.Constants.TO_BE_USER_USERNAME);
                JsonElement jsonElement = App.getGson().toJsonTree(invitee);
                participantsJsonArray.add(jsonElement);
            }

        } else {

            for (Long coreUserId :
                    request.getCoreUserIds()) {

                Invitee invitee = new Invitee();
                invitee.setId(coreUserId);
                invitee.setIdType(InviteType.Constants.TO_BE_USER_ID);
                JsonElement jsonElement = App.getGson().toJsonTree(invitee);
                participantsJsonArray.add(jsonElement);
            }

        }

        AsyncMessage chatMessage = new AsyncMessage();

        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(CoreConfig.token);
        chatMessage.setContent(participantsJsonArray.toString());
        chatMessage.setSubjectId(request.getThreadId());
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setType(ChatMessageType.Constants.ADD_CALL_PARTICIPANT);
        chatMessage.setTypeCode(Util.isNullOrEmpty(request.getTypeCode()) ? CoreConfig.typeCode : request.getTypeCode());

        JsonObject messageObj = (JsonObject) App.getGson().toJsonTree(chatMessage);


        return messageObj.toString();
    }

    public static String createRemoveCallParticipantMessage(RequestRemoveParticipants request, String uniqueId) {

        JsonArray participantsJsonArray = new JsonArray();


        for (Long participantId :
                request.getParticipantIds()) {
            participantsJsonArray.add(participantId);
        }

        AsyncMessage chatMessage = new AsyncMessage();

        chatMessage.setTokenIssuer(CoreConfig.tokenIssuer);
        chatMessage.setToken(CoreConfig.token);
        chatMessage.setContent(participantsJsonArray.toString());
        chatMessage.setSubjectId(request.getThreadId());
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setType(ChatMessageType.Constants.REMOVE_CALL_PARTICIPANT);
        chatMessage.setTypeCode(Util.isNullOrEmpty(request.getTypeCode()) ? CoreConfig.typeCode : request.getTypeCode());

        JsonObject messageObj = (JsonObject) App.getGson().toJsonTree(chatMessage);


        return messageObj.toString();
    }

    public static String createTerminateCallMessage(TerminateCallRequest request, String uniqueId) {


        AsyncMessage message = new AsyncMessage();
        message.setType(ChatMessageType.Constants.TERMINATE_CALL);
        message.setToken(CoreConfig.token);
        message.setSubjectId(request.getCallId());
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(Util.isNullOrEmpty(request.getTypeCode()) ? request.getTypeCode() : CoreConfig.typeCode);
        JsonObject a = (JsonObject) App.getGson().toJsonTree(message);
        return a.toString();

    }

    public static String createAcceptCallRequest(AcceptCallRequest request, String uniqueId) {

        CreateCallVO createCallVO = new CreateCallVO();
        createCallVO.setCreatorId(request.getCreatorId());
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
        message.setSubjectId(request.getCallId());
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(Util.isNullOrEmpty(request.getTypeCode()) ? request.getTypeCode() : CoreConfig.typeCode);
        JsonObject a = (JsonObject) App.getGson().toJsonTree(message);
        return a.toString();


    }

    public static String createDeliverCallRequestMessage(ChatMessage chatMessage) {

        AsyncMessage message = new AsyncMessage();
        message.setType(ChatMessageType.Constants.DELIVER_CALL_REQUEST);
        message.setToken(CoreConfig.token);
        message.setSubjectId(chatMessage.getSubjectId());
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(chatMessage.getUniqueId());
        message.setTypeCode(CoreConfig.typeCode);
        JsonObject messageObj = (JsonObject) App.getGson().toJsonTree(message);
        return messageObj.toString();

    }

    public static EndCallRequest createEndCallRequest(long subjectId) {
        return new EndCallRequest.Builder()
                .setCallId(subjectId)
                .build();
    }

    public static ChatResponse<CallRequestResult> handleOnCallRequest(ChatMessage chatMessage) {

        Log.d(TAG, "Chat Message: " + chatMessage.getContent());

        CreateCallVO createCallVO = App.getGson().fromJson(chatMessage.getContent(), CreateCallVO.class);

        CallRequestResult callRequestResult = new CallRequestResult(createCallVO);

        ChatResponse<CallRequestResult> response = new ChatResponse<>();
        response.setResult(callRequestResult);
        response.setUniqueId(chatMessage.getUniqueId());
        response.setSubjectId(chatMessage.getSubjectId());

        return response;


    }

    public static ChatResponse<CallRequestResult> handleOnGroupCallRequest(ChatMessage chatMessage) {

        Log.d(TAG, "Chat Message: " + chatMessage.getContent());

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

    public static ChatResponse<CallDeliverResult> handleOnCallDelivered(ChatMessage chatMessage) {

        CallParticipantVO participant = App.getGson().fromJson(chatMessage.getContent(), CallParticipantVO.class);

        CallDeliverResult result = new CallDeliverResult(participant);

        ChatResponse<CallDeliverResult> response = new ChatResponse<>();

        response.setResult(result);

        response.setSubjectId(chatMessage.getSubjectId());

        response.setUniqueId(chatMessage.getUniqueId());

        return response;

    }

    public static ChatResponse<StartedCallModel> handleOnCallStarted(ChatMessage chatMessage) {

        ChatResponse<StartedCallModel> response = new ChatResponse<>();

        String content = chatMessage.getContent();
        StartedCallModel result = App.getGson().fromJson(content, StartedCallModel.class);
        response.setResult(result);
        response.setUniqueId(chatMessage.getUniqueId());
        response.setSubjectId(chatMessage.getSubjectId());


        return response;
    }

    public static ChatResponse<EndCallResult> handleOnCallEnded(ChatMessage chatMessage) {

        ChatResponse<EndCallResult> response = new ChatResponse<>();

        EndCallResult result = new EndCallResult();

        result.setTypeCode(chatMessage.getTypeCode());

        result.setCallId(chatMessage.getSubjectId());

        response.setResult(result);
        response.setSubjectId(chatMessage.getSubjectId());
        response.setUniqueId(chatMessage.getUniqueId());

        return response;

    }

    public static ChatResponse<LeaveCallResult> handleOnParticipantLeft(ChatMessage chatMessage) {

        ChatResponse<LeaveCallResult> response = new ChatResponse<>();


        ArrayList<CallParticipantVO> participantsLeft = App.getGson().fromJson(chatMessage.getContent(), new TypeToken<ArrayList<CallParticipantVO>>() {
        }.getType());

        LeaveCallResult result = new LeaveCallResult();
        result.setCallId(chatMessage.getSubjectId());
        result.setCallParticipants(participantsLeft);

        response.setResult(result);
        response.setSubjectId(chatMessage.getSubjectId());
        response.setUniqueId(chatMessage.getUniqueId());

        return response;

    }

    public static ChatResponse<RemoveFromCallResult> handleOnParticipantRemoved(ChatMessage chatMessage) {

        ChatResponse<RemoveFromCallResult> response = new ChatResponse<>();


        ArrayList<CallParticipantVO> participantsLeft = App.getGson().fromJson(chatMessage.getContent(), new TypeToken<ArrayList<CallParticipantVO>>() {
        }.getType());

        RemoveFromCallResult result = new RemoveFromCallResult();
        result.setCallId(chatMessage.getSubjectId());
        result.setCallParticipants(participantsLeft);

        if (Util.isNotNullOrEmpty(participantsLeft))
            result.setUserRemoved(isUserRemoved(participantsLeft));


        response.setResult(result);
        response.setSubjectId(chatMessage.getSubjectId());
        response.setUniqueId(chatMessage.getUniqueId());

        return response;

    }

    public static boolean isUserRemoved(List<CallParticipantVO> removedParticipants) {

        return CoreConfig.userId != null && (CoreConfig.userId > 0 && isUserInRemoveList(removedParticipants));
    }

    private static boolean isUserInRemoveList(List<CallParticipantVO> removedParticipants) {

        for (CallParticipantVO removedParticipant :
                removedParticipants) {

            if (removedParticipant.getUserId().equals(CoreConfig.userId))
                return true;

        }
        return false;
    }


    public static ChatResponse<GetCallHistoryResult> handleOnGetCallHistory(ChatMessage chatMessage, Callback callback) {

        ChatResponse<GetCallHistoryResult> response = new ChatResponse<>();

        response.setUniqueId(chatMessage.getUniqueId());

        ArrayList<CallVO> calls = new ArrayList<>();

        long offset = callback != null ? callback.getOffset() : 0;

        try {
            calls = App.getGson().fromJson(chatMessage.getContent(), new TypeToken<ArrayList<CallVO>>() {
            }.getType());
        } catch (JsonSyntaxException ignored) {
        }

        response.setResult(new GetCallHistoryResult(calls, chatMessage.getContentCount(), (calls.size() + offset < chatMessage.getContentCount()), (calls.size() + offset)));

        return response;

    }

    public static ChatResponse<GetCallHistoryResult> handleOnGetCallHistoryFromCache(String uniqueId, ArrayList<CallVO> calls, long contentCount, long offset) {

        ChatResponse<GetCallHistoryResult> response = new ChatResponse<>();

        response.setUniqueId(uniqueId);

        response.setResult(new GetCallHistoryResult(calls, contentCount, (calls.size() + offset < contentCount), (calls.size() + offset)));

        response.setCache(true);

        return response;

    }

    public static ChatResponse<CallReconnectResult> handleOnCallReconnectReceived(ChatMessage chatMessage) {

        ChatResponse<CallReconnectResult> response = null;
        try {
            response = new ChatResponse<>();

            response.setUniqueId(chatMessage.getUniqueId());

            response.setSubjectId(chatMessage.getSubjectId());

            ClientDTO clientDTO = App.getGson().fromJson(chatMessage.getContent(), ClientDTO.class);

            CallReconnectResult result = new CallReconnectResult();

            result.setCallId(chatMessage.getSubjectId());

            result.setClientId(clientDTO.getClientId());

            response.setResult(result);

        } catch (Exception e) {
            Log.wtf(TAG, e);
        }

        return response;

    }

    public static ChatResponse<CallReconnectResult> handleOnCallConnectReceived(ChatMessage chatMessage) {

        ChatResponse<CallReconnectResult> response = null;
        try {
            response = new ChatResponse<>();

            response.setUniqueId(chatMessage.getUniqueId());

            response.setSubjectId(chatMessage.getSubjectId());

            ClientDTO clientDTO = App.getGson().fromJson(chatMessage.getContent(), ClientDTO.class);

            CallReconnectResult result = new CallReconnectResult();

            result.setCallId(chatMessage.getSubjectId());

            result.setClientId(clientDTO.getClientId());

            response.setResult(result);

        } catch (Exception e) {
            Log.wtf(TAG, e);
        }

        return response;

    }

    public static ChatResponse<JoinCallParticipantResult> handleOnParticipantJoined(ChatMessage chatMessage) {
        ChatResponse<JoinCallParticipantResult> response = null;
        try {
            response = new ChatResponse<>();

            response.setUniqueId(chatMessage.getUniqueId());

            response.setSubjectId(chatMessage.getSubjectId());

            ArrayList<CallParticipantVO> callParticipantVOS = App.getGson().fromJson(chatMessage.getContent(),
                    new TypeToken<ArrayList<CallParticipantVO>>() {
                    }.getType());

            JoinCallParticipantResult result = new JoinCallParticipantResult();

            result.setCallId(chatMessage.getSubjectId());

            result.setJoinedParticipants(callParticipantVOS);

            response.setResult(result);

        } catch (Exception e) {
            Log.wtf(TAG, e);
        }

        return response;
    }

    public static ChatResponse<CallStartResult> fillResult(ChatResponse<StartedCallModel> callResponse) {


        ChatResponse<CallStartResult> response = new ChatResponse<>();

        CallStartResult result = new CallStartResult(callResponse.getResult().getCallName(),
                callResponse.getResult().getCallImage());

        response.setResult(result);
        response.setSubjectId(callResponse.getSubjectId());
        response.setUniqueId(callResponse.getUniqueId());


        return response;
    }
}
