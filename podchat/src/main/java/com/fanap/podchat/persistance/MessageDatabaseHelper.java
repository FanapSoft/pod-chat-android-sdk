package com.fanap.podchat.persistance;

import android.content.Context;

import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.UserInfo;
import com.fanap.podchat.persistance.dao.MessageDao;

import java.util.List;

public class MessageDatabaseHelper extends BaseDatabaseHelper {

    public MessageDao messageDao;

    public MessageDatabaseHelper(Context context) {
        super(context);
        messageDao = appDatabase.getMessageDao();
    }

    public List<Contact> getContacts() {
        return messageDao.getContact();
    }

    public void save(List<Contact> contacts) {
        messageDao.insertContact(contacts);
    }

    public void saveUserInfo(UserInfo userInfo){
        messageDao.insertUserInfo(userInfo);
    }

    public UserInfo getUserInfo(){
        return messageDao.getUserInfo();
    }

//    public List<Thread> getThread() {
//        return messageDao.getThread();
//    }
//
//    public void saveThread(List<Thread> threads) {
//        messageDao.insertThread(threads);
//    }
}
