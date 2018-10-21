package com.fanap.podchat.util;

import android.support.annotation.NonNull;

import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.model.Contacts;
import com.fanap.podchat.model.OutPutAddContact;
import com.fanap.podchat.model.ResultAddContact;

public class Util {

    @NonNull
    public static OutPutAddContact getReformatOutPutAddContact(Contacts contacts) {
        OutPutAddContact outPutAddContact = new OutPutAddContact();
        outPutAddContact.setErrorCode(0);
        outPutAddContact.setErrorMessage("");
        outPutAddContact.setHasError(false);

        ResultAddContact resultAddContact = new ResultAddContact();
        resultAddContact.setContentCount(1);
        Contact contact = new Contact();

        contact.setCellphoneNumber(contacts.getResult().get(0).getCellphoneNumber());
        contact.setEmail(contacts.getResult().get(0).getEmail());
        contact.setFirstName(contacts.getResult().get(0).getFirstName());
        contact.setId(contacts.getResult().get(0).getId());
        contact.setLastName(contacts.getResult().get(0).getLastName());
        contact.setUniqueId(contacts.getResult().get(0).getUniqueId());
        outPutAddContact.setResult(resultAddContact);
        return outPutAddContact;
    }
}
