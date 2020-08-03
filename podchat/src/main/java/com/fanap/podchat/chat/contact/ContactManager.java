package com.fanap.podchat.chat.contact;

import com.fanap.podchat.chat.contact.result_model.ContactSyncedResult;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.ChatResponse;

public class ContactManager {





   public static ChatResponse<ContactSyncedResult> prepareContactSyncedResult(ChatMessage chatMessage){

        ChatResponse<ContactSyncedResult> response = new ChatResponse<>();

        ContactSyncedResult result = new ContactSyncedResult(chatMessage.getSubjectId());

        response.setResult(result);

        return response;

    }
}
