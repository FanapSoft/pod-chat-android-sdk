package com.fanap.podchat.mainmodel;

import com.fanap.podchat.cachemodel.ThreadVo;

import java.util.List;

public class Thread {
    private long id;
    private long joinDate;
    private Inviter inviter;
    private MessageVO lastMessageVO;
    private PinMessageVO pinMessageVO;
    private String title;
    private List<Participant> participants;
    private long time;
    private String lastParticipantName;
    private String lastParticipantImage;
    private boolean group;
    private long partner;
    private String image;
    private String description;
    private long unreadCount;
    private Boolean pin;
    private boolean mentioned;
    //last seen message info
    private String lastMessage;
    private long lastSeenMessageId;
    private long lastSeenMessageNanos;
    private long lastSeenMessageTime;
    //partner last seen message info
    private long partnerLastSeenMessageId;
    private long partnerLastSeenMessageTime;
    private long partnerLastSeenMessageNanos;
    private long partnerLastDeliveredMessageId;
    private long partnerLastDeliveredMessageTime;
    private long partnerLastDeliveredMessageNanos;
    private int type;
    private boolean mute;
    private String metadata;
    private boolean canEditInfo;
    private long participantCount;
    private Boolean canSpam;
    private Boolean admin;
    private String uniqueName;


    public Thread(
            long id,
            long joinDate,
            Inviter inviter,
            MessageVO lastMessageVO,
            String title,
            List<Participant> participants,
            long time,
            String lastMessage,
            String lastParticipantName,
            String lastParticipantImage,
            boolean group,
            long partner,
            String image,
            String description,
            long unreadCount,
            long lastSeenMessageId,
            long partnerLastSeenMessageId,
            long partnerLastDeliveredMessageId,
            long lastSeenMessageNanos,
            long lastSeenMessageTime,
            long partnerLastSeenMessageTime,
            long partnerLastSeenMessageNanos,
            long partnerLastDeliveredMessageTime,
            long partnerLastDeliveredMessageNanos,
            int type,
            boolean mute,
            String metadata,
            boolean canEditInfo,
            long participantCount,
            Boolean canSpam,
            Boolean admin,
            Boolean pin,
            Boolean mentioned,
            PinMessageVO pinMessageVO,
            String uniqueName) {
        this.id = id;
        this.joinDate = joinDate;
        this.inviter = inviter;
        this.lastMessageVO = lastMessageVO;
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
    }



    public Thread() {
    }

    public Boolean isPin() {
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

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
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

    public MessageVO getLastMessageVO() {
        return lastMessageVO;
    }

    public void setLastMessageVO(MessageVO lastMessageVO) {
        this.lastMessageVO = lastMessageVO;
    }

    public long getPartnerLastDeliveredMessageId() {
        return partnerLastDeliveredMessageId;
    }

    public void setPartnerLastDeliveredMessageId(long partnerLastDeliveredMessageId) {
        this.partnerLastDeliveredMessageId = partnerLastDeliveredMessageId;
    }

    public PinMessageVO getPinMessageVO() {
        return pinMessageVO;
    }

    public void setPinMessageVO(PinMessageVO pinMessageVO) {
        this.pinMessageVO = pinMessageVO;
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

    public String getLastParticipantImage() {
        return lastParticipantImage;
    }

    public void setLastParticipantImage(String lastParticipantImage) {
        this.lastParticipantImage = lastParticipantImage;
    }

    public Boolean getCanSpam() {
        return canSpam;
    }

    public void setCanSpam(Boolean canSpam) {
        this.canSpam = canSpam;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLastSeenMessageNanos() {
        return lastSeenMessageNanos;
    }

    public void setLastSeenMessageNanos(long lastSeenMessageNanos) {
        this.lastSeenMessageNanos = lastSeenMessageNanos;
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

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }
}
