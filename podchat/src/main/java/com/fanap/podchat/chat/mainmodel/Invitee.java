package com.fanap.podchat.chat.mainmodel;

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
