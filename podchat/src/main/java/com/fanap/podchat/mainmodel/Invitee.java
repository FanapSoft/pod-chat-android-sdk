package com.fanap.podchat.mainmodel;

/**     TO_BE_USER_SSO_ID: 1,
        TO_BE_USER_CONTACT_ID: 2,
        TO_BE_USER_CELLPHONE_NUMBER: 3,
        TO_BE_USER_USERNAME: 4,
        TO_BE_USER_ID: 5 // only in P2P mode
        */
public class Invitee  {

    private String id;

    private int idType;

    public Invitee(String id, int idType) {
        this.id = id;
        this.idType = idType;
    }

    public Invitee(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }
}
