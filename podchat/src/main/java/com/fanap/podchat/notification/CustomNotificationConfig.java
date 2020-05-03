package com.fanap.podchat.notification;

import android.app.Activity;
import android.support.annotation.NonNull;


public class CustomNotificationConfig {


    private String channelId;
    private String channelName;
    private String channelDescription;
    private Integer notificationImportance;
    @NonNull
    private String applicationId;
    @NonNull
    private String token;
    @NonNull
    private Activity activity;

    private CustomNotificationConfig(Builder builder) {

        channelId = builder.channelId;
        channelName = builder.channelName;
        channelDescription = builder.channelDescription;
        notificationImportance = builder.notificationImportance;
        applicationId = builder.applicationId;
        activity = builder.activity;
        token = builder.token;

    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public Integer getNotificationImportance() {
        return notificationImportance;
    }

    @NonNull
    public String getApplicationId() {
        return applicationId;
    }

    @NonNull
    public Activity getActivity() {
        return activity;
    }

    @NonNull
    public String getToken() {
        return token;
    }

    public static class Builder {

        private String channelId;
        private String channelName;
        private String channelDescription;
        private String token;
        private Integer notificationImportance;
        private String applicationId;
        private Activity activity;


        public Builder(@NonNull String applicationId, @NonNull Activity activity) {
            this.applicationId = applicationId;
            this.activity = activity;
        }

        public Builder setChannelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder setChannelName(String channelName) {
            this.channelName = channelName;
            return this;
        }

        public Builder setChannelDescription(String channelDescription) {
            this.channelDescription = channelDescription;
            return this;
        }

        public Builder setNotificationImportance(Integer notificationImportance) {
            this.notificationImportance = notificationImportance;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public CustomNotificationConfig build() {

            return new CustomNotificationConfig(this);
        }

    }


}
