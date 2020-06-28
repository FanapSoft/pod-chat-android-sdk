package com.fanap.podchat.chat.call.result_model;

import com.fanap.podchat.chat.call.model.CallVO;

import java.util.ArrayList;

public class GetCallHistoryResult {

    private ArrayList<CallVO> callsList;

    private int contentCount;


    public GetCallHistoryResult(ArrayList<CallVO> callsList, int contentCount) {
        this.callsList = callsList;
        this.contentCount = contentCount;
    }

    public ArrayList<CallVO> getCallsList() {
        return callsList;
    }

    public int getContentCount() {
        return contentCount;
    }
}
