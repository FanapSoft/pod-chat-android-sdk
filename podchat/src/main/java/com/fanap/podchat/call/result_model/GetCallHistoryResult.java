package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CallVO;

import java.util.ArrayList;

public class GetCallHistoryResult {

    private ArrayList<CallVO> callsList;

    private long contentCount;

    private boolean hasNext;

    private long nextOffset;


    public GetCallHistoryResult(ArrayList<CallVO> callsList, long contentCount, boolean hasNext, long nextOffset) {
        this.callsList = callsList;
        this.contentCount = contentCount;
        this.hasNext = hasNext;
        this.nextOffset = nextOffset;
    }

    public GetCallHistoryResult(ArrayList<CallVO> callsList, long contentCount) {
        this.callsList = callsList;
        this.contentCount = contentCount;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public long getNextOffset() {
        return nextOffset;
    }

    public ArrayList<CallVO> getCallsList() {
        return callsList;
    }

    public long getContentCount() {
        return contentCount;
    }
}
