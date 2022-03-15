package com.fanap.podchat.persistance.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.fanap.podchat.cachemodel.PhoneContact;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PhoneContactDao {

    @Insert(onConflict = REPLACE)
    void insertPhoneContacts(List<PhoneContact> phoneContacts);

    @Query("SELECT * FROM PHONECONTACT")
    List<PhoneContact> getPhoneContacts();

    @Insert(onConflict = REPLACE)
    void insertPhoneContact(PhoneContact phoneContact);
}
