package com.fanap.podchat.mainmodel;

/**     TO_BE_USER_SSO_ID: 1,
        TO_BE_USER_CONTACT_ID: 2,
        TO_BE_USER_CELLPHONE_NUMBER: 3,
        TO_BE_USER_USERNAME: 4,
        TO_BE_USER_ID: 5 // only in P2P mode
        */
public class Invitee  {

    private long id;
    private int idType;

    public Invitee(long id, int idType) {
        this.id = id;
        this.idType = idType;
    }

    public Invitee(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }
}
