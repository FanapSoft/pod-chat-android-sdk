package com.fanap.podchat.chat.contact;

import com.fanap.podchat.chat.contact.result_model.ContactSyncedResult;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactManager {


    public static ChatResponse<ContactSyncedResult> prepareContactSyncedResult(ChatMessage chatMessage) {

        ChatResponse<ContactSyncedResult> response = new ChatResponse<>();

        ContactSyncedResult result = new ContactSyncedResult(chatMessage.getSubjectId());

        response.setResult(result);

        return response;

    }


    //db query
//    order by hasUser desc, lastName is null or lastName='', lastName, firstName is null or firstName='', firstName LIMIT :count OFFSET :offset

    public static List<Contact> sortContacts(List<Contact> unsorted) {

        List<Contact> sorted = new ArrayList<>(unsorted);

        Collections.sort(sorted, compareContacts());

        return sorted;

    }

    private static Comparator<Contact> compareContacts() {
        return new Comparator<Contact>() {
            @Override
            public int compare(Contact contact1, Contact contact2) {

                if (contact1.isHasUser() && contact2.isHasUser()) {

                    if (Util.isNotNullOrEmpty(contact1.getLastName()) &&
                            Util.isNotNullOrEmpty(contact2.getLastName())) {

                        return contact1.getLastName().compareTo(contact2.getLastName());

                    } else if (Util.isNotNullOrEmpty(contact1.getFirstName()) &&
                            Util.isNotNullOrEmpty(contact2.getFirstName())) {

                        return contact1.getFirstName().compareTo(contact2.getFirstName());

                    } else return 1;

                } else return Boolean.compare(contact2.isHasUser(), contact1.isHasUser());


            }
        };
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
    }
}
