package com.fanap.podchat.chat.contact;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.contact.model.AddContactVO;
import com.fanap.podchat.chat.contact.result_model.ContactSyncedResult;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.BlockedContact;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultBlockList;
import com.fanap.podchat.model.ResultNotSeenDuration;
import com.fanap.podchat.requestobject.RequestAddContact;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.sentry.core.Sentry;
import rx.Observable;

public class ContactManager {


    public static ChatResponse<ContactSyncedResult> prepareContactSyncedResult(ChatMessage chatMessage) {

        ChatResponse<ContactSyncedResult> response = new ChatResponse<>();

        ContactSyncedResult result = new ContactSyncedResult(chatMessage.getSubjectId());

        response.setResult(result);

        return response;

    }


    public static String createAddContactRequest(RequestAddContact request,
                                                 String uniqueId) {
        AddContactVO addContactVO =
                new AddContactVO()
                        .setEmail(request.getEmail())
                        .setFirstName(request.getFirstName())
                        .setLastName(request.getLastName())
                        .setUserName(request.getUsername())
                        .setCellphoneNumber(request.getCellphoneNumber());

        String content = App.getGson().toJson(addContactVO);

        AsyncMessage message = new ChatMessage();

        message.setType(ChatMessageType.Constants.ADD_CONTACT);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(content);
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);

    }

    //db query
