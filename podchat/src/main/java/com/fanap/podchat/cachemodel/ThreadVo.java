package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.PinMessageVO;

import java.util.List;

@Entity
public class ThreadVo {
    @PrimaryKey
    private long id;
    private long joinDate;
    @Ignore
    private Inviter inviter;
    private long inviterId;
    @Ignore
    private CacheMessageVO lastMessageVO;
    private long lastMessageVOId;
    private String title;
    @Ignore
    private List<CacheParticipant> participants;
    private long time;
    private String lastMessage;
    private String lastParticipantName;
    private String lastParticipantImage;
    private boolean group;
    private long partner;
    private String image;
    private String description;
    private long unreadCount;
    @Deprecated
    private long lastSeenMessageId;
    @Deprecated
    private long partnerLastMessageId;
    @Deprecated
    private long partnerLastSeenMessageId;
    @Deprecated
    private long partnerLastDeliveredMessageId;
    private long lastSeenMessageNanos;
    private long lastSeenMessageTime;
    private long partnerLastSeenMessageTime;
    private long partnerLastSeenMessageNanos;
    private long partnerLastDeliveredMessageTime;
    private long partnerLastDeliveredMessageNanos;
    private int type;
    private boolean mute;
    private String metadata;
    private boolean canEditInfo;
    private long participantCount;
    private boolean canSpam;
    private boolean admin;
    private boolean pin;
    private boolean mentioned;
    @Ignore
    private PinMessageVO pinMessageVO;
    private String uniqueName;
    private String userGroupHash;
    private boolean closed;

    public String getUserGroupHash() {
        return userGroupHash;
    }

