package com.fanap.podchat.chat.thread.respone;

import com.fanap.podchat.chat.thread.request.CloseThreadRequest;

public class DeleteGroupResult extends CloseThreadRequest {
    public DeleteGroupResult(Builder builder) {
        super(builder);
    }

    public DeleteGroupResult() {
        super();
    }

}
