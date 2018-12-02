package com.fanap.podchat.util;

import android.support.annotation.NonNull;

import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.Contacts;
import com.fanap.podchat.model.ResultAddContact;

import java.util.ArrayList;
import java.util.List;

public class Util {

    @NonNull
    public static ChatResponse<ResultAddContact> getReformatOutPutAddContact(Contacts contacts, String uniqueId) {
        ChatResponse<ResultAddContact> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(uniqueId);

        ResultAddContact resultAddContact = new ResultAddContact();
        resultAddContact.setContentCount(1);
        Contact contact = new Contact();

        contact.setCellphoneNumber(contacts.getResult().get(0).getCellphoneNumber());
        contact.setEmail(contacts.getResult().get(0).getEmail());
        contact.setFirstName(contacts.getResult().get(0).getFirstName());
        contact.setId(contacts.getResult().get(0).getId());
        contact.setLastName(contacts.getResult().get(0).getLastName());
        contact.setUniqueId(contacts.getResult().get(0).getUniqueId());
        resultAddContact.setContact(contact);
        chatResponse.setResult(resultAddContact);
        return chatResponse;
    }

    public static boolean isNullOrEmpty(String string) {
        boolean check = false;
        if (string != null && !string.isEmpty()) {
            check = true;
        }
        return check;
    }

    public static <T extends Number> boolean isNullOrEmpty(ArrayList<T> list) {
        boolean check = true;
        if (list != null && list.size() > 0) {
            check = false;
        }
        return check;
    }

    public static <T extends Number> boolean isNullOrEmpty(T number) {
        boolean check = true;
        String num = String.valueOf(number);
        if (number != null && !num.equals("0")) {
            check = false;
        }
        return check;
    }

    public static <T extends Class> boolean isNullOrEmpty(List<T> list) {
        boolean check = true;
        if (list != null && list.size() <= 0) {
            check = false;
        }
        return check;
    }
    public static <T extends Number> boolean isNullOrEmptyNumber(List<T> list) {
        boolean check = true;
        if (list != null && list.size() <= 0) {
            check = false;
        }
        return check;
    }

}
