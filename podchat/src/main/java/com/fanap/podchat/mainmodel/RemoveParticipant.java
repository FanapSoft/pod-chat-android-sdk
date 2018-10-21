package com.fanap.podchat.mainmodel;

public class RemoveParticipant {

    private String content;
    private String token;
    private String tokenIssuer;
    private int type;
    private long subjectId;
    private String uniqueId;
    
    public String getContent() {
        return content;
    }

    
    public void setContent(String content) {
        this.content = content;
    }

    
    public String getToken() {
        return token;
    }

    
    public void setToken(String token) {
        this.token = token;
    }

    
    public String getTokenIssuer() {
        return tokenIssuer;
    }

    
    public void setTokenIssuer(String tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    
    public int getType() {
        return type;
    }

    
    public void setType(int type) {
        this.type = type;
    }

    
    public long getSubjectId() {
        return subjectId;
    }

    
    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    
    public String getUniqueId() {
        return uniqueId;
    }

    
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
