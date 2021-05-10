package com.fanap.podchat.chat.tag.result_model;

import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.model.TagVo;
import com.google.gson.annotations.SerializedName;

public class TagResult {
    TagVo tag ;

    public TagResult(TagVo tag) {
        this.tag = tag;
    }

    public TagVo getTag() {
        return tag;
    }

    public void setTag(TagVo tag) {
        this.tag = tag;
    }
}
