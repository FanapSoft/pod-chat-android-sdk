package com.fanap.podchat.call.model;

import java.io.Serializable;
import java.util.List;

public class RecordDetailDto implements Serializable {

    private int recordType = 1;
    private List<String> tags;
    private String content;

    public RecordDetailDto setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public RecordDetailDto setContent(String content) {
        this.content = content;
        return this;
    }
}
