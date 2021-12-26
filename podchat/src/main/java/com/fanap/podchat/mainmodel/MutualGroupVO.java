package com.fanap.podchat.mainmodel;

import java.io.Serializable;

public class MutualGroupVO implements Serializable {
    private Invitee toBeUserVO;

    private int offset = 0;
    private int count = 20;


    public Invitee getToBeUserVO() {
        return toBeUserVO;
    }

    public void setToBeUserVO(Invitee toBeUserVO) {
        this.toBeUserVO = toBeUserVO;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
