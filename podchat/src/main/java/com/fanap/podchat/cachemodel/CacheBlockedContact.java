package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.Entity;

import com.fanap.podchat.mainmodel.LinkedUser;



@Entity
public class CacheBlockedContact extends CacheContact {


    public CacheBlockedContact(String expireDate, long id, String firstName, long userId, String lastName, Boolean blocked, long creationDate, LinkedUser linkedUser, String cellphoneNumber, String email, String uniqueId, long notSeenDuration, boolean hasUser) {
        super(expireDate, id, firstName, userId, lastName, blocked, creationDate, linkedUser, cellphoneNumber, email, uniqueId, notSeenDuration, hasUser);
    }







}
