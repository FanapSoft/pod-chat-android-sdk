package com.fanap.podchat.chat.tag.result_model;

import com.fanap.podchat.model.TagParticipantVO;

import java.util.List;

public class TagParticipantResult {
    List<TagParticipantVO> tagParticipants;

    public void setTagParticipans(List<TagParticipantVO> tagParticipants) {
        this.tagParticipants = tagParticipants;
    }

    public List<TagParticipantVO> getTagParticipants() {
        return tagParticipants;
    }

    public void setTagParticipants(List<TagParticipantVO> tagParticipants) {
        this.tagParticipants = tagParticipants;
    }
}
