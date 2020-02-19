package com.fanap.podchat.chat.user.profile;

public class ResultUpdateProfile {

    private String bio;
    private String metadata;

    public ResultUpdateProfile() { }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getMetadata() { return metadata; }

    public void setMetadata(String metadata) { this.metadata = metadata; }
}
