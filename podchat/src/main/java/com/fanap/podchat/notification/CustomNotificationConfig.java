package com.fanap.podchat.notification;

import android.app.Activity;
import android.support.annotation.NonNull;


public class CustomNotificationConfig {


    private String channelId;
    private String channelName;
    private String channelDescription;
    private Integer notificationImportance;
    @NonNull
    private Activity targetActivity;
    private int icon;

    private CustomNotificationConfig(Builder builder) {

        channelId = builder.channelId;
        channelName = builder.channelName;
        channelDescription = builder.channelDescription;
        notificationImportance = builder.notificationImportance;
        targetActivity = builder.activity;
        this.icon = builder.icon;
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
    public Activity getTargetActivity() {
        return targetActivity;
    }

    public int getIcon() {
        return icon;
    }

    public static class Builder {

        private int icon;
        private String channelId;
        private String channelName;
        private String channelDescription;
        private Integer notificationImportance;
        private Activity activity;


        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        public Builder setChannelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder setIcon(int icon) {
            this.icon = icon;
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


        public CustomNotificationConfig build() {

            return new CustomNotificationConfig(this);
        }

    }


}