    public void setUserGroupHash(String userGroupHash) {
        this.userGroupHash = userGroupHash;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public boolean isPin() {
        return pin;
    }

    public void setPin(boolean pin) {
        this.pin = pin;
    }

    public boolean isMentioned() {
        return mentioned;
    }

    public void setMentioned(boolean mentioned) {
        this.mentioned = mentioned;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CacheParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<CacheParticipant> participants) {
        this.participants = participants;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLastParticipantName() {
        return lastParticipantName;
    }

    public void setLastParticipantName(String lastParticipantName) {
        this.lastParticipantName = lastParticipantName;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public long getPartner() {
        return partner;
    }

    public void setPartner(long partner) {
        this.partner = partner;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getLastSeenMessageId() {
        return lastSeenMessageId;
    }

    public void setLastSeenMessageId(long lastSeenMessageId) {
        this.lastSeenMessageId = lastSeenMessageId;
    }

    public long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }

    public Inviter getInviter() {
        return inviter;
    }

    public void setInviter(Inviter inviter) {
        this.inviter = inviter;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public CacheMessageVO getLastMessageVO() {
        return lastMessageVO;
    }

    public void setLastMessageVO(CacheMessageVO lastMessageVO) {
        this.lastMessageVO = lastMessageVO;
    }

    public long getPartnerLastMessageId() {
        return partnerLastMessageId;
    }

    public void setPartnerLastMessageId(long partnerLastMessageId) {
        this.partnerLastMessageId = partnerLastMessageId;
    }

    public long getPartnerLastDeliveredMessageId() {
        return partnerLastDeliveredMessageId;
    }

    public void setPartnerLastDeliveredMessageId(long partnerLastDeliveredMessageId) {
        this.partnerLastDeliveredMessageId = partnerLastDeliveredMessageId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public long getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(long participantCount) {
        this.participantCount = participantCount;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean isCanEditInfo() {
        return canEditInfo;
    }

    public void setCanEditInfo(boolean canEditInfo) {
        this.canEditInfo = canEditInfo;
    }

    public long getPartnerLastSeenMessageId() {
        return partnerLastSeenMessageId;
    }

    public void setPartnerLastSeenMessageId(long partnerLastSeenMessageId) {
        this.partnerLastSeenMessageId = partnerLastSeenMessageId;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public long getInviterId() {
        return inviterId;
    }

    public void setInviterId(long inviterId) {
        this.inviterId = inviterId;
    }

    public long getLastMessageVOId() {
        return lastMessageVOId;
    }

    public void setLastMessageVOId(long lastMessageVOId) {
        this.lastMessageVOId = lastMessageVOId;
    }

    public String getLastParticipantImage() {
        return lastParticipantImage;
    }

    public void setLastParticipantImage(String lastParticipantImage) {
        this.lastParticipantImage = lastParticipantImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCanSpam() {
        return canSpam;
    }

    public void setCanSpam(boolean canSpam) {
        this.canSpam = canSpam;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public long getLastSeenMessageTime() {
        return lastSeenMessageTime;
    }

    public void setLastSeenMessageTime(long lastSeenMessageTime) {
        this.lastSeenMessageTime = lastSeenMessageTime;
    }

    public long getPartnerLastSeenMessageTime() {
        return partnerLastSeenMessageTime;
    }

    public void setPartnerLastSeenMessageTime(long partnerLastSeenMessageTime) {
        this.partnerLastSeenMessageTime = partnerLastSeenMessageTime;
    }

    public long getPartnerLastSeenMessageNanos() {
        return partnerLastSeenMessageNanos;
    }

    public void setPartnerLastSeenMessageNanos(long partnerLastSeenMessageNanos) {
        this.partnerLastSeenMessageNanos = partnerLastSeenMessageNanos;
    }

    public long getPartnerLastDeliveredMessageTime() {
        return partnerLastDeliveredMessageTime;
    }

    public void setPartnerLastDeliveredMessageTime(long partnerLastDeliveredMessageTime) {
        this.partnerLastDeliveredMessageTime = partnerLastDeliveredMessageTime;
    }

    public long getPartnerLastDeliveredMessageNanos() {
        return partnerLastDeliveredMessageNanos;
    }

    public void setPartnerLastDeliveredMessageNanos(long partnerLastDeliveredMessageNanos) {
        this.partnerLastDeliveredMessageNanos = partnerLastDeliveredMessageNanos;
    }

    public long getLastSeenMessageNanos() {
        return lastSeenMessageNanos;
    }

    public void setLastSeenMessageNanos(long lastSeenMessageNanos) {
        this.lastSeenMessageNanos = lastSeenMessageNanos;
    }

    public PinMessageVO getPinMessageVO() {
        return pinMessageVO;
    }

    public void setPinMessageVO(PinMessageVO pinMessageVO) {
        this.pinMessageVO = pinMessageVO;
    }


    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public ThreadVo() {
    }

    public ThreadVo(long id,
                    long joinDate, Inviter inviter,
                    long inviterId, CacheMessageVO lastMessageVO,
                    long lastMessageVOId, String title,
                    List<CacheParticipant> participants, long time,
                    String lastMessage, String lastParticipantName,
                    String lastParticipantImage, boolean group,
                    long partner, String image, String description,
                    long unreadCount, long lastSeenMessageId,
                    long partnerLastMessageId, long partnerLastSeenMessageId,
                    long partnerLastDeliveredMessageId, long lastSeenMessageNanos,
                    long lastSeenMessageTime, long partnerLastSeenMessageTime,
                    long partnerLastSeenMessageNanos, long partnerLastDeliveredMessageTime,
                    long partnerLastDeliveredMessageNanos, int type,
                    boolean mute, String metadata, boolean canEditInfo,
                    long participantCount, boolean canSpam,
                    boolean admin, boolean pin, boolean mentioned,
                    PinMessageVO pinMessageVO,
                    String uniqueName,
                    String userGroupHash,
                    boolean closed) {
        this.id = id;
        this.joinDate = joinDate;
        this.inviter = inviter;
        this.inviterId = inviterId;
        this.lastMessageVO = lastMessageVO;
        this.lastMessageVOId = lastMessageVOId;
        this.title = title;
        this.participants = participants;
        this.time = time;
        this.lastMessage = lastMessage;
        this.lastParticipantName = lastParticipantName;
        this.lastParticipantImage = lastParticipantImage;
        this.group = group;
        this.partner = partner;
        this.image = image;
        this.description = description;
        this.unreadCount = unreadCount;
        this.lastSeenMessageId = lastSeenMessageId;
        this.partnerLastMessageId = partnerLastMessageId;
        this.partnerLastSeenMessageId = partnerLastSeenMessageId;
        this.partnerLastDeliveredMessageId = partnerLastDeliveredMessageId;
        this.lastSeenMessageNanos = lastSeenMessageNanos;
        this.lastSeenMessageTime = lastSeenMessageTime;
        this.partnerLastSeenMessageTime = partnerLastSeenMessageTime;
        this.partnerLastSeenMessageNanos = partnerLastSeenMessageNanos;
        this.partnerLastDeliveredMessageTime = partnerLastDeliveredMessageTime;
        this.partnerLastDeliveredMessageNanos = partnerLastDeliveredMessageNanos;
        this.type = type;
        this.mute = mute;
        this.metadata = metadata;
        this.canEditInfo = canEditInfo;
        this.participantCount = participantCount;
        this.canSpam = canSpam;
        this.admin = admin;
        this.pin = pin;
        this.mentioned = mentioned;
        this.pinMessageVO = pinMessageVO;
        this.uniqueName = uniqueName;
        this.userGroupHash = userGroupHash;
        this.closed = closed;
    }


}
