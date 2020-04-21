package com.fanap.podchat.persistance;

import com.fanap.podchat.cachemodel.PhoneContact;
import com.fanap.podchat.persistance.dao.PhoneContactDao;
import com.fanap.podchat.util.PodThreadManager;

import java.util.List;

import javax.inject.Inject;

public class PhoneContactDbHelper {

    private PhoneContactDao phoneContactDao;

    @Inject
    public PhoneContactDbHelper(PhoneContactDao phoneContactDao) {
        this.phoneContactDao = phoneContactDao;
    }

    public List<PhoneContact> getPhoneContacts() {
       return phoneContactDao.getPhoneContacts();
    }

    public void addPhoneContacts(List<PhoneContact> phoneContacts){
        new PodThreadManager()
                .doThisSafe(()-> phoneContactDao.insertPhoneContacts(phoneContacts));
    }


    public void addPhoneContact(PhoneContact phoneContact) {
        phoneContactDao.insertPhoneContact(phoneContact);
    }
}
