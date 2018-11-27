package com.fanap.podchat.mainmodel;

import java.util.List;

/**
 * + RequestCreateThread    {object}
 *    - ownerSsoId          {string}
 *    + invitees            {object}
 *       -id                {string}
 *       -idType            {int} ** inviteeVOidTypes
 *    - title               {string}
 *    - type                {int} ** createThreadTypes
 */
public class ChatThread  {

    private int type;
    private String ownerSsoId;
    private List<Invitee> invitees;
    private String title;

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
}