//    order by hasUser desc, lastName is null or lastName='', lastName, firstName is null or firstName='', firstName LIMIT :count OFFSET :offset

    public static List<Contact> sortContacts(List<Contact> unsorted) {

        List<Contact> sorted = new ArrayList<>(unsorted);

        Collections.sort(sorted, compareContacts());

        return sorted;

    }

    private static Comparator<Contact> compareContacts() {
        return (contact1, contact2) -> {

            if (contact1.isHasUser() && contact2.isHasUser()) {

                if (Util.isNotNullOrEmpty(contact1.getLastName()) &&
                        Util.isNotNullOrEmpty(contact2.getLastName())) {

                    return contact1.getLastName().compareTo(contact2.getLastName());

                } else if (Util.isNotNullOrEmpty(contact1.getLastName()) ||
                        Util.isNotNullOrEmpty(contact2.getLastName())) {

                    return (contact2.getLastName() != null ? contact2.getLastName() : "")
                            .compareTo((contact1.getLastName() != null ? contact1.getLastName() : ""));

                } else if (Util.isNotNullOrEmpty(contact1.getFirstName()) &&
                        Util.isNotNullOrEmpty(contact2.getFirstName())) {

                    return contact1.getFirstName().compareTo(contact2.getFirstName());

                } else if (Util.isNotNullOrEmpty(contact1.getFirstName()) ||
                        Util.isNotNullOrEmpty(contact2.getFirstName())) {

                    return (contact2.getFirstName() != null ? contact2.getFirstName() : "")
                            .compareTo((contact1.getFirstName() != null ? contact1.getFirstName() : ""));

                } else return 1;

            } else return Boolean.compare(contact2.isHasUser(), contact1.isHasUser());


        };
    }

    public static String prepareGetBlockListRequest(Long count, Long offset, String uniqueId, String typecode, String token) {
        JsonObject content = new JsonObject();

        if (offset != null) {
            content.addProperty("offset", offset);
        }
        if (count != null) {
            content.addProperty("count", count);
        } else {
            content.addProperty("count", 50);

        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content.toString());
        chatMessage.setType(ChatMessageType.Constants.GET_BLOCKED);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(token);
        chatMessage.setUniqueId(uniqueId);
        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(typecode)) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", typecode);
        }

        return jsonObject.toString();
    }

    public static String prepareBlockRequest(Long contactId, Long userId, Long threadId, String uniqueId, String mtypecode, String token) {

        JsonObject contentObject = new JsonObject();
        if (!Util.isNullOrEmpty(contactId)) {
            contentObject.addProperty("contactId", contactId);
        }
        if (!Util.isNullOrEmpty(userId)) {
            contentObject.addProperty("userId", userId);
        }
        if (!Util.isNullOrEmpty(threadId)) {
            contentObject.addProperty("threadId", threadId);
        }

        String json = contentObject.toString();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(json);
        chatMessage.setToken(token);
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTokenIssuer("1");
        chatMessage.setType(ChatMessageType.Constants.BLOCK);
        chatMessage.setTypeCode(mtypecode);

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(mtypecode)) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", mtypecode);
        }

        return jsonObject.toString();

    }

    public static String prepareUnBlockRequest(Long blockId, Long userId, Long threadId, Long contactId, String uniqueId, String mtypecode, String token) {

        ChatMessage chatMessage = new ChatMessage();

        JsonObject contentObject = new JsonObject();
        if (!Util.isNullOrEmpty(contactId)) {
            contentObject.addProperty("contactId", contactId);
        }
        if (!Util.isNullOrEmpty(userId)) {
            contentObject.addProperty("userId", userId);
        }
        if (!Util.isNullOrEmpty(threadId)) {
            contentObject.addProperty("threadId", threadId);
        }

        String jsonContent = contentObject.toString();


        chatMessage.setContent(jsonContent);
        chatMessage.setToken(token);
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTokenIssuer("1");
        chatMessage.setType(ChatMessageType.Constants.UNBLOCK);

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(chatMessage);
        jsonObject.remove("contentCount");
        jsonObject.remove("systemMetadata");
        jsonObject.remove("metadata");
        jsonObject.remove("repliedTo");

        if (Util.isNullOrEmpty(blockId)) {
            jsonObject.remove("subjectId");
        } else {
            jsonObject.remove("subjectId");
            jsonObject.addProperty("subjectId", blockId);
        }


        if (Util.isNullOrEmpty(mtypecode)) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", mtypecode);
        }

        return jsonObject.toString();
    }


    public static ChatResponse<ResultBlockList> prepareBlockListResponse(ChatMessage chatMessage) {
        ChatResponse<ResultBlockList> chatResponse = new ChatResponse<>();
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        ResultBlockList resultBlockList = new ResultBlockList();

        List<BlockedContact> blockedContacts = App.getGson().fromJson(chatMessage.getContent(), new TypeToken<ArrayList<BlockedContact>>() {
        }.getType());
        resultBlockList.setContacts(blockedContacts);
        chatResponse.setResult(resultBlockList);

        return chatResponse;
    }

    public static ChatResponse<ResultNotSeenDuration> prepareUpdateLastSeenResponse(ChatMessage message, JsonParser parser) {


        ChatResponse<ResultNotSeenDuration> response = new ChatResponse<>();

        JsonObject jsonObject = Util.objectToJson(message.getContent(), parser);

        Map<String, Long> idLastSeen = new HashMap<>();

        for (String key :
                jsonObject.keySet()) {

            idLastSeen.put(key, jsonObject.get(key).getAsLong());

        }


        ResultNotSeenDuration resultNotSeenDuration = new ResultNotSeenDuration();

        resultNotSeenDuration.setIdNotSeenPair(idLastSeen);

        response.setResult(resultNotSeenDuration);

        return response;
    }

    public static ChatResponse<ResultBlockList> prepareGetBlockListFromCache(String uniqueId, List<BlockedContact> cacheContacts) {

        ChatResponse<ResultBlockList> chatResponse = new ChatResponse<>();
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(uniqueId);
        chatResponse.setCache(true);
        ResultBlockList resultBlockList = new ResultBlockList();

        resultBlockList.setContacts(cacheContacts);
        chatResponse.setResult(resultBlockList);

        return chatResponse;
    }

    public static class ContactResponse {
        private List<Contact> contactsList;
        private long contentCount;
        private String source;

        public ContactResponse(List<Contact> contactList, long contentCount, String source) {
            this.contactsList = contactList;
            this.contentCount = contentCount;
            this.source = source;
        }

        public List<Contact> getContactsList() {
            return contactsList;
        }

        public long getContentCount() {
            return contentCount;
        }

        public String getSource() {
            return source;
        }
    }


    public static Observable<List<Contact>> getByUsername(String username, List<Contact> allContacts) {

        try {
            if (Util.isNotNullOrEmpty(username)) {
                return Observable.from(allContacts)
                        .filter(t -> t.getLinkedUser() != null)
                        .filter(t -> t.getLinkedUser().getUsername().contains(username))
                        .toList();
            } else {
                return Observable.from(allContacts).toList();
            }
        } catch (Exception e) {
            if (Sentry.isEnabled())
                Sentry.captureException(e);
            return Observable.from(allContacts).toList();
        }

    }
}
