package com.fanap.podchat.chat.tag.result_model;


import com.fanap.podchat.model.TagVo;

import java.util.List;


public class TagListResult {
    List<TagVo> tag ;

    public TagListResult() {

    }

    public TagListResult(List<TagVo> tag) {
        this.tag = tag;
    }

    public List<TagVo> getTags() {
        return tag;
    }

    public void setTags(List<TagVo> tag) {
        this.tag = tag;
    }
}
