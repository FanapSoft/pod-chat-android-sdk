package com.fanap.podchat.call.persist;

import androidx.room.Entity;

import com.fanap.podchat.cachemodel.CacheParticipant;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.util.Util;


@Entity
public class CacheCallParticipant extends CacheParticipant {

    private long callId;


    public CacheCallParticipant fromParticipant(Participant participant, long callId) {
        this.setCallId(callId);
        super.setId(participant.getId());
        super.setName(participant.getName());
        super.setFirstName(participant.getFirstName());
        super.setLastName(participant.getLastName());
        super.setImage(participant.getImage());
        super.setNotSeenDuration(participant.getNotSeenDuration());
        super.setContactId(participant.getContactId());
        super.setCoreUserId(participant.getCoreUserId());
        super.setContactName(participant.getContactName());
        super.setContactFirstName(participant.getContactFirstName());
        super.setContactLastName(participant.getContactLastName());
        super.setSendEnable(Util.parserBoolean(participant.getSendEnable()));
        super.setReceiveEnable(Util.parserBoolean(participant.getReceiveEnable()));
        super.setCellphoneNumber(participant.getCellphoneNumber());
        super.setEmail(participant.getEmail());
        super.setMyFriend(Util.parserBoolean(participant.getMyFriend()));
        super.setOnline(Util.parserBoolean(participant.getOnline()));
        super.setBlocked(Util.parserBoolean(participant.getBlocked()));
        super.setAdmin(Util.parserBoolean(participant.getAdmin()));
        super.setAuditor(Util.parserBoolean(participant.getAuditor()));
        super.setKeyId(participant.getKeyId());
        super.setUsername(participant.getUsername());
        super.setRoles(participant.getRoles());
        super.setChatProfileVO(participant.getChatProfileVO());

        return this;
    }

    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }

    public Participant toParticipant() {
        return new Participant(
                this.getId(),
                this.getName(),
                this.getFirstName(),
                this.getLastName(),
                this.getImage(),
                this.getNotSeenDuration(),
                this.getContactId(),
                this.getCoreUserId(),
                this.getContactName(),
                this.getContactFirstName(),
                this.getContactLastName(),
                this.getSendEnable(),
                this.getReceiveEnable(),
                this.getCellphoneNumber(),
                this.getEmail(),
                this.getMyFriend(),
                this.getOnline(),
                this.getBlocked(),
                this.getAdmin(),
                this.isAuditor(),
                this.getRoles(),
                this.getKeyId(),
                this.getUsername(),
                this.getChatProfileVO()
        );
    }
}
