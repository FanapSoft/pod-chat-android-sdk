package com.fanap.podchat.persistance;

import android.util.Log;

import com.fanap.podchat.cachemodel.PhoneContact;
import com.fanap.podchat.persistance.dao.PhoneContactDao;
import com.fanap.podchat.util.PodThreadManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class PhoneContactDbHelper {

    private PhoneContactDao phoneContactDao;

    @Inject
    public PhoneContactDbHelper(PhoneContactDao phoneContactDao) {
        this.phoneContactDao = phoneContactDao;
    }

    public List<PhoneContact> getPhoneContacts() {
        try {
            return phoneContactDao.getPhoneContacts();
        } catch (Exception e) {
            Log.e("CHAT_SDK", "Get Contacts failed " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void addPhoneContacts(List<PhoneContact> phoneContacts) {

        new PodThreadManager()
                .doThisSafe(() -> {
                    try {
                        phoneContactDao.insertPhoneContacts(phoneContacts);
                        Log.i("CHAT_SDK", "Save " + phoneContacts.size() + "Contact Successfully");
                    } catch (Exception e) {
                        Log.e("CHAT_SDK", "Save Contacts failed " + e.getMessage());
                    }
                });

    }


    public void addPhoneContact(PhoneContact phoneContact) {
        phoneContactDao.insertPhoneContact(phoneContact);
    }


}
