package com.fanap.podchat.chat.mainmodel;

import java.util.List;

/**
 * + RequestCreateThread    {object}
 * - ownerSsoId          {string}
 * + invitees            {object}
 * -id                {string}
 * -idType            {int} ** inviteeVOidTypes
 * - title               {string}
 * - type                {int} ** createThreadTypes
 */
public class ChatThread {

    private int type;
    private String ownerSsoId;
    private List<Invitee> invitees;
    private String title;
    private String description;
    private String image;
    private String metadata;

    public String getOwnerSsoId() {
        return ownerSsoId;
    }

    public void setOwnerSsoId(String ownerSsoId) {
        this.ownerSsoId = ownerSsoId;
    }

    public List<Invitee> getInvitees() {
        return invitees;
    }

    public void setInvitees(List<Invitee> invitees) {
        this.invitees = invitees;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
