package com.fanap.podchat.chat.participant;

import android.os.Build;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.RemoveParticipant;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultAddParticipant;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RemoveParticipantRequest;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.InviteType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

import io.sentry.core.Sentry;
import rx.Observable;

public class ParticipantsManager {


    public static String prepareRemoveParticipantsRequest(long threadId, List<Long> participantIds, String uniqueId, String mTypeCode, String token) {
        RemoveParticipant removeParticipant = new RemoveParticipant();
        removeParticipant.setTokenIssuer("1");
        removeParticipant.setType(ChatMessageType.Constants.REMOVE_PARTICIPANT);
        removeParticipant.setSubjectId(threadId);
        removeParticipant.setToken(token);
        removeParticipant.setUniqueId(uniqueId);

        JsonArray contacts = new JsonArray();
        for (Long p : participantIds) {
            contacts.add(p);
        }
        removeParticipant.setContent(contacts.toString());

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(removeParticipant);
        jsonObject.remove("contentCount");
        jsonObject.remove("systemMetadata");
        jsonObject.remove("metadata");
        jsonObject.remove("repliedTo");

        if (Util.isNullOrEmpty(mTypeCode)) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", mTypeCode);
        }

        String asyncContent = jsonObject.toString();
        return asyncContent;

    }


    public static String prepareAddParticipantsRequest(RequestAddParticipants request, String uniqueId, String mTypeCode, String token) {

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
        chatMessage.setToken(token);
        chatMessage.setContent(participantsJsonArray.toString());
        chatMessage.setSubjectId(request.getThreadId());
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setType(ChatMessageType.Constants.ADD_PARTICIPANT);
        chatMessage.setTypeCode(mTypeCode);

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(chatMessage);


        return jsonObject.toString();
    }


    public static ChatResponse<ResultAddParticipant> prepareAddParticipantResponse(ChatMessage chatMessage, Thread thread) {

        ChatResponse<ResultAddParticipant> chatResponse = new ChatResponse<>();

        ResultAddParticipant resultAddParticipant = new ResultAddParticipant();
        resultAddParticipant.setThread(thread);
        chatResponse.setErrorCode(0);
        chatResponse.setErrorMessage("");
        chatResponse.setHasError(false);
        chatResponse.setResult(resultAddParticipant);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        return chatResponse;
    }

    public static String prepareRemoveParticipantsRequestWithInvitee(RemoveParticipantRequest request, String uniqueId, String token, String mTypeCode) {
        RemoveParticipant removeParticipant = new RemoveParticipant();
        removeParticipant.setTokenIssuer("1");
        removeParticipant.setType(ChatMessageType.Constants.REMOVE_PARTICIPANT);
        removeParticipant.setSubjectId(request.getThreadId());
        removeParticipant.setToken(token);
        removeParticipant.setUniqueId(uniqueId);
        String content = "";

        if (request.getInvitees() != null && request.getInvitees().size() > 0) {
            content = App.getGson().toJson(request.getInvitees());
        } else {
            JsonArray contacts = new JsonArray();
            for (Long p : request.getParticipantIds()) {
                contacts.add(p);
            }
            content = contacts.toString();
        }

        removeParticipant.setContent(content);

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(removeParticipant);
        jsonObject.remove("contentCount");
        jsonObject.remove("systemMetadata");
        jsonObject.remove("metadata");
        jsonObject.remove("repliedTo");

        if (Util.isNullOrEmpty(mTypeCode)) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", mTypeCode);
        }

        String asyncContent = jsonObject.toString();
        return asyncContent;

    }

    public static Observable<List<Participant>> getByIds(List<Integer> ids, List<Participant> participants) {

        try {
            if (Util.isNotNullOrEmpty(ids))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return Observable.from(participants)
                            .filter(t -> ids.contains(Math.toIntExact(t.getContactId())))
                            .toList();
                } else {
                    return Observable.from(participants)
                            .filter(t -> ids.contains((int) t.getContactId()))
                            .toList();
                }
        } catch (Exception e) {
            if (Sentry.isEnabled())
                Sentry.captureException(e);
            return Observable.from(participants).toList();
        }

        return Observable.from(participants).toList();

    }
}
