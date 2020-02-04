package com.fanap.podchat.mainmodel;

public class Contact {
    private long id;
    private String firstName;
    private long userId;
    private String lastName;
    private Boolean blocked;
    private long creationDate;
    private LinkedUser linkedUser;
    private String cellphoneNumber;
    private String email;
    private String uniqueId;
    private long notSeenDuration;
    private boolean hasUser;
    private boolean cache = false;

    public Contact() {
    }

    public Contact(
            long id,
            String firstName,
            long userId,
            String lastName,
            Boolean blocked,
            long creationDate,
            LinkedUser linkedUser,
            String cellphoneNumber,
            String email,
            String uniqueId,
            long notSeenDuration,
            boolean hasUser
    ) {
        this.id = id;
        this.firstName = firstName;
        this.userId = userId;
        this.lastName = lastName;
        this.blocked = blocked;
        this.creationDate = creationDate;
        this.linkedUser = linkedUser;
        this.cellphoneNumber = cellphoneNumber;
        this.email = email;
        this.uniqueId = uniqueId;
        this.notSeenDuration = notSeenDuration;
        this.hasUser = hasUser;
    }

    public Contact(long id,
                   String firstName,
                   long userId,
                   String lastName,
                   Boolean blocked,
                   long creationDate,
                   LinkedUser linkedUser,
                   String cellphoneNumber,
                   String email,
                   String uniqueId,
                   long notSeenDuration,
                   boolean hasUser,
                   boolean cache) {
        this.id = id;
        this.firstName = firstName;
        this.userId = userId;
        this.lastName = lastName;
        this.blocked = blocked;
        this.creationDate = creationDate;
        this.linkedUser = linkedUser;
        this.cellphoneNumber = cellphoneNumber;
        this.email = email;
        this.uniqueId = uniqueId;
        this.notSeenDuration = notSeenDuration;
        this.hasUser = hasUser;
        this.cache = cache;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LinkedUser getLinkedUser() {
        return linkedUser;
    }

    public void setLinkedUser(LinkedUser linkedUser) {
        this.linkedUser = linkedUser;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isHasUser() {
        return hasUser;
    }

    public void setHasUser(boolean hasUser) {
        this.hasUser = hasUser;
    }

    public long getNotSeenDuration() {
        return notSeenDuration;
    }

    public void setNotSeenDuration(long notSeenDuration) {
        this.notSeenDuration = notSeenDuration;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }
}
