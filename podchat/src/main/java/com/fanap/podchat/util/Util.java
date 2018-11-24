package com.fanap.podchat.util;

import android.support.annotation.NonNull;

import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.model.Contacts;
import com.fanap.podchat.model.OutPutAddContact;
import com.fanap.podchat.model.ResultAddContact;

import java.util.ArrayList;

public class Util {

    @NonNull
    public static OutPutAddContact getReformatOutPutAddContact(Contacts contacts, String uniqueId) {
        OutPutAddContact outPutAddContact = new OutPutAddContact();
        outPutAddContact.setErrorCode(0);
        outPutAddContact.setErrorMessage("");
        outPutAddContact.setHasError(false);
        outPutAddContact.setUniqueId(uniqueId);
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
        outPutAddContact.setResult(resultAddContact);
        return outPutAddContact;
    }

    public static boolean isNullOrEmpty(String string) {
        boolean check = true;
        if (string != null && !string.isEmpty()) {
            check = false;
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

    public static <T extends Class> boolean isNullOrEmptyArray(ArrayList<T> list) {
        boolean check = true;
        if (list != null && list.size() <= 0) {
            check = false;
        }
        return check;
    }

}
